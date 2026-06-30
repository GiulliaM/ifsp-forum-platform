# Sprint 2 — Divisão de Trabalho e Arquitetura

> **Período:** 17/06 – 30/06/2026
> **Meta:** Gamificação, Personalização, Recursos Visuais, Suporte, Painel Pedagógico (US-20) e conclusão dos RNFs.

Responsabilidades por integrante (mesma base da Sprint 1):

- **Giullia** → `auth-service` + `api-gateway` + `algorithm-service`
- **Maria Eduarda** → `forum-service`
- **Raissa** → Suporte Técnico

---

## 📋 Divisão das histórias

### 🟣 Giullia — `auth` + `gateway` + `algorithm`

| US | O que é | Serviço | Status |
|----|---------|---------|--------|
| **US-10** | Cadastrar exercício | algorithm | ✅ Concluído (Sprint 1) |
| **US-11** | Ranking de usuários | gamificação | ✅ Concluído |
| **US-12** | Acumular pontos por contribuição + badges | gamificação | ✅ Concluído |
| **US-19** | Conclusão de segurança: refresh token + expiração configurável | auth | ✅ Concluído |
| **US-14** | Preferências de aprendizado (interesses, nível, linguagens) — dado de perfil | auth | ✅ Concluído |
| **US-13** | Sugestões personalizadas (tópicos + exercícios) | personalização | ✅ Concluído |
| **US-20** | Painel pedagógico — lado algoritmos (exercícios com maior taxa de erro) | algorithm | ✅ Concluído |

### 🟢 Maria Eduarda — `forum`

| US | O que é | Serviço | Status |
|----|---------|---------|--------|
| **US-15** | Inserir imagens e código formatado em publicações | forum | ✅ Concluído |
| **US-20** | Painel pedagógico — lado fórum (categorias com mais tópicos sem resposta aceita) | forum | ✅ Concluído |
| *(hooks)* | Emitir eventos de pontuação: tópico criado, comentário, like → US-12 | forum | ✅ Concluído |

### 🔵 Raissa — Suporte

| US | O que é | Serviço | Status |
|----|---------|---------|--------|
| **US-16** | Abrir chamado de suporte (protocolo, FAQ, status) | suporte | ✅ Concluído |
| **US-17** | Atender chamados (painel moderador, urgente 48h) | suporte | ✅ Concluído |

### ⚪ Fora de escopo (projeto é só de APIs/microsserviços)

O projeto **não terá frontend**. As CAs de interface ficam fora de escopo e devem ser
documentadas como tal na entrega; o que tem contrapartida de backend é mantido.

| US | Decisão no escopo só-API |
|----|--------------------------|
| **US-18** | Responsividade (320–2560px, Lighthouse, WCAG) → **fora de escopo** (puramente frontend). |
| **US-15** | Upload/incorporação de imagens e Markdown → backend só **armazena/serve** o conteúdo; preview em tempo real e syntax highlighting são de UI (fora de escopo). |
| **US-13/US-14** | Onboarding e seção "Recomendado para Você" são telas; backend entrega os **endpoints** de preferências e de recomendações. |
| **US-11/US-12** | Exibição de ranking/badges é UI; backend entrega os **endpoints** já especificados. |

---

## ⚙️ Arquitetura da Gamificação (US-11 e US-12)

A gamificação é o ponto que mais acopla os serviços, porque **os pontos nascem em vários lugares**:

| Ação | Origem | Pontos |
|------|--------|--------|
| Criar tópico | forum | +5 |
| Comentar | forum | +3 |
| Receber like | forum | +2 |
| Resolver exercício fácil | algorithm | +10 |
| Resolver exercício médio | algorithm | +20 |
| Resolver exercício difícil | algorithm | +40 |

### Decisão: novo `gamification-service`

Em vez de espalhar a lógica de pontos por forum e algorithm, centralizamos num **microsserviço próprio** (`gamification-service`, porta sugerida **:8084**, banco `ifsp_gamification`). Isso mantém o padrão "um serviço, um banco" da plataforma e deixa as regras de pontuação num lugar só.

```
   forum-service ──┐
                   ├──(POST /api/pontos/eventos)──► gamification-service ──► ifsp_gamification
 algorithm-service ┘                                      :8084
```

### Como os serviços avisam: chamada HTTP síncrona via evento

Quando algo pontuável acontece, o serviço de origem faz **um POST** para o gamification-service registrando o evento. É o caminho mais simples para o escopo da disciplina (não exige broker de mensageria).

```
POST /api/pontos/eventos        (chamada interna, passa pelo gateway)
Headers: X-User-Id: 1, X-User-Role: ESTUDANTE
{
  "tipo": "EXERCICIO_RESOLVIDO",   // enum: TOPICO_CRIADO, COMENTARIO, LIKE_RECEBIDO, EXERCICIO_RESOLVIDO
  "usuarioId": 1,                  // quem recebe os pontos (ex.: autor que recebeu o like)
  "referenciaId": 42,              // id do tópico/comentário/exercício que originou
  "dificuldade": "MEDIO"           // opcional, só para EXERCICIO_RESOLVIDO
}
```

> **Por que o gamification calcula os pontos, e não o emissor?** A tabela de pontos é regra de negócio da gamificação. O forum só sabe dizer *"o usuário X recebeu um like no comentário Y"*; quanto isso vale (+2) é decisão do gamification-service. Assim, mudar a tabela de pontos não exige mexer em forum nem algorithm.

### Idempotência (importante)

Cada evento precisa ser único para não pontuar duas vezes (ex.: dar e tirar like várias vezes, ou reenvio de request). Use uma **chave natural** `(tipo + referenciaId + usuarioOrigem)` com constraint UNIQUE — se o evento já existe, ignora.

Casos a tratar:
- **Like retirado** → evento de estorno (`LIKE_REMOVIDO`, -2) ou simplesmente apagar o evento de like daquela dupla (usuário, publicação).
- **Exercício resolvido mais de uma vez** → pontua só na **primeira** vez que o veredito vira `ACEITO` para aquele par (usuário, exercício).

### Modelo de dados (`ifsp_gamification`)

```
pontos_evento
├── id              BIGINT PK
├── tipo            VARCHAR     -- TOPICO_CRIADO, COMENTARIO, LIKE_RECEBIDO, EXERCICIO_RESOLVIDO
├── usuario_id      BIGINT      -- quem recebeu os pontos
├── referencia_id   BIGINT      -- id do tópico/comentário/exercício
├── pontos          INT         -- valor já calculado
├── criado_em       DATETIME
└── UNIQUE (tipo, referencia_id, usuario_id)

conquista              -- catálogo de badges
├── id              BIGINT PK
├── nome            VARCHAR
├── descricao       VARCHAR
└── criterio        VARCHAR     -- ex.: "PRIMEIRO_TOPICO", "10_EXERCICIOS"

usuario_conquista      -- badges desbloqueadas
├── id              BIGINT PK
├── usuario_id      BIGINT
├── conquista_id    BIGINT FK
├── desbloqueada_em DATETIME
└── UNIQUE (usuario_id, conquista_id)
```

> O total de pontos de cada usuário é a **soma** de `pontos_evento` (não guardamos um campo `total` redundante; ou, se quiser performance no ranking, manter um materializado e recalcular). Para o ranking (US-11), basta `SELECT usuario_id, SUM(pontos) ... GROUP BY usuario_id ORDER BY ... LIMIT 50`.

### Endpoints do `gamification-service`

| Método | Rota | Descrição | Auth |
|--------|------|-----------|------|
| POST | `/api/pontos/eventos` | Registrar evento pontuável (chamado por forum/algorithm) | ✅ interno |
| GET | `/api/ranking?escopo=geral&periodo=total` | Ranking (US-11): escopo geral/fórum/algoritmos, período semana/mês/total | ❌ |
| GET | `/api/pontos/me` | Histórico de pontos do usuário logado (US-12, CA2) | ✅ |
| GET | `/api/conquistas/me` | Conquistas desbloqueadas no perfil (US-12, CA3) | ✅ |

### Resumo das dependências (quem precisa de quem)

- **Giullia** cria o `gamification-service` e expõe `POST /api/pontos/eventos` + ranking.
- **Maria** chama esse endpoint quando há tópico criado / comentário / like no `forum-service`.
- **Giullia** (algorithm) chama esse endpoint quando uma submissão vira `ACEITO` pela primeira vez.

➡️ **Ordem recomendada:** primeiro o gamification-service com o endpoint de eventos (contrato fechado), depois forum e algorithm plugam as chamadas. Assim Maria não fica bloqueada esperando a lógica de ranking ficar pronta.

---

## 🆘 Arquitetura do Suporte Técnico (US-16 e US-17)

Novo microsserviço **`suporte-service`** (porta **8086**, banco `ifsp_suporte`), seguindo o mesmo padrão de "um serviço, um banco" do `gamification-service`: stateless, sem validar JWT (quem valida é o `api-gateway`, que injeta `X-User-Id`/`X-User-Role`), com `GlobalExceptionHandler` padronizado.

```
   estudante ──POST /api/suporte/chamados──► suporte-service ──► ifsp_suporte
   moderador ──GET/POST/PATCH /api/suporte/chamados──┘            :8086
```

### Modelo de dados (`ifsp_suporte`)

```
chamado
├── id, protocolo (único, gerado após o 1º save: "SUP-2026-000123")
├── usuario_id, tipo_problema (BUG/DUVIDA/SUGESTAO/OUTRO), descricao, captura_tela_url
├── status (ABERTO, EM_ATENDIMENTO, RESOLVIDO, ENCERRADO)
└── criado_em, atualizado_em (usado para calcular "urgente")

resposta_chamado  -- respostas do moderador, FK -> chamado
faq_entrada       -- catálogo de FAQ exibido antes da abertura, populado via DataInitializer
```

"Urgente" (CA4 US-17) é **calculado em tempo de leitura**: chamado em ABERTO/EM_ATENDIMENTO sem interação (`atualizado_em`) há mais de 48h.

### Endpoints do `suporte-service`

| Método | Rota | Quem | Descrição |
|--------|------|------|-----------|
| GET | `/api/suporte/faq` | autenticado | FAQ exibida antes da abertura (US-16, CA4) |
| POST | `/api/suporte/chamados` | estudante | abre chamado, retorna protocolo (US-16, CA1/CA2) |
| GET | `/api/suporte/chamados/me` | estudante | lista os próprios chamados com status (US-16, CA3) |
| GET | `/api/suporte/chamados/{protocolo}` | dono ou moderador | detalhe + respostas |
| GET | `/api/suporte/chamados` | moderador | painel com filtro `?status=` e flag `urgente` (US-17, CA1/CA4) |
| POST | `/api/suporte/chamados/{id}/respostas` | moderador | responde chamado (US-17, CA2) |
| PATCH | `/api/suporte/chamados/{id}/status` | moderador | altera status: EM_ATENDIMENTO/RESOLVIDO/ENCERRADO (US-17, CA3) |

> **Notificação por e-mail (CA2 de US-16/US-17), fora de escopo:** o projeto não tem nenhuma infraestrutura de envio de e-mail (não há `JavaMailSender`/SMTP em nenhum serviço). Como já decidido para US-15/US-18, o backend entrega o dado em tempo real via API (protocolo na resposta da abertura, resposta/status consultáveis imediatamente); o disparo de e-mail/push em si é responsabilidade de uma camada de notificação que não existe neste projeto só-API.

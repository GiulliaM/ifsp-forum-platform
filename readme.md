# IFSP Forum Platform — APIs e Microsserviços

Plataforma integrada de fórum de discussões e estudos de algoritmos desenvolvida para a disciplina de **APIs e Microsserviços** do curso de Análise e Desenvolvimento de Sistemas do **IFSP Campus Guarulhos**.

Inspirada no Stack Overflow e no LeetCode, a plataforma oferece fórum colaborativo, prática de algoritmos com feedback automático e gamificação.

---

## 👥 Equipe

| Nome | RA | Responsabilidade |
|------|----|-----------------|
| Giullia Maria de Camargo | GU305554X | `auth-service` + `api-gateway` + `algorithm-service` + `gamification-service` + `personalization-service` + `suporte-service` |
| Maria Eduarda Rodrigues | GU3054985 | `forum-service` |
| Raissa Carla Ferreira | GU3054781 | Suporte Técnico — planejado para a Sprint 2, não entregue; `suporte-service` (US-16/US-17) acabou implementado por Giullia para fechar o escopo |

---

## 🏗️ Arquitetura

```
                                  ┌─────────────────┐
                                  │   api-gateway   │  :8080
                                  │  (roteamento +  │
                                  │  validação JWT) │
                                  └────────┬────────┘
                                           │
          ┌──────────────┬─────────────────┼──────────────────┬────────────────────┬───────────────┐
          │              │                 │                  │                    │               │
   ┌──────▼──────┐  ┌────▼─────────┐  ┌───▼──────────┐  ┌───▼────────────┐  ┌────▼──────────────┐ ┌▼─────────────┐
   │auth-service │  │forum-service │  │algorithm-    │  │gamification-   │  │personalization-   │ │suporte-      │
   │   :8081     │  │   :8082      │  │service :8083 │  │service  :8084  │  │service     :8085  │ │service :8086 │
   │             │  │              │  │              │  │                │  │                   │ │              │
   │ ifsp_auth   │  │ ifsp_forum   │  │ifsp_algorithm│  │ifsp_gamificati │  │   (sem banco)     │ │ ifsp_suporte │
   └─────────────┘  └──────────────┘  └──────────────┘  └────────────────┘  └───────────────────┘ └──────────────┘
```

Cada serviço tem seu **próprio banco de dados MySQL**. A comunicação entre serviços passa pelo gateway, que valida o JWT e injeta `X-User-Id` e `X-User-Role` nos headers.

> ℹ️ **Nota técnica:** Como o Spring Cloud Gateway ainda não possui versão estável compatível com Spring Boot 4.0.6, o `api-gateway` roda em **Spring Boot 3.2.5 + Spring Cloud 2023.0.5** (combinação estável). Ele sobe na porta 8080, valida o JWT e roteia para os serviços. Os demais serviços continuam em Spring Boot 4.0.6.

---

## 📦 Status dos Microsserviços

| Serviço | Porta | Banco | User Stories | Status |
|---------|-------|-------|--------------|--------|
| `api-gateway` | 8080 | — | Roteamento + JWT | ✅ Funcional (Spring Boot 3.2.5) |
| `auth-service` | 8081 | `ifsp_auth` | US-19, US-14 | ✅ Concluído |
| `forum-service` | 8082 | `ifsp_forum` | US-01 a US-06, US-15, US-20 (fórum) | ✅ Concluído |
| `algorithm-service` | 8083 | `ifsp_algorithm` | US-07, 08, 09, 10, US-20 | ✅ Concluído |
| `gamification-service` | 8084 | `ifsp_gamification` | US-11, US-12 | ✅ Concluído |
| `personalization-service` | 8085 | — | US-13 | ✅ Concluído |
| `suporte-service` | 8086 | `ifsp_suporte` | US-16, US-17 | ✅ Concluído |

---

## 🧩 Decisões de arquitetura

| Decisão | Por quê |
|---|---|
| Um banco de dados por serviço | Cada serviço é dono do seu schema; evita acoplamento entre times e permite evoluir/trocar um banco sem afetar os outros (database-per-service). |
| Gateway valida o JWT e injeta `X-User-Id`/`X-User-Role`; os outros serviços confiam nesses headers | Centraliza a validação em um único ponto. Os serviços internos ficam stateless e simples — não duplicam lógica de criptografia/assinatura de token. |
| `gamification-service` centraliza a pontuação, em vez de cada serviço calcular seus próprios pontos | A tabela de pontos é regra de negócio da gamificação; forum e algorithm só avisam "o quê" aconteceu (evento), não decidem "quanto vale". Mudar a pontuação não exige tocar em forum/algorithm. |
| Idempotência de eventos de pontuação via constraint `UNIQUE (tipo, referencia_id, usuario_id)` | Evita pontuar duas vezes por reenvio de requisição ou múltiplos likes/unlikes, sem precisar de lock distribuído ou fila — suficiente para o escopo da disciplina. |
| `algorithm-service` julga por comparação de saídas, não executa código em sandbox | Implementar um sandbox de execução real (Docker, gVisor etc.) está fora do escopo da disciplina; a submissão envia as saídas já produzidas localmente e o serviço compara com o gabarito, mantendo o fluxo de veredito (US-09) sem o risco de rodar código arbitrário. |
| Sem infraestrutura de e-mail/push (nenhum `JavaMailSender`/SMTP em nenhum serviço) | O projeto é só-API; os dados que seriam notificados (protocolo de chamado, respostas, status) ficam disponíveis instantaneamente via endpoints. O disparo de notificação em si é responsabilidade de uma camada que não existe neste projeto. |
| `personalization-service` não tem banco próprio | Ele apenas agrega, em tempo de requisição, dados que já existem em `auth-service` (preferências), `forum-service` (tópicos) e `algorithm-service` (exercícios) — persistir uma cópia geraria dado duplicado e desatualizado. |

---

## ⚙️ Pré-requisitos

- Java 17+
- Maven 3.8+
- MySQL 8+ ou MariaDB 10.6+
- IntelliJ IDEA (recomendado)

---

## 🚀 Como rodar localmente

### 1. Clone o repositório

```bash
git clone https://github.com/GiulliaM/ifsp-forum-platform.git
cd ifsp-forum-platform
```

### 2. Crie os bancos de dados

```sql
CREATE DATABASE ifsp_auth;
CREATE DATABASE ifsp_forum;
CREATE DATABASE ifsp_algorithm;
CREATE DATABASE ifsp_gamification;
CREATE DATABASE ifsp_suporte;
```

> ℹ️ O `personalization-service` não usa banco de dados — agrega dados dos outros serviços via HTTP.

### 3. Configure o `application.properties` de cada serviço

O arquivo fica em `src/main/resources/application.properties` de cada serviço. Troque a senha se necessário:

```properties
spring.datasource.username=root
spring.datasource.password=SUA_SENHA_AQUI
```

### 4. Rode cada serviço

Abra cada pasta no IntelliJ e clique em **Run**, ou pelo terminal:

Abra um terminal separado para cada serviço e execute:

```powershell
# PowerShell (Windows)
cd auth-service;            .\mvnw.cmd spring-boot:run
cd forum-service;           .\mvnw.cmd spring-boot:run
cd algorithm-service;       .\mvnw.cmd spring-boot:run
cd gamification-service;    .\mvnw.cmd spring-boot:run
cd personalization-service; .\mvnw.cmd spring-boot:run
cd suporte-service;         .\mvnw.cmd spring-boot:run
```

```bash
# bash / Git Bash / Linux / macOS
cd auth-service           && ./mvnw spring-boot:run
cd forum-service          && ./mvnw spring-boot:run
cd algorithm-service      && ./mvnw spring-boot:run
cd gamification-service   && ./mvnw spring-boot:run
cd personalization-service && ./mvnw spring-boot:run
cd suporte-service        && ./mvnw spring-boot:run
```

> ⚠️ Suba o `auth-service` antes dos outros — ele é responsável pela geração do JWT.

---

## 🔐 Autenticação

O `auth-service` gera um **JWT** no login. Todos os endpoints protegidos precisam do token no header:

```
Authorization: Bearer SEU_TOKEN_AQUI
```

O token contém:
- `sub` — ID do usuário
- `perfil` — papel: `ESTUDANTE`, `MODERADOR` ou `ADMINISTRADOR`

Os outros serviços leem os headers `X-User-Id` e `X-User-Role` injetados pelo gateway e **não validam o JWT diretamente**.

---

## 🔒 Segurança e privacidade (US-19)

| Requisito (CA) | Como é atendido |
|---|---|
| Senhas com hash seguro | BCrypt no `auth-service`; a senha em texto puro nunca é persistida |
| Autenticação com expiração configurável | JWT assinado (HS384) com `jwt.expiration` configurável em `application.properties`; renovação via `POST /api/auth/refresh` sem precisar logar de novo |
| Sessão revogável | `POST /api/auth/logout` invalida o refresh token armazenado |
| Direito ao esquecimento (LGPD) | `DELETE /api/auth/usuarios/deletar` remove a conta e os dados associados |
| Aceite de termos no cadastro | `POST /api/auth/registrar` exige `termosAceitos: true`; o cadastro é rejeitado sem isso |
| Comunicação criptografada (HTTPS/TLS) | Fora do escopo do ambiente local de desenvolvimento (sem certificado configurado); em produção ficaria a cargo de um proxy reverso (nginx/load balancer) na frente do `api-gateway` |

---

## 🔗 Endpoints

### Auth Service (`localhost:8081`)

| Método | Rota | Descrição | Auth? |
|--------|------|-----------|-------|
| POST | `/api/auth/registrar` | Cadastro de novo usuário (sempre ESTUDANTE) | ❌ |
| POST | `/api/auth/login` | Login → retorna JWT + refresh token | ❌ |
| POST | `/api/auth/refresh` | Renova o access token via refresh token | ❌ |
| POST | `/api/auth/logout` | Revoga o refresh token (encerra sessão) | ❌ |
| GET | `/api/auth/usuarios/preferencias` | Retorna preferências de aprendizado | ✅ |
| PUT | `/api/auth/usuarios/preferencias` | Salva/atualiza preferências de aprendizado | ✅ |
| DELETE | `/api/auth/usuarios/deletar` | Excluir conta (LGPD) | ✅ |

**Exemplo de cadastro:**
```json
POST /api/auth/registrar
{
  "nome": "João Silva",
  "email": "joao@ifsp.edu.br",
  "senha": "123456",
  "termosAceitos": true
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "refreshToken": "a3f2c1d0-...",
  "expiresIn": 86400,
  "nome": "João Silva",
  "email": "joao@ifsp.edu.br",
  "perfil": "ESTUDANTE"
}
```

**Exemplo de preferências (US-14):**
```json
PUT /api/auth/usuarios/preferencias
Headers: X-User-Id: 1
{
  "nivel": "INTERMEDIARIO",
  "interesses": ["backend", "algoritmos"],
  "linguagens": ["Java", "Python"]
}
```

---

### Forum Service (`localhost:8082`)

| Método | Rota | Descrição | Auth? | Role |
|--------|------|-----------|-------|------|
| POST | `/api/topicos` | Criar tópico | ✅ | ESTUDANTE+ |
| GET | `/api/topicos` | Listar todos os tópicos | ❌ | — |
| GET | `/api/topicos/{id}` | Buscar tópico por ID | ❌ | — |
| DELETE | `/api/topicos/{id}` | Remover tópico | ✅ | MODERADOR |
| PATCH | `/api/topicos/{id}/encerrar` | Encerrar tópico | ✅ | MODERADOR |
| PATCH | `/api/topicos/{id}/fixar` | Fixar/desafixar tópico | ✅ | MODERADOR |
| POST | `/api/topicos/{id}/comentarios` | Comentar em tópico | ✅ | ESTUDANTE+ |
| PUT | `/api/comentarios/{id}` | Editar comentário (≤30 min) | ✅ | Dono |
| DELETE | `/api/comentarios/{id}` | Excluir comentário (≤30 min) | ✅ | Dono |
| POST | `/api/topicos/{id}/like` | Like/unlike em tópico | ✅ | ESTUDANTE+ |
| POST | `/api/comentarios/{id}/like` | Like/unlike em comentário | ✅ | ESTUDANTE+ |
| POST | `/api/topicos/{id}/seguir` | Seguir tópico | ✅ | ESTUDANTE+ |
| DELETE | `/api/topicos/{id}/seguir` | Desseguir tópico | ✅ | ESTUDANTE+ |
| GET | `/api/topicos/seguidos?categoria=&ordem=desc` | Listar tópicos seguidos (filtro por categoria, ordenação por data, destaque de novidades) | ✅ | ESTUDANTE+ |
| GET | `/api/topicos/sugeridos` | Tópicos sugeridos com base no histórico de participação do usuário (US-13) | ✅ | ESTUDANTE+ |
| GET | `/api/topicos/metricas/sem-resposta?periodo=7` | Painel pedagógico: categorias com mais tópicos sem resposta nos últimos 7 ou 30 dias (US-20) | ✅ | MODERADOR |

> ℹ️ **US-15:** tópicos e comentários aceitam o campo opcional `imageUrl`; o backend só armazena/serve a URL (upload e preview ficam fora de escopo, ver seção de UI).

> ℹ️ **Pontuação (US-12):** ao criar tópico, comentar ou receber like, o `forum-service` notifica o `gamification-service` via `POST /api/pontos/eventos`. O ranking do fórum é consultado em `gamification-service` (`GET /api/ranking?escopo=FORUM`), não há endpoint de ranking local no `forum-service`.

**Exemplo de criação de tópico:**
```json
POST /api/topicos
Headers: X-User-Id: 1, X-User-Role: ESTUDANTE
{
  "titulo": "Como funciona uma árvore binária?",
  "descricao": "Estou com dificuldade em entender a estrutura de uma árvore binária e como percorrê-la.",
  "categoria": "Estruturas de Dados"
}
```

---

### Algorithm Service (`localhost:8083`)

| Método | Rota | Descrição | Auth? | Role |
|--------|------|-----------|-------|------|
| GET | `/api/exercicios` | Listar exercícios (filtros: `?dificuldade=&categoria=`) | ❌ | — |
| GET | `/api/exercicios/{id}` | Detalhe do exercício | ❌ | — |
| POST | `/api/exercicios` | Cadastrar exercício | ✅ | MODERADOR |
| GET | `/api/exercicios/painel-pedagogico` | Top N exercícios com maior taxa de erro (US-20) | ✅ | MODERADOR |
| POST | `/api/submissoes` | Submeter solução | ✅ | ESTUDANTE+ |
| GET | `/api/submissoes/me` | Histórico de submissões | ✅ | ESTUDANTE+ |
| GET | `/api/submissoes/{id}/feedback` | Feedback detalhado | ✅ | Dono |

> ℹ️ **Juiz por comparação:** o `algorithm-service` não executa código em sandbox. A submissão envia o código e a **lista de saídas** produzidas pela solução (uma por caso de teste, na ordem). O serviço normaliza e compara com a saída esperada de cada caso, gravando o resultado individual (base do feedback da US-09). Veredito: `ACEITO` quando todos os casos passam, senão `RESPOSTA_ERRADA`.

**Exemplo de cadastro de exercício (US-10, MODERADOR):**
```json
POST /api/exercicios
Headers: X-User-Id: 9, X-User-Role: MODERADOR
{
  "titulo": "Soma de dois números",
  "enunciado": "Leia dois inteiros e imprima a soma deles.",
  "dificuldade": "FACIL",
  "categoria": "Matemática",
  "exemploEntrada": "2 3",
  "exemploSaida": "5",
  "publicar": true,
  "casosTeste": [
    { "entrada": "2 3", "saidaEsperada": "5" },
    { "entrada": "10 7", "saidaEsperada": "17" }
  ]
}
```

**Exemplo de submissão (US-08, ESTUDANTE):**
```json
POST /api/submissoes
Headers: X-User-Id: 1, X-User-Role: ESTUDANTE
{
  "exercicioId": 1,
  "linguagem": "JAVA",
  "codigo": "public class Main { ... }",
  "saidas": ["5", "17"]
}
```

---

### Personalization Service (`localhost:8085`)

| Método | Rota | Descrição | Auth? |
|--------|------|-----------|-------|
| GET | `/api/sugestoes` | Sugestões personalizadas de tópicos e exercícios (US-13) | ✅ |

Agrega dados do `auth-service` (preferências), `forum-service` (tópicos por interesse) e `algorithm-service` (exercícios por nível). Retorna até 5 sugestões de cada.

**Exemplo de resposta:**
```json
GET /api/sugestoes
Headers: X-User-Id: 1

{
  "topicos": [
    { "id": 3, "titulo": "Dúvida sobre Spring Boot", "categoria": "backend", "totalLikes": 12, "totalComentarios": 4 }
  ],
  "exercicios": [
    { "id": 7, "titulo": "Busca binária", "dificuldade": "MEDIO", "categoria": "algoritmos", "taxaAcerto": 42.5 }
  ]
}
```

---

### Gamification Service (`localhost:8084`)

| Método | Rota | Descrição | Auth? |
|--------|------|-----------|-------|
| POST | `/api/pontos/eventos` | Registrar evento pontuável (chamado por forum/algorithm) | ✅ interno |
| GET | `/api/pontos/me` | Extrato de pontos do usuário logado (US-12) | ✅ |
| GET | `/api/pontos/me/conquistas` | Badges desbloqueadas no perfil (US-12) | ✅ |
| GET | `/api/ranking?escopo=GERAL&periodo=TOTAL` | Ranking de usuários (US-11) | ❌ |

**Escopos do ranking:** `GERAL`, `FORUM`, `ALGORITMOS`  
**Períodos:** `SEMANA`, `MES`, `TOTAL`

**Exemplo de evento de pontuação:**
```json
POST /api/pontos/eventos
Headers: X-User-Id: 1, X-User-Role: ESTUDANTE
{
  "tipo": "EXERCICIO_RESOLVIDO",
  "usuarioId": 1,
  "referenciaId": 42,
  "dificuldade": "MEDIO"
}
```

**Tabela de pontos:**

| Ação | Pontos |
|------|--------|
| Criar tópico | +5 |
| Comentar | +3 |
| Receber like | +2 |
| Resolver exercício fácil | +10 |
| Resolver exercício médio | +20 |
| Resolver exercício difícil | +40 |

**Badges disponíveis:** `PRIMEIRO_TOPICO`, `PRIMEIRO_COMENTARIO`, `PRIMEIRO_LIKE`, `PRIMEIRO_EXERCICIO`, `10_EXERCICIOS`

> ℹ️ Idempotência garantida: o mesmo evento `(tipo + referenciaId + usuarioId)` nunca pontua duas vezes.

---

### Suporte Service (`localhost:8086`)

| Método | Rota | Descrição | Auth? | Role |
|--------|------|-----------|-------|------|
| GET | `/api/suporte/faq` | Lista a FAQ exibida antes da abertura de um chamado (US-16) | ✅ | — |
| POST | `/api/suporte/chamados` | Abre chamado de suporte, retorna o protocolo (US-16) | ✅ | ESTUDANTE+ |
| GET | `/api/suporte/chamados/me` | Lista os próprios chamados com status (US-16) | ✅ | ESTUDANTE+ |
| GET | `/api/suporte/chamados/{protocolo}` | Detalhe do chamado + respostas (dono ou moderador) | ✅ | Dono/MODERADOR |
| GET | `/api/suporte/chamados?status=` | Painel do moderador, com flag `urgente` (US-17) | ✅ | MODERADOR |
| POST | `/api/suporte/chamados/{id}/respostas` | Responde um chamado (US-17) | ✅ | MODERADOR |
| PATCH | `/api/suporte/chamados/{id}/status` | Altera status: `EM_ATENDIMENTO`, `RESOLVIDO`, `ENCERRADO` (US-17) | ✅ | MODERADOR |

> ℹ️ **Protocolo:** gerado no formato `SUP-AAAA-NNNNNN` ao abrir o chamado. **Urgência (CA4 US-17):** calculada em tempo de leitura — chamado em `ABERTO`/`EM_ATENDIMENTO` sem nenhuma interação há mais de 48h.

> ℹ️ **Notificação por e-mail:** o projeto não possui infraestrutura de envio de e-mail (sem `JavaMailSender`/SMTP em nenhum serviço). O protocolo e as respostas/status ficam disponíveis imediatamente via API; o disparo de e-mail/push é responsabilidade de uma camada de notificação fora do escopo deste projeto só-API.

**Exemplo de abertura de chamado (US-16):**
```json
POST /api/suporte/chamados
Headers: X-User-Id: 1, X-User-Role: ESTUDANTE
{
  "tipoProblema": "BUG",
  "descricao": "A página de submissões não carrega o histórico de tentativas.",
  "capturaTelaUrl": "https://exemplo.com/print.png"
}
```

**Exemplo de painel do moderador (US-17):**
```json
GET /api/suporte/chamados?status=ABERTO
Headers: X-User-Role: MODERADOR
```

---

## 🗂️ Estrutura de pacotes

Todos os serviços seguem o mesmo padrão:

```
src/main/java/br/edu/ifsp/guarulhos/[servico]/
├── controller/        ← endpoints REST
├── service/           ← regras de negócio
├── repository/        ← interfaces JPA
├── model/             ← entidades do banco
│   └── enums/
├── dto/
│   ├── request/       ← dados que chegam na API
│   └── response/      ← dados que saem da API
├── exception/         ← exceções customizadas
└── NomeApplication.java
```

---

## 🗃️ Banco de dados

As tabelas são criadas automaticamente pelo JPA (`ddl-auto=update`). Para visualizar após subir os serviços:

```sql
USE ifsp_auth;
SHOW TABLES;
DESCRIBE usuarios;

USE ifsp_forum;
SHOW TABLES;

USE ifsp_algorithm;
SHOW TABLES;
```

**Tabelas por banco:**

| Banco | Tabelas |
|-------|---------|
| `ifsp_auth` | `usuarios`, `refresh_tokens`, `preferencias_usuario`, `usuario_interesses`, `usuario_linguagens` |
| `ifsp_forum` | `topicos`, `comentarios`, `likes`, `seguimentos` |
| `ifsp_algorithm` | `exercicios`, `casos_teste`, `submissoes`, `resultados_caso_teste` |
| `ifsp_gamification` | `pontos_evento`, `conquista`, `usuario_conquista` |
| `ifsp_suporte` | `chamado`, `resposta_chamado`, `faq_entrada` |

---

## 🧪 Testes

Cada serviço tem testes unitários (JUnit 5 + Mockito) na camada de `service`, isolando a regra de negócio do banco de dados. Para rodar os testes de um serviço:

```powershell
cd auth-service; .\mvnw.cmd test
```
```bash
cd auth-service && ./mvnw test
```

| Serviço | Classes de teste | Cobre |
|---|---|---|
| `auth-service` | `RefreshTokenServiceTest`, `PreferenciaServiceTest` | Geração/renovação/revogação de refresh token; CRUD de preferências de aprendizado (US-14) |
| `forum-service` | `TopicoServiceTest`, `ComentarioServiceTest`, `LikeServiceTest`, `SeguimentoServiceTest`, `MetricaServiceTest`, `PontuacaoServiceTest` | Criação/moderação de tópicos, título duplicado, comentários, toggle de like, seguir/desseguir, painel pedagógico do fórum (US-20), hooks de pontuação (US-12) |
| `algorithm-service` | `ExercicioServiceTest`, `SubmissaoServiceTest`, `PainelPedagogicoServiceTest` | Cadastro de exercício (rascunho/publicação, validação de moderador), veredito da submissão por comparação de saídas, painel de exercícios com maior taxa de erro (US-20) |
| `gamification-service` | `PontuacaoServiceTest`, `RankingServiceTest` | Tabela de pontos por tipo de evento, idempotência, desbloqueio de badges, ranking por escopo/período |
| `personalization-service` | `SugestaoServiceTest` | Sugestões filtradas por preferências e fallback quando um serviço dependente está fora do ar |
| `suporte-service` | `ChamadoServiceTest`, `FaqServiceTest` | Geração de protocolo, regras de acesso (dono/moderador), chamado encerrado, flag de urgência (+48h) |
| `api-gateway` | — | Sem testes unitários (módulo fino de roteamento + filtro de JWT); validado manualmente via Postman |

> ℹ️ As classes `*ApplicationTests` (`@SpringBootTest`, um por serviço) sobem o contexto Spring completo e exigem o MySQL do serviço já rodando — são smoke tests de integração, não unitários. Para rodar só os testes unitários sem subir banco: `./mvnw test -Dtest='*ServiceTest'`.

---

## 📋 Sprints

| Sprint | Período | Meta | Status |
|--------|---------|------|--------|
| Sprint 1 | até 16/06/2026 | Fórum completo, Seguimento, Catálogo e Submissão de Algoritmos, Segurança | ✅ Concluída |
| Sprint 2 | 17/06 – 30/06/2026 | Gamificação, Personalização, Suporte, Painel Pedagógico e RNFs | ✅ Concluída |

---

## 📄 Documentação

- [Histórias de Usuário (PDF)](./Historias_de_Usuario_IFSP.pdf)
- [Enunciado do Tema 5 (PDF)](./TEMA_5.pdf)
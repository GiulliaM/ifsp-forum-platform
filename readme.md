# IFSP Forum Platform — APIs e Microsserviços

Plataforma integrada de fórum de discussões e estudos de algoritmos desenvolvida para a disciplina de **APIs e Microsserviços** do curso de Análise e Desenvolvimento de Sistemas do **IFSP Campus Guarulhos**.

Inspirada no Stack Overflow e no LeetCode, a plataforma oferece fórum colaborativo, prática de algoritmos com feedback automático e gamificação.

---

## 👥 Equipe

| Nome | RA | Responsabilidade |
|------|----|-----------------|
| Giullia Maria de Camargo | GU305554X | `auth-service` + `api-gateway` + `algorithm-service` |
| Maria Eduarda Rodrigues | GU3054985 | `forum-service` |
| Raissa Carla Ferreira | GU3054781 | Suporte Técnico (Sprint 2 — US-16 e US-17) |

---

## 🏗️ Arquitetura

```
                        ┌─────────────────┐
                        │   api-gateway   │  :8080
                        │  (roteamento +  │
                        │  validação JWT) │
                        └────────┬────────┘
                                 │
          ┌──────────────────────┼──────────────────────┐
          │                      │                      │
   ┌──────▼──────┐       ┌───────▼──────┐      ┌───────▼──────┐
   │auth-service │       │forum-service │      │algorithm-    │
   │   :8081     │       │   :8082      │      │service :8083 │
   │             │       │              │      │              │
   │ ifsp_auth   │       │ ifsp_forum   │      │ifsp_algorithm│
   └─────────────┘       └──────────────┘      └──────────────┘
```

Cada serviço tem seu **próprio banco de dados MySQL**. A comunicação entre serviços passa pelo gateway, que valida o JWT e injeta `X-User-Id` e `X-User-Role` nos headers.

> ℹ️ **Nota técnica:** Como o Spring Cloud Gateway ainda não possui versão estável compatível com Spring Boot 4.0.6, o `api-gateway` roda em **Spring Boot 3.2.5 + Spring Cloud 2023.0.5** (combinação estável). Ele sobe na porta 8080, valida o JWT e roteia para os serviços. Os demais serviços continuam em Spring Boot 4.0.6.

---

## 📦 Status dos Microsserviços

| Serviço | Porta | Banco | User Stories | Status |
|---------|-------|-------|--------------|--------|
| `api-gateway` | 8080 | — | Roteamento + JWT | ✅ Funcional (Spring Boot 3.2.5) |
| `auth-service` | 8081 | `ifsp_auth` | US-19 | ✅ Concluído |
| `forum-service` | 8082 | `ifsp_forum` | US-01 a US-06 | ✅ Concluído |
| `algorithm-service` | 8083 | `ifsp_algorithm` | US-07, 08, 09, 10 | ✅ Concluído |

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
```

### 3. Configure o `application.properties` de cada serviço

O arquivo fica em `src/main/resources/application.properties` de cada serviço. Troque a senha se necessário:

```properties
spring.datasource.username=root
spring.datasource.password=SUA_SENHA_AQUI
```

### 4. Rode cada serviço

Abra cada pasta no IntelliJ e clique em **Run**, ou pelo terminal:

```bash
# Em terminais separados — suba o auth-service primeiro
cd auth-service      && mvn spring-boot:run
cd forum-service     && mvn spring-boot:run
cd algorithm-service && mvn spring-boot:run
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

## 🔗 Endpoints

### Auth Service (`localhost:8081`)

| Método | Rota | Descrição | Auth? |
|--------|------|-----------|-------|
| POST | `/api/auth/registrar` | Cadastro de novo usuário (sempre ESTUDANTE) | ❌ |
| POST | `/api/auth/login` | Login → retorna JWT | ❌ |
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
  "nome": "João Silva",
  "email": "joao@ifsp.edu.br",
  "perfil": "ESTUDANTE"
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
| GET | `/api/exercicios` | Listar exercícios | ❌ | — |
| GET | `/api/exercicios/{id}` | Detalhe do exercício | ❌ | — |
| POST | `/api/exercicios` | Cadastrar exercício | ✅ | MODERADOR |
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
| `ifsp_auth` | `usuarios` |
| `ifsp_forum` | `topicos`, `comentarios`, `likes`, `seguimentos` |
| `ifsp_algorithm` | `exercicios`, `casos_teste`, `submissoes`, `resultados_caso_teste` |

---

## 📋 Sprints

| Sprint | Período | Meta | Status |
|--------|---------|------|--------|
| Sprint 1 | até 16/06/2026 | Fórum completo, Seguimento, Catálogo e Submissão de Algoritmos, Segurança | ✅ Concluída |
| Sprint 2 | 17/06 – 30/06/2026 | Gamificação, Personalização, Suporte, Painel Pedagógico e RNFs | 🔄 |

---

## 📄 Documentação

- [Histórias de Usuário (PDF)](./Historias_de_Usuario_IFSP.pdf)
- [Enunciado do Tema 5 (PDF)](./TEMA_5.pdf)
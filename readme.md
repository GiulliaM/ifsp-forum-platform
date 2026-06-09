# IFSP Forum Platform — APIs e Microsserviços

Plataforma integrada de fórum de discussões e estudos de algoritmos desenvolvida para a disciplina de **APIs e Microsserviços** do curso de Análise e Desenvolvimento de Sistemas do **IFSP Campus Guarulhos**.

Inspirada no Stack Overflow e no LeetCode, a plataforma oferece fórum colaborativo, prática de algoritmos com feedback automático e gamificação.

---

## 👥 Equipe

| Nome | RA |
|------|----|
| Giullia Maria de Camargo | GU305554X |
| Maria Eduarda Rodrigues | GU3054985 | 
| Raissa Carla Ferreira | GU3054781 | 

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

---

## 📦 Microsserviços

| Serviço | Porta | Banco | User Stories |
|---------|-------|-------|--------------|
| `api-gateway` | 8080 | — | Roteamento geral |
| `auth-service` | 8081 | `ifsp_auth` | US-19 |
| `forum-service` | 8082 | `ifsp_forum` | US-01 a US-06 |
| `algorithm-service` | 8083 | `ifsp_algorithm` | US-07, 08, 09, 10 |

---

## ⚙️ Pré-requisitos

- Java 17+
- Maven 3.8+
- MySQL 8+
- IntelliJ IDEA (recomendado)

---

## 🚀 Como rodar localmente

### 1. Clone o repositório

```bash
git clone https://github.com/GiulliaM/ifsp-forum-platform.git
cd ifsp-forum-platform
```

### 2. Cria os bancos de dados

```sql
CREATE DATABASE ifsp_auth;
CREATE DATABASE ifsp_forum;
CREATE DATABASE ifsp_algorithm;
```

### 3. Configure o `application.properties` de cada serviço

Em cada serviço, o arquivo fica em `src/main/resources/application.properties`.
Troque a senha se necessário:

```properties
spring.datasource.username=root
spring.datasource.password=SUA_SENHA_AQUI
```

### 4. Rode cada serviço

Abre cada pasta no IntelliJ como projeto Maven separado e clica em **Run**, ou pelo terminal:

```bash
# Em terminais separados:
cd auth-service && mvn spring-boot:run
cd forum-service && mvn spring-boot:run
cd algorithm-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

> ⚠️ Sobe o `auth-service` antes dos outros — o gateway depende do JWT que ele gera.

---

## 🔐 Autenticação

O `auth-service` gera um **JWT** no login. Todos os endpoints protegidos precisam do token no header:

```
Authorization: Bearer SEU_TOKEN_AQUI
```

O gateway valida o token e injeta automaticamente nos outros serviços:
- `X-User-Id` — ID do usuário logado
- `X-User-Role` — papel: `ESTUDANTE`, `MODERADOR` ou `ADMINISTRADOR`

Os outros serviços **não validam JWT** — só leem esses headers.

---

## 📋 Endpoints principais

### Auth Service (`localhost:8081`)
| Método | Rota | Descrição | Auth? |
|--------|------|-----------|-------|
| POST | `/api/auth/register` | Cadastro | ❌ |
| POST | `/api/auth/login` | Login → JWT | ❌ |
| DELETE | `/api/auth/usuarios/me` | Excluir conta (LGPD) | ✅ |

### Forum Service (`localhost:8082`)
| Método | Rota | Descrição | Auth? |
|--------|------|-----------|-------|
| POST | `/api/topicos` | Criar tópico | ✅ |
| GET | `/api/topicos` | Listar tópicos | ❌ |
| POST | `/api/topicos/{id}/comentarios` | Comentar | ✅ |
| POST | `/api/topicos/{id}/like` | Like/unlike | ✅ |
| POST | `/api/topicos/{id}/seguir` | Seguir tópico | ✅ |
| PATCH | `/api/topicos/{id}/encerrar` | Encerrar (moderador) | ✅ |

### Algorithm Service (`localhost:8083`)
| Método | Rota | Descrição | Auth? |
|--------|------|-----------|-------|
| GET | `/api/exercicios` | Listar exercícios | ❌ |
| GET | `/api/exercicios/{id}` | Detalhe do exercício | ❌ |
| POST | `/api/submissoes` | Submeter solução | ✅ |
| GET | `/api/submissoes/{id}/feedback` | Feedback detalhado | ✅ |

---

## 🗂️ Estrutura de pacotes

Todos os serviços seguem o mesmo padrão:

```
src/main/java/br/edu/ifsp/guarulhos/[servico]/
├── controller/       ← endpoints REST
├── service/          ← regras de negócio
├── repository/       ← interfaces JPA
├── model/            ← entidades do banco
│   └── enums/
├── dto/
│   ├── request/      ← dados que chegam na API
│   └── response/     ← dados que saem da API
└── NomeApplication.java
```

---

## 📌 Sprints

| Sprint | Período | Meta |
|--------|---------|------|
| Sprint 1 | até 09/06/2026 | Fórum, Seguimento, Algoritmos (core) e Segurança |
| Sprint 2 | 10/06 – 16/06/2026 | Gamificação, Personalização, Suporte e RNFs |

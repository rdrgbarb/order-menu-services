# order-menu-services

Two Spring Boot microservices (**menu** + **order**) for a simple ordering workflow, using **MongoDB** for persistence
and **RabbitMQ** for status-change notifications.

## Tech stack

- Java 21
- Spring Boot
- Maven (multi-module)
- MongoDB
- RabbitMQ (Spring AMQP)
- OpenAPI / Swagger UI (springdoc)
- Docker / docker compose

---

## Services & ports

| Service | Module           | Port   |
|---------|------------------|--------|
| menu    | `services/menu`  | `8081` |
| order   | `services/order` | `8082` |

---

## Quality gate (before committing)

From the repository root, run the full quality gate:

```bash
# Runs: compile + tests + package for all modules
# Also enforces formatting via Spotless (spotless:check bound to Maven 'verify')
./mvnw clean verify
```

If it fails due to formatting, apply formatting and re-run the gate:

```bash
./mvnw spotless:apply
./mvnw clean verify

```

## How to run

### Option A) Run with Docker (recommended smoke test)

Bring up Mongo + RabbitMQ + both services:

```bash
docker compose up --build
````

After it’s up:

* Menu Swagger UI: `http://localhost:8081/swagger-ui.html`
* Order Swagger UI: `http://localhost:8082/swagger-ui.html`
* RabbitMQ Management: `http://localhost:15672`

  * default user/pass: `guest` / `guest`
* MongoDB: `mongodb://localhost:27017`

Stop everything:

```bash
docker compose down
```

Reset volumes (drops Mongo data):

```bash
docker compose down -v
```

### Option B) Run locally (without Docker)

You need MongoDB and RabbitMQ running somewhere (local or external), then:

```bash
./mvnw clean verify
```

Run each service in separate terminals:

```bash
./mvnw -pl services/menu spring-boot:run
./mvnw -pl services/order spring-boot:run
```

---

## Configuration notes

Each service has its own `application.yaml`:

* `services/menu/src/main/resources/application.yaml`
* `services/order/src/main/resources/application.yaml`

When running via Docker Compose, services receive connection settings through environment variables:

* `SPRING_DATA_MONGODB_URI`
* `SPRING_RABBITMQ_HOST`
* `SPRING_RABBITMQ_PORT`

---

## Repository layout

* `README.md` — Project overview and how to run/build/test.
* `DEV_PLAN.md` — Development plan, milestones, checklist, TDD workflow, Conventional Commits guidance.
* `docker-compose.yml` — Local stack (Mongo + RabbitMQ + both services).
* `pom.xml` — Maven parent (multi-module).
* `services/`

  * `menu/` — Menu service (Spring Boot app + Dockerfile)
  * `order/` — Order service (Spring Boot app + Dockerfile)
* `.editorconfig` — Editor defaults.
* `.gitignore` — Ignore rules (build outputs, IDE files, logs, local configs).

---

## Build

Build everything from the repo root:

```bash
./mvnw clean package
```

Build a single module:

```bash
./mvnw -pl services/menu clean package
./mvnw -pl services/order clean package
```

---

## Test

Run all tests:

```bash
./mvnw test
```

---

## Conventional Commits

This repo follows Conventional Commits.

Format:

* `<type>(scope): <subject>`

Common types:

* `feat`, `fix`, `test`, `refactor`, `chore`, `docs`, `ci`, `build`

Scopes used:

* `init`, `menu`, `order`, `rabbit`, `docker`, `docs`, `ci`, `build`

Examples:

* `chore(init): bootstrap repository skeleton`
* `feat(menu): implement create and get menu items`
* `feat(order): create order with menu enrichment`
* `feat(rabbit): publish and consume order status events`
* `docs(readme): add runbook and swagger links`

---

## Troubleshooting

### Docker pulls timing out

If `docker pull` fails but `curl` works, try pulling from alternative registries (this project already uses
`public.ecr.aws` images in compose/Dockerfiles).

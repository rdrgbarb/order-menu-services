# order-menu-services

Two Spring Boot microservices (**menu** + **order**) for a simple ordering workflow, using **MongoDB** for persistence
and **RabbitMQ** for status-change notifications (simulation via logs).

## Tech stack

- Java 21 + Spring Boot 4
- Maven (multi-module)
- MongoDB 7
- RabbitMQ (Spring AMQP)
- OpenAPI / Swagger UI (springdoc)
- Docker / docker compose

---

## Developer ergonomics (Makefile)

Start here:
- `make help` -- list available commands

### Quality gate
- `make verify` -- formatting check + tests (`./mvnw verify`)

### Tests
- `make test` -- all tests
- `make test.menu` -- menu module tests
- `make test.order` -- order module tests

### Formatting (Spotless)
- `make spotless.check` -- check formatting
- `make spotless.apply` -- auto-fix formatting

### Docker (local stack)
- `make up` -- `docker compose up --build`
- `make down` -- `docker compose down -v`
- `make ps` -- list containers

### Logs (powerful filters)
- `make logs` -- follow logs (all)
- `make logs.order` / `make logs.menu` -- follow one service
- `make logs.order FILTER="NOTIFICATION|Published eventType"` -- grep logs live
- `make logs.order.err` -- focused stacktrace filters

---

## Services & ports

| Service | Module           | Port   |
|---------|------------------|--------|
| menu    | `services/menu`  | `8081` |
| order   | `services/order` | `8082` |

Infra:
- MongoDB: `27017`
- RabbitMQ: `5672`
- RabbitMQ Management: `15672` (default user/pass: `guest` / `guest` -- local dev)

---

## How to run (Docker recommended)

### 1) Boot everything
```bash
make up
```
Wait until both services are healthy (see logs if needed).

### 2) Scripted demo (creates menu items + orders + patches status)

```bash
chmod +x ./curl-examples.sh
ORDER_COUNT=10 ./curl-examples.sh
```

#### Scripted demo notes
`curl-examples.sh` is the end-to-end smoke test: it creates menu items, creates orders using returned `productId`s
(Order → Menu dependency), patches status (publishes event), and the Order consumer logs a `NOTIFICATION`.

Knobs:
- `MENU_COUNT`, `ORDER_COUNT`, `PATCH_STATUS`
- `OFFSET`, `LIMIT` (pagination for `GET /orders`)

```bash
# Create 5 menu items, 50 orders, and patch status to PREPARING
MENU_COUNT=5 ORDER_COUNT=50 PATCH_STATUS=PREPARING ./curl-examples.sh
```

### 3) Confirm notifications (Rabbit consumer)

Order service consumes the status-change event and logs a notification.

```bash
make logs.order FILTER="NOTIFICATION|Published eventType|order.status.changed"
```

### Stop (and reset volumes)

```bash
make down
```

### One-command smoke

```bash
make down
make up
ORDER_COUNT=10 ./curl-examples.sh
make logs.order FILTER="NOTIFICATION|Published eventType|order.status.changed"
```

---

## Swagger / OpenAPI

* Menu Swagger UI: `http://localhost:8081/swagger-ui/index.html`
* Order Swagger UI: `http://localhost:8082/swagger-ui/index.html`
* Menu OpenAPI JSON: `http://localhost:8081/v3/api-docs`
* Order OpenAPI JSON: `http://localhost:8082/v3/api-docs`

---

## Architecture notes

* **Layering**: controllers (HTTP) → services (use-cases) → repositories (Mongo). Order service also has a **Menu client** boundary.
* **Order item snapshot**: order stores `name/price` at creation time so historical orders remain stable even if menu changes.
* **Errors**: consistent `ApiError` responses via `@ControllerAdvice` (400/404/409/500).
* **Messaging**:

  * On `PATCH /orders/{id}/status`, Order service publishes an event to RabbitMQ.
  * Order service consumes the same event and logs a `NOTIFICATION` line (simulation).

Event payload fields:

* `eventType`, `orderId`, `customerId`, `customerName`, `status`, `occurredAt`

## Behavior & edge cases (documented decisions)

### Menu
- **Create Menu Item**: `available` defaults to `true` (server-side default).
- **Validation**: invalid request bodies return `400` with `ApiError` (including malformed JSON).

### Order
- **Menu dependency**: order creation calls Menu service to enrich items and snapshot `name/price`.
  - If Menu is unavailable, Order returns `503` with a clear message.
  - If a `productId` is invalid/not found, Order returns a client error (`400` or `404`, depending on implementation).
- **Status update**: `PATCH /orders/{id}/status`
  - invalid enum values (e.g., `"NOT_A_REAL_STATUS"`) return `400`
  - invalid transitions return `409`
  - unknown order id returns `404`

### Messaging (RabbitMQ)
- On status update, Order publishes a status-change event.
- Order also consumes the event and logs a line containing `NOTIFICATION` (simulation).
- This is a **demo flow** (not production-grade delivery guarantees; see Tradeoffs).

---

## Build / test

From repo root:

```bash
make verify
```

---

## Troubleshooting (fast)

* **Script fails early**: ensure dependencies are installed:
  - `curl`, `jq`
* **See error stacktraces quickly**:
```bash
make logs.order.err
make logs.menu.err
```

* **RabbitMQ UI**: `http://localhost:15672` (guest/guest)
* **Reset everything (drops Mongo volume)**:
```bash
make down
make up
```

---

## Tradeoffs / next steps

If this were going beyond a take-home:

- **Keep the core cleaner**: controllers stay thin; business rules live in explicit use-cases/services; clearer transaction boundaries (and optional DTO↔domain mapping).
- **Make messaging trustworthy**: outbox pattern + idempotency/dedup; retries/backoff + DLQ; resilience around publish/consume.
- **Make it easier to operate**: correlation IDs across HTTP + messages, structured logs, tracing/metrics, readiness/liveness checks.
- **Tighten the API contract**: consistent error format (Problem Details), stable status codes, versioning, and contract tests (including Order↔Menu).
- **Harden security + delivery**: OAuth/JWT, service-to-service hardening, rate limits; Testcontainers + a few integration/e2e checks in CI; basic runbook/dashboards.

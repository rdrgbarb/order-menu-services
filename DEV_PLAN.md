# Take-Home Development Plan (Quick Wins + TDD + Clean SDLC)

Guiding principles:
> - Ship thin vertical slices end-to-end (API → service → DB → tests).
> - TDD where it adds value (unit tests for logic, slice tests for APIs, integration tests for DB/messaging when
    useful).
> - Keep it boring: clean boundaries, clear names, minimal patterns.
> - Always keep a runnable state in `main`.
> - One repo, multi-module Maven, two Spring Boot services.

---

## Repo Structure (Multi-module Maven)

Target structure:

```text
.
├── pom.xml                      # parent (packaging: pom)
├── mvnw / mvnw.cmd / .mvn/
├── DEV_PLAN.md
├── README.md
├── docker-compose.yml
├── curl-examples.sh
├── .editorconfig
├── .gitignore
├── .gitattributes
├── .github/workflows/ci.yml
└── services
    ├── menu
    │   ├── pom.xml
    │   ├── Dockerfile
    │   └── src/main/java/...
    └── order
        ├── pom.xml
        ├── Dockerfile
        └── src/main/java/...
```

### Standard ports

* `menu`: **8081**
* `order`: **8082**
* MongoDB: **27017**
* RabbitMQ: **5672**
* RabbitMQ Management: **15672**

### Swagger URLs (to include in README)

* Menu Swagger UI: `http://localhost:8081/swagger-ui/index.html`
* Order Swagger UI: `http://localhost:8082/swagger-ui/index.html`
* Menu OpenAPI JSON: `http://localhost:8081/v3/api-docs`
* Order OpenAPI JSON: `http://localhost:8082/v3/api-docs`

---

## Milestones (timeboxed)

* **M1 (Foundation):** repo scaffolding + CI + Swagger reachable
* **M2 (Menu done):** Menu CRUD + pagination + tests
* **M3 (Order core done):** Create/Read/List + validation + tests
* **M4 (Messaging done):** status update publishes event + consumer logs notification
* **M5 (Delivery done):** docker-compose + curl script + README

---

## 0) Repository Setup & Baseline (foundation)

### 0.1 Multi-module Maven scaffolding

* [x] Create parent `pom.xml` (packaging `pom`)

  * [x] Define Java version + Spring Boot dependency management
  * [x] Centralize plugins (surefire, failsafe optional, spotless)
  * [x] Declare `<modules>`:

    * [x] `services/menu`
    * [x] `services/order`
* [x] Create `services/menu` Spring Boot app
* [x] Create `services/order` Spring Boot app
* [x] Ensure both services build from root: `./mvnw -q clean verify`

### 0.2 Build & quality baseline

* [x] Add Maven Wrapper (`mvnw`, `.mvn/`)
* [x] Add formatting (Spotless recommended)
* [x] Add `.editorconfig`, `.gitignore`, `.gitattributes`
* [x] Ensure both services start locally with minimal config

### 0.3 CI baseline (fast feedback)

* [x] Add GitHub Actions CI:

  * [x] `./mvnw -q verify`
  * [x] Cache Maven dependencies

### 0.4 Developer ergonomics

* [x] Add `Makefile` or `scripts/`:

  * [x] `make test` → `./mvnw -q verify`
  * [x] `make up` → `docker compose up --build`
  * [x] `make down` → `docker compose down -v`

✅ Deliverable: `./mvnw verify` passes on a clean clone.

---

## 1) Cross-cutting: Observability, Errors, and Swagger

### 1.1 Standard error handling (per service)

* [x] Define consistent error response model:

  * [x] `timestamp`, `status`, `error`, `message`, `path`, optional `details[]`
* [x] Implement `@ControllerAdvice` in `menu`:

  * [x] validation → 400
  * [x] not found → 404
  * [x] conflict/illegal state → 409
  * [x] unexpected → 500
* [x] Implement `@ControllerAdvice` in `order` (same pattern)

### 1.2 Validation at the edge (Add when request DTOs are introduced)

* [ ] Add Bean Validation annotations on request DTOs:

  * [ ] required fields
  * [ ] non-empty lists
  * [ ] positive quantities/prices (where applicable)

### 1.3 Swagger / OpenAPI (simple and good)

* [x] Add Springdoc OpenAPI dependency to both services
* [x] Confirm endpoints:

  * [x] Swagger UI available
  * [x] OpenAPI JSON available
* [ ] Add short descriptions to key endpoints + DTO fields (keep it minimal)

✅ Deliverable: Swagger UI available for both services.

---

## 2) Infrastructure: Docker Compose (keep laptop safe)

### 2.1 Dockerfiles

* [x] Add `Dockerfile` for `menu`
* [x] Add `Dockerfile` for `order`

### 2.2 docker-compose.yml

* [x] Create `docker-compose.yml` with:

  * [x] MongoDB
  * [x] RabbitMQ (+ management UI)
  * [x] `menu`
  * [x] `order`
* [x] Configure environment variables (service ports, Mongo URI, Rabbit URI)
* [x] Keep resource usage low (no extra containers)

### 2.3 Handy commands (for README)

* [x] `docker compose up --build`
* [x] `docker compose logs -f --tail=100 order`
* [x] `docker compose down -v`

✅ Deliverable: `docker compose up` starts all dependencies + apps.

---

## 3) Menu Service (Vertical Slices, TDD-first)

### 3.1 Domain & persistence

* [ ] Define `MenuItem` model:

  * [ ] `id`, `name`, `price`, `available`
* [ ] Create repository (Spring Data Mongo)
* [ ] Create DTOs:

  * [ ] `MenuItemCreateRequest`, `MenuItemUpdateRequest`, `MenuItemResponse`
  * [ ] `PaginatedResponse<T>` with `totalRecords`, `items[]`

### 3.2 Endpoints (thin slices + tests)

> For each endpoint: write test → implement minimal → refactor.

* [ ] POST `/menu-items`

  * [ ] ✅ Test: validation + created item returned
  * [ ] Implement controller/service/repo flow
* [ ] GET `/menu-items/{id}`

  * [ ] ✅ Test: returns 200 when exists, 404 when not
  * [ ] Implement
* [ ] GET `/menu-items?limit=&offset=`
  * [ ] ✅ Test: returns items + `totalRecords`
  * [ ] Implement (pagination)
* [ ] PUT `/menu-items/{id}`

  * [ ] ✅ Test: updates fields, 404 when missing
  * [ ] Implement
* [ ] DELETE `/menu-items/{id}`

  * [ ] ✅ Test: deletes, 404 when missing
  * [ ] Implement

### 3.3 Swagger polish

* [ ] Add concise tags/descriptions to Menu endpoints
* [ ] Confirm DTO schemas look reasonable in Swagger

✅ Deliverable: Menu Service complete and documented.

---

## 4) Order Service (Vertical Slices + external call + rules)

### 4.1 Domain & persistence

* [ ] Define models:

  * [ ] `Customer { id, name }`
  * [ ] `OrderItem { productId, name, price, quantity }`  ← snapshot name/price
  * [ ] `Order { id, customer, items, totalPrice, status, createdAt, updatedAt }`
* [ ] Define `OrderStatus` enum (as per challenge)
* [ ] Repository (Mongo)

### 4.2 HTTP client to Menu Service (separation of concerns)

* [ ] Create `MenuClient` interface
* [ ] Implement with `WebClient` or `RestClient`
* [ ] Add timeout configuration
* [ ] Decide and document behavior:

  * [ ] product not found → return 400 or 404 (pick one and keep consistent)
  * [ ] menu down → return 503 with clear message

### 4.3 Endpoints (thin slices + tests)

* [ ] POST `/orders`

  * [ ] ✅ Unit test: totalPrice calculation and item snapshot
  * [ ] ✅ Slice test: validation errors (empty items, missing customer)
  * [ ] Implement:

    * [ ] resolve products from `menu`
    * [ ] compute totalPrice
* [ ] GET `/orders/{id}`

  * [ ] ✅ Test: 200 when exists / 404 when not
  * [ ] Implement
* [ ] GET `/orders?customerId=&limit=&offset=`

  * [ ] ✅ Test: filter + pagination + `totalRecords`
  * [ ] Implement
* [ ] PATCH `/orders/{id}` (status update)

  * [ ] ✅ Unit test: valid/invalid transitions (if you enforce rules)
  * [ ] ✅ Test: 404 if missing
  * [ ] Implement

### 4.4 Swagger polish

* [ ] Tag + describe endpoints
* [ ] Ensure request/response examples look good

✅ Deliverable: Order Service core complete and documented.

---

## 5) Messaging with RabbitMQ (publish + consume)

### 5.1 Event contract

* [ ] Define event payload DTO:

  * [ ] `eventType`, `orderId`, `customerId`, `customerName`, `status`, `occurredAt`

### 5.2 Publisher on status change

* [ ] Publish event when PATCH updates status
* [ ] ✅ Test (choose one):

  * [ ] Unit test verifying publisher called
  * [ ] OR integration test with Rabbit container (if time)

### 5.3 Consumer: “notification simulation”

* [ ] Consumer listens to queue
* [ ] Logs notification including customer + status
* [ ] ✅ Test: consumer handles payload (unit test OK)

✅ Deliverable: status update triggers publish; consumer logs notification.

---

## 6) End-to-End Validation (compose + curls)

### 6.1 curl-examples.sh

* [ ] Create root `curl-examples.sh`
* [ ] Include:

  * [ ] create menu items
  * [ ] list menu items
  * [ ] create order using menu productIds
  * [ ] update order status
  * [ ] get order by id
  * [ ] get order history
  * [ ] show how to see notification logs

### 6.2 Smoke run from scratch

* [ ] `docker compose down -v`
* [ ] `docker compose up --build`
* [ ] Run `./curl-examples.sh`
* [ ] Confirm:

  * [ ] Swagger UIs reachable
  * [ ] Order creation works with menu dependency
  * [ ] Status update publishes event
  * [ ] Consumer logs notification

✅ Deliverable: One-command boot + scripted demo works.

---

## 7) README (final delivery)

### 7.1 README contents (simple and complete)

* [ ] Overview
* [ ] Tech stack
* [ ] How to run (compose)

  * [ ] prerequisites
  * [ ] ports list
* [ ] Swagger links (both services)
* [ ] Curl examples (how to run)
* [ ] Architecture notes:

  * [ ] separation of concerns (controllers/services/repositories/clients)
  * [ ] order snapshots name/price
  * [ ] error handling approach
  * [ ] event contract and notification simulation
* [ ] Testing:

  * [ ] how to run tests (`./mvnw verify`)
  * [ ] what is covered (unit/slice/integration)
* [ ] Tradeoffs + next steps:

  * [ ] retries/backoff, idempotency, outbox pattern, auth, observability

✅ Deliverable: reviewer can run + test + understand decisions quickly.

---

# Conventional Commits Guide (use this every commit)

## Scopes (standardize these)

* `init`, `menu`, `order`, `rabbit`, `docker`, `docs`, `ci`, `build`

## Types (most common)

* `feat`: new feature
* `fix`: bug fix
* `test`: tests only
* `refactor`: refactoring without behavior change
* `chore`: tooling/config
* `docs`: documentation
* `ci`: CI changes
* `build`: build system/dependencies

## Format

* `<type>(scope): <subject>`

## Examples (copy/paste)

* `chore(init): bootstrap maven multi-module structure`
* `chore(ci): add github actions build pipeline`
* `feat(menu): implement create and get menu items`
* `test(menu): add controller validation tests for menu endpoints`
* `feat(order): create order with menu enrichment`
* `feat(order): add order history endpoint with pagination`
* `feat(rabbit): publish order status change events`
* `feat(rabbit): consume status events and log notifications`
* `docs(readme): add run instructions, swagger links, and curl examples`
* `chore(docker): add compose stack for mongo and rabbitmq`
* `refactor(order): extract menu client boundary and improve layering`

## Rules of thumb

* One commit = one intention
* Keep commits small and reviewable
* Avoid mixing formatting with logic changes

---

# Daily Execution Checklist (quick wins)

* [ ] Start with a small slice + a test
* [ ] Keep endpoints runnable continuously
* [ ] Commit early and often using Conventional Commits
* [ ] Keep compose working; avoid large broken periods
* [ ] After each milestone: run `./mvnw verify`

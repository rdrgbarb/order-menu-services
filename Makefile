SHELL := /usr/bin/env bash
.DEFAULT_GOAL := help

MVNW := ./mvnw

# Modules (paths as in the multi-module root pom.xml)
MENU_MODULE  := services/menu
ORDER_MODULE := services/order

.PHONY: help
help: ## Show available commands
	@grep -E '^[a-zA-Z0-9_.-]+:.*## ' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*## "}; {printf "\033[36m%-22s\033[0m %s\n", $$1, $$2}'

# ---------------------------
# Maven / Quality gate
# ---------------------------

.PHONY: verify
verify: ## Run quality gate (spotless-check + tests) for the whole project
	$(MVNW) verify

.PHONY: test
test: ## Run tests for the whole project
	$(MVNW) test

.PHONY: test.menu
test.menu: ## Run tests for menu module only
	$(MVNW) -pl $(MENU_MODULE) test

.PHONY: test.order
test.order: ## Run tests for order module only
	$(MVNW) -pl $(ORDER_MODULE) test

# Spotless
.PHONY: spotless.check
spotless.check: ## Check formatting (whole project)
	$(MVNW) spotless:check

.PHONY: spotless.apply
spotless.apply: ## Fix formatting (whole project)
	$(MVNW) spotless:apply

.PHONY: spotless.check.menu
spotless.check.menu: ## Check formatting (menu only)
	$(MVNW) -pl $(MENU_MODULE) spotless:check

.PHONY: spotless.apply.menu
spotless.apply.menu: ## Fix formatting (menu only)
	$(MVNW) -pl $(MENU_MODULE) spotless:apply

.PHONY: spotless.check.order
spotless.check.order: ## Check formatting (order only)
	$(MVNW) -pl $(ORDER_MODULE) spotless:check

.PHONY: spotless.apply.order
spotless.apply.order: ## Fix formatting (order only)
	$(MVNW) -pl $(ORDER_MODULE) spotless:apply

# ---------------------------
# Docker compose
# ---------------------------

.PHONY: up
up: ## docker compose up --build
	docker compose up --build

.PHONY: down
down: ## docker compose down -v
	docker compose down -v

.PHONY: logs
logs: ## Tail logs from compose
	docker compose logs -f

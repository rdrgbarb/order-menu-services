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

# Usage examples:
#   make logs                          # all services (follow)
#   make logs SERVICE=menu             # only menu service (follow)
#   make logs SERVICE=menu FILTER=ERROR
#   make logs SERVICE=menu FILTER="ERROR|Exception|Caused by"
#   make logs SERVICE=menu SINCE=2m TAIL=500
#   make logs SERVICE=menu INSTANCE=2  # container instance menu-2 (scaled)
#   make logs.menu                     # shortcut for SERVICE=menu
#   make logs.order                    # shortcut for SERVICE=order
#   make logs.mongo                    # shortcut for SERVICE=mongo
#   make logs.rabbitmq                 # shortcut for SERVICE=rabbitmq

TAIL  ?= 200
SINCE ?=
FILTER ?=
SERVICE ?=
INSTANCE ?=
FOLLOW ?= 1

# Build docker compose args for logs
# - if SERVICE is empty => logs all services
# - if INSTANCE is set  => logs container name like menu-2 (works with scaled services)
define _compose_logs_args
logs $(if $(FOLLOW),-f,) --tail=$(TAIL) \
$(if $(SINCE),--since=$(SINCE),) \
$(if $(INSTANCE),$(SERVICE)-$(INSTANCE),$(SERVICE))
endef

.PHONY: up
up: ## docker compose up --build
	docker compose up --build

.PHONY: down
down: ## docker compose down -v
	docker compose down -v

.PHONY: ps
ps: ## Show compose containers
	docker compose ps

.PHONY: logs
logs: ## Tail logs (SERVICE=menu|order|mongo|rabbitmq, INSTANCE=2, FILTER="ERROR|Exception", SINCE=2m, TAIL=500)
	@set -euo pipefail; \
	cmd="docker compose $(call _compose_logs_args)"; \
	if [[ -n "$(FILTER)" ]]; then \
	  echo "$$cmd | grep -E --line-buffered '$(FILTER)'"; \
	  eval "$$cmd" | grep -E --line-buffered '$(FILTER)'; \
	else \
	  echo "$$cmd"; \
	  eval "$$cmd"; \
	fi

# Handy shortcuts
.PHONY: logs.menu logs.order logs.mongo logs.rabbitmq
logs.menu: ## Tail menu logs (same flags as logs)
	@$(MAKE) logs SERVICE=menu
logs.order: ## Tail order logs (same flags as logs)
	@$(MAKE) logs SERVICE=order
logs.mongo: ## Tail mongo logs (same flags as logs)
	@$(MAKE) logs SERVICE=mongo
logs.rabbitmq: ## Tail rabbitmq logs (same flags as logs)
	@$(MAKE) logs SERVICE=rabbitmq

# Focused filters you’ll use a lot for 500s
.PHONY: logs.err logs.menu.err logs.order.err
logs.err: ## Tail logs filtered to errors/stacktraces (set SERVICE=... optionally)
	@$(MAKE) logs SERVICE=$(SERVICE) FILTER="ERROR|WARN|Exception|Caused by|Stacktrace|org\.springframework|/menu-items|/orders"
logs.menu.err: ## Tail menu errors/stacktraces
	@$(MAKE) logs.err SERVICE=menu
logs.order.err: ## Tail order errors/stacktraces
	@$(MAKE) logs.err SERVICE=order

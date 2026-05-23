SHELL := /bin/bash

COMPOSE := docker compose -f docker-compose.yml
FRONTEND_DIR := ../FamilyBlock-Frontend
BACKEND_DIR := .
AGENT_DIR := ../FamilyBlock-Agent

.PHONY: help setup-env up up-build down restart ps logs logs-backend logs-frontend logs-db clean reset-db frontend-install frontend-lint frontend-build backend-package backend-test agent-setup-env verify

help:
	@echo "Family Block common commands"
	@echo ""
	@echo "Docker:"
	@echo "  make setup-env      Create local .env files if missing"
	@echo "  make agent-setup-env Create agent .env if missing"
	@echo "  make up             Start all Docker services"
	@echo "  make up-build       Build/pull and start all Docker services"
	@echo "  make down           Stop Docker services"
	@echo "  make restart        Restart Docker services"
	@echo "  make ps             Show Docker service status"
	@echo "  make logs           Follow all service logs"
	@echo "  make logs-backend   Follow backend logs"
	@echo "  make logs-frontend  Follow frontend logs"
	@echo "  make logs-db        Follow Postgres logs"
	@echo "  make clean          Stop services and remove anonymous containers"
	@echo "  make reset-db       Stop services and delete Postgres data volume"
	@echo ""
	@echo "Verification:"
	@echo "  make frontend-lint"
	@echo "  make frontend-build"
	@echo "  make backend-package"
	@echo "  make backend-test"
	@echo "  make verify         Run focused frontend build/API lint and backend package"

setup-env:
	@test -f "$(FRONTEND_DIR)/.env" || cp "$(FRONTEND_DIR)/.env.example" "$(FRONTEND_DIR)/.env"
	@test -f "$(BACKEND_DIR)/.env" || cp "$(BACKEND_DIR)/.env.example" "$(BACKEND_DIR)/.env"
	@if [ -d "$(AGENT_DIR)" ]; then $(MAKE) -C "$(AGENT_DIR)" setup-env; fi
	@echo "Local env files are present. Fill Firebase/API/device secrets before starting services."

agent-setup-env:
	@$(MAKE) -C "$(AGENT_DIR)" setup-env

up: setup-env
	$(COMPOSE) up

up-build: setup-env
	$(COMPOSE) up --build

down:
	$(COMPOSE) down

restart: down up

ps:
	$(COMPOSE) ps

logs:
	$(COMPOSE) logs -f

logs-backend:
	$(COMPOSE) logs -f backend

logs-frontend:
	$(COMPOSE) logs -f frontend

logs-db:
	$(COMPOSE) logs -f postgres

clean:
	$(COMPOSE) down --remove-orphans

reset-db:
	$(COMPOSE) down --volumes --remove-orphans

frontend-install:
	cd "$(FRONTEND_DIR)" && npm install

frontend-lint:
	cd "$(FRONTEND_DIR)" && npm run lint

frontend-build:
	cd "$(FRONTEND_DIR)" && npm run build

backend-package:
	bash ./mvnw -DskipTests package

backend-test:
	bash ./mvnw test

verify:
	cd "$(FRONTEND_DIR)" && npm run build && npx eslint src/api
	bash ./mvnw -DskipTests package

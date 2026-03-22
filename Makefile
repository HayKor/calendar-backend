.PHONY: dev-compose
dev-compose:
	docker compose -p calendar_api -f deployment/docker-compose.local.yml up -d --build --remove-orphans

.PHONY: dev-destroy
dev-destroy:
	docker compose -p calendar_api -f deployment/docker-compose.local.yml down -v --remove-orphans

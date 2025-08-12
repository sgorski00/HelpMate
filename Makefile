up:
	docker compose up -d

down:
	docker compose down

logs:
	docker compose logs -f

restart:
	docker compose restart

restart-app:
	docker compose down user-service
	docker compose up --build -d user-service

stop:
	docker stop $(docker ps -aq)

psql-keycloak:
	docker compose exec -it postgres-keycloak psql -U admin -d keycloak

backup-keycloak:
	docker compose exec -T keycloak /opt/keycloak/bin/kc.sh export --realm=helpmate --file=/opt/keycloak/data/import/helpmate.json --users=same_file --optimized

psql-user:
	docker compose exec -it postgres-user psql -U admin -d hm_user_db


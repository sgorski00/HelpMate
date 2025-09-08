up:
	mvn clean package -DskipTests
	docker compose up --build -d

down:
	docker compose down

logs:
	docker compose logs -f

restart:
	docker compose restart

restart-app:
	docker compose down user-service ticket-service notification-service comment-service
	mvn clean package -DskipTests
	docker compose up --build -d user-service ticket-service notification-service comment-service

restart-ticket:
	docker compose down ticket-service
	mvn clean package -pl ticket-service -am -DskipTests
	docker compose up --build -d ticket-service

restart-user:
	docker compose down user-service
	mvn clean package -pl user-service -am -DskipTests
	docker compose up --build -d user-service

restart-notification:
	docker compose down notification-service
	mvn clean package -pl notification-service -am -DskipTests
	docker compose up --build -d notification-service

restart-comment:
	docker compose down comment-service
	mvn clean package -pl comment-service -am -DskipTests
	docker compose up --build -d comment-service

stop:
	docker stop $(docker ps -aq)

psql-keycloak:
	docker compose exec -it postgres-keycloak psql -U admin -d keycloak

backup-keycloak:
	docker compose exec -T keycloak /opt/keycloak/bin/kc.sh export --realm=helpmate --file=/opt/keycloak/data/import/helpmate.json --users=same_file --optimized

psql-user:
	docker compose exec -it postgres-user psql -U admin -d hm_user_db

psql-ticket:
	docker compose exec -it postgres-ticket psql -U admin -d hm_ticket_db

psql-notification:
	docker compose exec -it postgres-notification psql -U admin -d hm_notification_db

psql-comment:
	docker compose exec -it postgres-comment psql -U admin -d hm_comment_db

rabbitmq-reset:
	docker compose exec -it rabbitmq rabbitmqctl stop_app
	docker compose exec -it rabbitmq rabbitmqctl reset
	docker compose exec -it rabbitmq rabbitmqctl start_app


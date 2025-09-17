# HelpMate

[![codecov](https://codecov.io/gh/sgorski00/HelpMate/graph/badge.svg?token=0D8GWT7VOE)](https://codecov.io/gh/sgorski00/HelpMate)

**HelpMate** is a IT Helpdesk web application built using a microservice architecture with Java, Spring Boot and RabbitMQ.

---

## Features

### Microservice Architecture
- Each domain is encapsulated as an independent Spring Boot microservice.

### Technology Stack
- **Java 21:** Modern language features for performance and readability.
- **Spring Boot:** Rapid microservice development, leveraging Spring Security, Spring Data JPA, and reactive programming where applicable.
- **Reactive Programming:** Notification service uses `ReactiveCrudRepository` for responsiveness and scalability.
- **Docker:** Each microservice is containerized with dedicated Dockerfiles (see `docker-files/`).

### Security
- **Keycloak Integration:** Centralized authentication and authorization utilizing JWTs for secure resource access.
- **Roles:** Security enforced at endpoint level, with role-based access controls (see `SecurityConfig.java` classes).
- **Stateless Sessions:** Uses JWT and stateless security.

### Database
- **PostgreSQL:** Dedicated databases per service.
- **Migrations:** Every service contains db migrations via Flyway. 

### Inter-Service Communication
- **REST API:** Clean APIs for each service.
- **WebClient:** Secure HTTP communication between services.
- **RabbitMQ:** Messaging integration for asynchronous events.

### Testing
- **Unit Tests:** Comprehensive test suites using JUnit and Mockito.
- **Code Coverage:** Integrated with Codecov for code coverage reports.

---

> This repository is inteded as a demo. Keycloak secrets are stored in the keycloak import file so be aware of that.

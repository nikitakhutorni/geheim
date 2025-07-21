# 🤫 Geheim (WIP)

Geheim ("secret", "confidential") is an end-to-end encrypted one-time secret sharing service.

*WIP*: Geheim is a work in progress and not usable yet.

## Stack
- Runtime: Java 24, Spring Boot 3.5.3, Spring Data JDBC, Flyway
- DB: PostgreSQL 16 in Docker
- Tests: JUnit 5, AssertJ, Testcontainers
- Build: Maven 3.9.10
- CI: GitHub Actions

## Development
Ensure Docker is installed.

Most actions are available via `make`, see `Makefile` for details.

### Run dev environment
To run the Postgres in Docker and run the Spring Boot application in dev profile, use:
```bash
make dev
```

To only start the development DB, use:
```bash
make up-db
```

### Run tests
To run all tests, including unit and integration tests, use:
```bash
make test
```
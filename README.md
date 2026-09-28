# 🏙️ Urban Alerts — Spring Boot API

> Backend of **Urban Alerts**, a civic incident reporting & management platform (ENIAD Berkane end-of-year project). Citizens report urban problems, agents intervene, supervisors track performance.

[![CI/CD](https://github.com/ahmedaminefaiz/springboot-api/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/ahmedaminefaiz/springboot-api/actions/workflows/ci-cd.yml)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)

🌐 **Live:** [urbain-alerts.crafters.dev](https://urbain-alerts.crafters.dev)

Related repos: [angular-frontend](https://github.com/ahmedaminefaiz/angular-frontend) · [python-FastApi](https://github.com/ahmedaminefaiz/python-FastApi) (AI similarity service)

---

## ✨ Features

- 🔐 JWT authentication & role-based access: **citizen, agent, super-agent, admin**
- 📍 Problem reports with photos, geolocation and configurable problem types
- 🛠️ **Interventions** workflow — agents post updates (mandatory report, status, photos, computed durations); closing (`CLOTUREE`) reserved to super-agents
- 🔔 Real-time notifications (WebSocket / STOMP)
- 📊 KPIs for supervisors
- 🤖 Calls the **CLIP similarity microservice** to flag duplicate reports
- 🗃️ Schema versioned with **Flyway**; DTO mapping with **MapStruct**

## 🏗️ Architecture

```mermaid
flowchart LR
    W[Angular web] --> N[Nginx]
    M[Flutter mobile] --> API
    N -- /api --> API[Spring Boot API]
    API --> DB[(PostgreSQL)]
    API -- HTTP --> AI[FastAPI + CLIP<br/>similarity service]
```

Packages: `controller` · `service` · `repository` · `entity` · `dto` · `config` · `filter` · `exception`

## ⚙️ CI/CD pipeline (GitHub Actions)

```mermaid
flowchart LR
    A[Push on main] --> B[mvn test]
    B --> C[mvn package]
    C --> D[Docker build & push<br/>Docker Hub]
    D --> E[Deploy DEV]
    D --> F[Deploy PROD]
```

- **CI** — Java 21, run the test suite, build the JAR
- **CD** — build & push the Docker image, then trigger **DEV** and **PROD** deployments (Dokploy)
- Immutable **commit-SHA image tags** to avoid race conditions between deployments
- Optimised Dockerfile: JRE runtime, non-root user, container-aware JVM flags, layer caching

## ▶️ Run locally

```bash
# PostgreSQL
docker run -d --name ua-pg -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:16

./mvnw spring-boot:run
./mvnw test
```

---

👤 **Ahmed Amine Faiz** · [GitHub](https://github.com/ahmedaminefaiz)

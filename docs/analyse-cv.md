# Analyse du projet Urban Alert — Spring Boot API

## 1. Est-ce un projet production-level ?

**Oui, avec des réserves.** Voici la distinction honnête :

### Ce qui est production-ready

| Aspect | Ce que tu as fait | Pourquoi c'est prod-level |
|---|---|---|
| **Architecture** | Controller → Service (interface + impl) → Repository → Entity | Séparation des responsabilités stricte, testable, maintenable |
| **DTOs** | Request/Response DTOs distincts par feature, jamais l'entité exposée directement | Évite l'over-fetching et protège le schéma interne |
| **MapStruct** | Mappers générés à la compilation, zéro reflection runtime | Plus rapide et plus sûr que ModelMapper |
| **Flyway** | 15 migrations versionnées, baseline-on-migrate en prod, out-of-order activé | Gestion de schéma DB reproductible et traçable |
| **JWT stateless** | OncePerRequestFilter, SecurityContext, sessions stateless | Pattern correct pour API REST scalable |
| **Validation** | Jakarta Validation sur les DTOs (regex marocain, @NotBlank, @Size...) | Validation à la frontière système, pas dans le service |
| **Profils Spring** | dev / prod séparés, credentials via env vars | Pas de secret hardcodé en prod |
| **Docker multi-stage** | Build Maven isolé, image runtime légère (JRE seulement) | Image prod optimisée, surface d'attaque réduite |
| **CI/CD branché** | Tests → Build → Push Docker → Webhook Dokploy | Pipeline complet : rien ne part en prod sans passer les tests |
| **RBAC** | 4 rôles (CITOYEN, AGENT, SUPER_AGENT, ADMIN), endpoints sécurisés par rôle | Contrôle d'accès métier réel |
| **Audit trail** | ProblemStatusHistory, timestamps auto sur alerts (trigger SQL) | Traçabilité obligatoire en production |
| **OTP WhatsApp** | Intégration Meta Graph API v25.0, codes expirables (15 min), invalidation des anciens codes | Auth à deux facteurs sur un vrai canal |

### Ce qui manque pour être 100% prod-level

| Manque | Impact |
|---|---|
| Pas de `@ControllerAdvice` global | Chaque controller a ses try/catch → duplication, incohérence des réponses d'erreur |
| Pas de rate limiting | Endpoint `/auth/login` exposé aux brute-force attacks |
| Pas de refresh token | JWT expire en 24h et l'user doit se reconnecter |
| Tests limités (H2 en mémoire) | Les tests n'utilisent pas PostgreSQL donc les migrations SQL ne sont pas testées |
| Logs non structurés | Pas de logback structuré (JSON) configuré proprement |
| Pas de health checks custom | `/actuator/health` non configuré pour Dokploy |

---

## 2. Flux JWT — Registration, Login & Auth

### A. Registration (`POST /api/v1/auth/register`)

```
Client ──► AuthController.register(SignupRequestDTO)
              │
              ▼
         AuthServiceImpl.register()
              │
              ├── 1. Vérifie que le phone n'existe pas (UserRepository.existsByPhone)
              │       └── Si oui → throw PhoneAlreadyExistsException (409)
              │
              ├── 2. Hash le password avec BCryptPasswordEncoder
              │
              ├── 3. Crée User avec status = PENDING_PHONE_VERIFICATION
              │       └── UserRepository.save(user)
              │
              └── 4. Génère OTP via OtpService
                       ├── Code 6 chiffres aléatoire
                       ├── Sauvegarde dans phone_verifications (expires dans 15 min)
                       ├── Invalide les anciens codes du même phone
                       └── WhatsAppService.sendOtp(phone, code)
                                └── Meta Graph API v25.0 (WhatsApp Business)
```

### B. Vérification OTP (`POST /api/v1/auth/verify-phone`)

```
Client ──► AuthController.verifyPhone(phone, code)
              │
              ▼
         AuthServiceImpl.verifyPhone()
              │
              ├── 1. Cherche le code non-expiré et non-utilisé
              │       (PhoneVerificationRepository.findTopByPhoneAndUsedFalse)
              │
              ├── 2. Vérifie expiration (expires_at > now())
              │
              ├── 3. Marque le code comme used = true
              │
              ├── 4. Met à jour le statut selon le rôle :
              │       ├── CITOYEN → ACTIVE (accès immédiat)
              │       └── AGENT/SUPER_AGENT → PENDING_APPROVAL (attente admin)
              │
              └── 5. Si CITOYEN → génère JWT et retourne LoginResponseDTO
```

### C. Login (`POST /api/v1/auth/login`)

```
Client ──► AuthController.login(LoginRequestDTO{phone, password})
              │
              ▼
         Spring AuthenticationManager.authenticate()
              │
              ├── Appelle AuthServiceImpl.loadUserByUsername(phone)
              │       └── UserRepository.findByPhone(phone) → UserDetails
              │
              ├── BCrypt compare password ──► OK ou BadCredentialsException
              │
              └── Si OK ──► AuthServiceImpl.login(phone)
                               │
                               ├── Vérifie status == ACTIVE (sinon erreur 403)
                               │
                               └── JwtServiceImpl.generateToken(user)
                                        │
                                        ├── subject = phone
                                        ├── claims = {userId, role}
                                        ├── expiration = now + 86400000ms (24h)
                                        ├── Signe avec HMAC-SHA256 + clé secrète (env var JWT_SECRET)
                                        └── Retourne LoginResponseDTO{token, role, status}
```

### D. Authentification de chaque requête protégée

```
Client ──► Request (Authorization: Bearer eyJhbGci...)
              │
              ▼
         JwtAuthenticationFilter (OncePerRequestFilter)
              │
              ├── 1. Extrait le token du header Authorization
              │
              ├── 2. JwtService.extractPhone(token) → phone (sujet du JWT)
              │
              ├── 3. Charge UserDetails via AuthService.loadUserByUsername(phone)
              │
              ├── 4. JwtService.isTokenValid(token, userDetails)
              │       ├── Vérifie signature (HMAC-SHA256)
              │       └── Vérifie expiration
              │
              ├── 5. Si valide → crée UsernamePasswordAuthenticationToken
              │           et l'injecte dans SecurityContextHolder
              │
              └── 6. Continue la chaîne de filtres → Controller
```

---

## 3. CI/CD Pipeline — Dokploy & Docker

### Architecture complète

```
Developer git push
       │
       ▼
  GitHub Actions (.github/workflows/ci-cd.yml)
       │
       ├── JOB 1: CI
       │       ├── checkout code
       │       ├── setup Java 21 (Temurin) + cache Maven ~/.m2
       │       ├── mvn clean test (H2 in-memory)
       │       └── mvn clean package -DskipTests (produit le JAR)
       │
       └── JOB 2: Deploy (needs: ci, only if ci passed)
               │
               ├── docker login (DOCKER_USERNAME + DOCKER_PASSWORD secrets)
               │
               ├── Si branche == main :
               │       ├── docker build -t <user>/spring-app:prod .
               │       ├── docker push <user>/spring-app:prod
               │       └── curl DEPLOY_WEBHOOK_PROD (Dokploy webhook)
               │
               └── Si branche == develop :
                       ├── docker build -t <user>/spring-app:dev .
                       ├── docker push <user>/spring-app:dev
                       └── curl DEPLOY_WEBHOOK_DEV (Dokploy webhook)
```

### Comment Dokploy reçoit le déploiement

Dokploy est un PaaS self-hosted (comme Coolify). Il expose un **webhook URL**. Quand GitHub Actions appelle ce webhook après le push Docker :

1. Dokploy détecte la nouvelle image `:prod` sur Docker Hub
2. Pull l'image
3. `docker stop` l'ancien conteneur
4. `docker run` le nouveau (avec les env vars configurées dans Dokploy)
5. Vérifie que le port 8080 répond (health check)

### Dockerfile multi-stage

```dockerfile
# Stage 1: BUILD (Maven + JDK 21)
FROM maven:3.9.6-eclipse-temurin-21 AS builder
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests   # → produit target/*.jar

# Stage 2: RUNTIME (JRE léger seulement)
FROM eclipse-temurin:21-jdk
COPY --from=builder target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Pourquoi multi-stage ?** L'image finale ne contient pas Maven ni les sources, seulement le JAR. Plus légère, surface d'attaque réduite.

### Gestion des secrets (GitHub Secrets)

| Secret | Usage |
|---|---|
| `DOCKER_USERNAME` | Login Docker Hub |
| `DOCKER_PASSWORD` | Login Docker Hub |
| `DEPLOY_WEBHOOK_DEV` | URL webhook Dokploy env dev |
| `DEPLOY_WEBHOOK_PROD` | URL webhook Dokploy env prod |

En production dans Dokploy, les variables d'environnement `PGHOST`, `PGPASSWORD`, `JWT_SECRET`, `WHATSAPP_ACCESS_TOKEN` etc. sont injectées dans le conteneur — jamais dans le code.

---

## 4. Bullet Points CV

### Backend / Spring Boot

- Designed and implemented a **stateless JWT authentication system** using Spring Security with `OncePerRequestFilter`, BCrypt password hashing, and HMAC-SHA256 token signing — supporting role-based access control (RBAC) across 4 user roles
- Built a **multi-step user onboarding flow**: phone registration → OTP verification via WhatsApp Business API (Meta Graph API v25.0) → role-based account activation (immediate or pending admin approval)
- Applied a clean **layered architecture** (Controller / Service interface + impl / Repository / DTO / MapStruct mapper) with strict separation between API contracts and database models
- Managed **15 incremental database schema migrations** using Flyway with versioned SQL scripts, environment-aware settings (`baseline-on-migrate`, `out-of-order`), and PostgreSQL triggers for auto-updating timestamps
- Implemented **domain-driven exception handling** with custom exception classes (`PhoneAlreadyExistsException`, `ProblemCannotBeModifiedException`, etc.) and structured JSON error responses
- Designed a **geo-located urban alert system** with hierarchical problem tracking: citizens report alerts (lat/lng) → super-agents aggregate into problems → agents resolve with full status history audit trail

### DevOps / CI-CD

- Configured a **full CI/CD pipeline with GitHub Actions**: automated test execution (`mvn clean test`), Docker image build, push to Docker Hub, and environment-specific deployment (dev/prod) triggered via Dokploy webhooks — zero manual deployment steps
- Built **multi-stage Docker images** (Maven build stage + slim JRE runtime stage) to minimize production image size and reduce attack surface
- Managed **environment separation** (dev/prod Spring profiles) with all secrets injected at runtime via environment variables — no credentials in source code or Docker images
- Implemented **branch-based deployment strategy**: `develop` branch deploys to dev environment, `main` branch deploys to production — with Docker image tagging (`:dev` / `:prod`) and separate Dokploy webhook triggers

### Stack technique

`Java 21` · `Spring Boot 4` · `Spring Security` · `JWT (jjwt 0.12)` · `PostgreSQL` · `Flyway` · `MapStruct` · `Docker` · `GitHub Actions` · `Dokploy` · `WhatsApp Business API` · `OpenAPI/Swagger` · `Lombok`

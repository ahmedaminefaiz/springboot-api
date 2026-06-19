# API Problems — ProblemController

**Projet:** Urban Alert — Spring Boot API  
**Base URL:** `http://localhost:8080/api/v1/problems`  
**Authentification:** JWT Bearer (header `Authorization: Bearer <token>`) — tous les endpoints sauf auth/swagger sont protégés.

**Statuts problème (`ProblemStatusEnum`):** `NEW`, `IN_PROGRESS`, `RESOLVED`, `REJECTED`

---

## Format des erreurs

```json
{
  "error": "Titre court",
  "message": "Détail",
  "timestamp": 1717000000000
}
```

---

## 1. CRUD

### POST `/v1/problems` — Créer un problème

**Rôle:** SUPER_AGENT uniquement (utilisateur connecté = créateur).

**Body (JSON) — champs requis:**

| Champ | Type | Requis | Description |
|-------|------|--------|-------------|
| `assignedToId` | Long | Oui | ID d'un utilisateur avec rôle AGENT |
| `alertIds` | List&lt;Long&gt; | Oui | Au moins une alerte (liste non vide) |

**Exemple:**
```json
{
  "assignedToId": 5,
  "alertIds": [12, 15]
}
```

**Succès:** `201` + `ProblemResponseDTO` (statut initial `NEW`, alertes passent en `IN_PROGRESS`).

**Exceptions gérées:**

| HTTP | Exception | Message typique |
|------|-----------|-----------------|
| 403 | NotSuperAgentException | Seuls les Super Agents peuvent créer… |
| 400 | NotAgentException | L'utilisateur … n'est pas un Agent |
| 404 | UserNotFoundException | Utilisateur / agent introuvable |
| 400 | NoAlertsAssignedException | Au moins une alerte doit être assignée |
| 400 | Validation `@Valid` | Champs `@NotNull` manquants |
| 500 | Exception | Erreur serveur |

---

### GET `/v1/problems/{id}` — Détail

**Paramètres:** `id` (path)

**Body:** aucun

**Succès:** `200` + `ProblemResponseDTO`

**Exceptions:** `404` ProblemNotFoundException

---

### GET `/v1/problems` — Liste paginée

**Query:** `page` (défaut 0), `size` (défaut 10)

**Succès:** `200` + Page de `ProblemResponseDTO`

**Exceptions:** `500` erreur générique

---

### GET `/v1/problems/user/my-problems` — Mes problèmes créés

**Query:** `page`, `size`

**Auth:** utilisateur connecté = SUPER_AGENT créateur

**Succès:** `200`

**Exceptions:** `404` UserNotFoundException

---

### GET `/v1/problems/assigned-to-me` — Problèmes assignés à moi

**Query:** `page`, `size`

**Auth:** AGENT assigné

**Succès:** `200`

**Exceptions:** `404` UserNotFoundException

---

### PUT `/v1/problems/{id}` — Mettre à jour

**Permissions:** SUPER_AGENT créateur **ou** AGENT assigné.

**Body — tous optionnels** (au moins un champ utile recommandé):

| Champ | Type | Requis | Description |
|-------|------|--------|-------------|
| `assignedToId` | Long | Non | Nouvel agent (doit être AGENT) |
| `addAlertIds` | List&lt;Long&gt; | Non | Alertes à ajouter |
| `removeAlertIds` | List&lt;Long&gt; | Non | Alertes à retirer (repassent en NEW) |

**Exemple:**
```json
{
  "assignedToId": 7,
  "addAlertIds": [20],
  "removeAlertIds": [12]
}
```

**Règles métier:** impossible si statut `RESOLVED` ou `REJECTED`.

**Succès:** `200` + `ProblemResponseDTO`

**Exceptions gérées dans le controller:**

| HTTP | Exception |
|------|-----------|
| 404 | ProblemNotFoundException |
| 409 | ProblemCannotBeModifiedException |
| 400 | InvalidAssignmentException, NotAgentException |
| 403 | InvalidProblemException (pas le droit de modifier) |

**Non capturées (→ 500 possible):** UserNotFoundException, AlertNotFoundException, AlertAlreadyAssignedException lors de add/remove alertes.

---

### DELETE `/v1/problems/{id}` — Supprimer

**Permissions:** SUPER_AGENT **créateur** uniquement.

**Body:** aucun

**Règles métier:**
- Refusé si statut `RESOLVED` ou `REJECTED`
- Avant suppression : détache toutes les alertes (`problem_id = null`)
- Si statut problème `NEW` ou `IN_PROGRESS` : alertes repassent en `NEW`

**Succès:** `204` No Content

**Exceptions:**

| HTTP | Exception |
|------|-----------|
| 404 | ProblemNotFoundException |
| 409 | ProblemCannotBeModifiedException |
| 403 | InvalidProblemException (pas le créateur) |

---

## 2. Gestion des alertes

### POST `/v1/problems/{problemId}/alerts/{alertId}`

**Body:** aucun

**Effet:** alerte liée au problème, statut alerte → `IN_PROGRESS`

**Succès:** `201`

| HTTP | Exception |
|------|-----------|
| 404 | ProblemNotFoundException, AlertNotFoundException |
| 409 | AlertAlreadyAssignedException |

---

### DELETE `/v1/problems/{problemId}/alerts/{alertId}`

**Body:** aucun

**Effet:** détache l'alerte, statut alerte → `NEW`

**Succès:** `200`

| HTTP | Exception |
|------|-----------|
| 404 | ProblemNotFoundException, AlertNotFoundException |

---

## 3. Statut

### PATCH `/v1/problems/{id}/status`

**Permissions:** SUPER_AGENT créateur **ou** AGENT assigné.

**Body requis:**

| Champ | Type | Requis |
|-------|------|--------|
| `newStatus` | ProblemStatusEnum | Oui |
| `comment` | String | Non |

**Exemple:**
```json
{
  "newStatus": "IN_PROGRESS",
  "comment": "Prise en charge par l'équipe"
}
```

**Effets:**
- `RESOLVED` → `resolvedAt` renseigné, toutes les alertes → `RESOLVED`
- `REJECTED` → toutes les alertes → `REJECTED`
- Historique enregistré dans `problem_status_history`

**Succès:** `200`

| HTTP | Exception |
|------|-----------|
| 404 | ProblemNotFoundException |
| 403 | InvalidProblemException |

---

### GET `/v1/problems/{id}/status`

**Succès:** `200` — `{ "problemId": 1, "status": "NEW" }`

**Exceptions:** `404` ProblemNotFoundException

---

### GET `/v1/problems/{id}/status-history`

**Query:** `page`, `size`

**Succès:** `200` + page de `ProblemStatusHistoryDTO`

**Exceptions:** `404` ProblemNotFoundException

---

## 4. Filtres & statistiques

| Méthode | URL | Paramètres |
|---------|-----|------------|
| GET | `/status/{status}` | path: NEW, IN_PROGRESS, RESOLVED, REJECTED ; query: page, size |
| GET | `/stats/count-by-status` | query: `status` (requis) |
| GET | `/stats/my-count` | — (utilisateur connecté) |
| GET | `/stats/assigned-count` | — (utilisateur connecté) |

---

## 5. Réponse type — ProblemResponseDTO

| Champ | Description |
|-------|-------------|
| id, status, createdAt, updatedAt, resolvedAt | Problème |
| createdBy | UserSummaryDTO (SuperAgent) |
| assignedTo | UserSummaryDTO (Agent) |
| alerts | Liste AlertSummaryDTO |
| statusHistory | Liste ProblemStatusHistoryDTO |

---

*Document généré à partir de ProblemController.java et DTOs associés.*

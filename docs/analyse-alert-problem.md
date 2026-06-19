# Analyse : Entités Alert & Problem

> Auteur : Oussama — Date : 2026-06-14

---

## 1. Relation entre Alert et Problem

La relation est **bidirectionnelle OneToMany / ManyToOne** :

- `Problem` → `Alert` : **OneToMany** (`mappedBy = "problem"`) — un problème regroupe plusieurs alertes.
- `Alert` → `Problem` : **ManyToOne** (`@JoinColumn(name = "problem_id", nullable = true)`) — une alerte peut être assignée à un seul problème, ou à aucun (nullable).

**Problem est le côté "parent" logique**, c'est lui qui regroupe des alertes. Alert est le côté "enfant" et possède la clé étrangère physique.

---

## 2. Compatibilité avec PostgreSQL

**La relation JPA est compatible et cohérente avec le schéma SQL.**

| Aspect | JPA (Java) | PostgreSQL (migration) |
|--------|-----------|----------------------|
| Colonne FK | `@JoinColumn(name = "problem_id", nullable = true)` | `problem_id BIGINT` (nullable, ajouté en V14) |
| Comportement suppression | Pas de `cascade` sur `Problem.alerts` | `ON DELETE SET NULL` sur `fk_alerts_problem` |
| Côté propriétaire | `Alert` (possède la FK) | `alerts.problem_id` |

Le choix de **ne pas mettre `CascadeType` côté `Problem.alerts`** est intentionnel et correct : quand un problème est supprimé, les alertes ne disparaissent pas, leur `problem_id` passe simplement à `NULL` en base (géré côté SQL). C'est la sémantique "les alertes survivent au problème".

> **Point à noter :** il y a un double mécanisme `updated_at` — le `@PreUpdate` JPA ET le trigger PostgreSQL font la même chose. Ça n'est pas un bug mais c'est redondant.

---

## 3. Logique métier fonction par fonction

### AlertServiceImpl — logique "signalement citoyen"

| Fonction | Ce qu'elle fait |
|----------|----------------|
| `createAlert` | Un utilisateur signale un problème urbain. Vérifie l'existence de l'user et de la catégorie. Le statut `NEW` est appliqué automatiquement via `@PrePersist`. |
| `getAlertById` / `getAllAlerts` | Lectures simples, marquées `readOnly`. |
| `getUserAlerts` | Alertes d'un utilisateur spécifique, avec pagination. |
| `updateAlert` | Modification partielle (title, description, etc.) **uniquement si le statut est `NEW`** et que l'alerte appartient à l'user (via `findByIdAndUserId`). Une alerte `IN_PROGRESS` ne peut plus être modifiée. |
| `deleteAlert` | Suppression **uniquement si `NEW`** et appartient à l'user. Une alerte déjà prise en charge ne peut pas être supprimée. |
| `verifyAlertIsNew` | Helper interne : lève une exception si le statut n'est pas `NEW`. Utilisé avant update/delete. |
| `verifyUserCanChangeStatus` | Interdit aux `CITOYEN` de changer le statut. Seuls `AGENT`, `SUPER_AGENT`, `ADMIN` peuvent le faire. |
| `changeStatus` | Changement direct de statut, sans vérification métier supplémentaire. Utilisé **uniquement par ProblemService** pour cascader les statuts. |
| `addImage` / `addVideo` | Ajoute une URL dans le champ JSON textuel `images` / `videos`. |
| `removeImage` / `removeVideo` | Retire une URL du JSON. Vérifie d'abord que l'URL existe bien dans la liste. |
| `getAlertsByCategory` / `getAlertsByStatus` / `searchAlerts` | Filtres paginés. |
| `countAlertsByStatus` / `countUserAlerts` | Statistiques. |
| `getCurrentUserId` | Résout l'ID de l'utilisateur connecté depuis `SecurityContextHolder` (par son numéro de téléphone). |

---

### ProblemServiceImpl — logique "traitement institutionnel"

| Fonction | Ce qu'elle fait |
|----------|----------------|
| `createProblem` | Crée un dossier de traitement. Vérifie que le créateur est `SUPER_AGENT`, que l'assigné est `AGENT`, et qu'au moins une alerte est fournie. Associe les alertes (qui passent en `IN_PROGRESS`), enregistre l'historique initial, et notifie les citoyens. |
| `getProblemById` / `getAllProblems` | Lectures simples. |
| `getProblemsCreatedBy` | Dossiers créés par un `SUPER_AGENT` spécifique. |
| `getProblemsAssignedTo` | Dossiers assignés à un `AGENT`. |
| `getProblemsByStatus` | Filtre par statut (`NEW`, `IN_PROGRESS`, `RESOLVED`, `REJECTED`). |
| `updateProblem` | Modification partielle (title, description, agent assigné, ajout/retrait d'alertes). Bloqué si le problème est `RESOLVED` ou `REJECTED`. Vérifie que l'user est le créateur ou l'agent assigné. |
| `deleteProblem` | Suppression réservée au **créateur** (`SUPER_AGENT`). Avant suppression, retire toutes les alertes liées et les remet en `NEW`. Bloqué si `RESOLVED` ou `REJECTED`. |
| `addAlertToProblem` | Associe une alerte à un problème. Vérifie qu'elle n'est pas déjà assignée ailleurs. Passe son statut en `IN_PROGRESS`. |
| `removeAlertFromProblem` | Détache une alerte et la remet en `NEW`. |
| `addAlertsToProblem` | Version batch de l'ajout (charge le problème une seule fois, puis boucle sur les alertes). Optimisé. |
| `changeStatus` | Point central du workflow. Change le statut du problème. Si `RESOLVED` → toutes les alertes liées passent en `RESOLVED`. Si `REJECTED` → toutes passent en `REJECTED`. Enregistre l'historique du changement. Notifie les citoyens. Seul le créateur ou l'agent assigné peut agir. |
| `getProblemStatus` | Lecture simple du statut courant. |
| `getStatusHistory` | Historique paginé des transitions de statut, trié du plus récent au plus ancien. |
| `verifySuperAgentRole` / `verifyAgentRole` | Vérifient le rôle d'un user, lèvent une exception métier si incorrect. |
| `verifyCanModifyProblem` | Vérifie que l'user est soit le créateur soit l'agent assigné. |
| `countProblemsByStatus` / `countProblemsAssignedTo` / `countProblemsCreatedBy` | Statistiques. |
| `notifyAlertCreators` | Parcourt les alertes du problème, récupère leur créateur, **log un message**. Pas d'implémentation réelle (TODO). |
| `getCurrentUserId` | Même logique que dans AlertService. |

---

## 4. Bonnes pratiques Spring — bilan

### Ce qui est bien fait

- `@Transactional` au niveau classe + `@Transactional(readOnly = true)` pour les lectures — correct, optimise les connexions.
- Injection par constructeur via `@RequiredArgsConstructor` — best practice (pas de `@Autowired` sur champ).
- `@Slf4j` pour le logging — propre.
- `FetchType.LAZY` sur toutes les relations — évite les N+1 non contrôlés.
- `@PrePersist` / `@PreUpdate` pour les timestamps — bien placé dans l'entité.
- Exceptions métier personnalisées (`ProblemNotFoundException`, `AlertAlreadyAssignedException`, etc.) — propre.
- `Page<T>` + `Pageable` pour la pagination — standard Spring Data.
- Séparation claire en couches : entity / repository / service / mapper (MapStruct) / controller / DTO.

---

### Ce qui mérite d'être amélioré

#### Duplication de `getCurrentUserId()`
La méthode est copiée-collée à l'identique dans `AlertServiceImpl` et `ProblemServiceImpl`. Elle devrait être extraite dans un service partagé (ex. `UserContextService`).

#### Re-chargement inutile de l'entité `User` dans `createProblem`
L'user est chargé une première fois (`userRepository.findById`), puis `verifySuperAgentRole(createdByUserId)` le recharge *une deuxième fois* en base. Deux requêtes SQL identiques pour rien.

#### Double assignation dans `addAlertToProblem`
`problem.addAlert(alert)` fait déjà `alert.setProblem(this)` en interne (via la méthode helper de l'entité), mais le service appelle quand même `alert.setProblem(problem)` juste après. C'est redondant.

#### `@Transactional` redondant sur `addAlertsToProblem`
La classe est déjà annotée `@Transactional`. Ajouter `@Transactional` sur la méthode sans paramètre différent ne change rien.

#### Images/vidéos stockées en JSON dans un champ `TEXT`
Les URLs sont stockées comme une chaîne JSON brute dans une colonne `TEXT`. C'est un anti-pattern : difficile à requêter, à valider, et non scalable. Une table `alert_media` ou un `@ElementCollection` serait plus propre.

#### `notifyAlertCreators` non implémentée
C'est un `TODO` critique. Elle est appelée dans `createProblem` et `changeStatus`, mais ne fait que du logging. Les citoyens ne reçoivent aucune notification réelle.

#### `verifyUserCanChangeStatus` mal annotée
Elle est marquée `@Transactional(readOnly = true)` alors que c'est une méthode de vérification sans retour. Sémantiquement incohérent.
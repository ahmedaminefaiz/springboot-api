-- V24__insert_kpi_test_data.sql
-- Données de test pour le dashboard KPI

-- ========== AGENTS SUPPLEMENTAIRES ==========

INSERT INTO users (nom, prenom, date_naissance, ville, email, password, phone, role, status, phone_verified)
VALUES
    ('Bennani', 'Sara', '1988-03-20', 'Casablanca', 'sara.superagent@urbanalert.ma',
     '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000003', 'SUPER_AGENT', 'ACTIVE', true),
    ('Idrissi', 'Youssef', '1992-07-10', 'Casablanca', 'youssef.agent@urbanalert.ma',
     '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000004', 'AGENT', 'ACTIVE', true),
    ('Karimi', 'Amine', '1990-04-10', 'Casablanca', 'amine.karimi@urbanalert.ma',
     '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000020', 'AGENT', 'ACTIVE', true),
    ('Soussi', 'Layla', '1993-08-22', 'Rabat', 'layla.soussi@urbanalert.ma',
     '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+212600000021', 'AGENT', 'ACTIVE', true)
ON CONFLICT (phone) DO NOTHING;

-- ========== PROBLEMES ==========
-- Colonnes actuelles : id, status, title, description, created_at, updated_at, resolved_at, criticality_id, user_id
-- (assigned_to a été supprimé en V19)

INSERT INTO problems (title, description, status, created_at, updated_at, criticality_id, user_id)
VALUES
    ('Panne éclairage secteur Hassan II',
     'Plusieurs lampadaires hors service dans le secteur, signalé par 3 citoyens.',
     'RESOLVED',
     NOW() - INTERVAL '20 days', NOW() - INTERVAL '5 days',
     (SELECT id FROM criticality WHERE name = 'Urgente'),
     (SELECT id FROM users WHERE phone = '+212600000003')),

    ('Accumulation déchets quartier Maarif',
     'Bacs satures depuis 7 jours, collecte non effectuee.',
     'IN_PROGRESS',
     NOW() - INTERVAL '15 days', NOW() - INTERVAL '2 days',
     (SELECT id FROM criticality WHERE name = 'Normale'),
     (SELECT id FROM users WHERE phone = '+212600000003')),

    ('Lampadaires corniche zone touristique',
     'Eclairage defaillant sur la corniche, impact sur la securite des touristes.',
     'IN_PROGRESS',
     NOW() - INTERVAL '10 days', NOW() - INTERVAL '1 day',
     (SELECT id FROM criticality WHERE name = 'Critique'),
     (SELECT id FROM users WHERE phone = '+212600000003')),

    ('Depot sauvage terrain vague Oujda',
     'Decharge sauvage signalee par plusieurs habitants du quartier.',
     'NEW',
     NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days',
     (SELECT id FROM criticality WHERE name = 'Faible'),
     (SELECT id FROM users WHERE phone = '+212600000003')),

    ('Panne eclairage gare Meknes',
     'Zone de stationnement sans eclairage, risque securitaire la nuit.',
     'RESOLVED',
     NOW() - INTERVAL '30 days', NOW() - INTERVAL '10 days',
     (SELECT id FROM criticality WHERE name = 'Bloquante'),
     (SELECT id FROM users WHERE phone = '+212600000003')),

    ('Ordures devant ecole primaire Tetouan',
     'Accumulation de dechets devant une ecole, risque sanitaire pour les enfants.',
     'IN_PROGRESS',
     NOW() - INTERVAL '5 days', NOW() - INTERVAL '1 day',
     (SELECT id FROM criticality WHERE name = 'Urgente'),
     (SELECT id FROM users WHERE phone = '+212600000003'));

-- resolved_at pour les problèmes résolus
UPDATE problems SET resolved_at = NOW() - INTERVAL '5 days'
WHERE title = 'Panne éclairage secteur Hassan II';

UPDATE problems SET resolved_at = NOW() - INTERVAL '10 days'
WHERE title = 'Panne eclairage gare Meknes';

-- ========== LIER DES ALERTES AUX PROBLEMES ==========
-- AlertStatusEnum valeurs : NEW, IN_PROGRESS, RESOLVED, REJECTED

UPDATE alerts SET
    problem_id   = (SELECT id FROM problems WHERE title = 'Panne éclairage secteur Hassan II'),
    qualified_at = NOW() - INTERVAL '19 days',
    status       = 'IN_PROGRESS'
WHERE title IN ('Lampadaire éteint rue Hassan II', 'Panne d éclairage parking municipal');

UPDATE alerts SET
    problem_id   = (SELECT id FROM problems WHERE title = 'Accumulation déchets quartier Maarif'),
    qualified_at = NOW() - INTERVAL '14 days',
    status       = 'IN_PROGRESS'
WHERE title IN ('Poubelles non vidées depuis une semaine', 'Déchets entassés près du marché central');

UPDATE alerts SET
    problem_id   = (SELECT id FROM problems WHERE title = 'Lampadaires corniche zone touristique'),
    qualified_at = NOW() - INTERVAL '9 days',
    status       = 'IN_PROGRESS'
WHERE title IN ('Éclairage éteint corniche de Tanger', 'Éclairage défaillant promenade du bord de mer');

UPDATE alerts SET
    problem_id   = (SELECT id FROM problems WHERE title = 'Depot sauvage terrain vague Oujda'),
    qualified_at = NOW() - INTERVAL '6 days',
    status       = 'IN_PROGRESS'
WHERE title = 'Dépôt sauvage terrain vague';

UPDATE alerts SET
    problem_id   = (SELECT id FROM problems WHERE title = 'Panne eclairage gare Meknes'),
    qualified_at = NOW() - INTERVAL '29 days',
    status       = 'RESOLVED'
WHERE title = 'Zone obscure parking gare de Meknès';

UPDATE alerts SET
    problem_id   = (SELECT id FROM problems WHERE title = 'Ordures devant ecole primaire Tetouan'),
    qualified_at = NOW() - INTERVAL '4 days',
    status       = 'IN_PROGRESS'
WHERE title = 'Déchets devant école primaire';

-- ========== INTERVENTIONS ==========
-- Colonnes : problem_id, agent_id, description, action_type, status, intervention_date, duration
-- (rapport et photo_url supprimes en V20)
-- InterventionActionTypeEnum : VISITE_TERRAIN, REPARATION, INSPECTION, TENTATIVE_RESOLUTION, DIAGNOSTIC, MISE_A_JOUR_OPERATIONNELLE
-- InterventionStatusEnum : AFFECTEE, EN_COURS, SUSPENDUE, EN_ATTENTE_AUTRE_EQUIPE, RESOLUE, PARTIELLEMENT_RESOLUE, ECHEC_INTERVENTION, CLOTUREE

INSERT INTO interventions (problem_id, agent_id, description, action_type, status, intervention_date, duration)
VALUES
    -- Problème 1 (RESOLVED) — intervention réussie
    ((SELECT id FROM problems WHERE title = 'Panne éclairage secteur Hassan II'),
     (SELECT id FROM users WHERE phone = '+212600000004'),
     'Remplacement de 4 lampadaires defectueux et verification du cablage electrique.',
     'REPARATION', 'RESOLUE',
     NOW() - INTERVAL '18 days', 180),

    -- Problème 2 (IN_PROGRESS) — en cours
    ((SELECT id FROM problems WHERE title = 'Accumulation déchets quartier Maarif'),
     (SELECT id FROM users WHERE phone = '+212600000020'),
     'Vidange partielle des bacs effectuee. Seconde collecte planifiee.',
     'MISE_A_JOUR_OPERATIONNELLE', 'EN_COURS',
     NOW() - INTERVAL '12 days', NULL),

    -- Problème 3 (IN_PROGRESS) — affectée, pas commencée
    ((SELECT id FROM problems WHERE title = 'Lampadaires corniche zone touristique'),
     (SELECT id FROM users WHERE phone = '+212600000021'),
     'Diagnostic technique des lampadaires de la corniche prevu.',
     'DIAGNOSTIC', 'AFFECTEE',
     NOW() - INTERVAL '8 days', NULL),

    -- Problème 5 (RESOLVED) — clôturée
    ((SELECT id FROM problems WHERE title = 'Panne eclairage gare Meknes'),
     (SELECT id FROM users WHERE phone = '+212600000004'),
     'Installation d un nouveau systeme d eclairage LED pour le parking de la gare.',
     'REPARATION', 'CLOTUREE',
     NOW() - INTERVAL '28 days', 240),

    -- Problème 6 — echec puis nouvelle tentative
    ((SELECT id FROM problems WHERE title = 'Ordures devant ecole primaire Tetouan'),
     (SELECT id FROM users WHERE phone = '+212600000020'),
     'Premiere collecte bloquee - camion en panne. Reaffectation prevue.',
     'TENTATIVE_RESOLUTION', 'ECHEC_INTERVENTION',
     NOW() - INTERVAL '3 days', 30),

    ((SELECT id FROM problems WHERE title = 'Ordures devant ecole primaire Tetouan'),
     (SELECT id FROM users WHERE phone = '+212600000021'),
     'Deuxieme tentative de collecte avec camion de remplacement.',
     'TENTATIVE_RESOLUTION', 'EN_COURS',
     NOW() - INTERVAL '1 day', NULL);
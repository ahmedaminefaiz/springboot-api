-- V21__create_criticality_table.sql

-- 1. Créer la table criticality
CREATE TABLE IF NOT EXISTS criticality (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    delay_hours INTEGER      NOT NULL
);

-- 2. Insérer les criticités par défaut
INSERT INTO criticality (name, delay_hours) VALUES
    ('Bloquante', 1),
    ('Urgente',   2),
    ('Critique',  8),
    ('Normale',   24),
    ('Faible',    72);

-- 3. Ajouter la colonne criticality_id à problems (nullable d'abord)
ALTER TABLE problems ADD COLUMN criticality_id BIGINT;

-- 4. Assigner la criticité 'Normale' aux problèmes existants
UPDATE problems
SET criticality_id = (SELECT id FROM criticality WHERE name = 'Normale');

-- 5. Passer la colonne NOT NULL
ALTER TABLE problems ALTER COLUMN criticality_id SET NOT NULL;

-- 6. Ajouter la contrainte FK
ALTER TABLE problems
    ADD CONSTRAINT fk_problems_criticality
    FOREIGN KEY (criticality_id) REFERENCES criticality(id) ON DELETE RESTRICT;

-- 7. Index pour les requêtes par criticité
CREATE INDEX IF NOT EXISTS idx_problems_criticality ON problems(criticality_id);
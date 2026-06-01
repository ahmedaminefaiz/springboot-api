-- V10__add_problem_column_to_alerts.sql
-- PostgreSQL Version - Ajoute la relation OneToMany entre Alert et Problem

-- Ajouter la colonne problem_id à la table alerts
ALTER TABLE alerts 
ADD COLUMN IF NOT EXISTS problem_id BIGINT;

-- Ajouter la contrainte de clé étrangère
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_alerts_problem'
    ) THEN
ALTER TABLE alerts
    ADD CONSTRAINT fk_alerts_problem
        FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE SET NULL;
END IF;
END $$;
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_alerts_ticket'
    ) THEN
ALTER TABLE alerts
    ADD CONSTRAINT fk_alerts_ticket
        FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE SET NULL;
END IF;
END $$;

-- Créer l'index pour optimiser les requêtes
CREATE INDEX IF NOT EXISTS idx_alerts_problem ON alerts(problem_id);

-- Note: La colonne ticket_id peut rester pour la compatibilité rétroactive
-- Les nouvelles alertes assignées à un problème utiliseront problem_id

-- V22__add_qualified_at_to_alerts.sql
-- Ajoute le timestamp de qualification sur les alertes
-- pour calculer le délai moyen de qualification (KPI dashboard)

ALTER TABLE alerts ADD COLUMN qualified_at TIMESTAMP;

-- Les alertes déjà liées à un problème reçoivent la date de création du problème comme approximation
UPDATE alerts a
SET qualified_at = (SELECT p.created_at FROM problems p WHERE p.id = a.problem_id)
WHERE a.problem_id IS NOT NULL;
-- Supprimer les colonnes rapport et photo_url de interventions
ALTER TABLE interventions DROP COLUMN IF EXISTS rapport;
ALTER TABLE interventions DROP COLUMN IF EXISTS photo_url;

-- Vider l'ancienne table intervention_photos (contenait les photos agent)
-- et la recreer pour les photos du SUPER AGENT
DROP TABLE IF EXISTS intervention_photos;
CREATE TABLE intervention_photos (
    intervention_id BIGINT NOT NULL,
    photo_url VARCHAR(2048),

    CONSTRAINT fk_intervention_photos FOREIGN KEY (intervention_id) REFERENCES interventions(id) ON DELETE CASCADE
);

-- Creer la table intervention_updates (historique des mises a jour agent)
CREATE TABLE intervention_updates (
    id BIGSERIAL PRIMARY KEY,
    intervention_id BIGINT NOT NULL,
    rapport TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_intervention_updates_intervention FOREIGN KEY (intervention_id) REFERENCES interventions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_intervention_updates_intervention ON intervention_updates(intervention_id);

-- Creer la table intervention_update_photos (photos agent par update)
CREATE TABLE intervention_update_photos (
    intervention_update_id BIGINT NOT NULL,
    photo_url VARCHAR(2048),

    CONSTRAINT fk_intervention_update_photos FOREIGN KEY (intervention_update_id) REFERENCES intervention_updates(id) ON DELETE CASCADE
);

-- V7__create_alerts_table.sql
-- PostgreSQL Version

-- Créer la table des alertes
CREATE TABLE IF NOT EXISTS alerts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    address VARCHAR(500),
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    is_anonymous BOOLEAN NOT NULL DEFAULT false,
    images TEXT,
    videos TEXT,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    ticket_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_alerts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_alerts_category FOREIGN KEY (category_id) REFERENCES problem_types(id) ON DELETE RESTRICT
);

-- Créer les indexes séparément (meilleure pratique PostgreSQL)
CREATE INDEX IF NOT EXISTS idx_alerts_user ON alerts(user_id);
CREATE INDEX IF NOT EXISTS idx_alerts_category ON alerts(category_id);
CREATE INDEX IF NOT EXISTS idx_alerts_status ON alerts(status);
CREATE INDEX IF NOT EXISTS idx_alerts_created_at ON alerts(created_at);

-- Créer un trigger pour mettre à jour automatically updated_at
CREATE OR REPLACE FUNCTION update_alerts_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER alerts_updated_at_trigger
    BEFORE UPDATE ON alerts
    FOR EACH ROW
    EXECUTE FUNCTION update_alerts_updated_at();

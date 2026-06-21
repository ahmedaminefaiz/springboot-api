-- V12__create_problems_table.sql
-- PostgreSQL Version

-- Créer la table des problèmes
CREATE TABLE IF NOT EXISTS problems (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    title VARCHAR(255),
    description TEXT,
    user_id BIGINT NOT NULL,
    assigned_to BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,

    CONSTRAINT fk_problems_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_problems_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE RESTRICT
);

-- Créer les indexes séparément (meilleure pratique PostgreSQL)
CREATE INDEX IF NOT EXISTS idx_problems_user ON problems(user_id);
CREATE INDEX IF NOT EXISTS idx_problems_assigned_to ON problems(assigned_to);
CREATE INDEX IF NOT EXISTS idx_problems_status ON problems(status);
CREATE INDEX IF NOT EXISTS idx_problems_created_at ON problems(created_at);

-- Créer un trigger pour mettre à jour automatically updated_at
CREATE OR REPLACE FUNCTION update_problems_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER problems_updated_at_trigger
BEFORE UPDATE ON problems
FOR EACH ROW
EXECUTE FUNCTION update_problems_updated_at();

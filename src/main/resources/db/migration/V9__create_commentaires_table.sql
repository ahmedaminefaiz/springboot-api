-- V9__create_commentaires_table.sql
-- PostgreSQL Version

CREATE TABLE IF NOT EXISTS commentaires (
                                            id BIGSERIAL PRIMARY KEY,
                                            contenu TEXT NOT NULL,
                                            user_id BIGINT NOT NULL,
                                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                            CONSTRAINT fk_commentaires_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Créer les indexes
CREATE INDEX IF NOT EXISTS idx_commentaires_user ON commentaires(user_id);
CREATE INDEX IF NOT EXISTS idx_commentaires_created_at ON commentaires(created_at);

-- Créer un trigger pour mettre à jour automatically updated_at
CREATE OR REPLACE FUNCTION update_commentaires_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER commentaires_updated_at_trigger
    BEFORE UPDATE ON commentaires
    FOR EACH ROW
    EXECUTE FUNCTION update_commentaires_updated_at();
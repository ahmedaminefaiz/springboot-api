-- V10__create_alert_commentaire_table.sql
-- PostgreSQL Version

CREATE TABLE IF NOT EXISTS alert_commentaire (
    alert_id BIGINT NOT NULL,
    commentaire_id BIGINT NOT NULL,

    PRIMARY KEY (alert_id, commentaire_id),
    CONSTRAINT fk_alert_comment_alert FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE,
    CONSTRAINT fk_alert_comment_commentaire FOREIGN KEY (commentaire_id) REFERENCES commentaires(id) ON DELETE CASCADE
);

-- Créer les indexes
CREATE INDEX IF NOT EXISTS idx_alert_commentaire_alert ON alert_commentaire(alert_id);
CREATE INDEX IF NOT EXISTS idx_alert_commentaire_commentaire ON alert_commentaire(commentaire_id);

-- V10__create_signalement_commentaire_table.sql

CREATE TABLE IF NOT EXISTS signalement_commentaire (
    signalement_id BIGINT NOT NULL,
    commentaire_id BIGINT NOT NULL,
    
    PRIMARY KEY (signalement_id, commentaire_id),
    CONSTRAINT fk_signal_comment_signalement FOREIGN KEY (signalement_id) REFERENCES signalements(id) ON DELETE CASCADE,
    CONSTRAINT fk_signal_comment_commentaire FOREIGN KEY (commentaire_id) REFERENCES commentaires(id) ON DELETE CASCADE,
    
    INDEX idx_commentaire_id (commentaire_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

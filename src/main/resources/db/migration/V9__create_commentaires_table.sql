-- V9__create_commentaires_table.sql

CREATE TABLE IF NOT EXISTS commentaires (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contenu LONGTEXT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_commentaires_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_commentaires_user (user_id),
    INDEX idx_commentaires_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

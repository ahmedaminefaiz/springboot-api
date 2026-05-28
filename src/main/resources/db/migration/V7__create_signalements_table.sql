-- V7__create_signalements_table.sql

-- Créer la table des signalements
CREATE TABLE IF NOT EXISTS signalements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description LONGTEXT NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    address VARCHAR(500),
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    is_anonymous BOOLEAN NOT NULL DEFAULT false,
    images LONGTEXT,
    videos LONGTEXT,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    ticket_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_signalements_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_signalements_category FOREIGN KEY (category_id) REFERENCES problem_types(id) ON DELETE RESTRICT,
    CONSTRAINT fk_signalements_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE SET NULL,
    
    INDEX idx_signalements_user (user_id),
    INDEX idx_signalements_category (category_id),
    INDEX idx_signalements_status (status),
    INDEX idx_signalements_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

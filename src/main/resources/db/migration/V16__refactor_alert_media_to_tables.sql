-- V16__refactor_alert_media_to_tables.sql
-- Remplacement des colonnes TEXT images/videos par des tables de jointure (@ElementCollection)

-- Suppression des anciennes colonnes JSON
ALTER TABLE alerts DROP COLUMN IF EXISTS images;
ALTER TABLE alerts DROP COLUMN IF EXISTS videos;

-- Table des images liées aux alertes
CREATE TABLE IF NOT EXISTS alert_images (
    alert_id BIGINT NOT NULL,
    image_url VARCHAR(2048) NOT NULL,
    CONSTRAINT fk_alert_images_alert FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_alert_images_alert_id ON alert_images(alert_id);

-- Table des vidéos liées aux alertes
CREATE TABLE IF NOT EXISTS alert_videos (
    alert_id BIGINT NOT NULL,
    video_url VARCHAR(2048) NOT NULL,
    CONSTRAINT fk_alert_videos_alert FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_alert_videos_alert_id ON alert_videos(alert_id);
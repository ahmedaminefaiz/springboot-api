-- Ajoute title et description aux problèmes (bases déjà migrées avec V12 sans ces colonnes)

ALTER TABLE problems
    ADD COLUMN IF NOT EXISTS title VARCHAR(255);

ALTER TABLE problems
    ADD COLUMN IF NOT EXISTS description TEXT;

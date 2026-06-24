-- Supprimer assigned_to de problems
ALTER TABLE problems DROP CONSTRAINT fk_problems_assigned_to;
DROP INDEX IF EXISTS idx_problems_assigned_to;
ALTER TABLE problems DROP COLUMN assigned_to;

-- Creer la table interventions
CREATE TABLE IF NOT EXISTS interventions (
    id BIGSERIAL PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    agent_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    rapport TEXT,
    action_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AFFECTEE',
    intervention_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    duration INTEGER,
    photo_url VARCHAR(2048),

    CONSTRAINT fk_interventions_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    CONSTRAINT fk_interventions_agent FOREIGN KEY (agent_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_interventions_problem ON interventions(problem_id);
CREATE INDEX IF NOT EXISTS idx_interventions_agent ON interventions(agent_id);
CREATE INDEX IF NOT EXISTS idx_interventions_status ON interventions(status);

-- Table des photos d'intervention (meme pattern que alert_images)
CREATE TABLE IF NOT EXISTS intervention_photos (
    intervention_id BIGINT NOT NULL,
    photo_url VARCHAR(2048),

    CONSTRAINT fk_intervention_photos FOREIGN KEY (intervention_id) REFERENCES interventions(id) ON DELETE CASCADE
);

-- V9__create_problem_status_history_table.sql
-- PostgreSQL Version

CREATE TABLE IF NOT EXISTS problem_status_history (
    id BIGSERIAL PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by VARCHAR(20) NOT NULL,
    comment TEXT,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_history_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
);

-- Créer les indexes
CREATE INDEX IF NOT EXISTS idx_problem_status_history_problem ON problem_status_history(problem_id);
CREATE INDEX IF NOT EXISTS idx_problem_status_history_new_status ON problem_status_history(new_status);
CREATE INDEX IF NOT EXISTS idx_problem_status_history_changed_at ON problem_status_history(changed_at);

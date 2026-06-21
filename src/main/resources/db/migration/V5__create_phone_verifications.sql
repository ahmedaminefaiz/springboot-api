CREATE TABLE IF NOT EXISTS phone_verifications (
    id         BIGSERIAL PRIMARY KEY,
    phone      VARCHAR(15)  NOT NULL,
    code       VARCHAR(6)   NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_phone_verifications_phone ON phone_verifications(phone);
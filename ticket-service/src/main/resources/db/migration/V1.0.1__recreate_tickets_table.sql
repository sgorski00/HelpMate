DROP TABLE tickets;
DROP SEQUENCE IF EXISTS tickets_id_seq;

CREATE TABLE IF NOT EXISTS tickets (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    reporter_id VARCHAR(36) NOT NULL,
    assignee_id VARCHAR(36),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);
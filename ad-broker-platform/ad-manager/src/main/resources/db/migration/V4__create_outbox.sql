CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    topic VARCHAR(255) NOT NULL
);

CREATE INDEX idx_outbox_created_at ON outbox_events(created_at);
CREATE TABLE IF NOT EXISTS monitoring.processed_events
(
    id               BIGSERIAL PRIMARY KEY,
    eventId          VARCHAR UNIQUE NOT NULL
);

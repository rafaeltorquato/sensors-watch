CREATE TABLE IF NOT EXISTS monitoring.thresholds
(
    id    SMALLSERIAL PRIMARY KEY,
    type  VARCHAR UNIQUE NOT NULL,
    value INTEGER                NOT NULL
);
INSERT INTO monitoring.thresholds(type, value)
values
    ('HUMIDITY', 50),
    ('TEMPERATURE', 35)
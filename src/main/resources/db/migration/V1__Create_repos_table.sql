CREATE TABLE repo
(
    ID              SERIAL PRIMARY KEY,
    name            TEXT      NOT NULL,
    volume_location TEXT      NOT NULL,
    is_private      BOOLEAN   NOT NULL,
    latest_push     TIMESTAMP NULL,
    latest_fetch    TIMESTAMP NULL
);

CREATE TABLE auth
(
    ID           INT NOT NULL DEFAULT 1 UNIQUE,
    access_token TEXT,
    PRIMARY KEY (ID)
);

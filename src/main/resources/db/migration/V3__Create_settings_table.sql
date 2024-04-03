CREATE TABLE settings
(
    ID                 INT UNIQUE,
    auto_repo_update   BOOLEAN NOT NULL DEFAULT FALSE,
    auto_commit_update BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (ID),
    CHECK (ID = 1)
);

INSERT INTO settings (ID)
VALUES (1);

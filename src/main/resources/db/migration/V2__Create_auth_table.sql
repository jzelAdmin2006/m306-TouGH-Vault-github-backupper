CREATE TABLE auth
(
    ID             INT UNIQUE   NOT NULL DEFAULT 1,
    [access_token] VARCHAR(MAX) NULL,
    PRIMARY KEY (ID)
)

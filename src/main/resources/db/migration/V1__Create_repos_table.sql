CREATE TABLE repo
(
    ID                INT IDENTITY,
    [name]            VARCHAR(MAX) NOT NULL,
    [volume_location] VARCHAR(MAX) NOT NULL,
    [latest_push]     DATETIME     NOT NULL,
    PRIMARY KEY (ID)
)

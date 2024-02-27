CREATE TABLE repo
(
    ID INT IDENTITY,
    [name] VARCHAR(MAX) NOT NULL,
    [volume_location] VARCHAR(MAX) NOT NULL,
    [latest_commit] VARCHAR(MAX) NOT NULL,
    PRIMARY KEY(ID)
)

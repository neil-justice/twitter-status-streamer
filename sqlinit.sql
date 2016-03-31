DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Status;
DROP TABLE IF EXISTS Mention;

CREATE TABLE User (
  uid INTEGER PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  followers INTEGER NOT NULL,
  friends INTEGER NOT NULL
);

CREATE TABLE Status (
  sid INTEGER PRIMARY KEY,
  text TEXT NOT NULL,
  timestamp INTEGER NOT NULL,
  author INTEGER REFERENCES User(uid)
);

CREATE TABLE Mention (
  mid INTEGER PRIMARY KEY,
  user INTEGER REFERENCES User(uid),
  mentioned INTEGER REFERENCES User(uid)
);

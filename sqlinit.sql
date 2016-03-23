DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Status;
DROP TABLE IF EXISTS Follower;
DROP TABLE IF EXISTS Mention;

CREATE TABLE User (
  uid INTEGER PRIMARY KEY,
  name VARCHAR(100)
);

CREATE TABLE Status (
  sid INTEGER PRIMARY KEY,
  text TEXT,
  timestamp INTEGER,
  author INTEGER REFERENCES User(uid)
);

CREATE TABLE Follower (
  fid INTEGER PRIMARY KEY,
  user INTEGER REFERENCES User(uid),
  follower INTEGER REFERENCES User(uid)
);

CREATE TABLE Mention (
  mid INTEGER PRIMARY KEY,
  user INTEGER REFERENCES User(uid),
  mentioned INTEGER REFERENCES User(uid),
  count INTEGER NOT NULL
);

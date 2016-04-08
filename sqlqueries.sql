-- Finds all Users who have no follower data:

SELECT User.uid
FROM User
LEFT JOIN Follower ON Follower.user = User.uid
WHERE Follower.user IS NULL;

-- Count distinct users:

SELECT COUNT(uid) FROM User;

-- Find all Users who have follower data:

SELECT User.uid
FROM User
INNER JOIN Follower ON Follower.user = User.uid
GROUP BY uid;

-- Find all Users who are also known followers

SELECT User.uid
FROM User INNER JOIN Follower ON Follower.follower = User.uid;

-- Count users who are also known followers

SELECT COUNT(*)
FROM User
INNER JOIN Follower ON Follower.follower = User.uid;

-- Count number of followers per user

SELECT User.uid, COUNT(Follower.follower)
FROM User INNER JOIN Follower ON Follower.user = User.uid
GROUP BY uid;

-- Count number of known followers per user

SELECT Follower.user, COUNT(*)
FROM User
INNER JOIN Follower ON Follower.follower = User.uid
GROUP BY Follower.user;

-- Count all followers (including those who are not known users)

SELECT COUNT(follower) FROM Follower;

-- Find all Users who are followers and have no follower data

SELECT User.uid
FROM User
LEFT JOIN Follower ON Follower.user = User.uid
WHERE Follower.user IS NULL
INTERSECT
SELECT User.uid
FROM User
INNER JOIN Follower ON Follower.follower = User.uid
GROUP BY uid;

-- count tweets made by users who are known to be followers

SELECT COUNT(sid)
FROM User INNER JOIN Follower ON Follower.follower = User.uid,
Status
WHERE author = User.uid;

-- Return everything needed for a status object

SELECT text, uid, name FROM
User INNER JOIN Status ON User.uid = Status.author;

-- count all distinct mentions

SELECT COUNT(*) FROM
(SELECT DISTINCT user, mentioned FROM Mention);

-- count all mentions

SELECT COUNT(*) FROM Mention;

-- coutn users by number of mentions

SELECT user, COUNT(mentioned) AS m FROM Mention GROUP BY user ORDER BY m ASC;

-- Count all tweets authored by someone who has mentioned someone else in the db

SELECT COUNT(*) FROM
Status
WHERE Status.author IN (SELECT DISTINCT user FROM Mention);

-- Find all recpirocated mentions:

SELECT DISTINCT a.user, a.mentioned
FROM Mention AS a INNER JOIN Mention AS b
ON a.user = b.mentioned AND b.user = a.mentioned AND a.user != b.user;

-- count all distinct recip. mentions:

SELECT COUNT(*) FROM (
SELECT DISTINCT a.user, a. mentioned, b.user, b.mentioned
FROM Mention AS a INNER JOIN Mention AS b
ON a.user = b.mentioned AND b.user = a.mentioned AND a.user != b.user);

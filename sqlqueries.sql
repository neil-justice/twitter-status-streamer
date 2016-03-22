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

-- Find all Users who are followers and users

SELECT User.uid
FROM User
INNER JOIN Follower ON Follower.follower = User.uid;

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

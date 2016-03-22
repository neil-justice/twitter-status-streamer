import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLConnection implements AutoCloseable
{
  static final String CONNECTION = "jdbc:sqlite:statuses.db";
  private Connection c;
  private int nonUnique = 0;

  public void open()
  {
    try {
      c = DriverManager.getConnection(CONNECTION);
      c.setAutoCommit(false);  // Allow transactions
      System.out.println("SQL Connection open.");
    } catch (SQLException e) {
      c = null;
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void close()
  {
    try {
      if (c == null) { return; }

      c.close();
      c = null;
      System.out.println("SQL connection closed.");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  // Wrapper for commit() method which gives info about the number of times an
  // INSERT into table User due to id uniqueness violation (i.e. a tweet from
  // this user has been streamed before and the user is already in th db)
  public void commitStream()
  {
    commit();
    System.out.println("" + nonUnique + "/100 are non-unique users.");
    nonUnique = 0;
  }

  public void commit()
  {
    try {
      c.commit();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void addStatus(long id, String text, long timestamp, long author)
  {
    if (c == null) { throw new IllegalStateException(); }

    try ( PreparedStatement s = c.prepareStatement(
         "INSERT INTO Status " +
         "VALUES( ?, ?, ?, ? )")) {

      s.setLong(1, id);
      s.setString(2, text);
      s.setLong(3, timestamp);
      s.setLong(4, author);

      s.execute();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void addUser(long id, String name)
  {
    if (c == null) { throw new IllegalStateException(); }

    try ( PreparedStatement s = c.prepareStatement(
         "INSERT INTO User " +
         "VALUES( ?, ? )")) {

      s.setLong(1, id);
      s.setString(2, name);

      s.execute();
    } catch (SQLException e) {
      nonUnique++;
    }
  }

  public void addFollower(long uid, long follower)
  {
    if (c == null) { throw new IllegalStateException(); }

    try ( PreparedStatement s = c.prepareStatement(
         "INSERT INTO Follower " +
         "VALUES( null, ?, ? )")) {

      s.setLong(1, uid);
      s.setLong(2, follower);

      s.execute();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public long count()
  {
    if (c == null) { throw new IllegalStateException(); }

    int count = 0;

    try ( PreparedStatement s = c.prepareStatement(
         "SELECT COUNT(*) FROM Status")) {

      ResultSet r = s.executeQuery();
      count = r.getInt(1);

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return count;
  }

  // Returns a list of users who
  // a. follow a known user
  // b. have not had their follower list populated.
  // If none exists, returns one who just satisfies b.
  public List<Long> getUserList()
  {
    List<Long> list =
    getUsers("SELECT User.uid"
            + " FROM User"
            + " LEFT JOIN Follower ON Follower.user = User.uid"
            + " WHERE Follower.user IS NULL"
            + " INTERSECT"
            + " SELECT User.uid"
            + " FROM User"
            + " INNER JOIN Follower ON Follower.follower = User.uid"
            + " GROUP BY uid");

    if (list.isEmpty()) {
      list = getUsers("SELECT User.uid"
                  + " FROM User"
                  + " LEFT JOIN Follower ON Follower.user = User.uid"
                  + " WHERE Follower.user IS NULL");
    }
    return list;
  }

  private List<Long> getUsers(String stmt)
  {
    if (c == null) { throw new IllegalStateException(); }

    List<Long> list = new ArrayList<Long>();

    try (PreparedStatement s = c.prepareStatement(stmt)) {

      ResultSet r = s.executeQuery();
      while (r.next()) {
        list.add(r.getLong("User.uid"));
      }
      return list;

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
  }
}

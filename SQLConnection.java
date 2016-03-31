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

  public void addUser(long id, String name, int followers,int friends)
  {
    if (c == null) { throw new IllegalStateException(); }

    try ( PreparedStatement s = c.prepareStatement(
         "INSERT INTO User " +
         "VALUES( ?, ?, ?, ? )")) {

      s.setLong(1, id);
      s.setString(2, name);
      s.setInt(3, followers);
      s.setInt(4, friends);

      s.execute();
    } catch (SQLException e) {
      nonUnique++;
    }
  }

  public long countStatuses()
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


  public List<DBUser> getUsers()
  {
    if (c == null) { throw new IllegalStateException(); }

    List<DBUser> list = new ArrayList<DBUser>();

    try (PreparedStatement s = c.prepareStatement(
         "SELECT uid, name FROM User")) {

      ResultSet r = s.executeQuery();
      while (r.next()) {
        DBUser u = new DBUser(r.getLong("uid"), r.getString("name"));
        list.add(u);
      }
      return list;

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
  }

  public List<DBStatus> getStatuses()
  {
    if (c == null) { throw new IllegalStateException(); }

    List<DBStatus> list = new ArrayList<DBStatus>();

    try (PreparedStatement s = c.prepareStatement(
         "SELECT text, uid, name FROM"
         + " User INNER JOIN Status ON User.uid = Status.author")) {

      ResultSet r = s.executeQuery();
      while (r.next()) {
        DBStatus st = new DBStatus(
          r.getString("text"),
          r.getLong("uid"),
          r.getString("name"));
        list.add(st);
      }
      return list;

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
  }

  public void addMention(long uid, long mentioned)
  {
    if (c == null) { throw new IllegalStateException(); }

    try ( PreparedStatement s = c.prepareStatement(
         "INSERT INTO Mention " +
         "VALUES( null, ?, ? )")) {

      s.setLong(1, uid);
      s.setLong(2, mentioned);

      s.execute();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}

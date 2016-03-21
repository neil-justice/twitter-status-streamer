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
      closeOnShutdown();
    } catch (SQLException e) {
      c = null;
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void close()
  {
    try {
      if (c == null) {
        return;
      }
      c.close();
      c = null;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  // Uses a shutdown hook to close the database connection
  private void closeOnShutdown()
  {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        close();
        System.out.println("SQL connection closed.");
      }
    });
  }

  public void commit()
  {
    try {
      c.commit();
      System.out.println("" + nonUnique + "non-unique users");
      nonUnique = 0;
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
}

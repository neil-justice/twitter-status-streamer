import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLConnection implements Runnable, AutoCloseable
{
  static final String CONNECTION = "jdbc:sqlite:statuses.db";
  private Connection c;

  public void open()
  {
    try {
      c = DriverManager.getConnection(CONNECTION);
    } catch (SQLException e) {
      c = null;
      e.printStackTrace();
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
      e.printStackTrace();
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
    } catch (Exception e) {
        e.printStackTrace();
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
    } catch (Exception e) {
        e.printStackTrace();
    }
  }

  @override
  public void run()
  {
    open();

    close();
  }
}

/* Named DBStatus to distinguish from Twitter4j's Status object. */
import java.util.*;

class DBStatus
{
  private String text;
  private Long uid;

  public DBStatus(String text, Long uid)
  {
    this.text = text;
    this.uid = uid;
  }

  public String text() { return text; }
  public long uid() { return uid; }
}

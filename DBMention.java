import java.util.*;

class DBMention
{
  private Long uid;
  private Long mentioned;

  public DBMention(Long uid, Long mentioned)
  {
    this.uid = uid;
    this.mentioned = mentioned;
  }

  public long uid() { return uid; }
  public long mentioned() { return mentioned; }
}

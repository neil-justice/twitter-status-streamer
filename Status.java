import java.util.*;

class Status
{
  private String text;
  private Long uid;
  private String username;
  private List<Long> mentions;
  
  public Status(String text, Long uid, String username, List<Long> mentions)
  {
    this.text = text;
    this.uid = uid;
    this.username = username;
    this.mentions = mentions;
  }
  
  public Status(String text, Long uid, String username)
  {
    this.text = text;
    this.uid = uid;
    this.username = username;
  }
  
  public String text() { return text; }
  public long uid() { return uid; }
  public String username() { return username; }
  public List<Long> mentions() { return mentions; }
  
  public void addMention(long mention)
  {
    mentions.add(mention);
  }
}
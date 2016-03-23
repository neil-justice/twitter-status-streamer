import java.util.*;

class User
{
  private Long uid;
  private String username;
  private List<String> statuses;
  private List<Long> mentions;
  
  public User(Long uid, String username)
  {
    this.uid = uid;
    this.username = username;
    statuses = new ArrayList<String>();
    mentions = new ArrayList<Long>();
  }
  
  public User(Long uid, String username, List<String> statuses)
  {
    this.uid = uid;
    this.username = username;
    this.statuses = statuses;
    mentions = new ArrayList<Long>();
  }
  
  public long uid() { return uid; }
  public String username() { return username; }
  public List<String> statuses() { return statuses; }
  public List<Long> mentions() { return mentions; }
  
  public void addStatus(String s) { statuses.add(s); }
  public void addMention(Long l) { mentions.add(l); }
}
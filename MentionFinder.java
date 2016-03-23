/* program to test how reasonable it is to use user mentions to build
 * a social graph of twitter. */

import java.util.*;

class MentionFinder
{
  private SQLConnection db;
  private List<Status> statuses;
  private List<User> users;
  private List<String> namesMentioned;
  private Map<String, Long> uidLookup;
  private char c = '@';
  private int count;
  
  public static void main(String[] args)
  {
    MentionFinder m = new MentionFinder();
    m.run();
  }
  
  public void run()
  {
    findMentions();
    addMentions();
    printResults();
  }
  
  public MentionFinder()
  {
    db = new SQLConnection();
    db.open();
    statuses = db.getStatuses();
    users = db.getUsers();
    namesMentioned = new ArrayList<String>();
    uidLookup = new HashMap<String, Long>();
    populateLookups();
  }
  
  private void populateLookups()
  {
    for (User u: users) {
      uidLookup.put(u.username(), u.uid());
    }
  }
  
  private void findMentions()
  {
    count = 0;
    List<Long> mentions;
    
    for (Status s: statuses) {
      if (s.text().contains("" + c)) {
        mentions = findMentionedUsers(s.text());
        for (long l: mentions) {
          s.addMention(l);
        }
        count++;
      }
    }
  }
  
  private List<Long> findMentionedUsers(String s)
  {
    String[] words = s.split(" ");
    List<Long> mentions = new ArrayList<Long>();
    
    for (String w: words) {
      if (w.startsWith("@")) {
        String name = w.replaceAll("[^a-zA-Z0-9_]", "");
        namesMentioned.add(name);
        Long l = uidLookup.get(name);
        if (l != null) {
          mentions.add(l);
        }
      }
    }
    
    return mentions;
  }
  
  private void addMentions()
  {
    for (Status s: statuses) {
      for (long l: s.mentions()) {
        db.addMention(s.uid(), l);
      }
    }
    db.commit();
  }
  
  private void printResults()
  {
    System.out.println("Total number of tweets: " + db.count());
    System.out.println("Number containing mentions: " + count);
    System.out.println("Number of discrete mentions: " + namesMentioned.size());
  }
}
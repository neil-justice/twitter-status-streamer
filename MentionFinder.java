/* program to test how reasonable it is to use user mentions to build
 * a social graph of twitter. */

import java.util.*;

class MentionFinder
{
  private SQLConnection db;
  private List<Status> statuses;
  private List<String> namesMentioned;
  private List<Long> IDsMentioned;
  private char c = '@';
  private int count;
  
  public static void main(String[] args)
  {
    MentionFinder m = new MentionFinder();
    m.findMentions();
    m.printResults();
  }
  
  public MentionFinder()
  {
    db = new SQLConnection();
    db.open();
    statuses = db.getStatuses();
    namesMentioned = new ArrayList<String>();
    IDsMentioned = new ArrayList<Long>();
  }
  
  public void findMentions()
  {
    count = 0;
    
    for (Status s: statuses) {
      if (s.text().contains("" + c)) {
        findMentionedUser(s.text());
        count++;
      }
    }
  }
  
  public void findMentionedUser(String s)
  {
    String[] words = s.split(" ");
    
    for (String w: words) {
      if (w.startsWith("@")) {
        String name = w.replaceAll("[^a-zA-Z0-9_]", "");
        namesMentioned.add(name);
        long l = db.getIDFromName(name);
        if (l != 0) {
          IDsMentioned.add(l);
        }
      }
    }
  }
  
  
  public void printResults()
  {
    System.out.println("Total number of tweets: " + db.count());
    System.out.println("Number containing mentions: " + count);
    System.out.println("Number of discrete mentions: " + namesMentioned.size());
    System.out.println("Number of known IDs mentioned: " + IDsMentioned.size());
  }
}
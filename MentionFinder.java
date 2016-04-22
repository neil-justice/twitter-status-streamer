/* Populates the Mention table in the database.  Each record has
 * an id, a user (who mentions), and a user mentioned.  This is
 * used as graph edge data. */

import java.util.*;

class MentionFinder
{
  private SQLConnection db;
  private List<DBStatus> statuses;
  private List<DBMention> mentions;
  private Map<String, Long> uidLookup;
  private char c = '@';
  private int count;

  public static void main(String[] args)
  {
    MentionFinder m = new MentionFinder();
    m.run();
  }

  public MentionFinder()
  {
    db = new SQLConnection();
    db.open();
    db.setCacheSize(524288);
    count = 0;
    
    mentions = new ArrayList<DBMention>();
    uidLookup = new HashMap<String, Long>();
    populateLookup();

    System.out.println("Loaded Userlist.");
  }

  // due to memory limitations not all statuses can be loaded from SQLite at
  // once.  They are loaded in chunks to the List statuses.  Once the mentions
  // have been found the statuses List is cleared, and once the mentions
  // are added to the db the mentions List is cleared.
  public void run()
  {
    long offset = 0;
    long amount = 500000;
    long curr = 0;
    
    do {
      statuses = db.getStatuses(offset, amount);
      System.out.println("" + amount + " statuses loaded.");
      curr = statuses.size();
      statuses.clear();      
      
      findMentions();
      System.out.println("Mentions found and converted to uids.");
      
      addMentions();
      mentions.clear();      
      System.out.print("Mentions added to db.  ");
      System.out.println("" + (amount + offset) +
                         " statuses checked so far.");
                         
      offset += amount;
    } while (curr > 0);
  }
  
  // A map between usernames and uids, used to convert mentioned names
  // into uids.
  private void populateLookup()
  {
    List<DBUser> users = db.getUsers();
    
    for (DBUser u: users) {
      uidLookup.put(u.username(), u.uid());
    }
    
    users.clear();
  }

  // Extracts all the usernames mentioned (preceded by an '@')
  // in each Status object, converts them to a uid, and adds them to
  // the 'mentions' field of the Status object.
  private void findMentions()
  {
    for (DBStatus s: statuses) {
      if (s.text().contains("" + c)) {
        findMentionedUsers(s.text(), s.uid());
        count++;
      }
    }
  }

  // Extracts any mentioned usernames and converts them to uids.
  private void findMentionedUsers(String text, Long uid)
  {
    String[] words = text.split(" ");

    for (String word: words) {
      if (word.startsWith("@")) {
        String name = word.replaceAll("[^a-zA-Z0-9_]", "");
        Long l = uidLookup.get(name);
        if (l != null) {
          mentions.add(new DBMention(uid, l));
        }
      }
    }
  }

  // Adds all mentions found to the Mentions table in the db. each
  // mention has an id, a user, and a user mentioned field.
  private void addMentions()
  {
    db.addMentions(mentions);
    db.commit();
  }
}

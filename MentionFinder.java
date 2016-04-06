/* Populates the Mention table in the database.  Each record has
 * an id, a user (who mentions), and a user mentioned.  This is
 * used as graph edge data. */

import java.util.*;

class MentionFinder
{
  private SQLConnection db;
  private List<DBStatus> statuses;
  private List<DBUser> users;
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
    count = 0;
    users = db.getUsers();
    mentions = new ArrayList<DBMention>();
    uidLookup = new HashMap<String, Long>();
    populateLookup();

    System.out.println("Loaded Userlist.");
  }

  public void run()
  {
    long offset = 0;
    long amount = 100000;
    long statusNum = db.countStatuses();

    while (offset <= statusNum) {
      statuses = db.getStatuses(offset, amount);
      System.out.println("" + amount + " statuses loaded.");
      
      findMentions();
      System.out.println("Mentions found and converted to uids.");
      
      addMentions();
      System.out.println("Mentions added to db.");
      System.out.println("" + (amount + offset) +
                         " statuses checked so far.");
                         
      offset += amount;
    }
  }
  
  // A map between usernames and uids, used to convert mentioned names
  // into uids.
  private void populateLookup()
  {
    for (DBUser u: users) {
      uidLookup.put(u.username(), u.uid());
    }
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
    statuses.clear();
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
    for (DBMention m: mentions) {
      db.addMention(m.uid(), m.mentioned());
    }
    db.commit();
    mentions.clear();
  }
}

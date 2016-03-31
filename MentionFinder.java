/* Populates the Mention table in the database.  Each record has
 * an id, a user (who mentions), and a user mentioned.  This is
 * used as graph edge data. */

import java.util.*;

class MentionFinder
{
  private SQLConnection db;
  private List<DBStatus> statuses;
  private List<DBUser> users;
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
    populateLookup();
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
    count = 0;
    List<Long> mentions;

    for (DBStatus s: statuses) {
      if (s.text().contains("" + c)) {
        mentions = findMentionedUsers(s.text());
        for (long l: mentions) {
          s.addMention(l);
        }
        count++;
      }
    }
  }

  // Extracts any mentioned usernames and converts them to uids.
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

  // Adds all mentions found to the Mentions table in the db. each
  // mention has an id, a user, and a user mentioned field.
  private void addMentions()
  {
    for (DBStatus s: statuses) {
      for (long l: s.mentions()) {
        db.addMention(s.uid(), l);
      }
    }
    db.commit();
  }

  private void printResults()
  {
    System.out.println("Total number of tweets: " + db.countStatuses());
    System.out.println("Number containing mentions: " + count);
    System.out.println("Number of discrete mentions: " + namesMentioned.size());
  }
}

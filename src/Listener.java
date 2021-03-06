/* Listens to  a stream.  When a status (message) is recieved, adds the raw
 * JSON data to the specified SQLite database.
 */

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterObjectFactory;

class Listener implements StatusListener
{
  private long count = 0;
  private SQLConnection db;

  public Listener(SQLConnection db)
  {
    this.db = db;

    count = db.countStatuses();
  }

  // Sends statuses to the connected SQL database in groups of 100.
  @Override
  public void onStatus(Status status)
  {
    String name    = status.getUser().getScreenName();
    long uid       = status.getUser().getId();

    String text    = status.getText();
    long sid       = status.getId();
    long timestamp = status.getCreatedAt().getTime();

    int followers  = status.getUser().getFollowersCount();
    int friends    = status.getUser().getFriendsCount();

    db.addUser(uid, name, followers, friends);
    db.addStatus(sid, text, timestamp, uid);

    count++;
    if (count % 100 == 0) {
      db.commitStream();
      System.out.println("" + count + " in db.");
    }
  }

  @Override
  public void onException(Exception e)
  {
    System.out.println("Exception: " + e.getMessage());
    e.getMessage();
    e.printStackTrace();
  }

  @Override
  public void onTrackLimitationNotice(int numberOfLimitedStatuses)
  {
    System.out.println("Warning: " + numberOfLimitedStatuses + 
                       " statuses missed from stream.");
  }

  @Override
  public void onStallWarning(StallWarning warning)
  {
    System.out.println("Got stall warning:" + warning);
  }

  @Override
  public void onScrubGeo(long arg0, long arg1) {}

  @Override
  public void onDeletionNotice(StatusDeletionNotice arg0) {}
}

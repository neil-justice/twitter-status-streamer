/* Listens to  a stream.  When a status (message) is recieved, adds the raw
 * JSON data to the specified mongoDB database.
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

  public Listener()
  {
    db = new SQLConnection();

    db.open();

    count = 0;
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

    if (count % 100 == 0) {
      db.beginTransaction();
    }

    db.addUser(uid, name);
    db.addStatus(sid, text, timestamp, uid);

    count++;
    if (count % 100 == 0) {
      System.out.println("" + count + " in db.");
      db.setTransactionSuccessful();
      db.endTransaction();
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
    System.out.println("Got track limitation notice:"
                       + numberOfLimitedStatuses);
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

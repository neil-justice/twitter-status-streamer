/* Uses the twitter4j library to interface with Twitter's REST API.
 * https://dev.twitter.com/rest/reference/get/followers/ids
 */

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.RateLimitStatus;
import twitter4j.conf.Configuration;

class FollowerFinder implements Runnable
{
  private Twitter twitter;
  private IDs ids;
  private long cursor;
  private SQLConnection db;

  public FollowerFinder(SQLConnection db, Configuration c)
  {
    this.db = db;

    twitter = new TwitterFactory(c).getInstance();
    cursor = -1; // For navigating result sets with >1 page
  }

  @Override
  public void run()
  {
    long l;
    int i;

    while ((l = db.getNextUser()) != 0) {
      if ((i = getCallsRemaining()) > 0) {
        System.out.println("" + i + " calls remaining.");
        find(l);
        System.out.println("Getting followers for User " + l);
      }
      else {
        int s = getSecondsUntilLimitReset();
        System.out.println("Sleeping now for " + s + " seconds");
        try {
          Thread.sleep(s * 1000);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  // Finds the number of calls to the REST API allowed in the current rate
  // limitation window.
  // see https://dev.twitter.com/rest/public/rate-limiting
  private int getCallsRemaining()
  {
    int callsRemaining = 0;

    try {
      RateLimitStatus limit = twitter.getRateLimitStatus("followers")
                            .get("/followers/ids");

      callsRemaining = limit.getRemaining();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    return callsRemaining;
  }

  private int getSecondsUntilLimitReset()
  {
    int secondsUntilLimitReset = 0;

    try {
      RateLimitStatus limit = twitter.getRateLimitStatus("followers")
                            .get("/followers/ids");

      secondsUntilLimitReset = limit.getSecondsUntilReset();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    return secondsUntilLimitReset;
  }

  // This method is private in order to avoid rate-limitation problems.
  private void find(long uid)
  {
    try {
      do {
        ids = twitter.getFollowersIDs(uid, cursor);
        for (long id : ids.getIDs()) {
          db.addFollower(uid, id);
        }
      } while ((cursor = ids.getNextCursor()) != 0);

      db.commit();

    } catch (TwitterException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }
}

/*  Interfaces with the Twitter streaming API using the twitter4j library.  see
 *  https://dev.twitter.com/streaming/reference/post/statuses/filter
 *  for more info on the connection.  Tweets are sent directly to an SQLite
 *  database.
 */

import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.TwitterObjectFactory;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.ConnectionLifeCycleListener;

class StatusStreamer implements Runnable
{
  private TwitterStream twitterStream;

  private SQLConnection db;

  public StatusStreamer(SQLConnection db, Configuration c)
  {
    this.db = db;
		try {
			twitterStream = new TwitterStreamFactory(c).getInstance();
    } catch (Exception e) {
			e.printStackTrace();
    }
  }
  
  @Override
  public void run()
  {
    open();
  }

  private FilterQuery setFilter()
  {
    String[] s =  { "Nazi","Ukraine","Whatsapp","Trump","Clinton","Fascist",
                    "Bernie","Hillary","Rubio","Cruz","President",
                    "Russia","Syria","Obama","Cameron","Merkel","Bitcoin",
                    "Refugee","Migrant","Refugees", "Migrants"
                  };

    FilterQuery q = new FilterQuery();

    q.language("en");
    q.track(s);

    return q;
  }

  public void open()
  { 
    Listener l = new Listener(db);
    LifeCycleListener lcl = new LifeCycleListener();
    
    twitterStream.addListener(l);
    twitterStream.addConnectionLifeCycleListener(lcl);

    FilterQuery q = setFilter();
    twitterStream.filter(q);
  }
  
  public void close()
  {
    twitterStream.cleanUp();
    twitterStream.shutdown();  
  }
}

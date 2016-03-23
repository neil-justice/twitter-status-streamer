import java.io.FileInputStream;
import java.util.Properties;

import twitter4j.conf.ConfigurationBuilder;
import twitter4j.conf.Configuration;

class DataMiner
{
  private SQLConnection db;
  private StatusStreamer streamer;
  //private FollowerFinder finder;
  private Configuration c;

  public static void main(String[] args)
  {
    DataMiner d = new DataMiner();
    d.run();
  }

  public DataMiner()
  {
    Properties prop = new Properties();

    try {
      prop.load(new FileInputStream("stream.properties"));

      ConfigurationBuilder cb = new ConfigurationBuilder();
      cb.setOAuthConsumerKey(prop.getProperty("CONSUMER_KEY"));
      cb.setOAuthConsumerSecret(prop.getProperty("CONSUMER_SECRET"));
      cb.setOAuthAccessToken(prop.getProperty("ACCESS_TOKEN"));
      cb.setOAuthAccessTokenSecret(prop.getProperty("ACCESS_TOKEN_SECRET"));

      c = cb.build();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void run()
  {
    db = new SQLConnection();
    db.open();
    closeOnShutdown();

    streamer = new StatusStreamer(db, c);
    streamer.run();
    //finder = new FollowerFinder(db, c);
    //finder.run();
  }

  // Uses a shutdown hook to close the database and stream connections on exit.
  private void closeOnShutdown()
  {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        db.close();
        streamer.close();
      }
    });
  }
}

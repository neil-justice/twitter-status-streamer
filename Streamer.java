import java.util.Properties;
import java.io.FileInputStream;

import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.TwitterObjectFactory;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.ConnectionLifeCycleListener;

class Streamer
{  
  private TwitterStream twitterStream;
	private Properties prop = new Properties();
  
  public static void main(String[] args)
  {
    Streamer s = new Streamer();
    s.open();
  }
  
  //loads properties for the Configuration object from a properties file
  public Streamer()
  {
		try {
			prop.load(new FileInputStream("stream.properties"));

			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setOAuthConsumerKey(prop.getProperty("CONSUMER_KEY"));
			cb.setOAuthConsumerSecret(prop.getProperty("CONSUMER_SECRET"));
			cb.setOAuthAccessToken(prop.getProperty("ACCESS_TOKEN"));
			cb.setOAuthAccessTokenSecret(prop.getProperty("ACCESS_TOKEN_SECRET"));
			cb.setJSONStoreEnabled(true);

			twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
      
    } catch (Exception e) {
			e.printStackTrace();
    }
  }

  private FilterQuery setFilter()
  {
    String keywordString = prop.getProperty("TWITTER_KEYWORDS");
    String[] keywords = keywordString.split(",");
    
    for (String s: keywords) {
      s = s.trim();
    }
    
    return new FilterQuery().track(keywords);
  }
  
  public void open() 
  {
    Listener l = new Listener();
    LifeCycleListener lcl = new LifeCycleListener();
    
    twitterStream.addListener(l);
    twitterStream.addConnectionLifeCycleListener(lcl);

    FilterQuery q = setFilter();
    twitterStream.filter(q);
  } 
}
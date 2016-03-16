// Listens to  a stream.  When a status (message) is recieved, adds the raw
// JSON data to the specified mongoDB database.

import java.io.File;
import java.io.FileOutputStream;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterObjectFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

class Listener implements StatusListener
{
  private String dbName = "stream";
  private String collName = "statuses";
  
  public Listener()
  {
    try {
      mongoClient = new MongoClient( "localhost" , 27017 );
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    DB db = mongoClient.getDB(dbName);
    DBCollection coll = db.getCollection(collName);
  }
  
  @Override
  public void onStatus(Status status) 
  {
    System.out.println(status.getUser().getScreenName() + ": " + status.getText());

    System.out.println("timestamp : " + 
                        String.valueOf(status.getCreatedAt().getTime()));
                      
    DBObject obj = (DBObject)JSON.parse
                   (TwitterObjectFactory.getRawJSON(status));
    coll.insert(obj);
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
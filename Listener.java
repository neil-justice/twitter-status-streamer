import java.io.File;
import java.io.FileOutputStream;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterObjectFactory;

class Listener implements StatusListener
{
  private FileOutputStream out;
  
  public Listener()
  {
    try {
      out = new FileOutputStream(new File("stream.json"), true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void shutdown()
  {
    try {
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
  }
  
  @Override
  public void onStatus(Status status) 
  {
    System.out.println(status.getUser().getScreenName() + ": " + status.getText());

    System.out.println("timestamp : " + 
                        String.valueOf(status.getCreatedAt().getTime()));
    try {
      out.write(TwitterObjectFactory.getRawJSON(status).getBytes());
    } catch (Exception e) {
      e.printStackTrace();
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
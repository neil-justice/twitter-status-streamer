import twitter4j.ConnectionLifeCycleListener;

class LifeCycleListener implements ConnectionLifeCycleListener
{
  @Override 
  public void onConnect() {}

  @Override 
  public void onDisconnect() { 
    System.out.println("Connection dropped");
  } 

  @Override 
  public void onCleanUp() {} 
} 
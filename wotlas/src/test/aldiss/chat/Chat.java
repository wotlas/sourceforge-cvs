

package chat;

import chat.common.*;

import wotlas.libs.net.*;
import java.util.Vector;

public class Chat implements NetConnectionListener
{

  private Vector members;

  public Chat(){
      members = new Vector(10);
  }
  
  public void dispatchMessage( String message ) {
      ChatServerMessage server_message = new ChatServerMessage( message );

      for( int i=0; i<members.size(); i++)
       ( ( NetPersonality ) members.get(i) ).queueMessage( server_message );

  }
  
  public void connectionCreated( NetPersonality personality ) {
     System.out.println("Chat : Connection opened");
     members.add( (Object) personality );
  }
  
  public void connectionClosed( NetPersonality personality ) {
     System.out.println("Chat : Connection closed");
     members.remove( (Object) personality );
  }


}
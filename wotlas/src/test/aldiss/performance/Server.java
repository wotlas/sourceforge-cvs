package performance;


import wotlas.libs.net.*;
import wotlas.utils.Debug;

import java.io.IOException;


public class Server extends NetServer
{

  public Server(String packages[]) {
     super( 7893, packages);
  }

  public void accessControl( NetPersonality personality, String key )
  {
      try{
         System.out.println("client accepté...");
         personality.setContext(personality);
         acceptClient( personality );
      }
      catch(IOException e) {
             Debug.signal( Debug.WARNING, this, e );
      }
  }


  public static void main( String argv[] ) {
         String packages[] = { "performance.msgserver" };
     //    Debug.setLevel(Debug.ERROR);
         Server server = new Server( packages );         
         server.start();
         System.out.println("serveur ok.");
  }

}

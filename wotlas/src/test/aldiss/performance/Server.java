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
         System.out.println("client accepted...");
         personality.setContext(personality);
         acceptClient( personality );
  }


  public static void main( String argv[] ) {
         String packages[] = { "performance.msgserver" };
     //  Debug.setLevel(Debug.ERROR);
         Server server = new Server( packages );         

     //  server.setMaximumOpenedSockets( 20 );
         server.start();
         System.out.println("server ok.");
  }

}

package chat;


import wotlas.libs.net.*;
import wotlas.utils.Debug;

import java.io.IOException;


public class Server extends NetServer
{
  private int count;
  
  private static Chat chat;

  public Server(String packages[]) {
     super( 7893, packages);
  }

  public void accessControl( NetPersonality personality, String key )
  {
      try{
         // does the client have the right password ?
            if( key.compareTo("sesame") == 0 )
            {
                 personality.setContext(chat);
                 personality.setConnectionListener(chat);
                 acceptClient( personality );

                 System.out.println("Client accepté :" +count+" key:"+key);
                 count++;
            }
            else {
                 System.out.println("Client refusé :" +count+" key:"+key);
                 refuseClient( personality, "mauvais mot de passe :"+key );
            }
      }
      catch(IOException e) {
             Debug.signal( Debug.WARNING, this, e );
      }
  }


  public static void main( String argv[] ) {
         String packages[] = { "chat.msgserver" };

         Server server = new Server( packages );
         Debug.setLevel( Debug.ERROR );
         
         chat = new Chat();
         server.start();

         System.out.println("serveur ok.");
  }

}

package chat;

import chat.gui.*;


import wotlas.libs.net.*;
import wotlas.libs.net.personality.*;
import wotlas.utils.IntroductionWindow;

import java.net.Socket;
import java.io.IOException;
import javax.swing.*;
import java.awt.event.*;


public class Client extends NetClient
{

  static NetPersonality personality;


  protected NetPersonality getNewDefaultPersonality( Socket socket )
  throws IOException {
          return new TormPersonality( socket, null );
  }


  public static void main( String argv[] ) {
     if(argv.length!=2) {
        System.out.println("Client <server-name> <password>");
        System.exit(1);
     }

     JFrame fr = new JFrame("Wheel Of Time - Light & Shadow");
     new IntroductionWindow( fr, "logo.jpg", 5000 );

  // création du chat
     ChatPanel chat = new ChatPanel( 300, 500 );

  // connexion au serveur
     Client client = new Client();
     String s[] = { "chat.msgclient" };   // la liste des messages que nous acceptons de traiter

     personality = client.connectToServer( argv[0], 7893, argv[1], chat, s );

  // Succès de la connexion ?
     if(personality!=null)
         System.out.println("server connection ok.");
     else {
         System.out.println("Error: "+client.getErrorMessage() );
         return;
     }

  // Rajout d'un listener pour gérer la connexion
     personality.setConnectionListener( chat );

  // afichage du chat
     fr.getContentPane().add( chat );
     
     fr.addWindowListener( new WindowAdapter() {
     	public void windowClosing( WindowEvent e ) {
            Client.personality.closeConnection();
          }
     });

     fr.pack();
     fr.setLocation(100,100);
     fr.show();
  }
}

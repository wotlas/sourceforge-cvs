package wotlas.client;


import wotlas.libs.net.*;
import wotlas.libs.net.personality.*;
import wotlas.common.message.account.*;

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
     if(argv.length!=3) {
        System.out.println("Client <server-name> <login> <password>");
        System.exit(1);
     }

  // connexion au serveur
     Client client = new Client();
     FalseClient falseClient = new FalseClient(argv[1],argv[2]);

     String s[] = { "wotlas.client.message.account" };

     personality = client.connectToServer( argv[0], 25500, "AccountServerPlease!", falseClient, s );

  // Succès de la connexion ?
     if(personality!=null)
         System.out.println("server connection ok.");
     else {
         System.out.println("Error: "+client.getErrorMessage() );
         return;
     }

  // Rajout d'un listener pour gérer la connexion
     personality.setConnectionListener( falseClient );

     try{
        new Object().wait();
     }catch(Exception e){
     }

  }

 ////////////////////////////////////////////////////////////////////////////////


static class FalseClient implements NetConnectionListener
{

  private NetPersonality perso;
  private String login;
  private String password;

  public FalseClient(String login, String password){
      this.login = login;
      this.password = password;
  }
  
  
  public void connectionCreated( NetPersonality personality ) {
     System.out.println("Connection opened,\nSending Login & Password");

     perso = personality;

     perso.queueMessage( new PasswordAndLoginMessage( login, password ) );

     try{
        wait( 2000 );
     }catch(Exception e){
     }

     if(perso==null)
        return;

     System.out.println("Sending account creation...");
     perso.queueMessage( new AccountCreationMessage() );

     try{
        wait();
     }catch(Exception e){
     }

     System.out.println("Press CTRL-C to quit...");
  }
  
  public void connectionClosed( NetPersonality personality ) {
     System.out.println("Connection closed");
     perso = null;
  }


}


}

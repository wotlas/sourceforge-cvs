package performance;


import wotlas.libs.net.*;

import java.net.Socket;
import java.io.IOException;
import javax.swing.*;
import java.awt.event.*;


public class Client
{

  public static int tmin[];
  public static int tavg[];
  public static int tmax[];
  public static int count;

  public static int nb_client;

  public static Object lock;

  public static void main( String argv[] )
  {
     if(argv.length!=3) {
        System.out.println("Client <server-name> <nb-client> <nb-operation>");
        System.exit(1);
     }


  // initialisation
     nb_client = Integer.parseInt(argv[1]);
     int nb_operation = Integer.parseInt(argv[2]);

     tmin = new int[nb_client];
     tavg = new int[nb_client];
     tmax = new int[nb_client];
     count=0;

     PerfThread perf[] = new PerfThread[nb_client];

     System.out.println("Création de "+nb_client+" connexions..." );


  // création du PerfThread
     NetClient client = new NetClient();

     for( int i=0; i< nb_client; i++ )
     {
         perf[i] = new PerfThread( i, nb_operation );

      // connexion au serveur
         String packages[] = { "performance.msgclient" };

         NetPersonality personality = client.connectToServer( argv[0], 7893, "", perf[i], packages );

      // Succès de la connexion ?
         if(personality!=null)
               System.out.println("connexion serveur "+i+"ok.");
         else {
               System.out.println("Error: "+client.getErrorMessage() );
               return;
         }

      // Rajout d'un listener pour gérer la connexion
         personality.setConnectionListener( perf[i] );
     }


     // on démarre tous les threads
        System.out.println("Lancement des tests de performance...");

        boolean fini = false;
     
        for( int i=0; i< nb_client; i++ )
            perf[i].start();


        lock = new Object();

        synchronized( lock ) {
          try{
              lock.wait();
          }
          catch(Exception e) {}
        }

     // Analyse des résultats...
        System.out.println("Analyse des résultats...\n");

        System.out.println("> Les Temps Minimum"); 
        System.out.println("   - min: "+PerfThread.getMin(tmin) );
        System.out.println("   - avg: "+PerfThread.getAvg(tmin) );
        System.out.println("   - max: "+PerfThread.getMax(tmin) );

        System.out.println("\n> Les Temps Moyens"); 
        System.out.println("   - min: "+PerfThread.getMin(tavg) );
        System.out.println("   - avg: "+PerfThread.getAvg(tavg) );
        System.out.println("   - max: "+PerfThread.getMax(tavg) );

        System.out.println("\n> Les Temps Maximum"); 
        System.out.println("   - min: "+PerfThread.getMin(tmax) );
        System.out.println("   - avg: "+PerfThread.getAvg(tmax) );
        System.out.println("   - max: "+PerfThread.getMax(tmax) );

System.exit(1);
  }


   synchronized static void incrCounter() {
	count++;
	if(count==nb_client)
           synchronized(lock){
	      lock.notify();
           }
   }

}

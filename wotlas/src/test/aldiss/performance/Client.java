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

     System.out.println("Creation of "+nb_client+" connection..." );


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
               System.out.println("server connection -"+i+"- ok.");
         else {
               System.out.println("Error: "+client.getErrorMessage() );
               return;
         }

      // Rajout d'un listener pour gérer la connexion
         personality.setConnectionListener( perf[i] );
     }


     // on démarre tous les threads
        System.out.println("Starting performance tests (140msg per connection)...");

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
        System.out.println("Results (Per Message Sent, Processed, Returned)...\n");

        System.out.println("> Min Times"); 
        System.out.println("   - min: "+PerfThread.getMin(tmin) );
        System.out.println("   - avg: "+PerfThread.getAvg(tmin) );
        System.out.println("   - max: "+PerfThread.getMax(tmin) );

        System.out.println("\n> Average Times"); 
        System.out.println("   - min: "+PerfThread.getMin(tavg) );
        System.out.println("   - avg: "+PerfThread.getAvg(tavg) );
        System.out.println("   - max: "+PerfThread.getMax(tavg) );

        System.out.println("\n> Max Times"); 
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



package performance;

import performance.common.*;

import wotlas.libs.net.*;
import java.io.IOException;

public class PerfThread extends Thread implements NetConnectionListener
{

  // temps des traitements
     private int t[] = new int[100];

  // nb opérations à effectuer
     private int nb_operation;

  // notre personalité
     private NetPersonality personality;

  // notre numero de thread
     private int numero_thread;

  /*------------------------------------------------------------------------*/

  // constructeur
     public PerfThread( int numero_thread, int nb_operation ){
        this.numero_thread = numero_thread;
        this.nb_operation = nb_operation;
     }
  
  /*------------------------------------------------------------------------*/

     public void run()
     {
       try{

        for( int i=0; i<140; i++ )
        {
           long t0 = System.currentTimeMillis();
           
           PerformanceClientMessage message = new PerformanceClientMessage( nb_operation );
           personality.queueMessage( message );
           personality.pleaseSendAllMessagesNow();

           personality.waitForAMessageToArrive();
           personality.pleaseReceiveAllMessagesNow();

          // sauvegarde du résultat
            if(i>=20 && i<120)
             t[i-20] = (int) ( System.currentTimeMillis()-t0 );
        }

       // Fin - transmission du résultat
          Client.tmin[numero_thread] = getMin( t );
          Client.tavg[numero_thread] = getAvg( t );
          Client.tmax[numero_thread] = getMax( t );

          Client.incrCounter();
       }
       catch(IOException e) {
       	   e.printStackTrace();
       }
     }

  /*------------------------------------------------------------------------*/

      public void connectionCreated( NetPersonality personality ) {
          this.personality = personality;
      }

  /*------------------------------------------------------------------------*/
  
     public void connectionClosed( NetPersonality personality ) {
          personality = null;
     }

  /*------------------------------------------------------------------------*/

     static public int getMin( int tab[] )
     {
        int mini = tab[0];
        
        for( int i=1; i<tab.length; i++ )
           if(tab[i]<mini)
              mini = tab[i];

        return mini;
     }

  /*------------------------------------------------------------------------*/

     static public int getMax( int tab[] )
     {
        int max = tab[0];
        
        for( int i=1; i<tab.length; i++ )
           if(tab[i]>max)
              max = tab[i];

        return max;
     }

  /*------------------------------------------------------------------------*/

     static public int getAvg( int tab[] )
     {
        int som=0;
        
        for( int i=0; i<tab.length; i++ )
              som += tab[i];

        return som/tab.length;
     }

  /*------------------------------------------------------------------------*/

     public NetPersonality getPersonality() {
         return personality;
     }

  /*------------------------------------------------------------------------*/
}
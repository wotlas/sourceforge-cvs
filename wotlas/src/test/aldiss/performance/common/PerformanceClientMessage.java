
package performance.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wotlas.libs.net.NetMessage;



public class PerformanceClientMessage extends NetMessage
{
 /*------------------------------------------------------------------------------------*/

  /** A message
   */
      protected int nb_operation;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
     public PerformanceClientMessage() {
          super( (byte)1, (byte)0 );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

     public PerformanceClientMessage( int nb_operation ) {
         this();
         this.nb_operation = nb_operation;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

     public void encode( DataOutputStream ostream ) throws IOException {
         ostream.writeInt( nb_operation );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

     public void decode( DataInputStream istream ) throws IOException {
         nb_operation = istream.readInt();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}


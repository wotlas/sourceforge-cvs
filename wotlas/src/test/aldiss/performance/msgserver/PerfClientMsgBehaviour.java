
package performance.msgserver;

import wotlas.libs.net.*;
import performance.common.*;
import java.util.Vector;

/** 
 * Associated behaviour
 *
 * @author Aldiss
 * @see wotlas.libs.net.message.ServerErrorMessage
 */

public class PerfClientMsgBehaviour extends PerformanceClientMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public PerfClientMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code
   */
     public void doBehaviour( Object context ) {
        NetPersonality personality = (NetPersonality) context;
        
        int i=0;

        while(i<nb_operation)
            i++;

        personality.queueMessage( new PerformanceServerMessage() );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}



package chat.msgserver;

import wotlas.libs.net.NetMessageBehaviour;
import chat.common.*;
import chat.*;

/** 
 * Associated behaviour
 *
 * @author Aldiss
 * @see wotlas.libs.net.message.ServerErrorMessage
 */

public class ChatClientMsgBehaviour extends ChatClientMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public ChatClientMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code
   */
     public void doBehaviour( Object context ) {
       ((Chat) context).dispatchMessage( message );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}



package chat.msgclient;

import wotlas.libs.net.NetMessageBehaviour;
import chat.common.*;
import chat.gui.*;

/** 
 * Associated behaviour
 *
 * @author Aldiss
 * @see wotlas.libs.net.message.ServerErrorMessage
 */

public class ChatServerMsgBehaviour extends ChatServerMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
     public ChatServerMsgBehaviour() {
          super();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Associated code
   */
     public void doBehaviour( Object context ) {
       ((ChatPanel) context).addChatText( message );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}


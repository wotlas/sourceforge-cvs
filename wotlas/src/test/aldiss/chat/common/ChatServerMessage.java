
package chat.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import wotlas.libs.net.NetMessage;



public class ChatServerMessage extends NetMessage
{
 /*------------------------------------------------------------------------------------*/

  /** A message
   */
      protected String message;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. Just initializes the message category and type.
   */
     public ChatServerMessage() {
          super( MessageRegistry.CHAT_CATEGORY, ChatMessageCategory.CHAT_SERVER_MSG );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with message
   *
   * @param message
   */
     public ChatServerMessage( String message ) {
         this();
         this.message = message;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

     public void encode( DataOutputStream ostream ) throws IOException {
         writeString( message, ostream );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

     public void decode( DataInputStream istream ) throws IOException {
          message = readString(istream);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}


 
package chat.common;

import wotlas.libs.net.NetMessageCategory;


public interface ChatMessageCategory extends NetMessageCategory {

       public final static byte CHAT_SERVER_MSG = 0;
       public final static byte CHAT_CLIENT_MSG = 1;
}


/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
 
package wotlas.libs.net;


import wotlas.utils.Debug;


/** This Message Factory is used to create new messages from their categoryID
 *  and typeID.
 * 
 * @author Aldiss
 * @see wotlas.libs.net.NetMessage
 * @see wotlas.libs.net.NetMessageBehaviour
 */

public class NetMessageFactory
{

 /*------------------------------------------------------------------------------------*/

  /** Default message factory (one per JVM).
   */
      private static NetMessageFactory msg_factory;


  /** MessageBehaviour Classes, classified by categoryID and typeID.
   */
      private Class msg_class[][];

 /*------------------------------------------------------------------------------------*/

  /** Private Constructor. 
   */
     private NetMessageFactory() {
         msg_factory = this;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructs a new NetMessageFactory if none exists.
   *
   * @return the created (or previouly created) factory
   */
     public static NetMessageFactory createMessageFactory()
     {
        if(msg_factory!=null)
           return msg_factory;  // there is one already

        NetMessageFactory factory = new NetMessageFactory();
        factory.loadMessageClasses();

        return factory;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Seek all the classes that implements the NetMessageBehaviour interface and 
   *  extends some kind of NetMessage. It then constructs the mg_class array
   *  with them, indexing the array by message category and message type.
   */
     private void loadMessageClasses()
     {
       // We load the Behaviour classes in a temporary array...
          Class class_list[] = NetMessageBehaviour.class.getClasses();

       // We create instances of these classes and determine the number
       // of message categories.
          NetMessage net_msg[] = new NetMessage[class_list.length];
          int max_category=-1;

          for( int i=0; i<class_list.length; i++ )
             if( class_list[i].isAssignableFrom( NetMessage.class ) ) {
             	try{
                   net_msg[i] = (NetMessage) class_list[i].newInstance();
                }
                catch( Exception e ) {
                   Debug.signal( Debug.WARNING, this, e );
                   continue;
                }

                if( max_category < net_msg[i].getMessageCategory() )
                    max_category = net_msg[i].getMessageCategory();
             }
  

          if(max_category==-1) {
              Debug.signal( Debug.ERROR, this, "No MessageBehaviour Classes Found !" );
              return;
          }


       // We count the number of message types per category
          int max_types[] = new int[max_category+1];
          
          for(int i=0; i<=max_category; i++)
              max_types[i]=-1;

           for(int i=0; i<net_msg.length; i++)
           {
              if(net_msg[i]==null)
                    continue;

              if( max_types[net_msg[i].getMessageCategory()] < net_msg[i].getMessageType() )
                    max_types[net_msg[i].getMessageCategory()] = net_msg[i].getMessageType();
           }


       // We create the final Class Array
          Class msg_class[][] = new Class[max_category+1][];

          for(int i=0; i<=max_category; i++)
             if( max_types[i]!=-1 )
                 msg_class[i] = new Class[max_types[i]+1];

          for(int i=0; i<net_msg.length; i++)
          {
              if(net_msg[i]==null)
                    continue;

              msg_class[ net_msg[i].getMessageCategory() ][ net_msg[i].getMessageType() ] = class_list[i];
          }


       // ... the dynamic load has succeed
          this.msg_class = msg_class;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the default MessageFactory.
   * 
   * @return the default NetMessageFactory, null if there is none
   *         (use createMessageFactory() to create one).
   */
     public static NetMessageFactory getDefaultMessageFactory() {
           return msg_factory;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get a new instance of a NetMessageBehaviour class from its categoryID and typeID.
   *  This method is used by the NetReceiver to reconstruct the received messages.
   * 
   * @param msg_category the message's category.
   * @param msg_type the message's type.
   * @return a new instance of the wanted NetMessageBehaviour.
   * @exception ClassNotFoundException if there is no associated class for these IDs.
   */
     public NetMessageBehaviour getNewMessageInstance( byte msg_category, byte msg_type )
     throws ClassNotFoundException, InstantiationException, IllegalAccessException
     {
         if( msg_category<0 || msg_class.length<msg_category )
              throw new ClassNotFoundException("Bad Message Category:"+msg_category);

         if( msg_type<0 || msg_class[msg_category].length<msg_type)
              throw new ClassNotFoundException("Bad Message Type:"+msg_type);

         
         return (NetMessageBehaviour) msg_class[msg_category][msg_type].newInstance();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}


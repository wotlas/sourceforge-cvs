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

import java.io.File;

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

 /*------------------------------------------------------------------------------------*/

  /** MessageBehaviour Classes, classified by categoryID and typeID.
   */
      private Class msg_class[][];

 /*------------------------------------------------------------------------------------*/

  /** To get the default MessageFactory.
   * 
   * @return the default NetMessageFactory, null if there is none
   *         (use createMessageFactory() to create one).
   */
     public static NetMessageFactory getDefaultMessageFactory() {
           return msg_factory;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructs a new NetMessageFactory if none exists. You have to give the name
   *  of the packages where we'll be able to find the NetMessageBehaviour classes.
   *
   * @param msg_package a list of packages where we can find NetMsgBehaviour Classes.
   * @return the created (or previouly created) factory
   */
     public static NetMessageFactory createMessageFactory( String msg_packages[] )
     {
        if(msg_factory!=null)
           return msg_factory;  // there is one already

        String package_list[];

     // We add the "wotlas.libs.net.message" package where
     // can be found the system behaviour messages
        if(msg_packages==null) {
            Debug.signal( Debug.WARNING, null, "No packages specified for behaviour messages!" );
            package_list = new String[1];
            package_list[0] = new String("wotlas.libs.net.message");
        }
        else {
              package_list = new String[msg_packages.length+1];
              System.arraycopy( msg_packages, 0, package_list, 0, msg_packages.length );
              package_list[msg_packages.length] = new String("wotlas.libs.net.message");
        }

     // We create the factory
        NetMessageFactory factory = new NetMessageFactory();
        factory.loadMessageClasses( package_list );

        return factory;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Private Constructor. 
   */
     private NetMessageFactory() {
         msg_factory = this;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Retrieves all the classes added using the addMessageBehaviourClass method. These
   *  classes implements the NetMessageBehaviour interface and extends some kind of
   *  NetMessage. It then constructs the mg_class array with them, indexing the array
   *  by message category and message type.
   *
   * This method is only called one time when we construct the singleton MessageFactory.
   *
   * @param package_list a list of packages where we can find NetMsgBehaviour Classes.
   */
     private void loadMessageClasses( String package_list[] )
     {
          NetMessage msg_list[] = new NetMessage[1];
     	  int nb_msg = 0;
          int max_category=-1;  // to determine the nb of categories

       // we load the classes from the different packages
       // and keep only the behaviour classes
          for(int i=0; i<package_list.length; i++)
          {
             String package_path = package_list[i].replace( '.', '/' );
             File package_files[] = new File( package_path ).listFiles();

             if( package_files==null ) {
                 Debug.signal( Debug.WARNING, this, "Empty Package : "+package_list[i] );
                 continue;
             }

             for( int j=0; j<package_files.length; j++ )
             {
                if( !package_files[j].isFile() || !package_files[j].getName().endsWith(".class") )
                    continue;

                try{
                     String name = package_files[j].getName();
                     Class cl = Class.forName( package_list[i] + '.'
                                                  + name.substring( 0, name.lastIndexOf(".class") ) );

                     if(cl.isInterface())
                           continue;

                     Object o = cl.newInstance();

                     if( !(o instanceof NetMessage) || !(o instanceof NetMessageBehaviour ) )
                         continue;

                  // is the array bigger enough ?
                     if( nb_msg>=msg_list.length ) {
                         NetMessage tmp_list[] = new NetMessage[msg_list.length+1];
                         System.arraycopy( msg_list, 0, tmp_list, 0, nb_msg );
                         msg_list = tmp_list;
                     }

                     msg_list[nb_msg] = (NetMessage) o;
                     nb_msg++;

                     if( max_category < msg_list[nb_msg-1].getMessageCategory() )
                         max_category = msg_list[nb_msg-1].getMessageCategory();
                }
                catch( Exception e ) {
                   Debug.signal( Debug.WARNING, this, e );
                }
             }
          }
  
          if(max_category==-1) {
              Debug.signal( Debug.ERROR, this, "No MessageBehaviour Classes Found !");
              return;
          }


       // We count the number of message types per category
          int max_types[] = new int[max_category+1];
          
          for(int i=0; i<=max_category; i++)
              max_types[i]=-1;

           for(int i=0; i<nb_msg; i++)
              if( max_types[msg_list[i].getMessageCategory()] < msg_list[i].getMessageType() )
                    max_types[msg_list[i].getMessageCategory()] = msg_list[i].getMessageType();


       // We create the final Class Array
          Class msg_class[][] = new Class[max_category+1][];

          for(int i=0; i<=max_category; i++)
             if( max_types[i]!=-1 )
                 msg_class[i] = new Class[max_types[i]+1];

          for(int i=0; i<nb_msg; i++)
              msg_class[ msg_list[i].getMessageCategory() ][ msg_list[i].getMessageType() ] = msg_list[i].getClass();


       // ... the dynamic load has succeed
          this.msg_class = msg_class;
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

  /** Delete current factory.
   */
     public void deleteFactory() {
     	msg_factory = null;
     	msg_class =null;
     }


 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To add a new message class to this factory. This method is for dynamic code update
   *  only ( from a maintenance console for example ). IT IS VERY IMPORTANT TO NOTE that
   *  this method is NOT synchronized. Therefore if you call it when the system is actually
   *  receiving messages you may encounter conflicts. As it was said, the best way to use
   *  this method is during a maintenance period of a few minutes ( no clients are
   *  connected, new clients are refused (see NetServer lock) ).
   *
   * @param class_name a complete class name : wotlas.server.message.MyNewMessage.
   * @return true in case of success, false otherwise.
   */
     private boolean addMessageClass( String class_name )
     {
       try
       {
         // We only work on a well created factory.
            if( msg_class == null ) {
                Debug.signal( Debug.WARNING, this, "Factory was not well created !" );
                return false;
            }

         // we try to find the given class by its name
            Class cl = Class.forName( class_name );

         // we check what we've found...
            if( cl==null ) {
                Debug.signal( Debug.WARNING, this, "Class not found !" );
                return false;
            }

            if( cl.isInterface() ) {
                Debug.signal( Debug.ERROR, this, "file was an interface !" );
                return false;
            }

         // we analyse an instance of this class...
            Object o = cl.newInstance();

            if( !(o instanceof NetMessage) || !(o instanceof NetMessageBehaviour ) ) {
                  Debug.signal( Debug.ERROR, this, "Not a right MessageBehaviour class !" );
                  return false;
            }

         // we retrieve message category & type
            int msg_category = ( (NetMessage) o).getMessageCategory();
            int msg_type = ( (NetMessage) o).getMessageType();

         // let's see if we need a msg_class reallocation ...
            if( msg_category >= msg_class.length )
            {
                 Class tmp[][] = new Class[msg_category+1][];
                 System.arraycopy( msg_class, 0, tmp, 0, msg_class.length );
                 msg_class = tmp;
            }

         // empty category ?
            if( msg_class[msg_category]==null ) {
                 msg_class[msg_category] = new Class[msg_type+1];
            }
            else if( msg_type >= msg_class[msg_category].length ) // category re-allocation ?
            {
                 Class tmp[] = new Class[msg_type+1];
                 System.arraycopy( msg_class[msg_category], 0, tmp,
                                   0, msg_class[msg_category].length );

                 msg_class[msg_category] = tmp;
            }

         // success
            msg_class[msg_category][msg_type] = cl;
            return true;
       }
       catch( Exception e ) {
           Debug.signal( Debug.ERROR, this, e );
           return false;
       }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}


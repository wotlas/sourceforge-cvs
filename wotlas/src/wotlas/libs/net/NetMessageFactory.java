/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2002 WOTLAS Team
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
import java.util.Hashtable;

import wotlas.utils.Debug;


/** For one NetMessage representing message data, there can be only one NetMessageBehaviour
 *  available on each side (local, remote). Therefore this Message Factory keeps in a table all the 
 *  message behaviour classes and their associated message data class name.
 *  We can then create message behaviour when they are wanted, given their super class name.
 * 
 * @author Aldiss
 * @see wotlas.libs.net.NetMessage
 * @see wotlas.libs.net.NetMessageBehaviour
 */

public class NetMessageFactory {

 /*------------------------------------------------------------------------------------*/

  /** Default message factory (one per default ClassLoader).
   */
     private static NetMessageFactory msgFactory;

 /*------------------------------------------------------------------------------------*/

  /** MessageBehaviour Classes, ordered by their mother class ( NetMessage child )
   */
     private Hashtable msgClasses;

 /*------------------------------------------------------------------------------------*/

  /** Static initialization.
   */
     static {
     	 msgFactory = new NetMessageFactory();
         msgFactory.addMessagePackage( "wotlas.libs.net.message" ); // system messages
     }

 /*------------------------------------------------------------------------------------*/

  /** To get the default MessageFactory.
   * @return the default NetMessageFactory
   */
     protected static NetMessageFactory getMessageFactory() {
           return msgFactory;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Protected Empty Constructor. 
   */
     protected NetMessageFactory() {
     	msgClasses = new Hashtable(50);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Adds new messages to the NetMessageFactory. You have to give the name of the packages
   *  where the message behaviour classes can be found. They are searched on the disk from
   *  current system directory.<br>
   *
   *  We don't check if the packages have already been added. Old message behaviour classes
   *  are replaced if they where already defined in the factory.
   *
   * @param packagesName a list of packages where we can find NetMsgBehaviour Classes.
   * @return the number of loaded messages.
   */
     protected int addMessagePackages( String packagesName[] ) {
         if(packagesName==null || packagesName.length==0)
            return 0;

         int nbMsg = 0;
            
         for( int i=0; i<packagesName.length; i++)
              nbMsg += addMessagePackage( packagesName[i] );
         
         return nbMsg;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Adds new messages to the NetMessageFactory. You have to give the name of the package
   *  where the message behaviour classes can be found. They are searched on the disk from
   *  current system directory.<br>
   *
   *  We don't check if the package has already been added. Old message behaviour classes
   *  are replaced if they where already defined in the factory.
   *
   * @param packageName a package name where we can find NetMsgBehaviour Classes.
   * @return the number of loaded messages.
   */
     protected int addMessagePackage( String packageName ) {

          if(packageName==null) return 0; // no packages to add

     	  int nbMsg = 0;

       // We get & load the classes of the given package
       // WE ASSUME THAT WE ARE NOT IN A JAR FILE
          File packageFiles[] = new File( packageName.replace( '.', '/' ) ).listFiles();

          if( packageFiles==null || packageFiles.length==0 ) {
              Debug.signal( Debug.WARNING, this, "Empty Package : "+packageName );
              return 0;
          }

          for( int i=0; i<packageFiles.length; i++ ) {

              if( !packageFiles[i].isFile() || !packageFiles[i].getName().endsWith(".class") )
                  continue;

           // We load the class file
              try{
                  String name = packageFiles[i].getName();
                  Class cl = Class.forName( packageName + '.'
                                            + name.substring( 0, name.lastIndexOf(".class") ) );

                  if(cl==null || cl.isInterface())
                     continue;

                  Object o = cl.newInstance();

                  if( !(o instanceof NetMessage) || !(o instanceof NetMessageBehaviour ) )
                      continue;

               // Ok, we have a valid Message Behaviour Class.
               // we check if this message behaviour already exists in our table.
               //   if( msgClasses.get( cl.getSuperclass().getName() )!=null )
               //       Debug.signal( Debug.WARNING, this, "Replacing message code :"+name );

                  msgClasses.put( cl.getSuperclass().getName(), cl );
                  nbMsg++;
              }
              catch( Exception e ) {
                  Debug.signal( Debug.WARNING, this, e );
              }
          }

          return nbMsg;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get a new instance of a NetMessageBehaviour given its super class name
   *  associated (NetMessage). This method is used by the NetReceiver to reconstruct
   *  the received messages.
   *
   * @param msgSuperClassName the message's super class name.
   * @return a new instance of the wanted NetMessageBehaviour.
   * @exception ClassNotFoundException if there is no associated class for the given name.
   * @exception InstantiationException should never occur since we instanciate the class in
   *            the addMessagePackage(s) method.
   * @exception IllegalAccessException if the class access has been secured.
   */
     public NetMessageBehaviour getNewMessageInstance( String msgSuperClassName )
     throws ClassNotFoundException, InstantiationException, IllegalAccessException {

         Class searchedClass = (Class) msgClasses.get( msgSuperClassName );

         if( searchedClass==null )
             throw new ClassNotFoundException(msgSuperClassName);

         return (NetMessageBehaviour) searchedClass.newInstance();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}


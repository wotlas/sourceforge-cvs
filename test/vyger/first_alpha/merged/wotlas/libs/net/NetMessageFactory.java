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

import java.util.Hashtable;
import wotlas.libs.net.message.ClientRegisterMsgBehaviour;
import wotlas.libs.net.message.EndOfConnectionMsgBehaviour;
import wotlas.libs.net.message.PingMsgBehaviour;
import wotlas.libs.net.message.ServerErrorMsgBehaviour;
import wotlas.libs.net.message.ServerWelcomeMsgBehaviour;
import wotlas.utils.Debug;
import wotlas.utils.Tools;

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
        NetMessageFactory.msgFactory = new NetMessageFactory();
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the default MessageFactory.
     * @return the default NetMessageFactory
     */
    protected static NetMessageFactory getMessageFactory() {
        return NetMessageFactory.msgFactory;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Protected Empty Constructor. 
     */
    protected NetMessageFactory() {
        //     	msgClasses = new Hashtable(50);
        this.msgClasses = new Hashtable(90);

        // We add system messages.
        addMessage(ServerWelcomeMsgBehaviour.class);
        addMessage(ServerErrorMsgBehaviour.class);
        addMessage(ClientRegisterMsgBehaviour.class);
        addMessage(EndOfConnectionMsgBehaviour.class);
        addMessage(PingMsgBehaviour.class);
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
    protected int addMessagePackages(String packagesName[]) {
        if (packagesName == null || packagesName.length == 0)
            return 0;

        // we search NetMessageBehaviour classes
        Class classes[] = null;

        try {
            classes = Tools.getImplementorsOf("wotlas.libs.net.NetMessageBehaviour", packagesName);
        } catch (ClassNotFoundException e) {
            Debug.signal(Debug.CRITICAL, this, e);
            return 0;
        } catch (SecurityException e) {
            Debug.signal(Debug.CRITICAL, this, e);
            return 0;
        } catch (RuntimeException e) {
            Debug.signal(Debug.ERROR, this, e);
            return 0;
        }

        if (classes == null || classes.length == 0)
            return 0;

        // We add the found classes to our list
        int nbMsg = 0;

        for (int i = 0; i < classes.length; i++)
            if (addMessage(classes[i]))
                nbMsg++;

        return nbMsg;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Adds a new NetMessageBehaviour class to our list.
     *
     * @param packageName a package name where we can find NetMsgBehaviour Classes.
     * @return true if the message has been accepted
     */
    protected boolean addMessage(Class classToAdd) {

        if (classToAdd == null || classToAdd.isInterface())
            return false;

        try {
            Object o = classToAdd.newInstance();

            if (!(o instanceof NetMessage) || !(o instanceof NetMessageBehaviour)) {
                Debug.signal(Debug.ERROR, this, "Provided class has a bad network message format ! " + classToAdd);
                return false;
            }
        } catch (Exception e) {
            Debug.signal(Debug.ERROR, this, e);
            return false;
        }

        // Ok, we have a valid Message Behaviour Class.
        this.msgClasses.put(classToAdd.getSuperclass().getName(), classToAdd);

        // Debug.signal(Debug.NOTICE, null, "Added Msg "+classToAdd);
        return true;
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
    public NetMessageBehaviour getNewMessageInstance(String msgSuperClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Class searchedClass = (Class) this.msgClasses.get(msgSuperClassName);

        if (searchedClass == null) {
            // System.out.println(" uhm wrong class, you should declare the package u'r using!");
            throw new ClassNotFoundException(msgSuperClassName);
        }

        return (NetMessageBehaviour) searchedClass.newInstance();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
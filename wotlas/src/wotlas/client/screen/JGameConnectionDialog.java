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

package wotlas.client.screen;

import wotlas.libs.net.*;
import wotlas.utils.Debug;
import wotlas.utils.Tools;

import wotlas.client.gui.JConnectionDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/** A small utility to connect to an account server using a JDialog.
 * <pre>
 *  Example : here is the case of a player of login "masterBob" whose secret password is "hulo"
 *            and player IDs are : 1 (server part) and 22 (client part)
 *
 *   NetPersonality myNetPersonality;
 *
 *   JGameConnectionDialog jconnect = new JGameConnectionDialog( frame, "myServer", 26000
 *                                                  "masterBob", "hulo", 1, 22, myDataManager );
 *   if( jconnect.hasSucceeded() ) 
 *       myNetPersonality = jconnect.getPersonality();
 *
 * </pre>
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetClient
 */

public class JGameConnectionDialog extends JConnectionDialog
{
 /*------------------------------------------------------------------------------------*/

   /** Constructor. Displays the JDialog and immediately tries to connect to the specified
    *  server. It displays eventual error messages in pop-ups.
    *  The detail of the parameters is the following :
    * 
    * @param frame frame owner of this JDialog
    * @param server server name (DNS or IP address)
    * @param port server port
    * @param login client login
    * @param password client password
    * @param localClientID ID given by the account server when the client created his player
    * @param originalServerID ID given by the account server when the client created his player
    * @param context context to set to messages ( see NetPersonality ).
    */

   public JGameConnectionDialog(Frame frame,String server,int port, String login,
                                String password, int localClientID, int originalServerID,
                                Object context) {
         super(frame, server, port, login+"-"+originalServerID+"-"
                                    +localClientID+":"+password, context);
   }

 /*------------------------------------------------------------------------------------*/

   /** To retrieve a list of the NetMessage packages to use with this server.
    */
    protected String[] getPackages() {
    	//String list[] = null; // no packages for now
      String list[] = {"wotlas.client.message.description"};
      
    	return list;
    }

 /*------------------------------------------------------------------------------------*/

}
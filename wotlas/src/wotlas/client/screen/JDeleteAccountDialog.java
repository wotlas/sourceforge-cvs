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


/** A small utility to connect to delete an account using a JDialog.
 * <pre>
 *  Example :
 *
 *   NetPersonality myNetPersonality;
 *
 *   JDeleteAccountDialog jconnect = new JDeleteAccountDialog( frame, "myServer", 
 *                                                             25000, "toto-1-23",
 *                                                             "myPassword" );
 * </pre>
 * 
 * A success message is displayed if the account has been deleted. There is no NetPersonality
 * to retrieve after the use of this dialog.
 *
 * @author Aldiss
 * @see wotlas.libs.net.NetClient
 */

public class JDeleteAccountDialog extends JConnectionDialog
{
 /*------------------------------------------------------------------------------------*/

   /** Constructor. Displays the JDialog and immediately tries to connect to the specified
    *  server. It displays eventual error messages in pop-ups.
    *  The detail of the parameters is the following :
    * 
    * @param frame frame owner of this JDialog
    * @param server server name (DNS or IP address)
    * @param port server port
    * @param accountName login+'-'+key
    * @param password
    */
   public JDeleteAccountDialog(Frame frame,String server,int port, String accountName, String password) {
         super(frame,server,port,"deleteAccount:"+accountName+":"+password,null);
   }

 /*------------------------------------------------------------------------------------*/

   /** To retrieve a list of the NetMessage packages to use with this server.
    */
    protected String[] getPackages() {
    	String list[] = { "wotlas.client.message.account" };    	
    	return list;
    }

 /*------------------------------------------------------------------------------------*/

   /** To display en error message in a pop-up.
    */
    protected void displayError( String error ) {
    	if(error.equals("Account Deleted SuccessFully.")) {
          hasSucceeded = true;
          JOptionPane.showMessageDialog( frame, error, "Success", JOptionPane.INFORMATION_MESSAGE );    	
        }
    	else
          JOptionPane.showMessageDialog( frame, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

 /*------------------------------------------------------------------------------------*/
}
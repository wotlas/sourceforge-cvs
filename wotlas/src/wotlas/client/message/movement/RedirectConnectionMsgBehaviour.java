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

package wotlas.client.message.movement;

import java.io.IOException;

import wotlas.client.*;
import wotlas.common.message.movement.*;
import wotlas.libs.net.NetMessageBehaviour;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the RedirectLeaveMapMessage...
 *
 * @author Aldiss, Petrus
 * @see wotlas.client.DataManager
 */

public class RedirectConnectionMsgBehaviour extends RedirectConnectionMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

   /** To tell if this message is to be invoked later or not.
    */
     private boolean invokeLater = true;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
  public RedirectConnectionMsgBehaviour() {
    super();
  }

 /*------------------------------------------------------------------------------------*/

  /** Associated code to this Message...
   *
   * @param sessionContext an object giving specific access to other objects needed to process
   *        this message.
   */
  public void doBehaviour( Object sessionContext ) {
      DataManager dataManager = (DataManager) sessionContext;
      PlayerImpl myPlayer = dataManager.getMyPlayer();

       if( invokeLater ) {
       	   if(primaryKey==null || !primaryKey.equals(myPlayer.getPrimaryKey()) ) {
       	   	Debug.signal(Debug.ERROR, this, "RECEIVED BAD REQUEST FOR CONNECTION !!!");
       	   	return;
       	   }

           invokeLater = false;
           dataManager.invokeLater( this );
           return;
       }

   // 1 - Freeze player
      Debug.signal( Debug.WARNING, this, "Connection Redirection : moving to another server !" );
      myPlayer.getMovementComposer().resetMovement();
      myPlayer = null;

   // 2 - We update our current client profile
      ProfileConfig currentProfile = dataManager.getCurrentProfileConfig();
      currentProfile.setServerID(remoteServerID);

      ProfileConfigList profileConfigList = ClientManager.getDefaultClientManager().getProfileConfigList();
      PersistenceManager.getDefaultPersistenceManager().saveProfilesConfig(profileConfigList);

      
   // 3 - Close current connection & wait reconnection to the new server
      ClientManager.getDefaultClientManager().setAutomaticLogin(true);
      dataManager.closeConnection();
      Debug.signal(Debug.NOTICE, this, "Connection redirection succeeded...");
  }

 /*------------------------------------------------------------------------------------*/

}


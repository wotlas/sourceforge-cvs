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

package wotlas.client.message.account;

import java.io.IOException;

import wotlas.client.DataManager;

import wotlas.common.message.account.*;
import wotlas.libs.net.NetMessageBehaviour;

/**
 * Associated behaviour to the WarningMessage...
 *
 * @author Aldiss, Petrus
 * @see wotlas.client.DataManager
 */
public class WarningMsgBehaviour extends WarningMessage implements NetMessageBehaviour
{
 /*------------------------------------------------------------------------------------*/

   /** To tell if this message is to be invoked later or not.
    */
     private boolean invokeLater = true;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
  public WarningMsgBehaviour() {
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

       if( invokeLater && dataManager.isRunning() ) {
           invokeLater = false;
           dataManager.invokeLater( this );
           return;
       }

      dataManager.showWarningMessage(info);
  }

 /*------------------------------------------------------------------------------------*/

}


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

package wotlas.client.message.description;

import wotlas.client.*;
import wotlas.client.DataManager;
import wotlas.client.screen.*;

import wotlas.common.message.description.*;

import wotlas.libs.net.NetMessageBehaviour;

import wotlas.utils.Debug;

import java.awt.*;

import java.io.IOException;


/**
 * Associated behaviour to the YourFakeNamesMessage...
 *
 * @author Petrus
 */

public class YourFakeNamesMsgBehaviour extends YourFakeNamesMessage implements NetMessageBehaviour
{

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
  public YourFakeNamesMsgBehaviour() {
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
    
    // Update of the panel
    Component c_lie = dataManager.getPlayerPanel().getTab("-lie-");
   
    if ( c_lie==null || !(c_lie instanceof LiePanel) ) {
      Debug.signal( Debug.ERROR, this, "LiePanel not found !");
      return;
    }

    LiePanel liePanel = (LiePanel) c_lie;    
    for (int i=0;i<fakeNamesLength; i++) {      
      liePanel.setFakeName(i, fakeNames[i]);
    }
    
    liePanel.setCurrentFakeName(currentFakeName);
  }

 /*------------------------------------------------------------------------------------*/

}


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

import wotlas.client.PlayerImpl;

import wotlas.utils.ALabel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** JPanel to show informations of the player
 *
 * @author Petrus
 */

public class JInfosPanel extends JPanel
{

  /** Player's short name
   */
  private ALabel lbl_playerName;
  
  /** Player's full name
   */
  private ALabel lbl_fullPlayerName;
  
  /** Player's location
   */
  private ALabel lbl_location;  
  
 /*------------------------------------------------------------------------------------*/

  public JInfosPanel(PlayerImpl player) {
    super();
    JPanel panel = new JPanel();
    panel.setOpaque(false);
    lbl_playerName = new ALabel("(" + player.getPlayerName() + ")");
    lbl_fullPlayerName = new ALabel(player.getFullPlayerName());    
    lbl_location = new ALabel();   
    panel.add(lbl_fullPlayerName);
    panel.add(lbl_playerName);
    add(panel);
  }

 /*------------------------------------------------------------------------------------*/
 
  /** Set player's short name
   */
  public void setPlayerName(String playerName) {
    lbl_playerName.setText("(" + playerName + ")");
  }
  
  /** Set player's full name
   */
  public void setFullPlayerName(String fullPlayerName) {
    lbl_fullPlayerName.setText(fullPlayerName);
  }
  
  /** Set player's location
   */
  public void setLocation(String location) {
    lbl_location.setText(location);
  }
 
 /*------------------------------------------------------------------------------------*/
 
}
/* Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import wotlas.utils.*;

/** JPanel to show the informations of the player
 *
 * @author MasterBob
 */

public class InfoPanel extends JPanel
{
  ALabel infoPlayerLabel = new ALabel();
  JTextArea playerTextArea;
  
  
  
 /*------------------------------------------------------------------------------------*/ 
  
  /** Consctructor.
   */
  public InfoPanel() {
    super();
    this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    infoPlayerLabel.setAlignmentX(0.5f);
    playerTextArea = new JTextArea(10,25);
    playerTextArea.setLineWrap(true);
    playerTextArea.setWrapStyleWord(true);    
    playerTextArea.setEditable(false);
    playerTextArea.setAlignmentX(0.5f);
    this.setText("boujour je suis un petit lutin, et j'ais tres faim");
    this.setLabelText("The Lutin");
    
    add(infoPlayerLabel);
    add(playerTextArea);
    
  }     
    
 public void setText(String text)
  {
   playerTextArea.setText(text);
  }

 public void setLabelText(String text)
  {
   infoPlayerLabel.setText(text);
  }

    
 }  
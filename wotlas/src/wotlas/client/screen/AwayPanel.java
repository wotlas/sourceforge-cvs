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
import wotlas.utils.aswing.*;

import wotlas.common.*;
import wotlas.common.message.description.PlayerAwayMessage;
import wotlas.client.*;

/** JPanel where to enter some text that will be displayed when you are not connected to the game.
 *
 * @author MasterBob, Aldiss
 */

public class AwayPanel extends JPanel
{
  private ATextArea playerTextArea;

  private AButton savePastButton;
  
 /*------------------------------------------------------------------------------------*/ 
  
  /** Constructor.
   */
  public AwayPanel() {
    super();
    this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

      ATextArea ta_infos = new ATextArea("Leave here a message that will be displayed when you are not connected:");
      ta_infos.setLineWrap(true);
      ta_infos.setWrapStyleWord(true);
      ta_infos.setEditable(false);
      ta_infos.setAlignmentX(0.5f);
      ta_infos.setOpaque(false);
      add(ta_infos);

      playerTextArea = new ATextArea(10,25);
      playerTextArea.setLineWrap(true);
      playerTextArea.setWrapStyleWord(true);    
      playerTextArea.setEditable(true);
      playerTextArea.setAlignmentX(0.5f);
      PlayerImpl player = DataManager.getDefaultDataManager().getMyPlayer();
      playerTextArea.setText(player.getPlayerAwayMessage());
      add(new JScrollPane(playerTextArea));    

      ImageIcon im_saveup  = new ImageIcon("../base/gui/save-up.gif");
      ImageIcon im_savedo  = new ImageIcon("../base/gui/save-do.gif");
      savePastButton = new AButton(im_saveup);
      savePastButton.setRolloverIcon(im_savedo);
      savePastButton.setPressedIcon(im_savedo);
      savePastButton.setBorderPainted(false);
      savePastButton.setContentAreaFilled(false);
      savePastButton.setFocusPainted(false);
      savePastButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        savePastButton.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {                    	
                PlayerImpl player = DataManager.getDefaultDataManager().getMyPlayer();

                String awayMessage = playerTextArea.getText();
                
                if( player.getPlayerAwayMessage().equals(awayMessage) )
                   return;

                if(awayMessage.length()>300) {
                   awayMessage = awayMessage.substring( 0, 299 );
                   playerTextArea.setText(awayMessage);
                }
                
                player.setPlayerAwayMessage( awayMessage );
                player.sendMessage( new PlayerAwayMessage( player.getPrimaryKey(), awayMessage ) );
            }
        });

      JPanel whitePanel = new JPanel();
      whitePanel.setBackground( Color.white );
      whitePanel.add(savePastButton);
      add(whitePanel);
  }


  /** To reset this panel.
   */
   public void reset() {
      PlayerImpl player = DataManager.getDefaultDataManager().getMyPlayer();
      playerTextArea.setText(player.getPlayerAwayMessage());
   }
 }

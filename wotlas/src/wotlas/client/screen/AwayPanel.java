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
    this.setLayout(new BorderLayout());

      ATextArea ta_infos = new ATextArea("You can enter here a message that will be displayed when you are not connected:");
      ta_infos.setLineWrap(true);
      ta_infos.setWrapStyleWord(true);
      ta_infos.setEditable(false);
      ta_infos.setAlignmentX(0.5f);
      ta_infos.setOpaque(false);
      add(ta_infos, BorderLayout.NORTH);

      JPanel centerPanel = new JPanel( new BorderLayout() );

      playerTextArea = new ATextArea(10,25);
      playerTextArea.setLineWrap(true);
      playerTextArea.setWrapStyleWord(true);    
      playerTextArea.setEditable(true);
      playerTextArea.setAlignmentX(0.5f);
      PlayerImpl player = ClientDirector.getDataManager().getMyPlayer();
      playerTextArea.setText(player.getPlayerAwayMessage());
      centerPanel.add(new JScrollPane(playerTextArea),BorderLayout.CENTER);
      add(centerPanel, BorderLayout.CENTER);

      ImageIcon im_saveup  = ClientDirector.getResourceManager().getImageIcon("save-up.gif");
      ImageIcon im_savedo  = ClientDirector.getResourceManager().getImageIcon("save-do.gif");
      savePastButton = new AButton(im_saveup);
      savePastButton.setRolloverIcon(im_savedo);
      savePastButton.setPressedIcon(im_savedo);
      savePastButton.setBorderPainted(false);
      savePastButton.setContentAreaFilled(false);
      savePastButton.setFocusPainted(false);
      savePastButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        savePastButton.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {                    	
                PlayerImpl player = ClientDirector.getDataManager().getMyPlayer();

                String awayMessage = playerTextArea.getText();
                
                if( player.getPlayerAwayMessage()!=null &&
                    player.getPlayerAwayMessage().equals(awayMessage) )
                   return;

                if(awayMessage.length()>400) {
                   awayMessage = awayMessage.substring( 0,399 );
                   playerTextArea.setText(awayMessage);
                }

                player.setPlayerAwayMessage( awayMessage );
                player.sendMessage( new PlayerAwayMessage( player.getPrimaryKey(), awayMessage ) );
            }
        });

      JPanel whitePanel = new JPanel();
      whitePanel.setBackground( Color.white );
      whitePanel.add(savePastButton);
      add(whitePanel,BorderLayout.SOUTH);
  }


  /** To reset this panel.
   */
   public void reset() {
      PlayerImpl player = ClientDirector.getDataManager().getMyPlayer();
      playerTextArea.setText(player.getPlayerAwayMessage());
   }
 }

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
import wotlas.common.message.description.PlayerPastMessage;
import wotlas.client.*;

/** JPanel to show the informations of the player
 *
 * @author MasterBob
 */

public class InfoPanel extends JPanel
{
  private ALabel infoPlayerLabel = new ALabel();
  private ATextArea playerTextArea;

  private boolean savePastButtonDisplayed  = false;

  private AButton savePastButton;
 
  private JPanel whitePanel;
  
 /*------------------------------------------------------------------------------------*/ 
  
  /** Constructor.
   */
  public InfoPanel() {
    super();
    this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    infoPlayerLabel.setAlignmentX(0.5f);
    playerTextArea = new ATextArea(10,25);
    playerTextArea.setLineWrap(true);
    playerTextArea.setWrapStyleWord(true);    
    playerTextArea.setEditable(false);
    playerTextArea.setAlignmentX(0.5f);
    this.setText("Click on a player...");
    this.setLabelText("No Player Selected");
    
    add(infoPlayerLabel);
    add(new JScrollPane(playerTextArea));    
  }
    
  public void setText(String text) {
    playerTextArea.setText(text);
  }

  public void setLabelText(String text) {
    infoPlayerLabel.setText(text);
  }

  public void setPlayerInfo( Player player ) {

    if( player==DataManager.getDefaultDataManager().getMyPlayer() ) {

       // Is there a valid past ?
          String past = player.getPlayerPast();

          if(past.length()==0 && !savePastButtonDisplayed) {
            // No past set we display the save button
                playerTextArea.setEditable(true);
                this.setLabelText("Your player's past:");
                this.setText("...");
                savePastButtonDisplayed = true;

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
                        savePastButton.setEnabled(false);
                        PlayerImpl player = DataManager.getDefaultDataManager().getMyPlayer();
                    	player.setPlayerPast( playerTextArea.getText() );
                        player.sendMessage( new PlayerPastMessage( player.getPrimaryKey(), player.getPlayerPast() ) );
                        InfoPanel.this.reset();
                        setPlayerInfo( player );
                    }
                });

                whitePanel = new JPanel();
                whitePanel.setBackground( Color.white );
                whitePanel.add(savePastButton);
                add(whitePanel);
                revalidate();
             
             return;
          }
          else if(past.length()==0)
             return;
    }
    else if(savePastButtonDisplayed)
         reset();

    setLabelText( player.getFullPlayerName() );

    setText( 
         "Nickname: "+player.getPlayerName()+"\n"+
         "Community: "+player.getWotCharacter().getCommunityName()+"\n"+
         "Rank: "+player.getWotCharacter().getCharacterRank()+"\n\n"+
         "Player Past: "+player.getPlayerPast() );
  }
    
    
  /** To reset this panel
   */
   public void reset() {

      if(savePastButtonDisplayed) {
         playerTextArea.setEditable(false);
         remove(savePastButton);
         remove(whitePanel);
         revalidate();
         savePastButton = null;
        whitePanel = null;
      }

      savePastButtonDisplayed=false;
      this.setText("Click on a player...");
      this.setLabelText("No Player Selected");
   }
   
 }  
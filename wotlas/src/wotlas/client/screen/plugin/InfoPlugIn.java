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

package wotlas.client.screen.plugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import wotlas.utils.*;
import wotlas.utils.aswing.*;

import wotlas.common.*;
import wotlas.common.message.description.PlayerPastMessage;
import wotlas.client.*;
import wotlas.client.screen.*;


/** Plug In that shows information on the selected player and enables the
 *  local player to set his/her past.
 *
 * @author MasterBob
 */

public class InfoPlugIn extends JPanelPlugIn {

 /*------------------------------------------------------------------------------------*/

  /** Player Name Label
   */
    private ALabel infoPlayerLabel = new ALabel();

  /** Information Text Area
   */
    private ATextArea playerTextArea;

  /** Can we show the 'save' button ?
   */
    private boolean savePastButtonDisplayed  = false;

  /** Save Button & its panel
   */
    private AButton savePastButton;
    private JPanel whitePanel;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public InfoPlugIn() {
       super();
       setLayout(new BorderLayout());

       infoPlayerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
       infoPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);

       playerTextArea = new ATextArea(10,25);
       playerTextArea.setLineWrap(true);
       playerTextArea.setWrapStyleWord(true);    
       playerTextArea.setEditable(false);
       playerTextArea.setAlignmentX(0.5f);

       setText("Click on a player...");
       setLabelText("No Player Selected");

       add(infoPlayerLabel,BorderLayout.NORTH);
       add(new JScrollPane(playerTextArea),BorderLayout.CENTER);
    }

 /*------------------------------------------------------------------------------------*/

  /** Called once to initialize the plug-in.
   *  @return if true we display the plug-in, return false if something fails during
   *          this init(), this way the plug-in won't be displayed.
   */
    public boolean init() {
       return true; // nothing special to init...
    }

 /*------------------------------------------------------------------------------------*/

  /** Called when we need to reset the content of this plug-in.
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
       setText("Click on a player...");
       setLabelText("No Player Selected");
    }

 /*------------------------------------------------------------------------------------*/

  /** To set the information text.
   */
    protected void setText(String text) {
       playerTextArea.setText(text);
    }

 /*------------------------------------------------------------------------------------*/

  /** To set the label text content.
   */
    protected void setLabelText(String text) {
       infoPlayerLabel.setText(text);
    }

 /*------------------------------------------------------------------------------------*/

  /** To set the player info given a player.
   */
    public void setPlayerInfo( Player player ) {

      if( player==ClientDirector.getDataManager().getMyPlayer() ) {

       // Is there a valid past ?
          String past = player.getPlayerPast();

          if(past.length()==0 && !savePastButtonDisplayed) {
            // No past set we display the save button
                playerTextArea.setEditable(true);
                this.setLabelText("Your player's past:");
                this.setText("...");
                savePastButtonDisplayed = true;

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
                        savePastButton.setEnabled(false);
                        PlayerImpl player = ClientDirector.getDataManager().getMyPlayer();
                    	player.setPlayerPast( playerTextArea.getText() );
                        player.sendMessage( new PlayerPastMessage( player.getPrimaryKey(), player.getPlayerPast() ) );
                        InfoPlugIn.this.reset();
                        setPlayerInfo( player );
                    }
                });

                whitePanel = new JPanel();
                whitePanel.setBackground( Color.white );
                whitePanel.add(savePastButton);
                add(whitePanel,BorderLayout.SOUTH);
                revalidate();
             
             return;
          }
          else if(past.length()==0)
             return;
      }
      else if(savePastButtonDisplayed)
         reset();

      setLabelText( player.getFullPlayerName(null) );

      if( player!=ClientDirector.getDataManager().getMyPlayer() )
         setText(   
            "Community: "+player.getWotCharacter().getCommunityName()+"\n"+
            "Rank: "+player.getWotCharacter().getCharacterRank()+"\n\n"+
            "Player Past: "+player.getPlayerPast() );
      else
         setText(
            "Nickname: "+player.getPlayerName()+"\n"+
            "Community: "+player.getWotCharacter().getCommunityName()+"\n"+
            "Rank: "+player.getWotCharacter().getCharacterRank()+"\n\n"+
            "Player Past: "+player.getPlayerPast() );
    }

 /*------------------------------------------------------------------------------------*/

   /** Returns the name of the plug-in that will be displayed in the JPlayerPanel.
    * @return a short name for the plug-in
    */
      public String getPlugInName() {
      	  return "Info";
      }

 /*------------------------------------------------------------------------------------*/

   /** Returns the name of the plug-in's author.
    * @return author name.
    */
      public String getPlugInAuthor() {
          return "Wotlas Team (MasterBob)";
      }

 /*------------------------------------------------------------------------------------*/

   /** Returns the tool tip text that will be displayed in the JPlayerPanel.
    * @return a short tool tip text
    */
      public String getToolTipText() {
          return "Selected Player Information";
      }

 /*------------------------------------------------------------------------------------*/

   /** Eventual index in the list of JPlayerPanels
    * @return -1 if the plug-in has to be added at the end of the plug-in list,
    *         otherwise a positive integer for a precise location.
    */
      public int getPlugInIndex() {
          return 0;
      }

 /*------------------------------------------------------------------------------------*/

   /** Tells if this plug-in is a system plug-in that represents some base
    *  wotlas feature.
    * @return true means system plug-in, false means user plug-in
    */
      public boolean isSystemPlugIn() {
      	  return true;
      }

 /*------------------------------------------------------------------------------------*/
   
 }  
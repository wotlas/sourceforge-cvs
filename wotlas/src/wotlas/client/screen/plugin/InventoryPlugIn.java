/* Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
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

package wotlas.client.screen.plugin;

import wotlas.client.*;
import wotlas.client.screen.*;
import wotlas.common.objects.inventories.*;
import wotlas.libs.aswing.*;
import wotlas.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;


/** Plug In that shows inventory of the player.
 *
 * @author Petrus
 */

public class InventoryPlugIn extends JPanelPlugIn {

 /*------------------------------------------------------------------------------------*/

    private InventoryLayout inventoryLayout;
    private JPanel whitePanel;
    private Image background;
    private BufferedImage dstIm;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
    public InventoryPlugIn() {
       super();
       setLayout(new BorderLayout());
              
       PlayerImpl player = ClientDirector.getDataManager().getMyPlayer();
       inventoryLayout = InventoryLayout.load(player.getWotCharacter());
       System.out.println(player.getWotCharacter().getClass().getName());
       ScreenRectangle slot = inventoryLayout.getBodySlot();              
       System.out.println(slot);       
       
       // We load the image
       MediaTracker mediaTracker = new MediaTracker(this);
       background = ClientDirector.getResourceManager().getGuiImage("menu.jpg");
       mediaTracker.addImage(background,0);
       try {
         mediaTracker.waitForAll(); // wait for all images to be in memory
       } catch(InterruptedException e){
         e.printStackTrace();
       }
       
       
       dstIm = new BufferedImage( background.getWidth(this), background.getHeight(this), BufferedImage.TYPE_INT_ARGB );
       System.out.println(background.getWidth(this));
       System.out.println(background.getHeight(this));
       Graphics2D g = dstIm.createGraphics();
       g.drawImage(background, 0, 0, null);
       
       if (slot != null) {
        for (int x=slot.getX();x<slot.getX()+slot.getWidth(); x++) {
            for (int y=slot.getY();y<slot.getY()+slot.getHeight(); y++) {
                dstIm.setRGB(x,y,dstIm.getRGB(x,y)+10);
            }
        }
       
        
       }
       
       /*JPanel centerPanel = new JPanel( new BorderLayout() ) {
         public void paintComponent(Graphics g) {
           g.drawImage(background,0,0,this);
         };
       };
       add(centerPanel, BorderLayout.CENTER);
       */
                    
    }

 /*------------------------------------------------------------------------------------*/
     
    public void paintComponent(Graphics g) {
        if (dstIm!=null)
            g.drawImage(dstIm,0,0,this);
    };

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

       
    }

 
 /*------------------------------------------------------------------------------------*/

   /** Returns the name of the plug-in that will be displayed in the JPlayerPanel.
    * @return a short name for the plug-in
    */
      public String getPlugInName() {
      	  return "Inventory";
      }

 /*------------------------------------------------------------------------------------*/

   /** Returns the name of the plug-in's author.
    * @return author name.
    */
      public String getPlugInAuthor() {
          return "Wotlas Team (Petrus)";
      }

 /*------------------------------------------------------------------------------------*/

   /** Returns the tool tip text that will be displayed in the JPlayerPanel.
    * @return a short tool tip text
    */
      public String getToolTipText() {
          return "Player Inventory";
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
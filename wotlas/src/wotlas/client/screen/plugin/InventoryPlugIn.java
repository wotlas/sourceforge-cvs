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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import wotlas.client.ClientDirector;
import wotlas.client.PlayerImpl;
import wotlas.client.screen.JPanelPlugIn;
import wotlas.common.objects.BaseObject;
import wotlas.common.objects.inventories.InventoryLayout;
import wotlas.libs.graphics2D.ImageIdentifier;
import wotlas.libs.graphics2D.ImageLibrary;
import wotlas.utils.ScreenRectangle;

/** Plug In that shows inventory of the player.
 *
 * @author Petrus
 */

// TODO:
// - find width & height of plugin
public class InventoryPlugIn extends JPanelPlugIn {

    /*------------------------------------------------------------------------------------*/

    private InventoryLayout inventoryLayout;
    private JPanel whitePanel;
    private Image background;
    private BufferedImage dstIm;
    private int width;
    private int height;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public InventoryPlugIn() {
        super();
        setLayout(new BorderLayout());
        setOpaque(false);
        setBackground(Color.black);

        this.width = 200;
        this.height = 300;
        this.dstIm = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = this.dstIm.createGraphics();

        PlayerImpl player = ClientDirector.getDataManager().getMyPlayer();
        this.inventoryLayout = InventoryLayout.load(player.getWotCharacter());

        ScreenRectangle slot;

        // We load the background        
        ImageLibrary imLib = ClientDirector.getDataManager().getImageLibrary();
        BufferedImage background = imLib.getImage(this.inventoryLayout.getBackgroundId());
        g.drawImage(background, 0, 0, null);

        // Debug
        BaseObject bObject = new BaseObject();
        String path[] = { "players-0", "symbols-2", "aes-sedai-symbols-0", "amyrlin-symbol-0.gif" };
        ImageIdentifier im = new ImageIdentifier(path);
        bObject.setInventoryPicture(im);
        setHeadArmor(bObject);
        // Fin debug

        //drawSlot(dstIm, inventoryLayout.getBagSlot());               
        drawSlot(this.dstIm, this.inventoryLayout.getBeltSlot());
        drawSlot(this.dstIm, this.inventoryLayout.getBodySlot());
        //drawSlot(dstIm, inventoryLayout.getBookSlot());               
        drawSlot(this.dstIm, this.inventoryLayout.getFeetSlot());
        drawSlot(this.dstIm, this.inventoryLayout.getHeadSlot());
        drawSlot(this.dstIm, this.inventoryLayout.getLeftBootSlot());
        drawSlot(this.dstIm, this.inventoryLayout.getLeftHandSlot());
        drawSlot(this.dstIm, this.inventoryLayout.getRightBootSlot());
        drawSlot(this.dstIm, this.inventoryLayout.getRightHandSlot());
        //drawSlot(dstIm, inventoryLayout.getPurseSlot());               
        //drawSlot(dstIm, inventoryLayout.getSleeveSlot());     

    }

    /*------------------------------------------------------------------------------------*/

    @Override
    public void paintComponent(Graphics g) {
        if (this.dstIm != null)
            g.drawImage(this.dstIm, 0, 0, this);
    };

    /*------------------------------------------------------------------------------------*/

    /** Called once to initialize the plug-in.
     *  @return if true we display the plug-in, return false if something fails during
     *          this init(), this way the plug-in won't be displayed.
     */
    @Override
    public boolean init() {
        return true; // nothing special to init...
    }

    /*------------------------------------------------------------------------------------*/

    /** Called when we need to reset the content of this plug-in.
     */
    @Override
    public void reset() {

    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the name of the plug-in that will be displayed in the JPlayerPanel.
     * @return a short name for the plug-in
     */
    @Override
    public String getPlugInName() {
        return "Inventory";
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the name of the plug-in's author.
     * @return author name.
     */
    @Override
    public String getPlugInAuthor() {
        return "Wotlas Team (Petrus)";
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the tool tip text that will be displayed in the JPlayerPanel.
     * @return a short tool tip text
     */
    @Override
    public String getToolTipText() {
        return "Player Inventory";
    }

    /*------------------------------------------------------------------------------------*/

    /** Eventual index in the list of JPlayerPanels
     * @return -1 if the plug-in has to be added at the end of the plug-in list,
     *         otherwise a positive integer for a precise location.
     */
    @Override
    public int getPlugInIndex() {
        return 0;
    }

    /*------------------------------------------------------------------------------------*/

    /** Tells if this plug-in is a system plug-in that represents some base
     *  wotlas feature.
     * @return true means system plug-in, false means user plug-in
     */
    @Override
    public boolean isSystemPlugIn() {
        return true;
    }

    /*------------------------------------------------------------------------------------*/

    /** Draws a slot on a background picture
     * @param dstIm background picture
     * @param slot slot to draw
     */
    private void drawSlot(BufferedImage dstIm, ScreenRectangle slot) {
        if (slot != null) {
            Graphics2D g = dstIm.createGraphics();
            g.drawRect(slot.getX(), slot.getY(), slot.getWidth(), slot.getHeight());
            /*for (int x=slot.getX();x<slot.getX()+slot.getWidth(); x++) {
              dstIm.setRGB(x,slot.getY(),Color.red.getRGB());
              dstIm.setRGB(x,slot.getY()+slot.getHeight(),Color.red.getRGB());
            }
            for (int y=slot.getY();y<slot.getY()+slot.getHeight(); y++) {
              dstIm.setRGB(slot.getX(),y,Color.red.getRGB());  
              dstIm.setRGB(slot.getX()+slot.getWidth(),y,Color.red.getRGB());  
            }*/
        }
    }

    /** Draws an object in a slot     
     * @param slot slot to fill
     * @param bObject wotlas base object
     */
    private void setSlot(ScreenRectangle slot, BaseObject bObject) {
        if (slot != null) {
            unsetSlot(slot);
            ImageIdentifier imId = bObject.getInventoryPicture();
            ImageLibrary imLib = ClientDirector.getDataManager().getImageLibrary();
            BufferedImage buffIm = imLib.getImage(imId);
            if (buffIm != null) {
                Graphics2D g = this.dstIm.createGraphics();
                g.drawImage(imLib.getImage(imId), slot.getX(), slot.getY(), this);
            }
        }
    }

    /** Clears a slot
     * @param slot to clear
     */
    private void unsetSlot(ScreenRectangle slot) {
        if (slot != null) {
            Graphics2D g = this.dstIm.createGraphics();

        }
    }

    /*------------------------------------------------------------------------------------*/

    /**
     * Inventory Interface implementation
     */

    /*------------------------------------------------------------------------------------*/

    /** Set the body armor.
     * @param bodyArmor the new body armor
     */
    public void setBodyArmor(BaseObject bObject) {
        setSlot(this.inventoryLayout.getBodySlot(), bObject);
    }

    /** Set the head armor.
     * @param headArmor the new head armor
     */
    public void setHeadArmor(BaseObject bObject) {
        setSlot(this.inventoryLayout.getHeadSlot(), bObject);
    }

    /** Set the heavy weapon.
     * @param heavyWeapon the new heavy weapon
     */
    public void setHeavyWeapon(BaseObject bObject) {
    }

    /** Set the bow.
     * @param bow the new bow
     */
    public void setBow(BaseObject bObject) {
        ;
    }

    /** Set the belt weapon.
     * @param beltWeapon the new belt weapon
     */
    public void setBeltWeapon(BaseObject bObject) {
        setSlot(this.inventoryLayout.getBeltSlot(), bObject);
    }

    /** Set the weapon hidden in right sleeve.
     * @param rightSleeveWeapon the new weapon hidden in right sleeve
     */
    public void setRightSleeveWeapon(BaseObject bObject) {
        ;
    }

    /** Set the weapon hidden in left sleeve.
     * @param leftSleeveWeapon the new weapon hidden in left sleeve
     */
    public void setLeftSleeveWeapon(BaseObject bObject) {
        ;
    }

    /** Set the weapon hidden in right boot.
     * @param rightBootWeapon the new weapon hidden in right boot
     */
    public void setRightBootWeapon(BaseObject bObject) {
        setSlot(this.inventoryLayout.getRightBootSlot(), bObject);
    }

    /** Set the weapon hidden in left boot.
     * @param leftBootWeapon the new weapon hidden in left boot
     */
    public void setLeftBootWeapon(BaseObject bObject) {
        setSlot(this.inventoryLayout.getLeftBootSlot(), bObject);
    }

    /** Set the purse.
     * @param purse the new purse
     */
    public void setPurse(BaseObject bObject) {
        setSlot(this.inventoryLayout.getPurseSlot(), bObject);
    }

    /** Set the bag.
     * @param bag the new bag
     */
    public void setBag(BaseObject bObject) {
        setSlot(this.inventoryLayout.getBagSlot(), bObject);
    }

    /** Set the belt.
     * @param belt the new belt
     */
    public void setBelt(BaseObject bObject) {
        setSlot(this.inventoryLayout.getBeltSlot(), bObject);
    }

    /** Set the object ready for right hand.
     * @param rightObject the new object ready for right hand
     */
    public void setRightObject(BaseObject bObject) {
        setSlot(this.inventoryLayout.getRightHandSlot(), bObject);
    }

    /** Set the object ready for left hand.
     * @param leftObject the new object ready for left hand
     */
    public void setLeftObject(BaseObject bObject) {
        setSlot(this.inventoryLayout.getLeftHandSlot(), bObject);
    }

    /** Set the book.
     * @param book the new book
     */
    public void setBook(BaseObject bObject) {
        setSlot(this.inventoryLayout.getBookSlot(), bObject);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
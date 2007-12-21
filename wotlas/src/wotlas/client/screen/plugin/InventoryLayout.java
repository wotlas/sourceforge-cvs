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

package wotlas.client.screen.plugin;

import java.io.File;
import wotlas.client.ClientDirector;
import wotlas.common.ResourceManager;
import wotlas.common.character.WotCharacter;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.utils.Debug;
import wotlas.utils.ScreenRectangle;

/** An Inventory Layout to postion the inventory slots
 *
 * @author Petrus 
 */

public class InventoryLayout {

    private ScreenRectangle bagSlot;
    private ScreenRectangle beltSlot;
    private ScreenRectangle bodySlot;
    private ScreenRectangle bookSlot;
    private ScreenRectangle feetSlot;
    private ScreenRectangle headSlot;
    private ScreenRectangle leftBootSlot;
    private ScreenRectangle leftHandSlot;
    private ScreenRectangle purseSlot;
    private ScreenRectangle rightBootSlot;
    private ScreenRectangle rightHandSlot;
    private ScreenRectangle sleeveSlot;

    private ImageIdentifier backgroundId;

    /** Empty Constructor for persistence.
     * Data is loaded by the PersistenceManager.
     */
    public InventoryLayout() {
        super();
    }

    public ScreenRectangle getBagSlot() {
        return this.bagSlot;
    }

    public void setBagSlot(ScreenRectangle rect) {
        this.bagSlot = rect;
    }

    public ScreenRectangle getBeltSlot() {
        return this.beltSlot;
    }

    public void setBeltSlot(ScreenRectangle rect) {
        this.beltSlot = rect;
    }

    public ScreenRectangle getBodySlot() {
        return this.bodySlot;
    }

    public void setBodySlot(ScreenRectangle rect) {
        this.bodySlot = rect;
    }

    public ScreenRectangle getBookSlot() {
        return this.bookSlot;
    }

    public void setBookSlot(ScreenRectangle rect) {
        this.bookSlot = rect;
    }

    public ScreenRectangle getFeetSlot() {
        return this.feetSlot;
    }

    public void setFeetSlot(ScreenRectangle rect) {
        this.feetSlot = rect;
    }

    public ScreenRectangle getHeadSlot() {
        return this.headSlot;
    }

    public void setHeadSlot(ScreenRectangle rect) {
        this.headSlot = rect;
    }

    public ScreenRectangle getLeftBootSlot() {
        return this.leftBootSlot;
    }

    public void setLeftBootSlot(ScreenRectangle rect) {
        this.leftBootSlot = rect;
    }

    public ScreenRectangle getLeftHandSlot() {
        return this.leftHandSlot;
    }

    public void setLeftHandSlot(ScreenRectangle rect) {
        this.leftHandSlot = rect;
    }

    public ScreenRectangle getPurseSlot() {
        return this.purseSlot;
    }

    public void setPurseSlot(ScreenRectangle rect) {
        this.purseSlot = rect;
    }

    public ScreenRectangle getRightBootSlot() {
        return this.rightBootSlot;
    }

    public void setRightBootSlot(ScreenRectangle rect) {
        this.rightBootSlot = rect;
    }

    public ScreenRectangle getRightHandSlot() {
        return this.rightHandSlot;
    }

    public void setRightHandSlot(ScreenRectangle rect) {
        this.rightHandSlot = rect;
    }

    public ScreenRectangle getSleeveSlot() {
        return this.sleeveSlot;
    }

    public void setSleeveSlot(ScreenRectangle rect) {
        this.sleeveSlot = rect;
    }

    public ImageIdentifier getBackgroundId() {
        return this.backgroundId;
    }

    public void setBackgroundId(ImageIdentifier imId) {
        this.backgroundId = imId;
    }

    /*------------------------------------------------------------------------------------*/

    /** To save this client configuration.
     */
    public boolean save(WotCharacter character) {
        ResourceManager rManager = ClientDirector.getResourceManager();

        if (!rManager.saveObject(this, rManager.getLayoutsDir() + character.getClass().getName())) {
            Debug.signal(Debug.ERROR, null, "Failed to save WotCharacter Layout.");
            return false;
        }

        return true;
    }

    /*------------------------------------------------------------------------------------*/

    /** To load the default client configuration.
     */
    public static InventoryLayout load(WotCharacter character) {
        ResourceManager rManager = ClientDirector.getResourceManager();
        String fileName = rManager.getLayoutsDir() + character.getClass().getName();
        InventoryLayout cfg = null;

        if (new File(fileName).exists()) {
            cfg = (InventoryLayout) rManager.loadObject(fileName);
        }

        if (cfg == null) {
            Debug.signal(Debug.ERROR, null, "Failed to load WotCharacter Layout. Creating a new one.");
            return new InventoryLayout();
        }

        return cfg;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

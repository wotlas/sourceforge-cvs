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

package wotlas.common.objects.inventories;

import wotlas.client.ClientDirector;

import wotlas.common.character.*;
import wotlas.common.ResourceManager;

import wotlas.libs.graphics2D.ImageIdentifier;

import wotlas.utils.Debug;
import wotlas.utils.ScreenRectangle;

import java.io.*;

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

  /** Empty Constructor for persitence.
   * Data is loaded by the PersistenceManager.
   */
  public InventoryLayout() {  
  }
  
  public ScreenRectangle getBagSlot() {
    return bagSlot;
  }
  public void setBagSlot(ScreenRectangle rect) {
    bagSlot = rect;
  }
  
  public ScreenRectangle getBeltSlot() {
    return beltSlot;
  }
  public void setBeltSlot(ScreenRectangle rect) {
    beltSlot = rect;
  }
  
  public ScreenRectangle getBodySlot() {
    return bodySlot;
  }
  public void setBodySlot(ScreenRectangle rect) {
    bodySlot = rect;
  }
    
  public ScreenRectangle getBookSlot() {
    return bookSlot;
  }
  public void setBookSlot(ScreenRectangle rect) {
    bookSlot = rect;
  }
  
  public ScreenRectangle getFeetSlot() {
    return feetSlot;
  }
  public void setFeetSlot(ScreenRectangle rect) {
    feetSlot = rect;
  }
  
  public ScreenRectangle getHeadSlot() {
    return headSlot;
  }
  public void setHeadSlot(ScreenRectangle rect) {
    headSlot = rect;
  }  
  
  public ScreenRectangle getLeftBootSlot() {
    return leftBootSlot;
  }
  public void setLeftBootSlot(ScreenRectangle rect) {
    leftBootSlot = rect;
  }
  
  public ScreenRectangle getLeftHandSlot() {
    return leftHandSlot;
  }
  public void setLeftHandSlot(ScreenRectangle rect) {
    leftHandSlot = rect;
  }
  
  public ScreenRectangle getPurseSlot() {
    return purseSlot;
  }
  public void setPurseSlot(ScreenRectangle rect) {
    purseSlot = rect;
  }
     
  public ScreenRectangle getRightBootSlot() {
    return rightBootSlot;
  }
  public void setRightBootSlot(ScreenRectangle rect) {
    rightBootSlot = rect;
  }
  
  public ScreenRectangle getRightHandSlot() {
    return rightHandSlot;
  }
  public void setRightHandSlot(ScreenRectangle rect) {
    rightHandSlot = rect;
  }
  
  public ScreenRectangle getSleeveSlot() {
    return sleeveSlot;
  }
  public void setSleeveSlot(ScreenRectangle rect) {
    sleeveSlot = rect;
  }  
  
  public ImageIdentifier getBackgroundId() {
    return backgroundId;
  }
  
  public void setBackgroundId(ImageIdentifier imId) {
    backgroundId = imId;
  }

 /*------------------------------------------------------------------------------------*/ 

  /** To save this client configuration.
   */
     public boolean save(BasicChar character) {
     	ResourceManager rManager = ClientDirector.getResourceManager();
     	
        if( !rManager.saveObject(this, rManager.getLayoutsDir()+character.getClass().getName() ) ) {
            Debug.signal( Debug.ERROR, null, "Failed to save BasicChar Layout.");
            return false;
        }
        
        return true;
     }

 /*------------------------------------------------------------------------------------*/ 

  /** To load the default client configuration.
   */
     public static InventoryLayout load(BasicChar character) {
     	ResourceManager rManager = ClientDirector.getResourceManager();
        String fileName = rManager.getLayoutsDir()+character.getClass().getName();
        InventoryLayout cfg = null;

         if( new File(fileName).exists() ) {
            cfg = (InventoryLayout) rManager.loadObject(fileName);
         }
         
         if(cfg==null) {
            Debug.signal( Debug.ERROR, null, "Failed to load BasicChar Layout. Creating a new one." );
            return new InventoryLayout();
         }
         
         

         return cfg;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

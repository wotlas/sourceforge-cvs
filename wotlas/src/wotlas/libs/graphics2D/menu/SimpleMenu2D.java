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

package wotlas.libs.graphics2D.menu;

import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.Point;

/** A simple 2D menu. Contains menu items and a grapical representation.
 *
 * @author Aldiss
 */

public class SimpleMenu2D implements Menu2D {

 /*------------------------------------------------------------------------------------*/

  /** Maximum number of items displayed at the same time. If the menu has more
   *  items than this limit, arrows are displayed to navigate in the list.
   */
    public static final int MAX_ITEMS_DISPLAYED = 10;

  /** Time to wait between two mouse move automatic navigation (ms)
   */
    public static final int MIN_TIME_BETWEEN_MOVE = 400;

 /*------------------------------------------------------------------------------------*/

  /** Menu name.
   */
    protected String name;

  /** Menu Items
   */
    protected SimpleMenu2DItem items[];

  /** Index of the currently selected item
   */
    protected int selectedItemIndex;

  /** Index of the first item displayed on top of the list ( used if the list
   *  contains too many items to be displayed )
   */
    protected int firstItemIndex;

  /** Is this menu visible ?
   */
    protected boolean isVisible;

  /** Our menu manager
   */
    protected Menu2DManager menuManager;

  /** Our graphical representation
   */
    protected SimpleMenu2DDrawable menuDrawable;

  /** TimeStamp for automatic actions
   */
    protected long timeStamp;

 /*------------------------------------------------------------------------------------*/

  /** Constructor with menu name.
   */
     public SimpleMenu2D( String name ) {
     	this( name, null );
     }

 /*------------------------------------------------------------------------------------*/

  /** Constructor with menu name and items to display.
   */
     public SimpleMenu2D( String name, String items[] ) {
     	this.name = name;
     	setItems( items );
     	selectedItemIndex = -1;
     	firstItemIndex = 0;
     	isVisible = false;
     	menuDrawable = new SimpleMenu2DDrawable(this);
     }

 /*------------------------------------------------------------------------------------*/

  /** To initialize this menu.
   */
     public void init( Menu2DManager menuManager ) {
         this.menuManager = menuManager;

         if(items!=null)
            for( int i=0; i<items.length; i++ )
                 if( items[i].link !=null )
                     items[i].link.init( menuManager );
     }

 /*------------------------------------------------------------------------------------*/

  /** To initialize the content of this menu. The list of items replace the previous
   *  one.
   */
     public void setItems( String itemsName[] ) {
       // previous items ?
         if( items!=null )
            for( int i=0; i<items.length; i++ )
                 if( items[i].link !=null )
                     items[i].link.hide();

       // items check
         if( itemsName==null ) {
            items = new SimpleMenu2DItem[0];
            return;
         }

         items = new SimpleMenu2DItem[itemsName.length];

         for( int i=0; i<itemsName.length; i++ )
              items[i] = new SimpleMenu2DItem( itemsName[i] );

       // we refresh the state of our drawable...
         if(menuDrawable!=null)
            menuDrawable.refreshState();
     }

 /*------------------------------------------------------------------------------------*/

  /** To get the item name at the specified index.
   * @return null if not found, the itemname otherwise
   */
     public String getItemName( int index) {
     	 if( index<0 || index>=items.length )
     	     return null;

         return items[index].itemName;
     }

 /*------------------------------------------------------------------------------------*/

  /** To change the name of an item.
   * @param oldItemName item to search & update
   * @param newItemName new name to set
   * @return true if the item was found, false if not found
   */
     public boolean changeItemName( String oldItemName, String newItemName ) {
         // we search the item index
            for( int i=0; i<items.length; i++ )
                 if( items[i].itemName.equals(oldItemName) ) {

                     if( items[i].link!=null && items[i].link.isVisible() )
                         items[i].link.hide();
                 
                     items[i].itemName = newItemName;
                     menuDrawable.refreshState();
                     return true;
                 }

            return false; // not found
     }

 /*------------------------------------------------------------------------------------*/

  /** To enable/disable an item.
   * @param itemName to search and to enable/disable
   * @param enabled true to enable, false to disable.
   * @return true if the item was found, false if not found
   */
     public boolean setEnabled( String itemName, boolean enabled ) {
         // we search the item index
            for( int i=0; i<items.length; i++ )
                 if( items[i].itemName.equals(itemName) ) {
                 
                     if( items[i].isEnabled && !enabled && items[i].link!=null
                         && items[i].link.isVisible() )
                         items[i].link.hide();
                 
                     items[i].isEnabled = enabled;
                     return true;
                 }

            return false; // not found
     }

 /*------------------------------------------------------------------------------------*/

  /** To remove a menu link on an item.
   * @param itemName to search and remove the link on
   * @return true if the item was found & the menu removed, false if not found
   */
     public boolean removeLink( String itemName ) {
         // we search the item index
            for( int i=0; i<items.length; i++ )
                 if( items[i].itemName.equals(itemName) ) {
                     if( items[i].link!=null && items[i].link.isVisible() )
                         items[i].link.hide();

                     items[i].link = null;
                     return true;
                 }

            return false; // not found
     }

 /*------------------------------------------------------------------------------------*/

  /** To add a menu link to an item. The link replaces any previous link.
   * @param itemName to search and add the link on
   * @param menu2D to add
   * @return true if the item was found & the menu added, false if not found
   */
     public boolean addLink( String itemName, SimpleMenu2D menu2D ) {
         // we search the item index
            for( int i=0; i<items.length; i++ )
                 if( items[i].itemName.equals(itemName) ) {
                     if( items[i].link!=null && items[i].link.isVisible() )
                         items[i].link.hide();

                     menu2D.init(menuManager); // we init the sub-menu
                     items[i].link = menu2D;
                     return true;
                 }

            return false; // not found
     }

 /*------------------------------------------------------------------------------------*/

  /** To show this menu2D at the specified screen point.
   * @param p screen point
   */
     public void show( Point p ) {
         menuDrawable.getRectangle().x = p.x;
         menuDrawable.getRectangle().y = p.y;

         if(!isVisible && menuManager!=null) {
            menuDrawable.animateMenu();
            menuManager.getGraphicsDirector().addDrawable( menuDrawable );
            isVisible=true;
         }
     }

 /*------------------------------------------------------------------------------------*/

  /** To hide this menu2D.
   */
     public void hide() {
         if(isVisible && menuManager!=null) {
            menuManager.getGraphicsDirector().removeDrawable( menuDrawable );
            isVisible=false;
            selectedItemIndex=-1;
            firstItemIndex=0;

            hideSubMenus();
         }
     }

 /*------------------------------------------------------------------------------------*/

  /** To hide this menu's sub-menus.
   */
     protected void hideSubMenus() {
       // We hide all the other sub-menus
          for( int i=0; i<items.length; i++ )
               if( items[i].link !=null )
                   items[i].link.hide();
     }

 /*------------------------------------------------------------------------------------*/

  /** Tells if this menu is visible on screen.
   * @return true if it's visible, false otherwise.
   */
     public boolean isVisible() {
        return isVisible;
     }

 /*------------------------------------------------------------------------------------*/

  /** To get the menu name.
   * @return the menu's name.
   */
     public String getName() {
       return name;
     }

 /*------------------------------------------------------------------------------------*/

  /** To find a menu by its name. The search is performed also among sub-menus.
   * @param menuName the menu's name to search
   * @return the menu if found, null if not.
   */
     public Menu2D findByName(String menuName) {
     	if(menuName.equals(name))
     	   return this;

       // search among sub-menus
         for( int i=0; i<items.length; i++ )
              if( items[i].link !=null ) {
              	  Menu2D menu = items[i].link.findByName(menuName);
              	  if(menu!=null) return menu;
              }

         return null; // not found
     }

 /*------------------------------------------------------------------------------------*/

  /** To process a mouse event.
   * @param mEvent the mouse event received on this menu
   * @return true if the event was processed by us or one of our sub-menus, false
   *         if the event was not for us.
   */
    public boolean mouseClicked( MouseEvent mEvent ) {
        if(!isVisible)
           return false;

      // is it a click on one of our sub-menus ?    
         int maxIndex = items.length;
        
         if(maxIndex>MAX_ITEMS_DISPLAYED)
            maxIndex = firstItemIndex + MAX_ITEMS_DISPLAYED;

         for( int i=firstItemIndex; i<maxIndex; i++ )
              if( items[i].link !=null && items[i].link.mouseClicked(mEvent) )
                  return true; // one of our sub-menus received the event

      // is the event for us ?
         if( menuDrawable.contains( mEvent.getX(), mEvent.getY() ) ) {
           // ok the event is for us !
           // we ask which item is selected...
             int index = menuDrawable.getItemAt( mEvent.getY() );
             
             if(index<firstItemIndex || maxIndex<=index ) return true;

           // need to update the first index displayed ?
             if( index==firstItemIndex && firstItemIndex!=0 ) {
                 firstItemIndex--; // backward navigation
                 hideSubMenus();
                 return true;
             }
             
             if( index==maxIndex-1 && index<items.length-1 ) {
                 firstItemIndex++; // forward navigation
                 hideSubMenus();
                 return true;
             }

             if( items[index].itemName.equals("-") ) return true;

             if( !items[index].isEnabled ) return true;

           // item is a link
             if( items[index].link!=null ) {

                 if( !items[index].link.isVisible() ) {
                     hideSubMenus();

                  // We display the newly selected menu
                     int x = menuDrawable.getX()+menuDrawable.getWidth()+1;
                     int y = menuDrawable.getItemY(index);

                     items[index].link.menuDrawable.setParentRectangle( menuDrawable.getRectangle() );
                     items[index].link.show( new Point(x,y) );
                     return true;
                 }
                 else {
                     items[index].link.hide();
                     return true; // we hid the linked menu
                 }
             }

           // ok, we have a click on an item !
           // we generate an event on our menu listeners.
             hideSubMenus();
             menuManager.dispatchEvent( new Menu2DEvent( this, items[index].itemName ) );
             return true;
         }
         
         return false;
    }

 /*------------------------------------------------------------------------------------*/

  /** To call when the mouse cursor is moved.
   * @param x mouse's x
   * @param y mouse's y
   */
    public boolean mouseMoved( int x, int y ) {

         if(!isVisible)
            return false;

      // is it a click on one of our sub-menus ?
         int maxIndex = items.length;
        
         if(maxIndex>MAX_ITEMS_DISPLAYED)
            maxIndex = firstItemIndex + MAX_ITEMS_DISPLAYED;

         for( int i=firstItemIndex; i<maxIndex; i++ )
              if( items[i].link !=null && items[i].link.mouseMoved( x, y ) )
                  return true; // one of our sub-menus received the event

      // is the event for us ?
         long now = System.currentTimeMillis();

         if( menuDrawable.contains( x, y ) ) {
             selectedItemIndex = menuDrawable.getItemAt( y );

             if( selectedItemIndex==firstItemIndex && firstItemIndex!=0 ) {

                 if(now-timeStamp < MIN_TIME_BETWEEN_MOVE)
                    return true;

                 timeStamp = now;
                 firstItemIndex--; // backward navigation
                 hideSubMenus();
             }
             else if( selectedItemIndex==maxIndex-1 && selectedItemIndex<items.length-1 ) {

                 if(now-timeStamp < MIN_TIME_BETWEEN_MOVE)
                    return true;

                 timeStamp = now;
                 firstItemIndex++; // forward navigation
                 hideSubMenus();
             }

             return true;
         }

         return false;
    }

 /*------------------------------------------------------------------------------------*/

  /** To get the available items.
   * @return available items a null length array if none.
   */
    protected SimpleMenu2DItem[] getItems() {
       return items;
    }

 /*------------------------------------------------------------------------------------*/

  /** To get the currently selected index
   * @return index
   */
    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

 /*------------------------------------------------------------------------------------*/

  /** To get the index of the first item displayed.
   * @return index
   */
    public int getFirstItemIndex() {
        return firstItemIndex;
    }

 /*------------------------------------------------------------------------------------*/

}

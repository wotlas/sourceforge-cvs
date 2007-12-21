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

  /** Time to wait before automatic scrolling (ms)
   */
    public static final int MIN_TIME_BEFORE_SCROLL = 2000;

  /** Time to wait between two mouse move automatic navigation (ms)
   */
    public static final int MIN_TIME_BETWEEN_SCROLL = 200;

  /** Min Time before we display a menu
   */
    public static final int MIN_TIME_BEFORE_MENU = 50;

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

  /** Is the first index an arrow ?
   */
    protected boolean isFirstIndexArrow;

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

  /** Can we automatically scroll ?
   */
    protected boolean scrollEnabled;

  /** Initial position when this menu is being dragged.
   */
    protected int dragFromX, dragFromY;

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
        isFirstIndexArrow = false;
     	isVisible = false;
     	scrollEnabled=false;
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
     public String getItemName( int index ) {
     	 if( index<0 || index>=items.length )
     	     return null;

         return items[index].itemName;
     }

 /*------------------------------------------------------------------------------------*/

  /** To get an item's index given its name.
   * @return -1 if not found, the item's index otherwise
   */
     public int getItemIndex( String itemName ) {
         // we search the item's index
            for( int i=0; i<items.length; i++ )
                 if( items[i].itemName.equals(itemName) )
                     return i;

          return -1; // not found
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

  /** To add an item to the list. This method is not synchronized.
   * @param itemName name of the item to add
   */
     public void addItem( String itemName ) {
       // any sub-menus too hide ?
         if( items!=null )
            for( int i=0; i<items.length; i++ )
                 if( items[i].link !=null )
                     items[i].link.hide();

       // items update
          SimpleMenu2DItem tmp[];
       
         if( items==null )
            tmp = new SimpleMenu2DItem[1];
         else
            tmp = new SimpleMenu2DItem[items.length+1];

         System.arraycopy( items, 0, tmp, 0, tmp.length-1 );
         tmp[tmp.length-1] = new SimpleMenu2DItem(itemName);

       // we refresh the state of our drawable...
         items = tmp;
         menuDrawable.refreshState();
     }

 /*------------------------------------------------------------------------------------*/

  /** To remove an item from the list. This method is not synchronized.
   * @param itemName name of the item to remove
   * @return true if removed, false if not found
   */
     public boolean removeItem( String itemName ) {

       // Item index
          int ind = getItemIndex(itemName);

          if( ind<0 )
             return false;
 
       // any sub-menus to hide ?
         if( items!=null )
            for( int i=0; i<items.length; i++ )
                 if( items[i].link !=null )
                     items[i].link.hide();

       // items update
          SimpleMenu2DItem tmp[] = new SimpleMenu2DItem[items.length-1];

          System.arraycopy( items, 0, tmp, 0, ind );

          if(ind+1<items.length)
            System.arraycopy( items, ind+1, tmp, ind, items.length-ind-1 );

       // we refresh the state of our drawable...
          items = tmp;
          menuDrawable.refreshState();
          return true;
     }

 /*------------------------------------------------------------------------------------*/

  /** To enable/disable an item.
   * @param itemName to search and to enable/disable
   * @param enabled true to enable, false to disable.
   * @return true if the item was found, false if not found
   */
     public boolean setItemEnabled( String itemName, boolean enabled ) {
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

  /** To add a menu link to an item. The link replaces any previous link.
   * @param itemName to search and add the link on
   * @param menu2D to add (beware ! MUST be a SimpleMenu2D here)
   * @return true if the item was found & the menu added, false if not found
   */
     public boolean addItemLink( String itemName, Menu2D menu2D ) {
         // we search the item index
            for( int i=0; i<items.length; i++ )
                 if( items[i].itemName.equals(itemName) ) {
                     if( items[i].link!=null && items[i].link.isVisible() )
                         items[i].link.hide();

                     menu2D.init(menuManager); // we init the sub-menu
                     items[i].link = (SimpleMenu2D) menu2D;
                     return true;
                 }

            return false; // not found
     }

 /*------------------------------------------------------------------------------------*/

  /** To remove a menu link on an item.
   * @param itemName to search and remove the link on
   * @return true if the item was found & the menu removed, false if not found
   */
     public boolean removeItemLink( String itemName ) {
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

  /** To show this menu2D at the specified screen point.
   * @param p screen point
   */
     public void show( Point p ) {
         menuDrawable.setNextPosition( p.x, p.y );

         if(!isVisible && menuManager!=null) {
            menuDrawable.animateMenu();
            menuDrawable.tick();
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
            isFirstIndexArrow=false;
            scrollEnabled = false;

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
             if( index==firstItemIndex && ( firstItemIndex!=0 || isFirstIndexArrow ) ) {
                 hideSubMenus();

                 if( isFirstIndexArrow && firstItemIndex==0 )
                    isFirstIndexArrow = false;
                 else
                    firstItemIndex--; // backward navigation

                 return true;
             }
             
             if( index==maxIndex-1 && index<items.length-1 ) {
                 hideSubMenus();

                 if( !isFirstIndexArrow && firstItemIndex==0 )
                    isFirstIndexArrow = true;
                 else
                    firstItemIndex++; // forward navigation

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
                     timeStamp = System.currentTimeMillis()+1000; // to avoid our menu to re-appear
                     return true; // we hide the linked menu
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

             if(selectedItemIndex<firstItemIndex || maxIndex<=selectedItemIndex ) return true;

             if( selectedItemIndex==firstItemIndex && ( firstItemIndex!=0 ||
                 isFirstIndexArrow ) ) {

                 if(!scrollEnabled && now-timeStamp < MIN_TIME_BEFORE_SCROLL)
                    return true;

                 if(now-timeStamp < MIN_TIME_BETWEEN_SCROLL)
                    return true;

                 scrollEnabled=true;
                 timeStamp = now;
                 hideSubMenus();

                 if( isFirstIndexArrow && firstItemIndex==0 )
                    isFirstIndexArrow = false;
                 else
                    firstItemIndex--; // backward navigation

                 return true;
             }

             if( selectedItemIndex==maxIndex-1 && selectedItemIndex<items.length-1 ) {

                 if(!scrollEnabled && now-timeStamp < MIN_TIME_BEFORE_SCROLL)
                    return true;

                 if(now-timeStamp < MIN_TIME_BETWEEN_SCROLL)
                    return true;

                 scrollEnabled=true;
                 timeStamp = now;
                 hideSubMenus();

                 if( !isFirstIndexArrow && firstItemIndex==0 )
                    isFirstIndexArrow = true;
                 else
                    firstItemIndex++; // forward navigation

                 return true;
             }

             scrollEnabled=false; // cancel scrolling

             if( items[selectedItemIndex].itemName.equals("-") ) return true;
             if( !items[selectedItemIndex].isEnabled ) return true;

           // item is a link
             if( items[selectedItemIndex].link!=null ) {

                 if( !items[selectedItemIndex].link.isVisible() ) {

                     if(now-timeStamp < MIN_TIME_BEFORE_MENU)
                        return true;

                     hideSubMenus();

                  // We display the newly selected menu
                     int xi = menuDrawable.getX()+menuDrawable.getWidth()+1;
                     int yi = menuDrawable.getItemY(selectedItemIndex);

                     items[selectedItemIndex].link.menuDrawable.setParentRectangle( menuDrawable.getRectangle() );
                     items[selectedItemIndex].link.show( new Point(xi,yi) );
                 }
             }

             timeStamp = now; // reset time-stamp
             return true;
         }

         return false;
    }

 /*------------------------------------------------------------------------------------*/

  /** To call when the mouse cursor is dragged.
   * @param dx mouse's dx
   * @param dy mouse's dy
   * @param startsNow tells if the drag movement is just about to start
   */
    public boolean mouseDragged( int dx, int dy, boolean startsNow ) {

         if(!isVisible)
            return false;

         for( int i=0; i<items.length; i++ )
               if( items[i].link !=null )
                   items[i].link.mouseDragged(dx,dy,startsNow);

         if(startsNow) {
            dragFromX = menuDrawable.getX();
            dragFromY = menuDrawable.getY();
            return true;
         }

         menuDrawable.setNextPosition( dragFromX + dx, dragFromY + dy );
         return true;
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

  /** To tell if the first item to display is an arrow.
   * @return index
   */
    public boolean isFirstIndexArrow() {
        return isFirstIndexArrow;
    }

 /*------------------------------------------------------------------------------------*/
}

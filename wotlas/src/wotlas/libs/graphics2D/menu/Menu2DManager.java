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

import wotlas.libs.graphics2D.GraphicsDirector;

import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.*;

/** A Menu2D Manager manages a tree of menus. The root menu can be displayed using
 *  show/hide methods... you also have to send the mouse events the menus can try to
 *  process by invoking the mouseClicked() method.
 *
 * @author Aldiss
 */

public class Menu2DManager {

 /*------------------------------------------------------------------------------------*/

   /** Our GraphicsDirector.
    */
     private GraphicsDirector gDirector;

   /** Our Listeners.
    */
     private ArrayList listeners;

   /** Our root menu.
    */
     private Menu2D rootMenu;

 /*------------------------------------------------------------------------------------*/

   /** Constructor.
    */
     public Menu2DManager() {
     	listeners = new ArrayList(3);
     }

 /*------------------------------------------------------------------------------------*/

   /** Constructor with root menu.
    * @param rootMenu root menu of our tree.
    */
     public Menu2DManager( Menu2D rootMenu ) {
        setRootMenu( rootMenu );
     }

 /*------------------------------------------------------------------------------------*/

  /** To initialize this menu manager.
   */
     public void init( GraphicsDirector gDirector ) {
       this.gDirector = gDirector;
     }

 /*------------------------------------------------------------------------------------*/

  /** To get our GraphicsDirector.
   */
     public GraphicsDirector getGraphicsDirector() {
     	return gDirector;
     }

 /*------------------------------------------------------------------------------------*/

  /** To show the root menu at the specified screen point.
   * @param p screen point
   */
     public void show( Point p ) {
        if(rootMenu!=null)
           rootMenu.show( p );
     }

 /*------------------------------------------------------------------------------------*/

  /** To hide the root menu.
   */
     public void hide() {
        if(rootMenu!=null)
           rootMenu.hide();
     }

 /*------------------------------------------------------------------------------------*/

  /** Tells if the root menu is visible.
   * @return true if it's visible, false otherwise.
   */
     public boolean isVisible() {
        if(rootMenu!=null)
           return rootMenu.isVisible();
        return false;
     }

 /*------------------------------------------------------------------------------------*/

  /** To get the root menu.
   * @return the root menu.
   */
     public Menu2D getRootMenu() {
        return rootMenu;
     }

 /*------------------------------------------------------------------------------------*/

  /** To set the root menu.
   * @return the root menu.
   */
     public void setRootMenu( Menu2D rootMenu ) {
       // hide previous menu
          if(this.rootMenu!=null)
             this.rootMenu.hide();

          this.rootMenu = rootMenu;
          rootMenu.init( this );
     }

 /*------------------------------------------------------------------------------------*/

  /** To find a menu by its name. The search is performed also among sub-menus.
   * @param menuName the menu's name to search
   * @return the menu if found, null if not.
   */
     public Menu2D findByName(String menuName) {
        if(rootMenu!=null)
           return rootMenu.findByName(menuName);
        return null;
     }

 /*------------------------------------------------------------------------------------*/

  /** To add a Menu2DEvent listener to this menu manager.
   */
     public synchronized void addMenu2DListener( Menu2DListener listener ) {
     	listeners.add( listener );
     }

 /*------------------------------------------------------------------------------------*/

  /** To remove a Menu2DEvent listener from this menu manager.
   *  @return true if the listener was removed, false otherwise
   */
     public synchronized boolean removeMenu2DListener( Menu2DListener listener ) {
        int index = listeners.indexOf( listener );
        
        if(index>=0) {
           listeners.remove( index );
           return true;
        }

        return false;
     }

 /*------------------------------------------------------------------------------------*/

  /** To dispatch a menu event among our listeners.
   *
   * @param e the menu event to dispatch
   */
     protected synchronized void dispatchEvent( Menu2DEvent e ) {
        Iterator it = listeners.iterator();

          while( it.hasNext() )
                ( (Menu2DListener) it.next() ).menuItemClicked( e );
     }

 /*------------------------------------------------------------------------------------*/

  /** To process a mouse event that has been received on the parent component
   *  of the Menu2DManager. It's your task to call this method to connect
   *  external events to the menu.
   *
   * @param mEvent the mouse event received on this menu
   * @return true if the event was processed by us or one of our sub-menus, false
   *         if the event was not for us.
   */
     public boolean mouseClicked( MouseEvent mEvent ) {
        if(rootMenu!=null)
           return rootMenu.mouseClicked(mEvent);
        return false;
     }

 /*------------------------------------------------------------------------------------*/

  /** To call when the mouse cursor is moved.
   * @param x mouse's x
   * @param y mouse's y
   */
    public void mouseMoved( int x, int y ) {
        if(rootMenu!=null)
           rootMenu.mouseMoved(x,y);
    }

 /*------------------------------------------------------------------------------------*/

  /** To call when the mouse cursor is dragged.
   * @param dx mouse's dx
   * @param dy mouse's dy
   * @param startsNow tells if the drag movement is just about to start
   */
    public void mouseDragged( int dx, int dy, boolean startsNow ) {
        if(rootMenu==null)
           return;

          rootMenu.mouseDragged(dx,dy,startsNow);
    }

 /*------------------------------------------------------------------------------------*/

}

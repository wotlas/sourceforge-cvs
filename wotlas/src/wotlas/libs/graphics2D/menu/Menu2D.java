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
import java.awt.Point;

/** General definition of a graphical 2D menu.
 *
 * @author Aldiss
 */

public interface Menu2D {

 /*------------------------------------------------------------------------------------*/

  /** To initialize this menu.
   */
     public void init( Menu2DManager menuManager );

 /*------------------------------------------------------------------------------------*/

  /** To show this menu2D at the specified screen point.
   * @param p screen point
   */
     public void show( Point p );

 /*------------------------------------------------------------------------------------*/

  /** To hide this menu2D.
   */
     public void hide();

 /*------------------------------------------------------------------------------------*/

  /** Tells if this menu is visible on screen.
   * @return true if it's visible, false otherwise.
   */
     public boolean isVisible();

 /*------------------------------------------------------------------------------------*/

  /** To get the menu name.
   * @return the menu's name.
   */
     public String getName();

 /*------------------------------------------------------------------------------------*/

  /** To find a menu by its name. The search is performed also among sub-menus.
   * @param menuName the menu's name to search
   * @return the menu if found, null if not.
   */
     public Menu2D findByName(String menuName);

 /*------------------------------------------------------------------------------------*/

  /** To process a mouse event.
   * @param mEvent the mouse event received on this menu
   * @return true if the event was processed by us or one of our sub-menus, false
   *         if the event was not for us.
   */
     public boolean mouseClicked( MouseEvent mEvent );

 /*------------------------------------------------------------------------------------*/

  /** To call when the mouse cursor is moved.
   * @param x mouse's x
   * @param y mouse's y
   */
    public boolean mouseMoved( int x, int y );

 /*------------------------------------------------------------------------------------*/

}

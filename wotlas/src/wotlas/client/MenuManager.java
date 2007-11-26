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

package wotlas.client;

import wotlas.libs.graphics2D.GraphicsDirector;
import wotlas.libs.graphics2D.menu.Menu2D;
import wotlas.libs.graphics2D.menu.Menu2DManager;
import wotlas.libs.graphics2D.menu.SimpleMenu2D;

/** Manages all the game menus the user can access by right-clicking on game objects, players, etc ...
 * @author Aldiss
 * @see wotlas.libs.graphics2D.menu.Menu2DManager
 */

public class MenuManager extends Menu2DManager {

    /*------------------------------------------------------------------------------------*/

    /** MAIN MENU NAME
     */
    final public static String MAIN_MENU_NAME = "Main Menu";

    /** OBJECT ITEM NAME
     */
    final public static String OBJECT_ITEM_NAME = "Object";

    /** KNOWLEDGE ITEM NAME
     */
    final public static String KNOWLEDGE_ITEM_NAME = "Knowledge";

    /** WEAVE ITEM NAME
     */
    final public static String WEAVE_ITEM_NAME = "Weave";

    /** DESCRIPTION ITEM NAME
     */
    final public static String DESCRIPTION_ITEM_NAME = "Description";

    /** Selected Object Item Index
     */
    final public static int SELECTED_OBJECT_ITEM_INDEX = 1;

    /** MAIN MENU - Player/Object Selected
     */
    final public static String MAIN_MENU_ITEMS[] = { "Current Selection :", "", "-", MenuManager.OBJECT_ITEM_NAME, MenuManager.WEAVE_ITEM_NAME, MenuManager.KNOWLEDGE_ITEM_NAME, "-", MenuManager.DESCRIPTION_ITEM_NAME, };

    /*------------------------------------------------------------------------------------*/

    /** Our Player.
     */
    private PlayerImpl player;

    /*------------------------------------------------------------------------------------*/

    /** Constructor with current player and gDirector.
     */
    public MenuManager(PlayerImpl player, GraphicsDirector gDirector) {
        super();
        this.player = player;
        init(gDirector);

        SimpleMenu2D mainMenu = new SimpleMenu2D(MenuManager.MAIN_MENU_NAME, MenuManager.MAIN_MENU_ITEMS);
        setRootMenu(mainMenu);
    }

    /*------------------------------------------------------------------------------------*/

    /** To Initialize the menus with the current player selection.
     */
    public void initContent(PlayerImpl target) {

        this.rootMenu.changeItemName(this.rootMenu.getItemName(MenuManager.SELECTED_OBJECT_ITEM_INDEX), target.getFullPlayerName());

        this.rootMenu.setItemEnabled(MenuManager.KNOWLEDGE_ITEM_NAME, true);
        this.rootMenu.setItemEnabled(MenuManager.OBJECT_ITEM_NAME, true);
        this.rootMenu.setItemEnabled(MenuManager.WEAVE_ITEM_NAME, true);
        this.rootMenu.setItemEnabled(MenuManager.DESCRIPTION_ITEM_NAME, true);

        // transmit the call on player's ObjectManager & WeaveManager

        // tmp code to display empty menus :
        this.rootMenu.addItemLink(MenuManager.OBJECT_ITEM_NAME, getEmptyMenu());
        this.rootMenu.addItemLink(MenuManager.WEAVE_ITEM_NAME, getEmptyMenu());
        this.rootMenu.addItemLink(MenuManager.KNOWLEDGE_ITEM_NAME, getEmptyMenu());
        this.rootMenu.setItemEnabled(MenuManager.DESCRIPTION_ITEM_NAME, false);
    }

    /*------------------------------------------------------------------------------------*/

    /** To Initialize the menus with the current object selection.
     *
      public void initContent( BaseObject object ) {
      }
     */

    /*------------------------------------------------------------------------------------*/

    /** To Initialize the menus with the current player selection.
     */
    public void initNoContent() {
        this.rootMenu.changeItemName(this.rootMenu.getItemName(MenuManager.SELECTED_OBJECT_ITEM_INDEX), "none");

        this.rootMenu.setItemEnabled(MenuManager.KNOWLEDGE_ITEM_NAME, false);
        this.rootMenu.setItemEnabled(MenuManager.OBJECT_ITEM_NAME, true);
        this.rootMenu.setItemEnabled(MenuManager.WEAVE_ITEM_NAME, false);
        this.rootMenu.setItemEnabled(MenuManager.DESCRIPTION_ITEM_NAME, false);

        // transmit the call on player's ObjectManager & WeaveManager

        // tmp code to display empty menus :        
        //  rootMenu.addItemLink( OBJECT_ITEM_NAME, getEmptyMenu() );
    }

    /*------------------------------------------------------------------------------------*/

    /** To clear the content of this manager.
     */
    public void clear() {
        this.player = null;
        this.gDirector = null;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get an empty menu.
     */
    public Menu2D getEmptyMenu() {
        String empty[] = { "empty" };
        SimpleMenu2D emptyMenu = new SimpleMenu2D("emptyMenu", empty);
        emptyMenu.setItemEnabled("empty", false);
        return emptyMenu;
    }

    /*------------------------------------------------------------------------------------*/
}

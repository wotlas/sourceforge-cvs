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

package wotlas.libs.graphics2d.menu;

import java.awt.Point;
import java.awt.event.MouseEvent;

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
    public SimpleMenu2D(String name) {
        this(name, null);
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with menu name and items to display.
     */
    public SimpleMenu2D(String name, String items[]) {
        this.name = name;
        setItems(items);
        this.selectedItemIndex = -1;
        this.firstItemIndex = 0;
        this.isFirstIndexArrow = false;
        this.isVisible = false;
        this.scrollEnabled = false;
        this.menuDrawable = new SimpleMenu2DDrawable(this);
    }

    /*------------------------------------------------------------------------------------*/

    /** To initialize this menu.
     */
    public void init(Menu2DManager menuManager) {
        this.menuManager = menuManager;

        if (this.items != null)
            for (int i = 0; i < this.items.length; i++)
                if (this.items[i].link != null)
                    this.items[i].link.init(menuManager);
    }

    /*------------------------------------------------------------------------------------*/

    /** To initialize the content of this menu. The list of items replace the previous
     *  one.
     */
    public void setItems(String itemsName[]) {
        // previous items ?
        if (this.items != null)
            for (int i = 0; i < this.items.length; i++)
                if (this.items[i].link != null)
                    this.items[i].link.hide();

        // items check
        if (itemsName == null) {
            this.items = new SimpleMenu2DItem[0];
            return;
        }

        this.items = new SimpleMenu2DItem[itemsName.length];

        for (int i = 0; i < itemsName.length; i++)
            this.items[i] = new SimpleMenu2DItem(itemsName[i]);

        // we refresh the state of our drawable...
        if (this.menuDrawable != null)
            this.menuDrawable.refreshState();
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the item name at the specified index.
     * @return null if not found, the itemname otherwise
     */
    public String getItemName(int index) {
        if (index < 0 || index >= this.items.length)
            return null;

        return this.items[index].itemName;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get an item's index given its name.
     * @return -1 if not found, the item's index otherwise
     */
    public int getItemIndex(String itemName) {
        // we search the item's index
        for (int i = 0; i < this.items.length; i++)
            if (this.items[i].itemName.equals(itemName))
                return i;

        return -1; // not found
    }

    /*------------------------------------------------------------------------------------*/

    /** To change the name of an item.
     * @param oldItemName item to search & update
     * @param newItemName new name to set
     * @return true if the item was found, false if not found
     */
    public boolean changeItemName(String oldItemName, String newItemName) {
        // we search the item index
        for (int i = 0; i < this.items.length; i++)
            if (this.items[i].itemName.equals(oldItemName)) {

                if (this.items[i].link != null && this.items[i].link.isVisible())
                    this.items[i].link.hide();

                this.items[i].itemName = newItemName;
                this.menuDrawable.refreshState();
                return true;
            }

        return false; // not found
    }

    /*------------------------------------------------------------------------------------*/

    /** To add an item to the list. This method is not synchronized.
     * @param itemName name of the item to add
     */
    public void addItem(String itemName) {
        // any sub-menus too hide ?
        if (this.items != null)
            for (int i = 0; i < this.items.length; i++)
                if (this.items[i].link != null)
                    this.items[i].link.hide();

        // items update
        SimpleMenu2DItem tmp[];

        if (this.items == null)
            tmp = new SimpleMenu2DItem[1];
        else
            tmp = new SimpleMenu2DItem[this.items.length + 1];

        System.arraycopy(this.items, 0, tmp, 0, tmp.length - 1);
        tmp[tmp.length - 1] = new SimpleMenu2DItem(itemName);

        // we refresh the state of our drawable...
        this.items = tmp;
        this.menuDrawable.refreshState();
    }

    /*------------------------------------------------------------------------------------*/

    /** To remove an item from the list. This method is not synchronized.
     * @param itemName name of the item to remove
     * @return true if removed, false if not found
     */
    public boolean removeItem(String itemName) {

        // Item index
        int ind = getItemIndex(itemName);

        if (ind < 0)
            return false;

        // any sub-menus to hide ?
        if (this.items != null)
            for (int i = 0; i < this.items.length; i++)
                if (this.items[i].link != null)
                    this.items[i].link.hide();

        // items update
        SimpleMenu2DItem tmp[] = new SimpleMenu2DItem[this.items.length - 1];

        System.arraycopy(this.items, 0, tmp, 0, ind);

        if (ind + 1 < this.items.length)
            System.arraycopy(this.items, ind + 1, tmp, ind, this.items.length - ind - 1);

        // we refresh the state of our drawable...
        this.items = tmp;
        this.menuDrawable.refreshState();
        return true;
    }

    /*------------------------------------------------------------------------------------*/

    /** To enable/disable an item.
     * @param itemName to search and to enable/disable
     * @param enabled true to enable, false to disable.
     * @return true if the item was found, false if not found
     */
    public boolean setItemEnabled(String itemName, boolean enabled) {
        // we search the item index
        for (int i = 0; i < this.items.length; i++)
            if (this.items[i].itemName.equals(itemName)) {

                if (this.items[i].isEnabled && !enabled && this.items[i].link != null && this.items[i].link.isVisible())
                    this.items[i].link.hide();

                this.items[i].isEnabled = enabled;
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
    public boolean addItemLink(String itemName, Menu2D menu2D) {
        // we search the item index
        for (int i = 0; i < this.items.length; i++)
            if (this.items[i].itemName.equals(itemName)) {
                if (this.items[i].link != null && this.items[i].link.isVisible())
                    this.items[i].link.hide();

                menu2D.init(this.menuManager); // we init the sub-menu
                this.items[i].link = (SimpleMenu2D) menu2D;
                return true;
            }

        return false; // not found
    }

    /*------------------------------------------------------------------------------------*/

    /** To remove a menu link on an item.
     * @param itemName to search and remove the link on
     * @return true if the item was found & the menu removed, false if not found
     */
    public boolean removeItemLink(String itemName) {
        // we search the item index
        for (int i = 0; i < this.items.length; i++)
            if (this.items[i].itemName.equals(itemName)) {
                if (this.items[i].link != null && this.items[i].link.isVisible())
                    this.items[i].link.hide();

                this.items[i].link = null;
                return true;
            }

        return false; // not found
    }

    /*------------------------------------------------------------------------------------*/

    /** To show this menu2D at the specified screen point.
     * @param p screen point
     */
    public void show(Point p) {
        this.menuDrawable.setNextPosition(p.x, p.y);

        if (!this.isVisible && this.menuManager != null) {
            this.menuDrawable.animateMenu();
            this.menuDrawable.tick();
            this.menuManager.getGraphicsDirector().addDrawable(this.menuDrawable);
            this.isVisible = true;
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** To hide this menu2D.
     */
    public void hide() {
        if (this.isVisible && this.menuManager != null) {
            this.menuManager.getGraphicsDirector().removeDrawable(this.menuDrawable);
            this.isVisible = false;
            this.selectedItemIndex = -1;
            this.firstItemIndex = 0;
            this.isFirstIndexArrow = false;
            this.scrollEnabled = false;

            hideSubMenus();
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** To hide this menu's sub-menus.
     */
    protected void hideSubMenus() {
        // We hide all the other sub-menus
        for (int i = 0; i < this.items.length; i++)
            if (this.items[i].link != null)
                this.items[i].link.hide();
    }

    /*------------------------------------------------------------------------------------*/

    /** Tells if this menu is visible on screen.
     * @return true if it's visible, false otherwise.
     */
    public boolean isVisible() {
        return this.isVisible;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the menu name.
     * @return the menu's name.
     */
    public String getName() {
        return this.name;
    }

    /*------------------------------------------------------------------------------------*/
    /** To find a menu by its name. The search is performed also among sub-menus.
     * @param menuName the menu's name to search
     * @return the menu if found, null if not.
     */
    public Menu2D findByName(String menuName) {
        if (menuName.equals(this.name))
            return this;

        // search among sub-menus
        for (int i = 0; i < this.items.length; i++)
            if (this.items[i].link != null) {
                Menu2D menu = this.items[i].link.findByName(menuName);
                if (menu != null)
                    return menu;
            }

        return null; // not found
    }

    /*------------------------------------------------------------------------------------*/

    /** To process a mouse event.
     * @param mEvent the mouse event received on this menu
     * @return true if the event was processed by us or one of our sub-menus, false
     *         if the event was not for us.
     */
    public boolean mouseClicked(MouseEvent mEvent) {
        if (!this.isVisible)
            return false;

        // is it a click on one of our sub-menus ?    
        int maxIndex = this.items.length;

        if (maxIndex > SimpleMenu2D.MAX_ITEMS_DISPLAYED)
            maxIndex = this.firstItemIndex + SimpleMenu2D.MAX_ITEMS_DISPLAYED;

        for (int i = this.firstItemIndex; i < maxIndex; i++)
            if (this.items[i].link != null && this.items[i].link.mouseClicked(mEvent))
                return true; // one of our sub-menus received the event

        // is the event for us ?
        if (this.menuDrawable.contains(mEvent.getX(), mEvent.getY())) {
            // ok the event is for us !
            // we ask which item is selected...
            int index = this.menuDrawable.getItemAt(mEvent.getY());

            if (index < this.firstItemIndex || maxIndex <= index)
                return true;

            // need to update the first index displayed ?
            if (index == this.firstItemIndex && (this.firstItemIndex != 0 || this.isFirstIndexArrow)) {
                hideSubMenus();

                if (this.isFirstIndexArrow && this.firstItemIndex == 0)
                    this.isFirstIndexArrow = false;
                else
                    this.firstItemIndex--; // backward navigation

                return true;
            }

            if (index == maxIndex - 1 && index < this.items.length - 1) {
                hideSubMenus();

                if (!this.isFirstIndexArrow && this.firstItemIndex == 0)
                    this.isFirstIndexArrow = true;
                else
                    this.firstItemIndex++; // forward navigation

                return true;
            }

            if (this.items[index].itemName.equals("-"))
                return true;

            if (!this.items[index].isEnabled)
                return true;

            // item is a link
            if (this.items[index].link != null) {

                if (!this.items[index].link.isVisible()) {
                    hideSubMenus();

                    // We display the newly selected menu
                    int x = this.menuDrawable.getX() + this.menuDrawable.getWidth() + 1;
                    int y = this.menuDrawable.getItemY(index);

                    this.items[index].link.menuDrawable.setParentRectangle(this.menuDrawable.getRectangle());
                    this.items[index].link.show(new Point(x, y));
                    return true;
                } else {
                    this.items[index].link.hide();
                    this.timeStamp = System.currentTimeMillis() + 1000; // to avoid our menu to re-appear
                    return true; // we hide the linked menu
                }
            }

            // ok, we have a click on an item !
            // we generate an event on our menu listeners.
            hideSubMenus();
            this.menuManager.dispatchEvent(new Menu2DEvent(this, this.items[index].itemName));
            return true;
        }

        return false;
    }

    /*------------------------------------------------------------------------------------*/

    /** To call when the mouse cursor is moved.
     * @param x mouse's x
     * @param y mouse's y
     */
    public boolean mouseMoved(int x, int y) {

        if (!this.isVisible)
            return false;

        // is it a click on one of our sub-menus ?
        int maxIndex = this.items.length;

        if (maxIndex > SimpleMenu2D.MAX_ITEMS_DISPLAYED)
            maxIndex = this.firstItemIndex + SimpleMenu2D.MAX_ITEMS_DISPLAYED;

        for (int i = this.firstItemIndex; i < maxIndex; i++)
            if (this.items[i].link != null && this.items[i].link.mouseMoved(x, y))
                return true; // one of our sub-menus received the event

        // is the event for us ?
        long now = System.currentTimeMillis();

        if (this.menuDrawable.contains(x, y)) {
            this.selectedItemIndex = this.menuDrawable.getItemAt(y);

            if (this.selectedItemIndex < this.firstItemIndex || maxIndex <= this.selectedItemIndex)
                return true;

            if (this.selectedItemIndex == this.firstItemIndex && (this.firstItemIndex != 0 || this.isFirstIndexArrow)) {

                if (!this.scrollEnabled && now - this.timeStamp < SimpleMenu2D.MIN_TIME_BEFORE_SCROLL)
                    return true;

                if (now - this.timeStamp < SimpleMenu2D.MIN_TIME_BETWEEN_SCROLL)
                    return true;

                this.scrollEnabled = true;
                this.timeStamp = now;
                hideSubMenus();

                if (this.isFirstIndexArrow && this.firstItemIndex == 0)
                    this.isFirstIndexArrow = false;
                else
                    this.firstItemIndex--; // backward navigation

                return true;
            }

            if (this.selectedItemIndex == maxIndex - 1 && this.selectedItemIndex < this.items.length - 1) {

                if (!this.scrollEnabled && now - this.timeStamp < SimpleMenu2D.MIN_TIME_BEFORE_SCROLL)
                    return true;

                if (now - this.timeStamp < SimpleMenu2D.MIN_TIME_BETWEEN_SCROLL)
                    return true;

                this.scrollEnabled = true;
                this.timeStamp = now;
                hideSubMenus();

                if (!this.isFirstIndexArrow && this.firstItemIndex == 0)
                    this.isFirstIndexArrow = true;
                else
                    this.firstItemIndex++; // forward navigation

                return true;
            }

            this.scrollEnabled = false; // cancel scrolling

            if (this.items[this.selectedItemIndex].itemName.equals("-"))
                return true;
            if (!this.items[this.selectedItemIndex].isEnabled)
                return true;

            // item is a link
            if (this.items[this.selectedItemIndex].link != null) {

                if (!this.items[this.selectedItemIndex].link.isVisible()) {

                    if (now - this.timeStamp < SimpleMenu2D.MIN_TIME_BEFORE_MENU)
                        return true;

                    hideSubMenus();

                    // We display the newly selected menu
                    int xi = this.menuDrawable.getX() + this.menuDrawable.getWidth() + 1;
                    int yi = this.menuDrawable.getItemY(this.selectedItemIndex);

                    this.items[this.selectedItemIndex].link.menuDrawable.setParentRectangle(this.menuDrawable.getRectangle());
                    this.items[this.selectedItemIndex].link.show(new Point(xi, yi));
                }
            }

            this.timeStamp = now; // reset time-stamp
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
    public boolean mouseDragged(int dx, int dy, boolean startsNow) {

        if (!this.isVisible)
            return false;

        for (int i = 0; i < this.items.length; i++)
            if (this.items[i].link != null)
                this.items[i].link.mouseDragged(dx, dy, startsNow);

        if (startsNow) {
            this.dragFromX = this.menuDrawable.getX();
            this.dragFromY = this.menuDrawable.getY();
            return true;
        }

        this.menuDrawable.setNextPosition(this.dragFromX + dx, this.dragFromY + dy);
        return true;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the available items.
     * @return available items a null length array if none.
     */
    protected SimpleMenu2DItem[] getItems() {
        return this.items;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the currently selected index
     * @return index
     */
    public int getSelectedItemIndex() {
        return this.selectedItemIndex;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the index of the first item displayed.
     * @return index
     */
    public int getFirstItemIndex() {
        return this.firstItemIndex;
    }

    /*------------------------------------------------------------------------------------*/

    /** To tell if the first item to display is an arrow.
     * @return index
     */
    public boolean isFirstIndexArrow() {
        return this.isFirstIndexArrow;
    }

    /*------------------------------------------------------------------------------------*/
}

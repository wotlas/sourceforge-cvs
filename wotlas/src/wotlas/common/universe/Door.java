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

package wotlas.common.universe;

import java.awt.Point;
import java.awt.Rectangle;
import wotlas.common.ImageLibRef;
import wotlas.libs.graphics2D.Drawable;
import wotlas.libs.graphics2D.ImageIdentifier;
import wotlas.libs.graphics2D.drawable.DoorDrawable;
import wotlas.libs.pathfinding.AStarDouble;

/** A Door on an InteriorMap... Doors are possessed by RoomLinks.
 *
 * @author Petrus, Aldiss
 * @see wotlas.common.universe.RoomLink
 */

public class Door {
    /*------------------------------------------------------------------------------------*/

    /** Max square distance from which the player can open/close this door.
     */
    public static final int MAX_ACTION_DIST = 2500;

    /*------------------------------------------------------------------------------------*/

    /** Does this Door has a Lock ?
     */
    private boolean hasLock;

    /** X position of the door ( top-left corner )
     */
    private int x;

    /** Y position of the door ( top-left corner )
     */
    private int y;

    /** Door Type ( indicating which pivot point to use )
     */
    private byte doorType;

    /** Door Variation Angle (in radians).
     */
    private float variationAngle;

    /** Points out which Door image to use.
     */
    private ImageIdentifier doorImage;

    /*------------------------------------------------------------------------------------*/

    /** Door drawable to use...
     */
    transient private DoorDrawable doorDrawable;

    /** For the server side : door opened ?
     */
    transient private boolean isOpened;

    /** Door RoomID.
     */
    transient private int myRoomID;

    /** Door RoomLinkID.
     */
    transient private int myRoomLinkID;

    /** Is the door displayed on screen ?
     */
    transient private boolean isDisplayed;

    /*------------------------------------------------------------------------------------*/

    /** Constructor
     */
    public Door() {
        this.hasLock = false;
        this.isOpened = false;
        this.isDisplayed = false;
    }

    /*------------------------------------------------------------------------------------*/

    /** To construct a Door with no lock.
     */
    public Door(int x, int y, float variationAngle, byte doorType, ImageIdentifier doorImage) {
        this();
        this.x = x;
        this.y = y;
        this.doorType = doorType;
        this.doorImage = doorImage;
        this.variationAngle = variationAngle;
    }

    /*------------------------------------------------------------------------------------*/

    /** Getter / Setter for persistence
     */
    public boolean getHasLock() {
        return this.hasLock;
    }

    public void setHasLock(boolean hasLock) {
        this.hasLock = hasLock;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public byte getDoorType() {
        return this.doorType;
    }

    public void setDoorType(byte doorType) {
        this.doorType = doorType;
    }

    public ImageIdentifier getDoorImage() {
        return this.doorImage;
    }

    public void setDoorImage(ImageIdentifier doorImage) {
        this.doorImage = doorImage;
    }

    public void setVariationAngle(float varAngle) {
        this.variationAngle = varAngle;
    }

    public float getVariationAngle() {
        return this.variationAngle;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get one of my RoomID.
     */
    public int getMyRoomID() {
        return this.myRoomID;
    }

    /** To set one of my RoomID.
     */
    public void setMyRoomID(int myRoomID) {
        this.myRoomID = myRoomID;
    }

    /** To get my RoomLinkID.
     */
    public int getMyRoomLinkID() {
        return this.myRoomLinkID;
    }

    /** To set my RoomLinkID.
     */
    public void setMyRoomLinkID(int myRoomLinkID) {
        this.myRoomLinkID = myRoomLinkID;
    }

    /*------------------------------------------------------------------------------------*/

    /** To clean this door : erases the associated DoorDrawable.
     */
    public void clean() {
        this.doorDrawable = null;
        this.isDisplayed = false;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the Door Drawable to add to the GraphicsDirector.
     * @return DoorDrawable corresponding to this room.
     */
    public Drawable getDoorDrawable() {
        if (!AStarDouble.isInitialized())
            return null;

        if (this.doorDrawable != null)
            return this.doorDrawable;

        //  Door Drawable creation
        this.doorDrawable = new DoorDrawable(this.x, this.y, 0.0f, this.variationAngle, this.doorType, this.doorImage, ImageLibRef.DOOR_PRIORITY);
        this.doorDrawable.useAntialiasing(true);
        this.doorDrawable.setOwner(this); // we are the owner of this door draw...

        if (this.isOpened) {
            this.doorDrawable.setOpened();
            AStarDouble.cleanRectangle(this.doorDrawable.getRealDoorRectangle());
        } else {
            this.doorDrawable.setClosed();
            AStarDouble.fillRectangle(this.doorDrawable.getRealDoorRectangle());
        }

        return this.doorDrawable;
    }

    /*------------------------------------------------------------------------------------*/

    /** To open the door...
     */
    public synchronized void open() {
        if (this.doorDrawable == null) {
            this.isOpened = true;
            return;
        }

        this.doorDrawable.open();
        AStarDouble.cleanRectangle(this.doorDrawable.getRealDoorRectangle());
    }

    /*------------------------------------------------------------------------------------*/

    /** To close the door...
     */
    public synchronized void close() {
        if (this.doorDrawable == null) {
            this.isOpened = false;
            return;
        }

        this.doorDrawable.close();
        AStarDouble.fillRectangle(this.doorDrawable.getRealDoorRectangle());
    }

    /*------------------------------------------------------------------------------------*/

    /** To open the door without any animation...
     */
    public synchronized void setOpened() {
        if (this.doorDrawable == null) {
            this.isOpened = true;
            return;
        }

        this.doorDrawable.setOpened();
        AStarDouble.cleanRectangle(this.doorDrawable.getRealDoorRectangle());
    }

    /*------------------------------------------------------------------------------------*/

    /** To close the door without any animation...
     */
    public synchronized void setClosed() {
        if (this.doorDrawable == null) {
            this.isOpened = false;
            return;
        }

        this.doorDrawable.setClosed();
        AStarDouble.fillRectangle(this.doorDrawable.getRealDoorRectangle());
    }

    /*------------------------------------------------------------------------------------*/

    /** To test if the door is opened...
     */
    public boolean isOpened() {
        if (this.doorDrawable != null)
            return this.doorDrawable.isOpened();
        else
            return this.isOpened;
    }

    /*------------------------------------------------------------------------------------*/

    /** To test if the door is displayed on screen...
     */
    public boolean isDisplayed() {
        return this.isDisplayed;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set that the door is displayed on screen...
     */
    public void setIsDisplayed(boolean isDisplayed) {
        this.isDisplayed = isDisplayed;
    }

    /*------------------------------------------------------------------------------------*/

    /** To check if the player is near this door...
     * @param playerRectangle player 's rectangle
     * @return true if the player is near the door.
     */
    public boolean isPlayerNear(Rectangle playerRectangle) {
        if (this.doorDrawable == null)
            return false;

        int xpc = playerRectangle.x + playerRectangle.width / 2;
        int ypc = playerRectangle.y + playerRectangle.height / 2;

        int xdc = this.doorDrawable.getX() + this.doorDrawable.getWidth() / 2;
        int ydc = this.doorDrawable.getY() + this.doorDrawable.getHeight() / 2;

        if ((xdc - xpc) * (xdc - xpc) + (ydc - ypc) * (ydc - ypc) <= Door.MAX_ACTION_DIST)
            return true;

        return false;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get a valid point near this door on the side of the player...
     * @param playerRectangle player 's rectangle
     * @return a point near the door.
     */
    public Point getPointNearDoor(Rectangle playerRectangle) {
        if (this.doorDrawable == null)
            return null;

        Point resultPoint = new Point();
        Rectangle doorR = this.doorDrawable.getRealDoorRectangle();

        int xpc = playerRectangle.x + playerRectangle.width / 2;
        int ypc = playerRectangle.y + playerRectangle.height / 2;

        int xdc = doorR.x + doorR.width / 2;
        int ydc = doorR.y + doorR.height / 2;

        if (this.doorType == DoorDrawable.VERTICAL_TOP_PIVOT || this.doorType == DoorDrawable.VERTICAL_BOTTOM_PIVOT) {
            resultPoint.y = ydc;

            if (xpc < xdc)
                resultPoint.x = ((xdc / AStarDouble.getTileSize()) - AStarDouble.getSpriteSize()) * AStarDouble.getTileSize();
            else
                resultPoint.x = ((xdc / AStarDouble.getTileSize()) + AStarDouble.getSpriteSize()) * AStarDouble.getTileSize();
        } else {
            resultPoint.x = xdc;

            if (ypc < ydc)
                resultPoint.y = ((ydc / AStarDouble.getTileSize()) - AStarDouble.getSpriteSize()) * AStarDouble.getTileSize();
            else
                resultPoint.y = ((ydc / AStarDouble.getTileSize()) + AStarDouble.getSpriteSize()) * AStarDouble.getTileSize();
        }

        return resultPoint;
    }

    /*------------------------------------------------------------------------------------*/

    /** To check if the player can go to the specified direction...
     * @param true if the player can move...
     */
    public boolean canMove(Rectangle playerRectangle, Point destPoint) {
        if (this.doorDrawable == null || destPoint.x < 0 || destPoint.y < 0)
            return true;

        int xpc = playerRectangle.x + playerRectangle.width / 2;
        int ypc = playerRectangle.y + playerRectangle.height / 2;

        int xdc = this.doorDrawable.getX() + this.doorDrawable.getWidth() / 2;
        int ydc = this.doorDrawable.getY() + this.doorDrawable.getHeight() / 2;

        if (this.doorType == DoorDrawable.VERTICAL_TOP_PIVOT || this.doorType == DoorDrawable.VERTICAL_BOTTOM_PIVOT) {
            if ((xdc < xpc && xdc < destPoint.x) || (xdc > xpc && xdc > destPoint.x))
                return true;
            return false;
        } else if ((ydc < ypc && ydc < destPoint.y) || (ydc > ypc && ydc > destPoint.y))
            return true;

        return false;
    }

    /*------------------------------------------------------------------------------------*/

}

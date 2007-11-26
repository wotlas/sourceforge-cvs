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

package wotlas.libs.graphics2D.drawable;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import wotlas.libs.graphics2D.Drawable;
import wotlas.libs.graphics2D.DrawableOwner;
import wotlas.libs.graphics2D.ImageIdentifier;
import wotlas.libs.graphics2D.ImageLibrary;

/** The DoorDrawable will represent the action of closing and opening a door.
 *  4 style of doors are considered.
 *
 *  This drawable can have an owner ( use the getOwner/setOwner methods ).
 *
 * @author MasterBob, Aldiss
 */

public class DoorDrawable extends Drawable implements DrawableOwner {

    /*------------------------------------------------------------------------------------*/

    /**
     * The anchor point for the rotation will depent of the type of the door
     */

    /** Door's Rotation set to     *
     *                           |---|
     *                           |   |
     *                           |   |
     *                           |   |
     *                           |---|
     */
    public final static byte VERTICAL_TOP_PIVOT = 0;

    /** Door's Rotation set to    |---|
      *                           |   |
      *                           |   |
      *                           |   |
      *                           |---|
      *                             *
      */
    public final static byte VERTICAL_BOTTOM_PIVOT = 1;

    /** Door's Rotation set to    |-----------------|
     *                           *|                 |
     *                            |-----------------|
     */
    public final static byte HORIZONTAL_LEFT_PIVOT = 2;

    /** Door's Rotation set to    |-----------------|
      *                           |                 |*
      *                           |-----------------|
      */
    public final static byte HORIZONTAL_RIGHT_PIVOT = 3;

    /*------------------------------------------------------------------------------------*/

    /** Owner of this door drawable.
     */
    private Object owner;

    /** Current Image Identifier.
     */
    private ImageIdentifier image;

    /** the initial angle if needed to represent a closing door
     */
    private float iniAngle = 0f;

    /** the variationAngle
     */
    private float variationAngle = 0f;

    /** the current angle
     *  we don't take into account the iniAngle to define the currentAngle
     *  we will just do the correction due to the iniAngle on the paint method
     */
    private float currentAngle = 0f;

    /** Door's type
     */
    private byte doorType;

    /** indicate if we are opening the door
     */
    private boolean isOpening = false;

    /** indicate if we are closing the door
     */
    private boolean isClosing = false;

    /** our current Rectangle when the door is closed.
     */
    private Rectangle rDoorClosed;

    /** Rectangle representing the door opened.
     */
    private Rectangle rDoorOpened;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with doorType mode ( see public static fields ).
     *
     * @param positionX x position of the top-left corner of the door 
     * @param positionY y position of the top-left corner of the door 
     * @param iniAngle optional initial angle
     * @param variationAngle variation angle for the door (in radians, ex: +pi/2, -pi/2 );
     * @param doorType type of the door
     * @param image door image
     * @param priority sprite's priority
     */
    public DoorDrawable(int positionX, int positionY, float iniAngle, float variationAngle, byte doorType, ImageIdentifier image,
            short priority) {
        super();

        this.rDoorClosed = new Rectangle();
        this.rDoorOpened = new Rectangle();

        this.rDoorClosed.x = positionX;
        this.rDoorClosed.y = positionY;
        this.r = this.rDoorClosed;

        this.priority = priority;
        this.iniAngle = iniAngle;
        this.variationAngle = variationAngle;
        this.doorType = doorType;
        this.image = image;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To initialize this drawable with the ImageLibrary. Don't call it yourself ! it's
     *  done automatically when you call addDrawable on the GraphicsDirector.
     *
     *  IF you need the ImageLib for some special inits just extend this method and don't
     *  forget to call a super.init(imageLib) !
     *
     *  @param imagelib ImageLibrary where you can take the images to display.
     */
    @Override
    protected void init(ImageLibrary imageLib) {
        super.init(imageLib);

        // Compute rectangle for door closed
        this.rDoorClosed.width = getImageLibrary().getWidth(this.image);
        this.rDoorClosed.height = getImageLibrary().getHeight(this.image);

        // Compute rectangle for door opened
        if (this.iniAngle <= 1.0 && Math.abs(this.variationAngle) >= 1.55 /*Math.PI/2*/) {
            switch (this.doorType) {
                //default:
                case HORIZONTAL_LEFT_PIVOT:
                    this.rDoorOpened.x = this.rDoorClosed.x;

                    if (this.variationAngle > 0)
                        this.rDoorOpened.y = this.rDoorClosed.y;
                    else
                        this.rDoorOpened.y = this.rDoorClosed.y - this.rDoorClosed.width + this.rDoorClosed.height;
                    break;

                case HORIZONTAL_RIGHT_PIVOT:
                    this.rDoorOpened.x = this.rDoorClosed.x + this.rDoorClosed.width - this.rDoorClosed.height;

                    if (this.variationAngle > 0)
                        this.rDoorOpened.y = this.rDoorClosed.y - this.rDoorClosed.width + this.rDoorClosed.height;
                    else
                        this.rDoorOpened.y = this.rDoorClosed.y;
                    break;

                case VERTICAL_BOTTOM_PIVOT:
                    this.rDoorOpened.y = this.rDoorClosed.y + this.rDoorClosed.height - this.rDoorClosed.width;

                    if (this.variationAngle > 0)
                        this.rDoorOpened.x = this.rDoorClosed.x;
                    else
                        this.rDoorOpened.x = this.rDoorClosed.x - this.rDoorClosed.height + this.rDoorClosed.width;
                    break;

                case VERTICAL_TOP_PIVOT:
                    this.rDoorOpened.y = this.rDoorClosed.y;

                    if (this.variationAngle > 0)
                        this.rDoorOpened.x = this.rDoorClosed.x - this.rDoorClosed.height + this.rDoorClosed.width;
                    else
                        this.rDoorOpened.x = this.rDoorClosed.x;
                    break;
            }

            this.rDoorOpened.width = this.rDoorClosed.height;
            this.rDoorOpened.height = this.rDoorClosed.width;
        } else {
            // we create a general rectangle
            switch (this.doorType) {
                //default:
                case HORIZONTAL_LEFT_PIVOT:
                    this.rDoorOpened.x = this.rDoorClosed.x;
                    this.rDoorOpened.y = this.rDoorClosed.y - this.rDoorClosed.width;
                    this.rDoorOpened.width = this.rDoorClosed.width;
                    this.rDoorOpened.height = this.rDoorClosed.height + 2 * this.rDoorClosed.width;
                    break;

                case HORIZONTAL_RIGHT_PIVOT:
                    this.rDoorOpened.x = this.rDoorClosed.x;
                    this.rDoorOpened.y = this.rDoorClosed.y - this.rDoorClosed.width;
                    this.rDoorOpened.width = this.rDoorClosed.width;
                    this.rDoorOpened.height = this.rDoorClosed.height + 2 * this.rDoorClosed.width;
                    break;

                case VERTICAL_BOTTOM_PIVOT:
                    this.rDoorOpened.x = this.rDoorClosed.x - this.rDoorClosed.height;
                    this.rDoorOpened.y = this.rDoorClosed.y;
                    this.rDoorOpened.width = this.rDoorClosed.width + 2 * this.rDoorClosed.height;
                    this.rDoorOpened.height = this.rDoorClosed.height;
                    break;

                case VERTICAL_TOP_PIVOT:
                    this.rDoorOpened.x = this.rDoorClosed.x - this.rDoorClosed.height;
                    this.rDoorOpened.y = this.rDoorClosed.y;
                    this.rDoorOpened.width = this.rDoorClosed.width + 2 * this.rDoorClosed.height;
                    this.rDoorOpened.height = this.rDoorClosed.height;
                    break;
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the real door rectangle, not its influence zone.
     * @return strict rectangle representing the door CLOSED.
     */
    public Rectangle getRealDoorRectangle() {
        return new Rectangle(this.rDoorClosed);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the current angle of the doorDrawable
     */
    public float getCurrentAngle() {
        return this.currentAngle;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the angle of variation of the doorDrawable
     *  @param varAngle the angle of the variation of the rotation
     */
    public void setVariationAngle(float varAngle) {
        this.variationAngle = varAngle;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the variationAngle of the doorDrawable
     */
    public float getVariationAngle() {
        return this.variationAngle;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the current angle of the doorDrawable
     *  @param angle the angle of the rotation
     */
    public void setCurrentAngle(float angle) {
        this.currentAngle = angle;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     *  set/stop the action of closing door
     */
    private void setClosing(boolean closing) {
        this.isClosing = closing;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     *  set/stop the action of opening door
     */
    private void setOpening(boolean opening) {
        this.isOpening = opening;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     *  return true if the door is opening
     */
    public boolean isOpening() {
        return this.isOpening;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     *  return true if the door is closing
     */
    public boolean isClosing() {
        return this.isClosing;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     *  set the door to a stable closed view
     */
    public void setClosed() {
        this.isOpening = false;
        this.isClosing = false;
        this.currentAngle = this.iniAngle;
        this.r = this.rDoorClosed;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     *  set the door to a stable open view
     */
    public void setOpened() {
        this.isOpening = false;
        this.isClosing = false;
        this.currentAngle = this.iniAngle + this.variationAngle;
        this.r = this.rDoorOpened;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     *  return true if the door is in a stable closed view
     */
    public boolean isClosed() {
        if (this.isOpening == false && this.isClosing == false && this.currentAngle == this.iniAngle)
            return true;
        else
            return false;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     *  return true if the door is in a stable opened view
     */
    public boolean isOpened() {
        if ((this.isOpening == false) && (this.isClosing == false) && (this.currentAngle == this.iniAngle + this.variationAngle))
            return true;
        else
            return false;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To open the door
     */
    public void open() {
        setClosing(false);
        setOpening(true);
        this.r = this.rDoorOpened;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To close the door
     */
    public void close() {
        setOpening(false);
        setClosing(true);
        this.r = this.rDoorClosed;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Paint method called by the GraphicsDirector. The specified rectangle represents
     *  the displayed screen in background cordinates ( see GraphicsDirector ).
     *
     *  @param gc graphics 2D use for display (double buffering is handled by the
     *         GraphicsDirector)
     *  @param screen display zone of the graphicsDirector, in background coordinates.
     */
    @Override
    public void paint(Graphics2D gc, Rectangle screen) {

        // 1 - Need to display this sprite ?
        if (!this.r.intersects(screen))
            return;

        // 2 - any affine transform ?
        AffineTransform affTr = null;

        //calcul de la rotation eventuelle
        float rotation = this.iniAngle + this.currentAngle;

        if (rotation != 0.0) {
            // Rotation Transformation
            affTr = new AffineTransform();
            int anchorX = 0, anchorY = 0;

            switch (this.doorType) {
                //default:
                case HORIZONTAL_LEFT_PIVOT:
                    anchorX = this.rDoorClosed.x + this.rDoorClosed.height / 2;
                    anchorY = this.rDoorClosed.y + this.rDoorClosed.height / 2;
                    break;

                case HORIZONTAL_RIGHT_PIVOT:
                    anchorX = this.rDoorClosed.x + this.rDoorClosed.width - this.rDoorClosed.height / 2;
                    anchorY = this.rDoorClosed.y + this.rDoorClosed.height / 2;
                    break;

                case VERTICAL_BOTTOM_PIVOT:
                    anchorX = this.rDoorClosed.x + this.rDoorClosed.width / 2;
                    anchorY = this.rDoorClosed.y + this.rDoorClosed.height - this.rDoorClosed.width / 2;
                    break;

                case VERTICAL_TOP_PIVOT:
                    anchorX = this.rDoorClosed.x + this.rDoorClosed.width / 2;
                    anchorY = this.rDoorClosed.y + this.rDoorClosed.width / 2;
                    break;

            }

            affTr.rotate(rotation, anchorX - screen.x, anchorY - screen.y);
        }

        // 3 - image display
        BufferedImage bufIm = getImageLibrary().getImage(this.image);

        if (affTr == null)
            gc.drawImage(bufIm, this.rDoorClosed.x - screen.x, this.rDoorClosed.y - screen.y, null);
        else {
            affTr.translate(this.rDoorClosed.x - screen.x, this.rDoorClosed.y - screen.y);
            gc.drawImage(bufIm, affTr, null);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Tick method called by the GraphicsDirector. This tick method has a returned value
     *  that indicates if the drawable is still living or must be deleted. Some Drawables
     *  always return "still living", it is then the task of the program that uses
     *  the GraphicsDirector to manage the destruction of drawables.
     *
     *  @return true if the drawable is "live", false if it must be deleted.
     */
    @Override
    public boolean tick() {

        if (this.isOpening) {
            this.currentAngle += this.variationAngle / 10;
            if (Math.abs(this.currentAngle) >= Math.abs(this.variationAngle))
                setOpened();
        }

        if (this.isClosing) {
            this.currentAngle -= this.variationAngle / 10;
            if (Math.abs(this.currentAngle - this.variationAngle) >= Math.abs(this.variationAngle))
                setClosed();
        }

        return true; // no update needed and a DoorDrawable is always "live" by default.
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the owner of this drawable. By 'owner' we mean the object which this
      *  Drawable is the graphical representation.
      *
      * @return Object owner of this drawable.
      */
    public Object getOwner() {
        return this.owner;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the owner of this drawable. By 'owner' we mean the object which this
      *  Drawable is the graphical representation.
      *
      * @param object owner of this drawable.
      */
    public void setOwner(Object owner) {
        this.owner = owner;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

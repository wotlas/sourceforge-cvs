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

package wotlas.utils;

import java.awt.Rectangle;
import wotlas.libs.persistence.BackupReady;

/** A Rectangle class as the java.awt.Rectangle class SHOULD have been ( I still don't
 *  understand why the java.awt.Rectangle.getX() getY() etc. return doubles !!!!! )
 *
 *  Of course in this implementation we return integers ... for advanced features
 *  we rely on the Rectangle class ( by using the toRectangle() method )...
 *
 * @author Aldiss, Diego
 */

public class ScreenRectangle implements BackupReady {
    /*------------------------------------------------------------------------------------*/

    /** x position.
     */
    public int x;

    /** y position.
     */
    public int y;

    /** width 
     */
    public int width;

    /** height 
     */
    public int height;

    /*------------------------------------------------------------------------------------*/

    /** Empty Constructor.
     */
    public ScreenRectangle() {
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public ScreenRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with ScreenRectangle.
     */
    public ScreenRectangle(ScreenRectangle other) {
        this.x = other.x;
        this.y = other.y;
        this.width = other.width;
        this.height = other.height;
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with Rectangle.
     */
    public ScreenRectangle(Rectangle other) {
        this.x = other.x;
        this.y = other.y;
        this.width = other.width;
        this.height = other.height;
    }

    /*------------------------------------------------------------------------------------*/

    /** Complete Setter.
     */
    public void setToRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /*------------------------------------------------------------------------------------*/

    /** X getter.
     */
    public int getX() {
        return this.x;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Y getter.
     */
    public int getY() {
        return this.y;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** X setter
     */
    public void setX(int x) {
        this.x = x;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Y setter
     */
    public void setY(int y) {
        this.y = y;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Width getter.
     */
    public int getWidth() {
        return this.width;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Height getter.
     */
    public int getHeight() {
        return this.height;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Width setter
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Height setter
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a Rectangle representation of this ScreenRectangle.
     * @return Rectangle
     */
    public Rectangle toRectangle() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a string representation of this ScreenRectangle.
     */
    @Override
    public String toString() {
        return "ScreenRectangle [ " + this.x + ", " + this.y + ", " + this.width + ", " + this.height + " ]";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** id version of data, used in serialized persistance.
     */
    public int ExternalizeGetVersion() {
        return 1;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** write object data with serialize.
     */
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeInt(this.x);
        objectOutput.writeInt(this.y);
        objectOutput.writeInt(this.width);
        objectOutput.writeInt(this.height);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
     */
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.x = objectInput.readInt();
            this.y = objectInput.readInt();
            this.width = objectInput.readInt();
            this.height = objectInput.readInt();
        } else {
            // to do.... when new version
        }
    }
}

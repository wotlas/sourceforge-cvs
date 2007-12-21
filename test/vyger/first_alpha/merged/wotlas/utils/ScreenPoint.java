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

import java.awt.Point;
import wotlas.libs.persistence.BackupReady;

/** A Point class as the java.awt.Point class SHOULD have been ( I still don't
 *  understand why the java.awt.Rectangle.getX() getY() return doubles !!!!! )
 *
 *  Of course in this implementation we return integers ... for advanced features
 *  we rely on the Point class ( by using the toPoint() method )...
 *
 * @author Aldiss, Diego
 */

public class ScreenPoint implements BackupReady {
    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    /*------------------------------------------------------------------------------------*/

    /** x position.
     */
    public int x;

    /** y position.
     */
    public int y;

    /*------------------------------------------------------------------------------------*/

    /** Empty Constructor.
     */
    public ScreenPoint() {
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public ScreenPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with ScreenPoint.
     */
    public ScreenPoint(ScreenPoint other) {
        this.x = other.x;
        this.y = other.y;
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with Point.
     */
    public ScreenPoint(Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    /*------------------------------------------------------------------------------------*/

    /** Complete Setter.
     */
    public void setToPoint(int x, int y) {
        this.x = x;
        this.y = y;
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

    /** To get a Point representation of this ScreenPoint.
     * @return Point
     */
    public Point toPoint() {
        return new Point(this.x, this.y);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get a string representation of this ScreenPoint.
     */
    @Override
    public String toString() {
        return "ScreenPoint [ " + this.x + ", " + this.y + " ]";
    }

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
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
     */
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.x = objectInput.readInt();
            this.y = objectInput.readInt();
        } else {
            // to do.... when new version
        }
    }
}
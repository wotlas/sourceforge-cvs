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

/** A Point class as the java.awt.Point class SHOULD have been ( I still don't
 *  understand why the java.awt.Rectangle.getX() getY() return doubles !!!!! )
 *
 *  Of course in this implementation we return integers ... for advanced features
 *  we rely on the Point class ( by using the toPoint() method )...
 *
 * @author Aldiss
 */

public class ScreenPoint
{
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
    public ScreenPoint( int x, int y ) {
    	this.x = x;
    	this.y = y;
    }

 /*------------------------------------------------------------------------------------*/

  /** Constructor with ScreenPoint.
   */
    public ScreenPoint(ScreenPoint other ) {
        this.x = other.x;
        this.y = other.y;
    }

 /*------------------------------------------------------------------------------------*/

  /** Constructor with Point.
   */
    public ScreenPoint( Point other ) {
        this.x = other.x;
        this.y = other.y;
    }

 /*------------------------------------------------------------------------------------*/

   /** Complete Setter.
    */
    public void setToPoint( int x, int y ) {
    	this.x = x;
    	this.y = y;
    }

 /*------------------------------------------------------------------------------------*/

  /** X getter.
   */
    public int getX() {
    	return x;
    }   

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Y getter.
   */
    public int getY() {
    	return y;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** X setter
   */
    public void setX( int x ) {
    	this.x=x;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Y setter
   */
    public void setY( int y ) {
    	this.y=y;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get a Point representation of this ScreenPoint.
   * @return Point
   */
    public Point toPoint(){
        return new Point( x, y );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get a string representation of this ScreenPoint.
   */
    public String toString() {
    	return "ScreenPoint [ "+x+", "+y+" ]";
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

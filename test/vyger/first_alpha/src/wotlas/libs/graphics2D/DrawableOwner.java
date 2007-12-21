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

package wotlas.libs.graphics2D;


/** An interface drawables can implement to tell that they have an owner. For example
 *  take the case of an Ant class which is a SpriteDataSupplier for a Sprite representing
 *  an Ant. Because the wotlas.libs.graphics2D.drawable.Sprite implements the DrawableOwner
 *  interface, if you call the getOwner() method on the Ant's Sprite you get the Ant object.
 *
 *  This interface is especially useful for the GraphicsDirector : given a point on the screen
 *  it is able to find the sprite you target and then return to you the owner object. In our
 *  previous example it means you are able to get the Ant Object by clicking on its sprite.
 *
 *  So, if you create new Drawable Objects that have an owner AND if you want to be able
 *  to get it by a simple mouse click on the screen, implement this interface !
 *
 *  See the GraphicsDirector findOwner() method for more details.
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2D.ImageLibrary
 * @see wotlas.libs.graphics2D.ImageIdentifier
 */

public interface DrawableOwner {

 /*------------------------------------------------------------------------------------*/

   /** To get the owner of this drawable. By 'owner' we mean the object which this
    *  Drawable is the graphical representation.
    *
    * @return Object owner of this drawable.
    */
     public Object getOwner();

 /*------------------------------------------------------------------------------------*/

}

/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
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


/** Identifies an image in the ImageLibrary.
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2D.ImageLibrary
 */

public class ImageIdentifier {

 /*------------------------------------------------------------------------------------*/

  /** Image category ( Animals, Houses, SpaceShips ).
   */
    public short imageCategory;

  /** Image set ( White Cat, Mouse, Black Cat, etc... ).
   */
    public short imageSet;

  /** Image action ( Jumping white cat, Walking white cat, etc... ).
   */
    public short imageAction;

  /** Image index ( first image of jumping white cat, second image... etc )
   */
    public short imageIndex;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
   public ImageIdentifier() {
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with the first three ids. The last id ( imageIndex ) is set to 0.
   *
   * @param cat image category
   * @param set image set
   * @param act image action
   */
   public ImageIdentifier( short imCat, short imSet, short imAct) {
      imageCategory = imCat;
      imageSet = imSet;
      imageAction = imAct;
      imageIndex = 0;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Constructor with all ids.
   *
   * @param cat image category
   * @param set image set
   * @param act image action
   * @param ind image index
   */
   public ImageIdentifier( short imCat, short imSet, short imAct, short imInd ) {
      imageCategory = imCat;
      imageSet = imSet;
      imageAction = imAct;
      imageIndex = imInd;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Construction from an other ImageIdentifier.
   *
   * @param otherIm other ImageIdentifier to copy
   */
   public ImageIdentifier( ImageIdentifier otherIm ) {
      imageCategory = otherIm.imageCategory;
      imageSet = otherIm.imageSet;
      imageAction = otherIm.imageAction;
      imageIndex = otherIm.imageIndex;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  // Getters & Setters

   public short getImageCategory() { return this.imageCategory; }
   public short getImageSet() { return this.imageSet; }
   public short getImageAction() { return this.imageAction; }
   public short getImageIndex() { return this.imageIndex; }

   public void setImageCategory(short imCat) { this.imageCategory = imCat; }
   public void setImageSet(short imSet) { this.imageSet = imSet; }
   public void setImageAction(short imAct) { this.imageAction = imAct; }
   public void setImageIndex(short imInd) { this.imageIndex = imInd;}

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To String method.
   * @return string representation
   */
    public String toString() {
       return "ImageIdentifier c:"+imageCategory+", s:"+imageSet+", a:"+imageAction
              +", i:"+imageIndex;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

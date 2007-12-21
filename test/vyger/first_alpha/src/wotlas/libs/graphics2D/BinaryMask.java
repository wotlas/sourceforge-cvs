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

import java.awt.MediaTracker;
import java.awt.Image;
import java.awt.image.BufferedImage;


/** A small utility to create a mask (binary array) from an image<br>
 * white color => false<br>
 * other color => true<br>
 *
 * @author Petrus
 */
public class BinaryMask
{

 /*------------------------------------------------------------------------------------*/

  /** To create a mask
   * 
   * @param buffImg a buffered image
   * @return the created mask
   */
  static public boolean[][] create(BufferedImage buffImg) {
    int imgWidth = buffImg.getWidth();
    int imgHeight = buffImg.getHeight();
    boolean[][] mask = new boolean[imgWidth][imgHeight];

    for (int j=0; j<imgHeight; j++) {
      for (int i=0; i<imgWidth; i++) {
        mask[i][j] = (buffImg.getRGB(i, j) == -1) ? false : true;
      }
    }
    return mask;
  }
 
 /*------------------------------------------------------------------------------------*/

  /** To create a mask
   *
   * @param path image path
   * @return the created mask
   *
  static public boolean[][] create(String path) {
    BufferedImage maskBuffImg = ImageLibrary.loadBufferedImage(path);
    return create(maskBuffImg);
  } 

 /*------------------------------------------------------------------------------------*/
   
}
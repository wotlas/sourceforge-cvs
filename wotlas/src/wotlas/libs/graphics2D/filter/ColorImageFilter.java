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

package wotlas.libs.graphics2D.filter;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.filter.color.*;

import java.awt.image.*;
import java.awt.*;

/** A DynamicImageFilter that can change the colors of a BufferefImage. You can only
 *  change 'types' of colors : all blue pixels, all green pixels, ... many changes
 *  can be performed at the same time as the 'addColorChange' method can be called
 *  more than one time.
 *
 *  Actually this filter only works with fixed source colors and target colors. See
 *  the addColorChange javadoc for more details.
 *
 * @author Aldiss
 * @see wotlas.libs.graphics2D.DynamicImageFilter
 */

public class ColorImageFilter implements DynamicImageFilter {

 /*------------------------------------------------------------------------------------*/

   /** Blue Color Type
    */
      public final static ColorType blue = (ColorType) new BlueColor();

   /** Green Color Type
    */
      public final static ColorType green = (ColorType) new GreenColor();

   /** Yellow Color Type
    */
      public final static ColorType yellow = (ColorType) new YellowColor();

   /** Light Yellow Color Type
    */
      public final static ColorType lightYellow = (ColorType) new LightYellowColor();

   /** Red Color Type
    */
      public final static ColorType red = (ColorType) new RedColor();

   /** Brown Color Type
    */
      public final static ColorType brown = (ColorType) new BrownColor();

   /** White Color Type
    */
      public final static ColorType white = (ColorType) new WhiteColor();

   /** Light Gray Color Type
    */
      public final static ColorType lightgray = (ColorType) new LightGrayColor();

   /** Gray Color Type
    */
      public final static ColorType gray = (ColorType) new GrayColor();

   /** Dark Gray Color Type
    */
      public final static ColorType darkgray = (ColorType) new DarkGrayColor();

 /*------------------------------------------------------------------------------------*/

   /** Color Type couples (source & target) for our color change.
    */
      private ColorType colorChangeKey[][];

 /*------------------------------------------------------------------------------------*/

    /** To add a ColorChangeKey to this ColorImageFilter. How does it work ?
     *  well, this is simple. Here is an example :
     *
     *  addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.green );
     *
     *  With this key we'll transform all the blue pixels in green pixels.
     *
     * @param colorSourceId source ColorType
     * @param colorTargetId target ColorType
     */
      public void addColorChangeKey( ColorType colorSourceId, ColorType colorTargetId )
      {
         ColorType key[] = new ColorType[2];
         key[0] = colorSourceId;
         key[1] = colorTargetId;
    
         if (colorChangeKey == null) {
             colorChangeKey = new ColorType[1][];
             colorChangeKey[0] = key;
         } else {
             ColorType tmp[][] = new ColorType[colorChangeKey.length+1][];
             System.arraycopy(colorChangeKey, 0, tmp, 0, colorChangeKey.length);
             tmp[colorChangeKey.length] = key;
             colorChangeKey = tmp;
         }
      }

 /*------------------------------------------------------------------------------------*/

   /** To create a new filtered image from an image source.
    *
    * @param srcIm source BufferedImage we take our data from (not modified).
    * @return new BufferedImage constructed from the given image.
    */
     public BufferedImage filterImage( BufferedImage srcIm ){
     
          if( srcIm==null ) return null;
     
          int width = srcIm.getWidth();
          int height = srcIm.getHeight();

       // 1 - New Buffered Image
          if(colorChangeKey==null)
              return srcIm;
       
          BufferedImage dstIm = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

       // 2 - Color Filter
          for (int i=0; i<width; i++)
             for (int j=0; j<height; j++)
               dstIm.setRGB( i, j, filterPixel( srcIm.getRGB(i,j) ) );

          return dstIm;
     }

 /*------------------------------------------------------------------------------------*/

    /** To filter a pixel according to our ColorChange keys...
     *  @param argb pixel color
     */
       private int filterPixel( int argb ) {

       	   short alpha = getAlpha( argb );

           if( alpha == 0 )
               return argb; // transparent pixel

           short min=getRed( argb ), mid=getGreen( argb ), max=getBlue( argb ),tmp;

        // 1 - Color to remplace ?
           byte keyID = -1;
        
           for( byte i=0; i<colorChangeKey.length; i++)
                if( colorChangeKey[i][0].isFromThisColorType( min, mid, max ) ) {
                       keyID = i;
                       break;
                }

           if(keyID==-1)
              return argb;

        // 2 - We sort our min, mid, max luminosity components
           if(min>mid) {
              tmp = min;
              min = mid;
              mid = tmp;
           }
        
           if(max<mid) {
              tmp = max;
              max = mid;
              mid = tmp;
           }

           if(min>mid) {
              tmp = min;
              min = mid;
              mid = tmp;
           }

       // 3 - Color replace
          return colorChangeKey[keyID][1].setToColorType(min, mid, max) | (alpha << 24);
       }

 /*------------------------------------------------------------------------------------*/

     /** To get the blue component of a argb pixel.
      * @param argb pixel of the DirectColorModel type.
      * @return the blue component in the [0,255] range
      */
       private short getBlue( int argb ) {
           return (short) (argb & 0xff);
       }

 /*------------------------------------------------------------------------------------*/

     /** To get the green component of a argb pixel.
      * @param argb pixel of the DirectColorModel type.
      * @return the green component in the [0,255] range
      */
       private short getGreen( int argb ) {
          return (short) ((argb & 0xff00) >> 8 );
       }

 /*------------------------------------------------------------------------------------*/

     /** To get the red component of a argb pixel.
      * @param argb pixel of the DirectColorModel type.
      * @return the red component in the [0,255] range
      */
       private short getRed( int argb ) {
          return (short) ((argb & 0xff0000) >> 16 );
       }

 /*------------------------------------------------------------------------------------*/

     /** To get the alpha component of a argb pixel.
      * @param argb pixel of the DirectColorModel type.
      * @return the alpha component in the [0,255] range
      */
       private short getAlpha( int argb ) {
          return (short) ((argb & 0xff000000) >> 24 );
       }

 /*------------------------------------------------------------------------------------*/

}

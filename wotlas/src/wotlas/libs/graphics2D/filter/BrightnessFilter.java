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

package wotlas.libs.graphics2D.filter;

import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.filter.color.*;

import java.awt.image.*;
import java.awt.*;

/** A DynamicImageFilter that can change the brightness of a BufferefImage.
 *
 * @author Petrus
 * @see wotlas.libs.graphics2D.DynamicImageFilter
 */

public class BrightnessFilter implements DynamicImageFilter {

 /*------------------------------------------------------------------------------------*/   

   /** Color Type couples (source & target) for our color change.
    */
      static private short[][] brightnessMask;
            
      static private int tilesize;
      
      private short brightness;

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
          if(brightnessMask==null)
              return srcIm;
       
          BufferedImage dstIm = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

       // 2 - Brightness Filter
          for (int i=0; i<width; i++)
             for (int j=0; j<height; j++)
               dstIm.setRGB( i, j, filterPixel( srcIm.getRGB(i,j) ) );

          return dstIm;
     }
     
 /*------------------------------------------------------------------------------------*/

   /** To set the brightness mask
    *
    * m_brightnessMask the gray scale brightness mask
    * m_tilesize cell size of the mask (in pixels)
    */
     static public void setBrightnessMask(short[][] m_brightnessMask, int m_tilesize){
          brightnessMask = m_brightnessMask;
          tilesize = m_tilesize;
     }
   
   /** To set the brightness parameter
    *
    * @param x x coordinate of pixel
    * @param y y coordinate of pixel    
    */          
     public void setBrightness(float x, float y) {
        if (brightnessMask!=null)
            brightness = brightnessMask[(int) (x/tilesize)][(int) (y/tilesize)];
     }
     

 /*------------------------------------------------------------------------------------*/

    /** To filter a pixel according to our brightness value
     *  @param argb pixel color
     */
       private int filterPixel( int argb ) {

       	   short alpha = getAlpha( argb );

           if( alpha == 0 )
               return argb; // transparent pixel
	   	   
           short redIndex   = getRed( argb );
           short blueIndex  = getBlue( argb );
           short greenIndex = getGreen( argb );
           
           float[] hsbvals = new float[3];
           Color.RGBtoHSB(redIndex, greenIndex, blueIndex, hsbvals);
           
           // Color replace
           float newBrightness = hsbvals[2]-brightness;
           if (newBrightness<0) {
           	newBrightness = 0;
           } else {
           	if (newBrightness>1) {
           		newBrightness = 1;
           	}
           }
           return Color.HSBtoRGB(hsbvals[0], hsbvals[1], newBrightness) | (alpha << 24);
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

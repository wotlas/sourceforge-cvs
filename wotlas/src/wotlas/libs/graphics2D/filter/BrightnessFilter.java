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
      static private byte[][] brightnessMask;
            
      static private int tilesize;
      
      private float brightness;

 /*------------------------------------------------------------------------------------*/

   /** To create a new filtered image from an image source.
    *
    * @param srcIm source BufferedImage we take our data from (not modified).
    * @return new BufferedImage constructed from the given image.
    */
     public BufferedImage filterImage( BufferedImage srcIm ){
     
          if( srcIm==null ) return null;
     
       // 1 - New Buffered Image
          if(brightnessMask==null || brightness==0)
              return srcIm;
          
          int width = srcIm.getWidth();
          int height = srcIm.getHeight();
       
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
     static public void setBrightnessMask(byte[][] m_brightnessMask, int m_tilesize){
          brightnessMask = m_brightnessMask;
          tilesize = m_tilesize;
     }
   
   /** To set the brightness parameter
    *
    * @param x x coordinate of pixel
    * @param y y coordinate of pixel    
    */          
     public void setBrightness(float x, float y) {
     	int xb = (int) (x/tilesize);
     	int yb = (int) (y/tilesize);

        if(xb<0) xb=0;
        if(yb<0) yb=0;
        if(xb>=brightnessMask.length) xb=brightnessMask.length-1;
        if(yb>=brightnessMask[0].length) yb=brightnessMask[0].length-1;

        if (brightnessMask!=null)
            brightness = ((float) brightnessMask[xb][yb])/255;
     }
     

 /*------------------------------------------------------------------------------------*/

    /** To filter a pixel according to our brightness value
     *  @param argb pixel color
     */
       private int filterPixel( int argb ) {

       	   short alpha = Converter.getAlpha( argb );

           if( alpha == 0 )
               return argb; // transparent pixel
	   	   
           short redIndex   = Converter.getRed( argb );
           short blueIndex  = Converter.getBlue( argb );
           short greenIndex = Converter.getGreen( argb );
           
           float[] hsbvals = new float[3];
           Color.RGBtoHSB(redIndex, greenIndex, blueIndex, hsbvals);
           
           // Color replace
           float newBrightness = hsbvals[2]+brightness;
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

}

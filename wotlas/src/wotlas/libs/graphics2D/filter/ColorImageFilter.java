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

import java.awt.image.*;
import java.awt.*;

/** 
 * @author Aldiss
 * @see wotlas.libs.graphics2D.ImageLibrary
 * @see wotlas.libs.graphics2D.ImageIdentifier
 */

public class ColorImageFilter implements DynamicImageFilter {

 /*------------------------------------------------------------------------------------*/

   /**
    */

 /*------------------------------------------------------------------------------------*/

   /** To create a new filtered image from an image source.
    *
    * @return new BufferedImage constructed from the given image.
    */
     public BufferedImage filterImage( BufferedImage srcIm ){
     
          if( srcIm==null ) return null;
     
          int width = srcIm.getWidth();
          int height = srcIm.getHeight();

       // 1 - New Buffered Image
          BufferedImage dstIm = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

       // 2 - Color Filter
          for (int i=0; i<width; i++)
             for (int j=0; j<height; j++)
               dstIm.setRGB( i, j, filterPixel( srcIm.getRGB(i,j) ) );

          return dstIm;
     }

 /*------------------------------------------------------------------------------------*/

       private int filterPixel( int argb ) {
       	   short alpha = getAlpha( argb );

           if( alpha == 0 ) return argb; // transparent pixel

           short min=getRed( argb ), mid=getGreen( argb ), max=getBlue( argb ),tmp;

           if( !isBlue( min, mid, max ) )
               return argb;

          // sort min, mid, max      	
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

          return setToGreen(min, mid, max) | 0xff000000;
       }

 /*------------------------------------------------------------------------------------*/

       private boolean isBlue( short r, short g, short b ) {
       	     if( b > g && b > r )
       	         return true;
       	     return false;
       }

 /*------------------------------------------------------------------------------------*/

       private boolean isYellow( short r, short g, short b ) {
       	     if( r > g && b < 70 && b<g && r-g<=30 )
       	         return true;
       	     return false;
       }

 /*------------------------------------------------------------------------------------*/

       private int setToGreen( short min, short mid, short max ) {
             return (mid<<16) | (max<<8) | min;       	     	
       }

 /*------------------------------------------------------------------------------------*/

       private short getBlue( int argb ) {
           return (short) (argb & 0xff);
       }

 /*------------------------------------------------------------------------------------*/

       private short getGreen( int argb ) {
          return (short) ((argb & 0xff00) >> 8 );
       }

 /*------------------------------------------------------------------------------------*/

       private short getRed( int argb ) {
          return (short) ((argb & 0xff0000) >> 16 );
       }

 /*------------------------------------------------------------------------------------*/

       private short getAlpha( int argb ) {
          return (short) ((argb & 0xff000000) >> 24 );
       }

 /*------------------------------------------------------------------------------------*/

}

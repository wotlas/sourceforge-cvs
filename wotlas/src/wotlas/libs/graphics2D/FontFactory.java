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

import java.awt.Font;
import java.io.*;
import java.util.Hashtable;


/** This class gives access to fonts. It's a singleton because we need to be able to
 *  access to it from anywhere : swing classes, drawables, etc...
 *
 *  Of course you are free if you don't want to use the singleton pattern here.
 *
 * @author Aldiss
 */

public class FontFactory {

 /*------------------------------------------------------------------------------------*/

  /** Debug mode
   */
     public static boolean DEBUG_MODE = false;

  /** Default factory.
   */
     private static FontFactory defaultFontFactory;

 /*------------------------------------------------------------------------------------*/

  /** Our font table where we store fonts by their name.
   */
     protected Hashtable fonts;

  /** Path where user fonts are stored (ex: ../base/fonts that contains "Lucida.ttf").
   */
     protected String userFontPath;

  /** Optional font resource locator to tell us where to take fonts.
   */
     protected FontResourceLocator resourceLocator;

 /*------------------------------------------------------------------------------------*/

  /** To create the default factory.
   * @param userFontPath where the user fonts are stored
   */
    public static FontFactory createDefaultFontFactory( FontResourceLocator resourceLocator ) {
    	if(defaultFontFactory==null)
    	   defaultFontFactory = new FontFactory(resourceLocator);
    	return defaultFontFactory;
    }

 /*------------------------------------------------------------------------------------*/

  /** To create the default factory.
   * @param userFontPath where the user fonts are stored
   */
    public static FontFactory createDefaultFontFactory( String userFontPath ) {
    	if(defaultFontFactory==null)
    	   defaultFontFactory = new FontFactory(userFontPath);
    	return defaultFontFactory;
    }

 /*------------------------------------------------------------------------------------*/

  /** To get the default font factory.
   * @return null if there are none
   */
    public static FontFactory getDefaultFontFactory() {
    	return defaultFontFactory;
    }

 /*------------------------------------------------------------------------------------*/

  /** Constructor with font resource locator.
   * @param resourceLocator to get access to fonts
   */
    protected FontFactory( FontResourceLocator resourceLocator ) {
    	fonts = new Hashtable(5);
        this.resourceLocator = resourceLocator;
        init();
    }

 /*------------------------------------------------------------------------------------*/

  /** Constructor with user font path.
   * @param userFontPath where the user fonts are stored
   */
    protected FontFactory( String userFontPath ) {
    	fonts = new Hashtable(5);
        this.userFontPath = userFontPath;
        init();
    }

 /*------------------------------------------------------------------------------------*/

  /** To init the factory with the available fonts.
   */
    public void init() {

       /**
        **  DECLARE HERE YOUR FONTS.
        **
        **  Note : this manual load is temporary, when I'll have time I'll implement
        **  a simple dynamic font download method and suppress manual declaration.
        **/
           addFont( new Font("Dialog", Font.PLAIN, 10) );
           addFont( loadUserFont("Lblack.ttf") );
    }

 /*------------------------------------------------------------------------------------*/

   /** Adds a new font to our table.
    */
    protected void addFont( Font f ) {
          if(f==null)
             return;

          if( fonts.containsKey(f.getFontName()) ) {
              if(DEBUG_MODE)
                 System.out.println("Font already exists ! "+f.getFontName() );
              return;
          }

          fonts.put( f.getFontName(), f );

          if(DEBUG_MODE)
             System.out.println("Added font: "+f.getFontName());
    }

 /*------------------------------------------------------------------------------------*/

  /** To get a font
   *  @param fontName the font name as returned by the Font.getFontName() method.
   *  @return the wanted font, the "Dialog" font if the specified font is not found.
   */
    public Font getFont( String fontName ) {

       if( !fonts.containsKey(fontName) ) {
           if(DEBUG_MODE)
              System.out.println( "Font "+fontName +" not found !" );

           return (Font) fonts.get("dialog");
       }

       return (Font) fonts.get( fontName );
    }

 /*------------------------------------------------------------------------------------*/

  /** To load a font from the user font directory.
   *
   * @param fontFileName font file name in the user directory.
   */
    protected Font loadUserFont( String fontFileName ) {

         if(resourceLocator==null) {
            try {
                File file = new File(userFontPath+File.separator+fontFileName);
                FileInputStream fis = new FileInputStream(file);
                return Font.createFont(Font.TRUETYPE_FONT, fis);
            }
            catch (Exception e) {
      	        e.printStackTrace();

                if(DEBUG_MODE)
                   System.out.println("Failed to load font from file : "+fontFileName);

                return null;
            }
         }
         else {
            InputStream is = resourceLocator.getFontStream(fontFileName);
            if(is==null) return null;
            try{
                return Font.createFont(Font.TRUETYPE_FONT, is);
            }
            catch (Exception e) {
      	        e.printStackTrace();

                if(DEBUG_MODE)
                   System.out.println("Failed to load font from file : "+fontFileName);

                return null;
            }
         }
    }

 /*------------------------------------------------------------------------------------*/

}

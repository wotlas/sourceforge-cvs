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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
    public static FontFactory createDefaultFontFactory(FontResourceLocator resourceLocator) {
        if (FontFactory.defaultFontFactory == null)
            FontFactory.defaultFontFactory = new FontFactory(resourceLocator);
        return FontFactory.defaultFontFactory;
    }

    /*------------------------------------------------------------------------------------*/

    /** To create the default factory.
     * @param userFontPath where the user fonts are stored
     */
    public static FontFactory createDefaultFontFactory(String userFontPath) {
        if (FontFactory.defaultFontFactory == null)
            FontFactory.defaultFontFactory = new FontFactory(userFontPath);
        return FontFactory.defaultFontFactory;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the default font factory.
     * @return null if there are none
     */
    public static FontFactory getDefaultFontFactory() {
        return FontFactory.defaultFontFactory;
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with font resource locator.
     * @param resourceLocator to get access to fonts
     */
    protected FontFactory(FontResourceLocator resourceLocator) {
        this.fonts = new Hashtable(5);
        this.resourceLocator = resourceLocator;
        init();
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with user font path.
     * @param userFontPath where the user fonts are stored
     */
    protected FontFactory(String userFontPath) {
        this.fonts = new Hashtable(5);
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
        addFont(null, new Font("Dialog", Font.PLAIN, 10));
        Font ftLucida = loadUserFont("Lblack.ttf");
        addFont("Lucida Blackletter Regular", ftLucida);
        addFont("Lucida Blackletter", ftLucida);
    }

    /*------------------------------------------------------------------------------------*/

    /** Adds a new font to our table.
     */
    protected void addFont(String name, Font f) {
        if (f == null)
            return;

        if (this.fonts.containsKey(f.getFontName()) || (name != null && this.fonts.containsKey(name))) {
            if (FontFactory.DEBUG_MODE)
                System.out.println("Font already exists ! " + f.getFontName());
            return;
        }

        if (name == null) {
            this.fonts.put(f.getFontName(), f);
        } else {
            this.fonts.put(name, f);
        }

        if (FontFactory.DEBUG_MODE)
            System.out.println("Added font: " + f.getFontName());
    }

    /*------------------------------------------------------------------------------------*/

    /** To get a font
     *  @param fontName the font name as returned by the Font.getFontName() method.
     *  @return the wanted font, the "Dialog" font if the specified font is not found.
     */
    public Font getFont(String fontName) {

        if (!this.fonts.containsKey(fontName)) {
            if (FontFactory.DEBUG_MODE)
                System.out.println("Font " + fontName + " not found !");

            return (Font) this.fonts.get("dialog");
        }

        return (Font) this.fonts.get(fontName);
    }

    /*------------------------------------------------------------------------------------*/

    /** To load a font from the user font directory.
     *
     * @param fontFileName font file name in the user directory.
     */
    protected Font loadUserFont(String fontFileName) {

        if (this.resourceLocator == null) {
            try {
                File file = new File(this.userFontPath, fontFileName);
                FileInputStream fis = new FileInputStream(file);
                return Font.createFont(Font.TRUETYPE_FONT, fis);
            } catch (Exception e) {
                e.printStackTrace();

                if (FontFactory.DEBUG_MODE)
                    System.out.println("Failed to load font from file : " + fontFileName);

                return null;
            }
        } else {
            InputStream is = this.resourceLocator.getFontStream(fontFileName);
            if (is == null)
                return null;
            try {
                return Font.createFont(Font.TRUETYPE_FONT, is);
            } catch (Exception e) {
                e.printStackTrace();

                if (FontFactory.DEBUG_MODE)
                    System.out.println("Failed to load font from file : " + fontFileName);

                return null;
            }
        }
    }

    /*------------------------------------------------------------------------------------*/

}

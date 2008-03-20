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

package wotlas.libs.graphics2d.drawable;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.util.Map;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.FontFactory;
import wotlas.libs.graphics2d.ImageLibrary;

/** A MultiLineText is used to display motionless text ons screen.
 *
 * @author MasterBob, Aldiss, Petrus
 */

public class MultiLineText extends Drawable {

    /*------------------------------------------------------------------------------------*/

    /** Alignement of the text
     */
    static public short LEFT_ALIGNMENT = 0;
    static public short RIGHT_ALIGNMENT = 1;

    /** Default Font Name used.
     */
    static private String defaultFontName = "Lucida Blackletter Regular";

    /*------------------------------------------------------------------------------------*/

    /** The text to write
     */
    private String[] text;

    /** The color of the text
     */
    private Color color = Color.black;

    /** The name of the font used
     */
    private String fontName;

    /** The font used
     */
    private Font font;

    /** determine if the text is fix or not in the screen (true)
     *  xs et ys define where the text will be written in the screen only if fix = true otherwise he will be written at x an y on the map coordinate
     *  note that in that case the x,y,with and height show a laction in whitch if there is a contact with the screen
     *  the text will be xriten at the  xs and ys location
     *  s for screen coordinate
     */
    int xs;
    int ys;

    /** the dimension of the text.
    */
    private float size = 20.0f;

    /** True if we must recalculate text width & height
     */
    private boolean recalculate;

    /** Text width
     */
    private int widthText = 0;

    /** Space between lines
     */
    private int gap;

    /** Space around the text
     */
    private int textSpace = 6;

    /** y coordinate of each line
     */
    private int heightsText[];

    /** True if text is left aligned (default)
     */
    private boolean isLeftAligned = true;

    /*------------------------------------------------------------------------------------*/

    /** To get the default font name.
     */
    static public String getDefaultFontName() {
        return MultiLineText.defaultFontName;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the default font name.
     */
    static public void setDefaultFontName(String fontName) {
        MultiLineText.defaultFontName = fontName;
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor. We use the default font (see static getter/setter). 
     *
     * @param xs MultiLineText's xs top left corner of the text
     * @param ys MultiLineText's ys top left corner of the text
     * @param priority MultiLineText's priority
     */
    public MultiLineText(String[] text, int xs, int ys, short priority) {
        this(text, xs, ys, Color.black, 20.0f, MultiLineText.defaultFontName, priority, MultiLineText.LEFT_ALIGNMENT);
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     *
     * @param xs space between top left corner and left border if LEFT_ALIGNMENT<br>
     *        or space between top right corner and right border if RIGHT_ALIGNMENT
     * @param ys MultiLineText's ys top left corner of the text
     * @param text MultiLineText's text
     * @param color MultiLineText's color
     * @param size MultiLineText's size
     * @param font MultiLineText's font
     * @param priority MultiLineText's priority
     * @param alignment LEFT_ALIGNMENT or RIGHT_ALIGNMENT
     */
    public MultiLineText(String[] text, int xs, int ys, Color color, float size, String font, short priority, short alignment) {
        super();
        this.text = text;
        this.recalculate = true;
        this.xs = xs;
        this.ys = ys;
        this.fontName = font;
        this.size = size;
        setColor(color);
        this.priority = priority;
        useAntialiasing(true);
        this.isLeftAligned = (alignment == MultiLineText.LEFT_ALIGNMENT);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To initialize this drawable with the ImageLibrary. Don't call it yourself ! it's
     *  done automatically when you call addDrawable on the GraphicsDirector.
     *
     *  IF you need the ImageLib for some special inits just extend this method and don't
     *  forget to call a super.init(imageLib) !
     *
     *  @param imagelib ImageLibrary where you can take the images to display.
     */
    @Override
    protected void init(ImageLibrary imageLib) {
        super.init(imageLib);

        setFont(this.fontName);
        setSize(this.size);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     * define the text
     */
    public void setText(String[] text) {
        this.text = text;
        this.recalculate = true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     * define the color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     * define the size
     */
    public void setSize(float size) {
        this.size = size;
        this.font = this.font.deriveFont(Font.PLAIN, size);
        Map<TextAttribute, ?> fontAttributes = this.font.getAttributes();
        this.recalculate = true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the current font.
     */
    public void setFont(String fontName) {
        this.font = FontFactory.getDefaultFontFactory().getFont(fontName);
        this.font = this.font.deriveFont(Font.PLAIN, this.size);
        Map<TextAttribute, ?> fontAttributes = this.font.getAttributes();
        this.recalculate = true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Paint method called by the GraphicsDirector. The specified rectangle represents
     *  the displayed screen in background cordinates ( see GraphicsDirector ).
     *
     *  @param gc graphics 2D use for display (double buffering is handled by the
     *         GraphicsDirector)
     *  @param screen display zone of the graphicsDirector, in background coordinates.
     */
    @Override
    public void paint(Graphics2D gc, Rectangle screen) {

        if (this.font != null) {
            gc.setFont(this.font);
        }

        if (this.text == null) {
            return;
        }

        if (this.recalculate) {
            this.heightsText = null;
            this.heightsText = new int[this.text.length];

            FontRenderContext frc = gc.getFontRenderContext();
            TextLayout t;
            
            if (this.text[0].length() > 0) {
             	t= new TextLayout(this.text[0], gc.getFont(), frc);
	            this.heightsText[0] = (int) t.getBounds().getHeight();
    	        this.widthText = (int) t.getBounds().getWidth();
        	    gc.setColor(this.color);

            	if (this.isLeftAligned) {
                	gc.drawString(this.text[0], this.xs, this.ys + this.heightsText[0]);
	            } else {
    	            gc.drawString(this.text[0], (int) screen.getWidth() - this.xs - this.widthText, this.ys + this.heightsText[0]);
	            }

    	        this.gap = this.heightsText[0] / 2; //spaces between lines (half height of the text)
   	        }

            for (int i = 1; i < this.text.length; i++) {
                if (this.text[i].length() > 0) {
	                t = new TextLayout(this.text[i], gc.getFont(), frc);

	                if (((int) t.getBounds().getWidth()) > this.widthText) {
    	                this.widthText = ((int) t.getBounds().getWidth());
        	        }

	                this.heightsText[i] = this.heightsText[i - 1] + (int) t.getBounds().getHeight() + this.gap;

	                if (this.isLeftAligned) {
    	                gc.drawString(this.text[i], this.xs, this.ys + this.heightsText[i]);
        	        } else {
            	        gc.drawString(this.text[i], (int) screen.getWidth() - this.xs - this.widthText, this.ys + this.heightsText[i]);
                	}
                }
            }

            this.r.width = this.widthText + 12;
            this.r.height = this.heightsText[this.text.length - 1] + 10;
            if (this.isLeftAligned) {
                this.r.x = this.xs;
            } else {
                this.r.x = (int) screen.getWidth() - this.xs;
            }
            this.r.y = this.ys;
            this.recalculate = false;
        } else {
            // Display background rectangle
            gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            gc.setColor(Color.white);

            if (this.isLeftAligned) {
                gc.fillRect(this.xs - 6, this.ys - 3, this.r.width, this.r.height);
                gc.setColor(Color.black);
                gc.draw3DRect(this.xs - 6, this.ys - 3, this.r.width, this.r.height, false);
            } else {
                gc.fillRect((int) screen.getWidth() - this.xs - this.widthText - 6, this.ys - 3, this.r.width, this.r.height);
                gc.setColor(Color.black);
                gc.draw3DRect((int) screen.getWidth() - this.xs - this.widthText - 6, this.ys - 3, this.r.width, this.r.height, false);
            }

            gc.setComposite(AlphaComposite.SrcOver); // restore

            // display text
            gc.setColor(this.color);

            if (this.isLeftAligned) {
                for (int i = 0; i < this.text.length; i++) {
                    gc.drawString(this.text[i], this.xs, this.ys + this.heightsText[i]);
                }
            } else {
                for (int i = 0; i < this.text.length; i++) {
                    gc.drawString(this.text[i], (int) screen.getWidth() - this.xs - this.widthText, this.ys + this.heightsText[i]);
                }
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Tick method called by the GraphicsDirector. This tick method has a returned value
     *  that indicates if the drawable is still living or must be deleted. Some Drawables
     *  always return "still living", it is then the task of the program that uses
     *  the GraphicsDirector to manage the destruction of drawables.
     *
     *  @return true if the drawable is "live", false if it must be deleted.
     */
    @Override
    public boolean tick() {
        return true; // no update needed and a MotionlessSprite is always "live" by default.
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

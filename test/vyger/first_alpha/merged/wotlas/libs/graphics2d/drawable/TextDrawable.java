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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.Map;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.FontFactory;
import wotlas.libs.graphics2d.ImageLibrary;

/** A Drawable to display text at the top of another Drawable. To display text at an
 *  absolute position on screen use the MutiLineText drawable.
 *
 * @author MasterBob, Aldiss, Petrus
 */

public class TextDrawable extends Drawable {

    /*------------------------------------------------------------------------------------*/

    /** To set High Quality / Medium Quality text display
     */
    private static boolean highQualityTextDisplay = false;

    /** Default Font Name used.
     */
    static private String defaultFontName = "Lucida Blackletter Regular";

    /*------------------------------------------------------------------------------------*/

    /** Text to write
     */
    private String text;

    /** the color of the text
     */
    private Color color;

    /** Font to use.
     */
    private Font font;

    /** The name of the font used
     */
    private String fontName;

    /** Text Font Size. .
    */
    private float size;

    /** Drawable with which we are linked. helps to define our x and y and to display if
     *  other drawable display...
     */
    private Drawable refDrawable = null;

    /** TimeStamp indicating when we'll need to remove our drawable from screen.
     *  If -1 we have infinite life. The TextDrawable must be removed manually.
     */
    private long timeLimit;

    /** True if we must calculate text width and height
     */
    private boolean recalculate;

    /** Text width / 2
     */
    private int demiWidthText = 0;

    /** Text height
     */
    private int heightText = 0;

    /** Life time of text.
     * If -1 we have infinite life. The TextDrawable must be removed manually.
     */
    private int lifeTime;

    /** TextLayout to use to draw the text shape...
     */
    private TextLayout t;

    /*------------------------------------------------------------------------------------*/

    /** To get if this TextDrawable must be high/medium quality
     */
    public static boolean getHighQualityTextDisplay() {
        return TextDrawable.highQualityTextDisplay;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set if this TextDrawable must be high/medium quality
     */
    public static void setHighQualityTextDisplay(boolean highQualityTextDisplay) {
        TextDrawable.highQualityTextDisplay = highQualityTextDisplay;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the default font name.
     */
    static public String getDefaultFontName() {
        return TextDrawable.defaultFontName;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the default font name.
     */
    static public void setDefaultFontName(String fontName) {
        TextDrawable.defaultFontName = fontName;
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with drawable to use as reference. We use the default font name.
     *  The default color is black & size 12.
     *
     *  The TextDrawable has infinite life.
     *
     * @param text textDrawble's text
     * @param refDrawable textDrawble's refDrawable
     * @param priority textDrawble's priority
     */
    public TextDrawable(String text, Drawable refDrawable, short priority) {
        this(text, refDrawable, Color.black, 13.0f, TextDrawable.defaultFontName, priority, -1);
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with our reference Drawable, text color, size, fontName.
     *
     *  The TextDrawable has infinite life.
     *
     * @param text textDrawble's text
     * @param refDrawable textDrawble's refDrawable
     * @param color textDrawble's color
     * @param size textDrawble's size
     * @param fontName textDrawble's fontName
     * @param priority textDrawble's priority
     * @param lifeTime time duration this TestDrawable shows on screen (in ms).
     */
    public TextDrawable(String text, Drawable refDrawable, Color color, float size, String fontName, short priority) {
        this(text, refDrawable, color, size, fontName, priority, -1);
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with our reference Drawable, text color, size, fontName & lifeTime.
     *
     * @param text textDrawble's text
     * @param refDrawable textDrawble's refDrawable
     * @param color textDrawble's color
     * @param size textDrawble's size
     * @param fontName textDrawble's fontName
     * @param priority textDrawble's priority
     * @param lifeTime time duration this TextDrawable is shown on screen (in ms). The drawable
     *        is automatically removed after that time period.
     */
    public TextDrawable(String text, Drawable refDrawable, Color color, float size, String fontName, short priority, int lifeTime) {
        super();
        this.text = text;
        this.recalculate = true;
        setReferenceDrawable(refDrawable);
        this.fontName = fontName;
        this.size = size;
        setColor(color);
        this.priority = priority;
        this.timeLimit = System.currentTimeMillis() + lifeTime;
        this.lifeTime = lifeTime;
        useAntialiasing(true);
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
     *  Define our reference drawable;
     */
    public void setReferenceDrawable(Drawable refDrawable) {
        this.refDrawable = refDrawable;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     * define the text;
     */
    public void setText(String text) {
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
     * define the font size.
     */
    public void setSize(float size) {
        this.size = size;
        this.font = this.font.deriveFont(Font.PLAIN, size);
        Map<TextAttribute, ?> fontAttributes = this.font.getAttributes();
        this.recalculate = true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     * To set the font for this Drawable.
     */
    public void setFont(String fontName) {
        this.font = FontFactory.getDefaultFontFactory().getFont(fontName);
        this.font = this.font.deriveFont(Font.PLAIN, this.size);
        Map<TextAttribute, ?> fontAttributes = this.font.getAttributes();
        this.recalculate = true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     * To get the current font.
     */
    public Font getFont() {
        return this.font;
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

        //        if( !r.intersects(screen) )
        //            return;

        if (this.font != null) {
            gc.setFont(this.font);
        }

        if (this.text != null && this.text.length() != 0) {
            if (this.recalculate) {
                FontRenderContext frc = gc.getFontRenderContext();
                this.t = new TextLayout(this.text, gc.getFont(), frc);
                this.r.width = this.refDrawable.getWidth();
                this.r.height = this.refDrawable.getHeight();
                this.demiWidthText = (int) this.t.getBounds().getWidth() / 2;
                this.heightText = (int) this.t.getBounds().getHeight();
                this.recalculate = false;
            }

            if (!TextDrawable.highQualityTextDisplay) {
                // transparent rectangle
                // BLOCKED BY DIEGO, ONLY TO SEE WHT HAPPEN
                gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
                gc.setColor(Color.white);
                gc.fillRect(this.r.x - screen.x + (this.r.width / 2) - this.demiWidthText, this.r.y - screen.y - this.heightText, 2 * this.demiWidthText + 2, this.heightText);
                gc.setComposite(AlphaComposite.SrcOver); // suppressing alpha

                // drawing text...
                gc.setColor(this.color);
                //   gc.setColor(Color.black);
                gc.drawString(this.text, this.r.x - screen.x + (this.r.width / 2) - this.demiWidthText, this.r.y - screen.y - 2);
            } else {
                Shape sha = this.t.getOutline(AffineTransform.getTranslateInstance(this.r.x - screen.x + (this.r.width / 2) - this.demiWidthText, this.r.y - screen.y - 2));
                gc.setColor(new Color(30, 30, 30));
                gc.setStroke(new BasicStroke(1.8f));
                gc.draw(sha);
                gc.setColor(this.color);
                gc.fill(sha);
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

        if (this.refDrawable != null) {
            this.r.x = this.refDrawable.getX();
            this.r.y = this.refDrawable.getY();
        }

        // persistent TextDrawable
        if (this.lifeTime == -1) {
            return true;
        }

        if (this.timeLimit < 0) {
            return true;
        }

        if (this.timeLimit - System.currentTimeMillis() < 0) {
            return false;
        }
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns true if the text is still displayed on screen
     */
    public boolean isLive() {
        // persistent TextDrawable
        if (this.lifeTime == -1) {
            return true;
        }

        if (this.timeLimit - System.currentTimeMillis() < 0) {
            return false;
        }
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To reset the current time limit of display.
     */
    public void resetTimeLimit() {
        this.timeLimit = System.currentTimeMillis() + this.lifeTime;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

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

package wotlas.libs.graphics2D.drawable;

import wotlas.libs.graphics2D.*;
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.font.*;

/** A MultiLineText is used to display motionless text ons screen.
 *
 * @author MasterBob, Aldiss, Petrus
 */

public class MultiLineText extends Drawable {

 /*------------------------------------------------------------------------------------*/

   /** Alignement of the text
    */
    static public short LEFT_ALIGNMENT  = 0;
    static public short RIGHT_ALIGNMENT = 1;

   /** Default Font Name used.
    */
    static private String defaultFontName = "Lblack.ttf";

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
     	return defaultFontName;
     }

 /*------------------------------------------------------------------------------------*/

    /** To set the default font name.
     */
     static public void setDefaultFontName( String fontName ) {
     	defaultFontName = fontName;
     }

 /*------------------------------------------------------------------------------------*/

  /** Constructor. We use the default font (see static getter/setter). 
   *
   * @param xs MultiLineText's xs top left corner of the text
   * @param ys MultiLineText's ys top left corner of the text
   * @param priority MultiLineText's priority
   */
    public MultiLineText( String[] text, int xs, int ys, short priority) {
    	this(text,xs,ys,Color.black,20.0f,defaultFontName,priority,LEFT_ALIGNMENT);
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
    public MultiLineText( String[] text, int xs, int ys, Color color, float size, String font, short priority, short alignment) {
    	super();
        this.text = text;
        recalculate = true;
        this.xs = xs;
        this.ys = ys;
        fontName = font;
        this.size=size;
        setColor(color);
        this.priority = priority;
        useAntialiasing(true);
        isLeftAligned = (alignment == LEFT_ALIGNMENT);
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
     protected void init( ImageLibrary imageLib ) {
     	super.init(imageLib);
     	
     	setFont(fontName);
        setSize(size);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /**
  * define the text
  */
  public void setText(String[] text) {
   this.text = text;
   recalculate = true;
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
    font = font.deriveFont(Font.PLAIN, size);
    Map fontAttributes = font.getAttributes();
    recalculate = true;
   }



 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /**
  * To set the current font.
  */
  public void setFont(String fontName) {
    try {
      String fontPath = getImageLibrary().getUserFontPath();

      FileInputStream fis = new FileInputStream(fontPath+File.separator+fontName);
      font = Font.createFont(Font.TRUETYPE_FONT, fis);
      font = font.deriveFont(Font.PLAIN, size);
      Map fontAttributes = font.getAttributes();
      recalculate = true;
    } catch (Exception e) {
      font = new Font("Dialog", Font.PLAIN, (int)size);
      e.printStackTrace();
    }
  }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Paint method called by the GraphicsDirector. The specified rectangle represents
   *  the displayed screen in background cordinates ( see GraphicsDirector ).
   *
   *  @param gc graphics 2D use for display (double buffering is handled by the
   *         GraphicsDirector)
   *  @param screen display zone of the graphicsDirector, in background coordinates.
   */
    public void paint( Graphics2D gc, Rectangle screen ) {

        if (font != null)
          gc.setFont(font);

        if(text == null)
           return;

        if (recalculate) {
              heightsText = null;
              heightsText = new int[text.length];

              FontRenderContext frc = gc.getFontRenderContext();

              TextLayout t = new TextLayout(text[0],gc.getFont(),frc);
              heightsText[0] = (int) t.getBounds().getHeight();
              widthText      = (int) t.getBounds().getWidth();
              gc.setColor(color);

              if (isLeftAligned)
                gc.drawString(this.text[0], xs, ys+heightsText[0]);
              else
                gc.drawString(this.text[0], (int)screen.getWidth()-xs-widthText, ys+heightsText[0]);

              gap = (int) heightsText[0]/2; //spaces between lines (half height of the text)

              for (int i=1; i<text.length; i++)
              {
                t = new TextLayout(text[i],gc.getFont(),frc);

                if ( ((int) t.getBounds().getWidth()) > widthText)
                  widthText = ((int) t.getBounds().getWidth());

                heightsText[i] = heightsText[i-1] + (int) t.getBounds().getHeight() + gap;

                if (isLeftAligned)
                  gc.drawString(this.text[i], xs, ys+heightsText[i]);
                else
                  gc.drawString(this.text[i], (int)screen.getWidth()-xs-widthText, ys+heightsText[i]);
              }

              r.width = widthText + 12;
              r.height = heightsText[text.length-1] + 10;
              if (isLeftAligned) {
                r.x = xs;
              } else {
                r.x = (int)screen.getWidth()-xs;
              }
              r.y = ys;
              recalculate = false;
        } else {
           // Display background rectangle
               gc.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f ) );
               gc.setColor( Color.white );

               if (isLeftAligned) {
                 gc.fillRect(xs-6,ys-3,r.width,r.height);
                 gc.setColor( Color.black );
                 gc.draw3DRect(xs-6,ys-3,r.width,r.height,false);
               } else {
                 gc.fillRect((int)screen.getWidth()-xs-widthText-6,ys-3,r.width,r.height);
                 gc.setColor( Color.black );
                 gc.draw3DRect((int)screen.getWidth()-xs-widthText-6,ys-3,r.width,r.height,false);
               }

               gc.setComposite( AlphaComposite.SrcOver ); // restore

            // display text
               gc.setColor(color);

               if (isLeftAligned)
                   for (int i=0; i<text.length; i++)
                       gc.drawString(this.text[i], xs, ys+heightsText[i]);
               else
                   for (int i=0; i<text.length; i++)
                       gc.drawString(this.text[i], (int)screen.getWidth()-xs-widthText, ys+heightsText[i]);
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
     public boolean tick() {
        return true; // no update needed and a MotionlessSprite is always "live" by default.
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

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

/** A MotiolessSprite is a sprite that has no DataSupplier. It is used to just display
 *  an image on the GraphicsDirector. The image can change ( hasAnimation=true in constructor)
 *  but you can not change its (x,y) cordinates once set in the constructor.
 *
 *  A MotionlessSprite is especially useful for background images.
 *
 * @author MasterBob, Aldiss
 */

public class MultiLineText extends Drawable {

 /*------------------------------------------------------------------------------------*/

   /**le text a ecrire
    */
     private String[] text;

   /**the color of the text
    */
     private Color color = Color.black;

   /**le font utilise
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


 /*------------------------------------------------------------------------------------*/

  /** Constructor. Default font is Lucida BlackLetter.
   *
   * @param xs MultiLineText's xs top left corner of the text
   * @param ys MultiLineText's ys top left corner of the text
   * @param priority MultiLineText's priority
   */
    public MultiLineText( String[] text, int xs, int ys, short priority) {
    	super();
        this.text = text;
        this.xs = xs;
        this.ys = ys;

        setFont("Lblack.ttf");
        this.priority = priority;
        useAntialiasing(true);
    }

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   *
   * @param xs MultiLineText's xs top left corner of the text
   * @param ys MultiLineText's ys top left corner of the text
   * @param text MultiLineText's text
   * @param color MultiLineText's color
   * @param size MultiLineText's size
   * @param font MultiLineText's font
   * @param priority MultiLineText's priority
   */
    public MultiLineText( String[] text, int xs, int ys, Color color, float size, String font, short priority) {
    	super();
        this.text = text;
        this.xs = xs;
        this.ys = ys;
        setFont(font);
        setSize(size);
        setColor(color);
        this.priority = priority;
        useAntialiasing(true);
    }



 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /**
  * define the text
  */
 public void setText(String[] text)
  {
   this.text = text;
  }


 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /**
  * define the color
  */
 public void setColor(Color color)
  {
   this.color = color;
  }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /**
  * define the size
  */
  public void setSize(float size)
   {
    this.size = size;
    font = font.deriveFont(Font.PLAIN, size);
    Map fontAttributes = font.getAttributes();
    //System.out.println("Attrihbutes=" + fontAttributes);
   }



 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /**
  * To set the current font.
  */
  public void setFont(String fontName){
   try {
      String fontPath = ImageLibrary.getDefaultImageLibrary().getDataBasePath()+File.separator+"fonts";
   
      FileInputStream fis = new FileInputStream(fontPath+File.separator+fontName);
      font = Font.createFont(Font.TRUETYPE_FONT, fis);
      //System.out.println("Font=" + font);
      font = font.deriveFont(Font.PLAIN, size);
      Map fontAttributes = font.getAttributes();
      //System.out.println("Attrihbutes=" + fontAttributes);
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

        gc.setColor(color);

        if (font != null)
          gc.setFont(font);

       gc.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f ) );


       int widthText = 0;
       int heightText = 0;

        if(text != null)
         {
          FontRenderContext frc = gc.getFontRenderContext();
          TextLayout t = new TextLayout(text[0],gc.getFont(),frc);
          int gap = ((int) t.getBounds().getHeight())/2; //spaces between lines (same height of the text)
          for(int i=0; i<text.length; i++)
           {
            frc = gc.getFontRenderContext();
            t = new TextLayout(text[i],gc.getFont(),frc);

            if(  ((int) t.getBounds().getWidth())  > widthText  ) widthText  = ((int) t.getBounds().getWidth()) ;
            if(i!=0) heightText += ((int) t.getBounds().getHeight() +gap);
            else heightText += ((int) t.getBounds().getHeight());

            gc.drawString(this.text[i], xs, ys+heightText);
           }
          r.x = xs;
          r.y = ys;
          r.width = widthText;
          r.height = heightText;
         }

        gc.draw3DRect(xs,ys,widthText,heightText,true);

        gc.setComposite( AlphaComposite.SrcOver ); // restore
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

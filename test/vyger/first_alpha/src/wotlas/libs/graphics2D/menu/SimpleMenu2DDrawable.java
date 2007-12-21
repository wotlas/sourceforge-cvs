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

package wotlas.libs.graphics2D.menu;

import wotlas.libs.graphics2D.*;
import java.awt.*;
import java.util.*;
import java.awt.font.*;
import java.awt.geom.*;

import java.net.URL;

/** A drawable that displays a menu representing the content of the SimpleMenu2D.
 *
 * @author Aldiss
 */

public class SimpleMenu2DDrawable extends Drawable {

 /*------------------------------------------------------------------------------------*/

   /** Default Font Name used.
    */
    // static private String defaultFontName = "dialog";
    // diego : i cant see menu 'cause it seems dialog isnt the right name, at least on my pc with win2k
    static private String defaultFontName = "Dialog.plain";

 /*------------------------------------------------------------------------------------*/

   /** Some geometric definitions.
    */
    static final private int DIST_BETWEEN_TWO_LINES = 5;
    static final private int MINIMUM_MENU_WIDTH = 40;
    static final private int HORIZONTAL_BORDER = 5;
    static final private int BAR_IMAGE_WIDTH = 100;

   /** Animation speed : time for 100 pixels moved
    */
    static final private int ANIM_SPEED = 300; // 300ms to display 100 pixels

   /** Maximum time them menu is displayed if none of its item is selected
    */
    static final private int DISPLAY_TIMEOUT = 3000; // 3s

   /** DRAWABLE PRIORITIES
    */
    static final public short MENU_PRIORITY      = 1000;      // menu drawable

   /** Static Menu images
    */
    static public Image middleBarImage;
    static public Image arrowRightImage;
    static public Image arrowRightSelectImage;
    static public Image arrowDownImage;
    static public Image arrowDownSelectImage;
    static public Image arrowUpImage;
    static public Image arrowUpSelectImage;

   /** We load the images...
    */
    static {
      // we load our small menu images
        String basePath = "images/";
        MediaTracker tracker = new MediaTracker(new Label());

        URL url = SimpleMenu2DDrawable.class.getResource(basePath+"middle-bar.gif");
/*
        if( url==null ) {
          // We try inside a JAR...
             basePath = "/wotlas/libs/graphics2D/menu/images/";
             url = SimpleMenu2DDrawable.class.getResource(basePath+"middle-bar.gif");
        }
*/
        middleBarImage = Toolkit.getDefaultToolkit().getImage( url );
        tracker.addImage(middleBarImage,0);

        url = SimpleMenu2DDrawable.class.getResource(basePath+"arrow-right.gif");
        arrowRightImage = Toolkit.getDefaultToolkit().getImage( url );
        tracker.addImage(arrowRightImage,1);

        url = SimpleMenu2DDrawable.class.getResource(basePath+"arrow-right-select.gif");
        arrowRightSelectImage = Toolkit.getDefaultToolkit().getImage( url );
        tracker.addImage(arrowRightSelectImage,2);

        url = SimpleMenu2DDrawable.class.getResource(basePath+"arrow-down.gif");
        arrowDownImage = Toolkit.getDefaultToolkit().getImage( url );
        tracker.addImage(arrowDownImage,3);

        url = SimpleMenu2DDrawable.class.getResource(basePath+"arrow-down-select.gif");
        arrowDownSelectImage = Toolkit.getDefaultToolkit().getImage( url );
        tracker.addImage(arrowDownSelectImage,4);

        url = SimpleMenu2DDrawable.class.getResource(basePath+"arrow-up.gif");
        arrowUpImage = Toolkit.getDefaultToolkit().getImage( url );
        tracker.addImage(arrowUpImage,5);

        url = SimpleMenu2DDrawable.class.getResource(basePath+"arrow-up-select.gif");
        arrowUpSelectImage = Toolkit.getDefaultToolkit().getImage( url );
        tracker.addImage(arrowUpSelectImage,6);

         try{
             tracker.waitForAll();
         }catch(InterruptedException e) { e.printStackTrace(); }
    };

   /** Selected text color
    */
       public static final Color blueColor = new Color( 20, 80, 160 );

 /*------------------------------------------------------------------------------------*/

   /** Our Simple Menu2D from which we'll take our data from. 
    */
     private SimpleMenu2D menu2D;

   /** Font to use.
    */
     private Font font;

   /** The name of the font used
    */
     private String fontName;

   /** Text Font Size.
    */
     private float size;

   /** True if we must recompute text width and height
    */
     private boolean recompute;

   /** Our parent menu rectangle (just used when the menu is displayed)
    */
     private Rectangle parentRectangle;

   /** TimeStamp for menu animation
    */
     private long timeStamp;

   /** Next left-upper Point of the menu location
    */
     private Point pNext;

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

   /** Constructor with our menu reference.
    * @param menu2D menu data
    * @param p position where the menu should appear.
    * @param priority drawable priority
    */
     public SimpleMenu2DDrawable( SimpleMenu2D menu2D ) {
    	super();
    	this.menu2D = menu2D;
        priority = MENU_PRIORITY;
        fontName=defaultFontName;
        r.x = 0;
        r.y = 0;
        recompute = true;
        size = 10;
        pNext = new Point(-1,-1);
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
     	
     	setFont(fontName);  // init the font & size
        setSize(size);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To refresh the menu's state
   */
    public void refreshState() {
        recompute = true;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To animate the menu.
   */
    public void animateMenu() {
        timeStamp = System.currentTimeMillis(); // timestamp == now
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the parent menu rectangle.
   */
    public void setParentRectangle( Rectangle r ) {
    	parentRectangle = r;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** We return the index of the item which is at the y range.
   */
    public int getItemAt(int y){
        return menu2D.getFirstItemIndex()
            + ( y-r.y-DIST_BETWEEN_TWO_LINES/2-1 )/( (int)size+DIST_BETWEEN_TWO_LINES );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** We return the y of the item which has the given index.
   */
    public int getItemY(int index){
    	index = index-menu2D.getFirstItemIndex();
    	
        return r.y+index*((int)size+DIST_BETWEEN_TWO_LINES);
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /**
   * define the font size.
   */
    public void setSize(float size){
       this.size = size;
       font = font.deriveFont(Font.PLAIN, size);
       recompute = true;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the font for this Drawable.
   */
    public void setFont(String fontName){
       font = FontFactory.getDefaultFontFactory().getFont(fontName);
       font = font.deriveFont(Font.PLAIN, size);
       recompute=true;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the current font.
    */
     public Font getFont(){
     	return font;
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

        if( font==null || menu2D==null )
            return;

        gc.setFont(font);

      // 1 - recompute the menu's dimension ?
        if( recompute ) {
            if( menu2D.getItems().length!=0 ) {
                FontRenderContext frc = gc.getFontRenderContext();
                SimpleMenu2DItem items[] = menu2D.getItems();
                int totalHeight = 0;
                int maxWidth = MINIMUM_MENU_WIDTH;

                for( int i=0; i<items.length; i++ ) {
                     TextLayout t = new TextLayout( items[i].itemName, font, frc );
                     int widthText   = (int) t.getBounds().getWidth();

                     if(widthText>maxWidth)
                        maxWidth = widthText;
                }

                if( items.length>SimpleMenu2D.MAX_ITEMS_DISPLAYED )
                    totalHeight = DIST_BETWEEN_TWO_LINES + SimpleMenu2D.MAX_ITEMS_DISPLAYED*((int)size+DIST_BETWEEN_TWO_LINES);
                else
                    totalHeight = DIST_BETWEEN_TWO_LINES + items.length*((int)size+DIST_BETWEEN_TWO_LINES);

                r.width = maxWidth + 2*HORIZONTAL_BORDER+1;
                r.height = totalHeight;
            }
            else {
                r.width = MINIMUM_MENU_WIDTH + 2*HORIZONTAL_BORDER;
                r.height = (int)size+2*DIST_BETWEEN_TWO_LINES;
            }

          // we have some parent rectangle info, we use it to place our menu
          // on the left if there isn't enough space on the right
            if(parentRectangle!=null) {
            	if( r.x > parentRectangle.x && screen.width-r.x < r.width )
            	    r.x = parentRectangle.x-r.width-1;

               parentRectangle=null;
            }

            recompute = false;
        }

      // 2 - Update menu position ?
        if( r.x+r.width > screen.width && r.x>0 )
            r.x -= r.x+r.width-screen.width;

        if( r.y+r.height > screen.height && r.y>0 )
            r.y -= r.y+r.height-screen.height;

        if( r.x<0 ) r.x=0;
        if( r.y<0 ) r.y=0;

      // 3 - We draw the menu ...

        long now = System.currentTimeMillis();
        int rHeight = r.height;

        if( now-timeStamp < (int)( ANIM_SPEED*( (double)r.height/100 ) ) ) {
          // Animation
             rHeight = (int)( r.height*(now-timeStamp)/( ANIM_SPEED*( (double)r.height/100 ) ) );
        }

       // transparent rectangle
         gc.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f ) );
         gc.setColor(Color.white);
         gc.fillRect(r.x,r.y,r.width,rHeight);
         gc.setComposite( AlphaComposite.SrcOver ); // suppressing alpha
        
       // drawing border
         gc.drawLine( r.x, r.y, r.x+r.width, r.y );
         gc.drawLine( r.x, r.y, r.x, r.y+rHeight );
         
         gc.setColor(Color.gray);
         gc.drawLine( r.x+r.width, r.y, r.x+r.width, r.y+rHeight );
         gc.drawLine( r.x, r.y+rHeight, r.x+r.width, r.y+rHeight );

      // top image
         for( int i=0; i<r.width/BAR_IMAGE_WIDTH; i+=BAR_IMAGE_WIDTH )
              gc.drawImage( middleBarImage, r.x+i, r.y-3, null );

         int totalWidth =(r.width/BAR_IMAGE_WIDTH)*BAR_IMAGE_WIDTH;

         gc.drawImage( middleBarImage, r.x+totalWidth,
                       r.y-3, r.width-totalWidth+1, middleBarImage.getHeight(null), null );

      // drawing text...
         int y = r.y+DIST_BETWEEN_TWO_LINES;
         int maxIndex = menu2D.getItems().length;

         if(maxIndex>SimpleMenu2D.MAX_ITEMS_DISPLAYED)
            maxIndex = menu2D.getFirstItemIndex() + SimpleMenu2D.MAX_ITEMS_DISPLAYED;

         for( int i=menu2D.getFirstItemIndex(); i<maxIndex; i++ ) {

            if( (r.y+rHeight)<(y+(int)size) )
                break;

            if( i==menu2D.getFirstItemIndex() && menu2D.isFirstIndexArrow() ) {
               // Up Arrow
                if( menu2D.getSelectedItemIndex()!=i )
                    gc.drawImage( arrowUpImage, r.x+(r.width-arrowUpImage.getWidth(null))/2, y+5, null );
                else
                    gc.drawImage( arrowUpSelectImage, r.x+(r.width-arrowUpSelectImage.getWidth(null))/2+1, y+6, null );
            }
            else if( i==maxIndex-1  && maxIndex < menu2D.getItems().length ) {
               // Down Arrow
                if( menu2D.getSelectedItemIndex()!=i )
                    gc.drawImage( arrowDownImage, r.x+(r.width-arrowDownImage.getWidth(null))/2, y+(int)size-2, null );
                else
                    gc.drawImage( arrowDownSelectImage, r.x+(r.width-arrowDownSelectImage.getWidth(null))/2+1, y+(int)size-1, null );
            }
            else if( menu2D.getItems()[i].itemName.equals("-") ) {
                gc.setColor(Color.gray);
                gc.drawLine( r.x+HORIZONTAL_BORDER, y+1+(int)(size/2), r.x+r.width-HORIZONTAL_BORDER, y+1+(int)(size/2) );
                gc.setColor(Color.white);
                gc.drawLine( r.x+HORIZONTAL_BORDER, y+2+(int)(size/2), r.x+r.width-HORIZONTAL_BORDER, y+2+(int)(size/2) );
            }
            else {
             // Standard item to display
               if( menu2D.getItems()[i].isEnabled ) {
                   if( menu2D.getSelectedItemIndex()!=i )
                       gc.setColor(Color.black);
                   else
                       gc.setColor(blueColor);
               }
               else
                  gc.setColor(Color.gray);

               RenderingHints savedRenderHints = gc.getRenderingHints(); // save    
               RenderingHints antiARenderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                              RenderingHints.VALUE_ANTIALIAS_ON);
               antiARenderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
               gc.setRenderingHints( antiARenderHints );

               gc.drawString(menu2D.getItems()[i].itemName, r.x+HORIZONTAL_BORDER, y+size);

               if( menu2D.getItems()[i].link!=null ) {
                   if( menu2D.getSelectedItemIndex()!=i )
                      gc.drawImage( arrowRightImage, r.x+r.width-HORIZONTAL_BORDER+1, y+(int)size-arrowRightImage.getHeight(null), null );
                   else
                      gc.drawImage( arrowRightSelectImage, r.x+r.width-HORIZONTAL_BORDER+1, y+(int)size-arrowRightSelectImage.getHeight(null), null );
               }

               gc.setRenderingHints( savedRenderHints );
            }

            y += size+DIST_BETWEEN_TWO_LINES;
         }
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To set the next position of the menu (upper-left cordinates).
    */
     public synchronized void setNextPosition( int x, int y ) {
          pNext.x = x;
          pNext.y = y;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Tick method called by the GraphicsDirector. This tick method has a returned value
   *  that indicates if the drawable is still living or must be deleted. Some Drawables
   *  always return "still living", it is then the task of the program that uses
   *  the GraphicsDirector to manage the destruction of drawables.
   *
   *  @return true if the drawable is "live", false if it must be deleted.
   */
     public synchronized boolean tick() {
        long now = System.currentTimeMillis();

        if( now-timeStamp>DISPLAY_TIMEOUT && ( 
            menu2D.getSelectedItemIndex()==-1 || menu2D.getSelectedItemIndex()>=menu2D.getItems().length ) )
            menu2D.hide();

        if(pNext.x!=-1 && pNext.y!=-1) {
           r.x = pNext.x;
           r.y = pNext.y;
           pNext.x=-1;
           pNext.y=-1;
        }
        return true;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
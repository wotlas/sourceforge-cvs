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

package wotlas.libs.graphics2d.menu;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.net.URL;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.FontFactory;
import wotlas.libs.graphics2d.ImageLibrary;

/** A drawable that displays a menu representing the content of the SimpleMenu2D.
 *
 * @author Aldiss
 */

public class SimpleMenu2DDrawable extends Drawable {

    /*------------------------------------------------------------------------------------*/

    /** Default Font Name used.
     */
    static private String defaultFontName = "dialog";

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
    static final public short MENU_PRIORITY = 1000; // menu drawable

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

        URL url = SimpleMenu2DDrawable.class.getResource(basePath + "middle-bar.gif");
        /*
                if( url==null ) {
                  // We try inside a JAR...
                     basePath = "/wotlas/libs/graphics2d/menu/images/";
                     url = SimpleMenu2DDrawable.class.getResource(basePath+"middle-bar.gif");
                }
        */
        SimpleMenu2DDrawable.middleBarImage = Toolkit.getDefaultToolkit().getImage(url);
        tracker.addImage(SimpleMenu2DDrawable.middleBarImage, 0);

        url = SimpleMenu2DDrawable.class.getResource(basePath + "arrow-right.gif");
        SimpleMenu2DDrawable.arrowRightImage = Toolkit.getDefaultToolkit().getImage(url);
        tracker.addImage(SimpleMenu2DDrawable.arrowRightImage, 1);

        url = SimpleMenu2DDrawable.class.getResource(basePath + "arrow-right-select.gif");
        SimpleMenu2DDrawable.arrowRightSelectImage = Toolkit.getDefaultToolkit().getImage(url);
        tracker.addImage(SimpleMenu2DDrawable.arrowRightSelectImage, 2);

        url = SimpleMenu2DDrawable.class.getResource(basePath + "arrow-down.gif");
        SimpleMenu2DDrawable.arrowDownImage = Toolkit.getDefaultToolkit().getImage(url);
        tracker.addImage(SimpleMenu2DDrawable.arrowDownImage, 3);

        url = SimpleMenu2DDrawable.class.getResource(basePath + "arrow-down-select.gif");
        SimpleMenu2DDrawable.arrowDownSelectImage = Toolkit.getDefaultToolkit().getImage(url);
        tracker.addImage(SimpleMenu2DDrawable.arrowDownSelectImage, 4);

        url = SimpleMenu2DDrawable.class.getResource(basePath + "arrow-up.gif");
        SimpleMenu2DDrawable.arrowUpImage = Toolkit.getDefaultToolkit().getImage(url);
        tracker.addImage(SimpleMenu2DDrawable.arrowUpImage, 5);

        url = SimpleMenu2DDrawable.class.getResource(basePath + "arrow-up-select.gif");
        SimpleMenu2DDrawable.arrowUpSelectImage = Toolkit.getDefaultToolkit().getImage(url);
        tracker.addImage(SimpleMenu2DDrawable.arrowUpSelectImage, 6);

        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    /** Selected text color
     */
    public static final Color blueColor = new Color(20, 80, 160);

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
        return SimpleMenu2DDrawable.defaultFontName;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the default font name.
     */
    static public void setDefaultFontName(String fontName) {
        SimpleMenu2DDrawable.defaultFontName = fontName;
    }

    /*------------------------------------------------------------------------------------*/

    /** Constructor with our menu reference.
     * @param menu2D menu data
     * @param p position where the menu should appear.
     * @param priority drawable priority
     */
    public SimpleMenu2DDrawable(SimpleMenu2D menu2D) {
        super();
        this.menu2D = menu2D;
        this.priority = SimpleMenu2DDrawable.MENU_PRIORITY;
        this.fontName = SimpleMenu2DDrawable.defaultFontName;
        this.r.x = 0;
        this.r.y = 0;
        this.recompute = true;
        this.size = 10;
        this.pNext = new Point(-1, -1);
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

        setFont(this.fontName); // init the font & size
        setSize(this.size);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To refresh the menu's state
     */
    public void refreshState() {
        this.recompute = true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To animate the menu.
     */
    public void animateMenu() {
        this.timeStamp = System.currentTimeMillis(); // timestamp == now
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the parent menu rectangle.
     */
    public void setParentRectangle(Rectangle r) {
        this.parentRectangle = r;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** We return the index of the item which is at the y range.
     */
    public int getItemAt(int y) {
        return this.menu2D.getFirstItemIndex() + (y - this.r.y - SimpleMenu2DDrawable.DIST_BETWEEN_TWO_LINES / 2 - 1) / ((int) this.size + SimpleMenu2DDrawable.DIST_BETWEEN_TWO_LINES);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** We return the y of the item which has the given index.
     */
    public int getItemY(int index) {
        index = index - this.menu2D.getFirstItemIndex();

        return this.r.y + index * ((int) this.size + SimpleMenu2DDrawable.DIST_BETWEEN_TWO_LINES);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**
     * define the font size.
     */
    public void setSize(float size) {
        this.size = size;
        this.font = this.font.deriveFont(Font.PLAIN, size);
        this.recompute = true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the font for this Drawable.
     */
    public void setFont(String fontName) {
        this.font = FontFactory.getDefaultFontFactory().getFont(fontName);
        this.font = this.font.deriveFont(Font.PLAIN, this.size);
        this.recompute = true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the current font.
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

        if (this.font == null || this.menu2D == null)
            return;

        gc.setFont(this.font);

        // 1 - recompute the menu's dimension ?
        if (this.recompute) {
            if (this.menu2D.getItems().length != 0) {
                FontRenderContext frc = gc.getFontRenderContext();
                SimpleMenu2DItem items[] = this.menu2D.getItems();
                int totalHeight = 0;
                int maxWidth = SimpleMenu2DDrawable.MINIMUM_MENU_WIDTH;

                for (int i = 0; i < items.length; i++) {
                    TextLayout t = new TextLayout(items[i].itemName, this.font, frc);
                    int widthText = (int) t.getBounds().getWidth();

                    if (widthText > maxWidth)
                        maxWidth = widthText;
                }

                if (items.length > SimpleMenu2D.MAX_ITEMS_DISPLAYED)
                    totalHeight = SimpleMenu2DDrawable.DIST_BETWEEN_TWO_LINES + SimpleMenu2D.MAX_ITEMS_DISPLAYED * ((int) this.size + SimpleMenu2DDrawable.DIST_BETWEEN_TWO_LINES);
                else
                    totalHeight = SimpleMenu2DDrawable.DIST_BETWEEN_TWO_LINES + items.length * ((int) this.size + SimpleMenu2DDrawable.DIST_BETWEEN_TWO_LINES);

                this.r.width = maxWidth + 2 * SimpleMenu2DDrawable.HORIZONTAL_BORDER + 1;
                this.r.height = totalHeight;
            } else {
                this.r.width = SimpleMenu2DDrawable.MINIMUM_MENU_WIDTH + 2 * SimpleMenu2DDrawable.HORIZONTAL_BORDER;
                this.r.height = (int) this.size + 2 * SimpleMenu2DDrawable.DIST_BETWEEN_TWO_LINES;
            }

            // we have some parent rectangle info, we use it to place our menu
            // on the left if there isn't enough space on the right
            if (this.parentRectangle != null) {
                if (this.r.x > this.parentRectangle.x && screen.width - this.r.x < this.r.width)
                    this.r.x = this.parentRectangle.x - this.r.width - 1;

                this.parentRectangle = null;
            }

            this.recompute = false;
        }

        // 2 - Update menu position ?
        if (this.r.x + this.r.width > screen.width && this.r.x > 0)
            this.r.x -= this.r.x + this.r.width - screen.width;

        if (this.r.y + this.r.height > screen.height && this.r.y > 0)
            this.r.y -= this.r.y + this.r.height - screen.height;

        if (this.r.x < 0)
            this.r.x = 0;
        if (this.r.y < 0)
            this.r.y = 0;

        // 3 - We draw the menu ...

        long now = System.currentTimeMillis();
        int rHeight = this.r.height;

        if (now - this.timeStamp < (int) (SimpleMenu2DDrawable.ANIM_SPEED * ((double) this.r.height / 100))) {
            // Animation
            rHeight = (int) (this.r.height * (now - this.timeStamp) / (SimpleMenu2DDrawable.ANIM_SPEED * ((double) this.r.height / 100)));
        }

        // transparent rectangle
        gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        gc.setColor(Color.white);
        gc.fillRect(this.r.x, this.r.y, this.r.width, rHeight);
        gc.setComposite(AlphaComposite.SrcOver); // suppressing alpha

        // drawing border
        gc.drawLine(this.r.x, this.r.y, this.r.x + this.r.width, this.r.y);
        gc.drawLine(this.r.x, this.r.y, this.r.x, this.r.y + rHeight);

        gc.setColor(Color.gray);
        gc.drawLine(this.r.x + this.r.width, this.r.y, this.r.x + this.r.width, this.r.y + rHeight);
        gc.drawLine(this.r.x, this.r.y + rHeight, this.r.x + this.r.width, this.r.y + rHeight);

        // top image
        for (int i = 0; i < this.r.width / SimpleMenu2DDrawable.BAR_IMAGE_WIDTH; i += SimpleMenu2DDrawable.BAR_IMAGE_WIDTH)
            gc.drawImage(SimpleMenu2DDrawable.middleBarImage, this.r.x + i, this.r.y - 3, null);

        int totalWidth = (this.r.width / SimpleMenu2DDrawable.BAR_IMAGE_WIDTH) * SimpleMenu2DDrawable.BAR_IMAGE_WIDTH;

        gc.drawImage(SimpleMenu2DDrawable.middleBarImage, this.r.x + totalWidth, this.r.y - 3, this.r.width - totalWidth + 1, SimpleMenu2DDrawable.middleBarImage.getHeight(null), null);

        // drawing text...
        int y = this.r.y + SimpleMenu2DDrawable.DIST_BETWEEN_TWO_LINES;
        int maxIndex = this.menu2D.getItems().length;

        if (maxIndex > SimpleMenu2D.MAX_ITEMS_DISPLAYED)
            maxIndex = this.menu2D.getFirstItemIndex() + SimpleMenu2D.MAX_ITEMS_DISPLAYED;

        for (int i = this.menu2D.getFirstItemIndex(); i < maxIndex; i++) {

            if ((this.r.y + rHeight) < (y + (int) this.size))
                break;

            if (i == this.menu2D.getFirstItemIndex() && this.menu2D.isFirstIndexArrow()) {
                // Up Arrow
                if (this.menu2D.getSelectedItemIndex() != i)
                    gc.drawImage(SimpleMenu2DDrawable.arrowUpImage, this.r.x + (this.r.width - SimpleMenu2DDrawable.arrowUpImage.getWidth(null)) / 2, y + 5, null);
                else
                    gc.drawImage(SimpleMenu2DDrawable.arrowUpSelectImage, this.r.x + (this.r.width - SimpleMenu2DDrawable.arrowUpSelectImage.getWidth(null)) / 2 + 1, y + 6, null);
            } else if (i == maxIndex - 1 && maxIndex < this.menu2D.getItems().length) {
                // Down Arrow
                if (this.menu2D.getSelectedItemIndex() != i)
                    gc.drawImage(SimpleMenu2DDrawable.arrowDownImage, this.r.x + (this.r.width - SimpleMenu2DDrawable.arrowDownImage.getWidth(null)) / 2, y + (int) this.size - 2, null);
                else
                    gc.drawImage(SimpleMenu2DDrawable.arrowDownSelectImage, this.r.x + (this.r.width - SimpleMenu2DDrawable.arrowDownSelectImage.getWidth(null)) / 2 + 1, y + (int) this.size - 1, null);
            } else if (this.menu2D.getItems()[i].itemName.equals("-")) {
                gc.setColor(Color.gray);
                gc.drawLine(this.r.x + SimpleMenu2DDrawable.HORIZONTAL_BORDER, y + 1 + (int) (this.size / 2), this.r.x + this.r.width - SimpleMenu2DDrawable.HORIZONTAL_BORDER, y + 1 + (int) (this.size / 2));
                gc.setColor(Color.white);
                gc.drawLine(this.r.x + SimpleMenu2DDrawable.HORIZONTAL_BORDER, y + 2 + (int) (this.size / 2), this.r.x + this.r.width - SimpleMenu2DDrawable.HORIZONTAL_BORDER, y + 2 + (int) (this.size / 2));
            } else {
                // Standard item to display
                if (this.menu2D.getItems()[i].isEnabled) {
                    if (this.menu2D.getSelectedItemIndex() != i)
                        gc.setColor(Color.black);
                    else
                        gc.setColor(SimpleMenu2DDrawable.blueColor);
                } else
                    gc.setColor(Color.gray);

                RenderingHints savedRenderHints = gc.getRenderingHints(); // save    
                RenderingHints antiARenderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                antiARenderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                gc.setRenderingHints(antiARenderHints);

                gc.drawString(this.menu2D.getItems()[i].itemName, this.r.x + SimpleMenu2DDrawable.HORIZONTAL_BORDER, y + this.size);

                if (this.menu2D.getItems()[i].link != null) {
                    if (this.menu2D.getSelectedItemIndex() != i)
                        gc.drawImage(SimpleMenu2DDrawable.arrowRightImage, this.r.x + this.r.width - SimpleMenu2DDrawable.HORIZONTAL_BORDER + 1, y + (int) this.size - SimpleMenu2DDrawable.arrowRightImage.getHeight(null), null);
                    else
                        gc.drawImage(SimpleMenu2DDrawable.arrowRightSelectImage, this.r.x + this.r.width - SimpleMenu2DDrawable.HORIZONTAL_BORDER + 1, y + (int) this.size - SimpleMenu2DDrawable.arrowRightSelectImage.getHeight(null), null);
                }

                gc.setRenderingHints(savedRenderHints);
            }

            y += this.size + SimpleMenu2DDrawable.DIST_BETWEEN_TWO_LINES;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the next position of the menu (upper-left cordinates).
     */
    public synchronized void setNextPosition(int x, int y) {
        this.pNext.x = x;
        this.pNext.y = y;
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
    public synchronized boolean tick() {
        long now = System.currentTimeMillis();

        if (now - this.timeStamp > SimpleMenu2DDrawable.DISPLAY_TIMEOUT && (this.menu2D.getSelectedItemIndex() == -1 || this.menu2D.getSelectedItemIndex() >= this.menu2D.getItems().length))
            this.menu2D.hide();

        if (this.pNext.x != -1 && this.pNext.y != -1) {
            this.r.x = this.pNext.x;
            this.r.y = this.pNext.y;
            this.pNext.x = -1;
            this.pNext.y = -1;
        }
        return true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
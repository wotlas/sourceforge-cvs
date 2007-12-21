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

package wotlas.libs.graphics2d;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/** A GraphicsDirector is the root class of this graphics2d engine. It manages
 *  Drawables and has a WindowPolicy for scrollings.
 *
 *  the only synchronized methods in GraphicsDirector are paint() and tick()
 *  so if you happen to handle events or change parameters do it with care !
 *
 * @author MasterBob, Aldiss, Petrus, Diego
 * @see wotlas.libs.graphics2d.ImageLibrary
 * @see wotlas.libs.graphics2d.Drawable
 * @see wotlas.libs.graphics2d.DrawableIterator
 */

public class GraphicsDirector extends JPanel {

    /*------------------------------------------------------------------------------------*/

    /** Represents the visible part of the JPanel (it has the JPanel's size)
     *  and is expressed in the background's coordinate (we use the background as a
     *  reference here, because all Drawables should be expressed in background
     *  coordinates).
     */
    protected Rectangle screen;

    /** Background's Dimension. The background can be any Drawable,
     *  we don't have to possess a handle it, we only need its dimension.
     */
    protected Dimension background;

    /** Our Drawable reference, we need to know which drawable our windowPolicy
     *  is going to refer to center the screen.
     */
    protected Drawable refDrawable;

    /** Our drawables. They are sorted by priority.
     */
    protected DrawableIterator drawables;

    /** The image library from which will take our images.
     */
    protected ImageLibrary imageLib;

    /** Our WindowPolicy. It tells us how to move the screen on the background.
     */
    protected WindowPolicy windowPolicy;

    /*------------------------------------------------------------------------------------*/

    /** Can we display our drawables ?
     */
    protected boolean display;

    /** Lock for repaint...
     */
    protected Object lockPaint = new Object();

    /** OffScreen image for the GraphicsDirector. 
     */
    protected Image backBufferImage;

    /** FOR REPAINT SOLUTION 2
     ** To repaint the screen.
     * 
      private Thread paintThread;
    
     ** Is there already a Thread waiting to repaint the screen ?
     *
      private boolean isLocked;

     ** END OF REPAINT SOLUTION 2 */

    /*------------------------------------------------------------------------------------*/

    /** Constructor. The window policy is not supposed to change during the life of the
     *  GraphicsDirector, but you can still change it by invoking the setWindowPolicy()
     *  method. The ImageLibrary is set to the default one : ImageLibrary.getDefault...
     *
     * @param windowPolicy a policy that manages window scrolling.
     * @exception ImageLibraryException if no ImageLibrary is found.
     */
    public GraphicsDirector(WindowPolicy windowPolicy) throws ImageLibraryException {
        this(windowPolicy, null);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor. The window policy is not supposed to change during the life of the
     *  GraphicsDirector, but you can still change it by invoking the setWindowPolicy()
     *  method. If the imageLibrary is set to null we seek for a default one.
     *
     * @param windowPolicy a policy that manages window scrolling.
     * @param imageLib ImageLibrary to use for this GraphicsDirector.
     * @exception ImageLibraryException if no ImageLibrary is found.
     */
    public GraphicsDirector(WindowPolicy windowPolicy, ImageLibrary imageLib) throws ImageLibraryException {
        super(false); // we don't use the default JPanel double-buffering

        if (imageLib == null) {
            this.imageLib = ImageLibrary.getDefaultImageLibrary();

            if (this.imageLib == null)
                throw new ImageLibraryException("No Image Library Found !");
        } else
            this.imageLib = imageLib;

        this.display = false;
        this.drawables = new DrawableIterator();
        setWindowPolicy(windowPolicy);
        setBackground(Color.white);
        // isLocked=false;  // remove this comment for REPAINT solution 2
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To initialize the GraphicsDirector when using TileMap. A call to this method suppresses all the
     *  previously possessed Drawable Objects.
     *  <p><b>IMPORTANT:</b> The backDrawable & refDrawable are automatically added to
     *  the GraphicsDirector's Drawable list.
     *
     * @param backDrawable the drawable that you will use as a reference for your 2D cordinates.
     * @param refDrawable reference Drawable for screen movements. The way the screen moves
     *        is dictated by the WindowPolicy and refers to this drawable.
     * @param screen initial dimension for this JPanel
     */
    public void preTileMapInitWithPlayer(Drawable refDrawable, Dimension screen) {

        // we reset the GraphicsDirector's drawables
        this.drawables.clear();
        addDrawable(refDrawable);
        // Background dims
        this.display = false;
        // Screen defaults
        this.screen = new Rectangle(screen);
        setPreferredSize(screen);
        setMinimumSize(new Dimension(10, 10));

        // We set the new drawable reference an tick our WindowPolicy.
        this.refDrawable = refDrawable;
    }

    /** To initialize the GraphicsDirector when using TileMap. A call to this method suppresses all the
    *  previously possessed Drawable Objects.
    *  <p><b>IMPORTANT:</b> The backDrawable & refDrawable are automatically added to
    *  the GraphicsDirector's Drawable list.
    *
    * @param backDrawable the drawable that you will use as a reference for your 2D cordinates.
    * @param refDrawable reference Drawable for screen movements. The way the screen moves
    *        is dictated by the WindowPolicy and refers to this drawable.
    * @param screen initial dimension for this JPanel
    */
    public void preTileMapInit(Dimension screen) {

        // we reset the GraphicsDirector's drawables
        this.drawables.clear();
        // Background dims
        this.display = false;
        // Screen defaults
        this.screen = new Rectangle(screen);
        setPreferredSize(screen);
        setMinimumSize(new Dimension(50, 50));
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    public void tileMapInit(Dimension background) {
        this.background = background;
        setMaximumSize(background);
        this.windowPolicy.tick();
        this.display = true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To initialize the GraphicsDirector. A call to this method suppresses all the
     *  previously possessed Drawable Objects.
     *  <p><b>IMPORTANT:</b> The backDrawable & refDrawable are automatically added to
     *  the GraphicsDirector's Drawable list.
     *
     * @param backDrawable the drawable that you will use as a reference for your 2D cordinates.
     * @param refDrawable reference Drawable for screen movements. The way the screen moves
     *        is dictated by the WindowPolicy and refers to this drawable.
     * @param screen initial dimension for this JPanel
     */
    public void init(Drawable backDrawable, Drawable refDrawable, Dimension screen) {

        // we reset the GraphicsDirector's drawables
        this.drawables.clear();
        addDrawable(backDrawable);
        addDrawable(refDrawable);

        // Background dims
        this.display = false;
        this.background = new Dimension(backDrawable.getWidth(), backDrawable.getHeight());

        // Screen defaults
        this.screen = new Rectangle(screen);
        setPreferredSize(screen);
        setMaximumSize(this.background);
        setMinimumSize(new Dimension(10, 10));

        // We set the new drawable reference an tick our WindowPolicy.
        this.refDrawable = refDrawable;
        this.windowPolicy.tick();
        this.display = true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set the window policy.
     */
    public void setWindowPolicy(WindowPolicy windowPolicy) {
        this.windowPolicy = windowPolicy;
        windowPolicy.init(this);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Our customized repaint method
     */
    @Override
    public void repaint() {
        if (this.lockPaint == null)
            return; // to prevent repaint() calls during the constructor call.

        paint(getGraphics());

        // SOLUTION 2 : (gives better performance but is not stable on slow computers)
        /*
               synchronized( lockPaint ) {

                 if(paintThread==null) {
                   isLocked=false;

                   paintThread =new Thread() {
                     public void run() {
                      while( true )
                        try{
                            GraphicsDirector.this.paint( GraphicsDirector.this.getGraphics() );

                            synchronized( lockPaint ) {
                                isLocked=true;
                                lockPaint.notifyAll();
                            	lockPaint.wait(400);
                            	isLocked=false;
                            }
                        }catch( Exception e ) {
                           System.out.println("Exception in repaint() : "+e);
                           isLocked=false;
                        }
                     }
                   };

                   paintThread.start();
                 }
                 else {
                      try{
                          if(!isLocked)
                             lockPaint.wait(400);
                      }catch( Exception e ) {}

                      lockPaint.notify();
                 }
               }
        */
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To avoid flickering.
     */
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To paint this JPanel.
     *
     * @param gc graphics object.
     */
    @Override
    public void paint(Graphics gc) {
        if (gc == null || this.screen == null || getHeight() <= 0 || getWidth() <= 0)
            return;

        // double-buffer init
        if (this.backBufferImage == null || getWidth() != this.backBufferImage.getWidth(this) || getHeight() != this.backBufferImage.getHeight(this))
            this.backBufferImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics backBufferGraphics = this.backBufferImage.getGraphics();

        if (!this.display) {
            backBufferGraphics.setColor(Color.white);
            backBufferGraphics.fillRect(0, 0, getWidth(), getHeight());
            gc.drawImage(this.backBufferImage, 0, 0, this);
            return;
        }

        Graphics2D gc2D = (Graphics2D) backBufferGraphics;

        // Anti-aliasing init
        RenderingHints savedRenderHints = gc2D.getRenderingHints(); // save    
        RenderingHints antiARenderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        antiARenderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        boolean previousHadAntiA = false;
        final Rectangle r_screen = new Rectangle(this.screen);

        backBufferGraphics.setColor(Color.white);
        backBufferGraphics.fillRect(0, 0, getWidth(), getHeight());

        synchronized (this.drawables) {

            this.drawables.resetIterator();

            while (this.drawables.hasNext()) {
                Drawable d = this.drawables.next();

                // Set Anti-aliasing or not ?
                if (d.wantAntialiasing() && !previousHadAntiA) {
                    previousHadAntiA = true;
                    gc2D.setRenderingHints(antiARenderHints);
                } else if (!d.wantAntialiasing() && previousHadAntiA) {
                    previousHadAntiA = false;
                    gc2D.setRenderingHints(savedRenderHints);
                }

                d.paint(gc2D, r_screen);
            }

        }

        // Rendering Hints restore...
        gc2D.setRenderingHints(savedRenderHints);

        // double-buffer print
        gc.drawImage(this.backBufferImage, 0, 0, this);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To add a drawable.
     *
     * @param dr drawable to add.
     */
    public void addDrawable(Drawable dr) {
        if (dr == null)
            return;

        // We initialize the drawable.
        dr.init(this.imageLib);

        // We insert it in our list (list ordered by priority)
        synchronized (this.drawables) {
            this.drawables.resetIterator();

            while (this.drawables.hasNext()) {
                Drawable current = this.drawables.next();

                if (current.getPriority() > dr.getPriority()) {
                    this.drawables.insert(dr);
                    return;
                }
            }

            this.drawables.add(dr);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To remove a drawable.
     *
     * @param dr drawable to remove.
     */
    public void removeDrawable(Drawable dr) {
        if (dr == null)
            return;

        synchronized (this.drawables) {
            this.drawables.resetIterator();

            while (this.drawables.hasNext())
                if (this.drawables.next() == dr) {
                    this.drawables.remove();
                    return;
                }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To remove all the drawables.
     */
    public void removeAllDrawables() {
        synchronized (this.drawables) {
            this.drawables.clear();
            this.refDrawable = null;
            this.display = false;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** The tick method updates our screen position, drawables and repaint the whole thing.
     *  Never call repaint on the graphics director, call tick() !
     */
    public void tick() {
        if (this.screen == null)
            return;

        // 1 - We update our screen dimension.
        synchronized (this.drawables) {
            this.screen.width = getWidth();
            this.screen.height = getHeight();
        }

        // 2 - We update our WindowPolicy
        if (getWidth() > 0 && getHeight() > 0)
            this.windowPolicy.tick();

        // 3 - We tick all our sprites
        synchronized (this.drawables) {
            this.drawables.resetIterator();

            while (this.drawables.hasNext())
                if (!this.drawables.next().tick())
                    this.drawables.remove();
        }

        // 4 - We repaint all our prites
        repaint();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set a new drawable reference.
     *
     * @param newRefDrawable new drawable reference
     */
    public void setRefDrawable(Drawable newRefDrawable) {
        this.refDrawable = newRefDrawable;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the current drawable reference.
     *
     * @return drawable reference
     */
    public Drawable getRefDrawable() {
        return this.refDrawable;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the screen rectangle. Any changes to the returned rectangle are
     *  affected to the original.
     */
    public Rectangle getScreenRectangle() {
        return this.screen;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the background dimension.
     */
    public Dimension getBackgroundDimension() {
        return this.background;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Given a point we search for the first drawable that implements the DrawableOwner
     *  interface AND contains the point. We then return the owner of the drawable.
     *
     *  The search in the Drawable list is performed backward : we inspect top sprites
     *  ( high priority ) first.
     *
     *  @param x x cordinate
     *  @param y y cordinate
     *  @return the owner of the targeted drawable, null if none or not found.
     */
    public Object findOwner(int x, int y) {

        synchronized (this.drawables) {
            this.drawables.resetIteratorToEnd();

            while (this.drawables.hasPrev()) {
                Drawable d = this.drawables.prev();

                if (d instanceof DrawableOwner)
                    if (d.contains(x + this.screen.x, y + this.screen.y) && ((DrawableOwner) d).getOwner() != null)
                        return ((DrawableOwner) d).getOwner();
            }
        }

        return null;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Given a point we search for the first drawable that implements the DrawableOwner
     *  interface AND contains the point. We then return the owner of the drawable.
     *
     *  @param p Point
     *  @return the owner of the targeted drawable, null if none or not found.
     */
    public Object findOwner(Point p) {
        return findOwner(p.x, p.y);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the ImageLibrary associated to this GraphicsDirector.
     *  @return our ImageLibrary
     */
    public ImageLibrary getImageLibrary() {
        return this.imageLib;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
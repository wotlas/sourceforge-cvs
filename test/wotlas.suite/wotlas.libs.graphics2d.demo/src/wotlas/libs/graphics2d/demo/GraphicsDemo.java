/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - 2002 WOTLAS Team
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
package wotlas.libs.graphics2d.demo;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.FontFactory;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.ImageLibrary;
import wotlas.libs.graphics2d.drawable.MultiRegionImage;
import wotlas.libs.graphics2d.policy.LimitWindowPolicy;

/** Main Class. Displays a window that only contains a GraphicsDirector.
 *  The Graphics Director is the main class of our 2D engine : it's a JPanel where
 *  are displayed 'Drawable' objects.
 */
public class GraphicsDemo extends JFrame implements AWTEventListener {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Our graphics director */
    private GraphicsDirector gDirector;

    /** Our ImageLibrary */
    private ImageLibrary imageLib;

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /**  The woman the user is going to control */
    private Woman myWoman;
    /** Other women displayed on screen */
    private Woman women[];

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Constructor
     */
    public GraphicsDemo() {
        super("Wotlas Graphics2D Engine Demo");

        // 0 - We create a font factory that will manage the user fonts we want to display.
        // If you are only using default Java Fonts ( Dialog, Serif, etc. ) you don't need
        // one. In this demo we'll use the Lucida BlackLetter font found in the "data/fonts"
        // directory, so we need to create a font factory. We create the factory by giving
        // the path to the user fonts. Another creation method is also available if you want
        // load fonts from a JAR or over a network.
        //
        // IMPORTANT : note that the default behaviour of our factory is to manually load
        // the Lucida Blackletter font found in 'data/fonts/'. To change this behaviour
        // you'll have to edit the FontFactory init() method. In a next version of this API
        // we'll dynamically load the fonts found in 'data/fonts/'. This is not the case for
        // now. You have to declare the fonts you want to use in the FontFactory.
        FontFactory.createDefaultFontFactory("data/fonts");

        // 1 - Image Library Creation. The images of our Image Library are in 'data/graphics'
        // We load all the images from directories which name is not ending with "-JIT"
        // (images loaded Just In Time) or "-EXC" (for exclude and exclusive).
        // The images of the database can be accessed via an ImageIdentifier object.
        imageLib = ImageLibrary.createImageLibrary("data/graphics");

        // 2 - We create our player. 'true' means here that we'll control the character.
        myWoman = new Woman(true, imageLib);

        // 3 - Graphics Director Creation. The GraphicsDirector manages the drawables that
        // are displayed on screen, and refreshes the screen + updates the drawables state
        // when its tick() method is called. We choose a window policy that makes the game
        // screen move only when the player reaches the limit of the screen.
        //
        // If you have Java 1.4 you can replace 'new GraphicsDirector( ... , ... );' by
        // 'new EnhancedGraphicsDirector(...,...)' . This new EnhancedGraphicsDirector uses
        // Java 1.4 volatile image and thus renders images faster.
        //
        gDirector = new GraphicsDirector(new LimitWindowPolicy(), imageLib);

        // 3.1 - We create an ImageIdentifier that represent the image we want to use
        // as background. Our background image is huge so we splited it into multiple parts.
        ImageIdentifier backgroundIm = new ImageIdentifier("maps-1/demo-map-0");

        // 3.2 - We use a "MultiRegionImage" to display our background image.
        // A MultiRegionImage only loads the images around a reference ( our Woman character )
        // and unload the other images. A MultiRegionImage only works with an image that
        // has been splited into regular parts (grid). If you want to just use a simple image
        // as background just create a "MotionlessSprite".
        Drawable backgroundDr = (Drawable) new MultiRegionImage(
                myWoman.getDrawable(), // our reference for image loading
                380, // perception radius of our reference
                380, // grid deltax
                200, // grid deltay
                760, // image's total width
                800, // image's total height
                backgroundIm // background image
                );

        // 3.3 - We initialize the GraphicsDirector. To initialize properly it needs
        // (1) a drawable object that will represent the background, (2) another drawable
        // object the GraphicsDirector's screen view will follow.
        gDirector.init(
                backgroundDr, // background drawable
                myWoman.getDrawable(), // reference for screen movements
                new Dimension(400, 400) // screen default dimension
                );

        // 4 - Now that the GraphicsDirector has been created & initialized we can init
        // some extra visual properties of our character...
        myWoman.initVisualProperties(gDirector);

        // 5 - ... and add 30 other Woman to the map...
        women = new Woman[30];

        for (int i = 0; i < 30; i++) {
            women[i] = new Woman(false, imageLib);
            women[i].initVisualProperties(gDirector);
        }

        // 6 - We add key listeners on ourselves via an AWTEventListener, since the
        // javax.swing.JFrame is a little buggy on addKeyListener...
        Toolkit.getDefaultToolkit().addAWTEventListener(this, KeyEvent.KEY_EVENT_MASK);

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                gDirector.removeAllDrawables(); // a bit useless... but let's stay clean...
                System.exit(0);
            }
        });

        gDirector.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                // The user clicked on the screen, we ask the GraphicsDirector (1) if a
                // 'Drawable' object has been clicked and (2) if we can get the 'owner' of
                // the 'Drawable' ( i.e. for a Sprite the owner is a SpriteDataSupplier )
                Object selectedObject = gDirector.findOwner(e.getX(), e.getY());

                if (selectedObject != null && selectedObject instanceof Woman) {
                    // We display text & aura
                    Woman selectedWoman = (Woman) selectedObject;

                    // We add a label & aura to the selected woman.
                    gDirector.addDrawable(selectedWoman.getTextDrawable());
                    gDirector.addDrawable(selectedWoman.getAura());
                }
            }
        });

        // 7 - We add the GraphicsDirector to our JFrame (it's a JPanel) & display the whole...
        getContentPane().add(new JLabel("Use key arrows to move the woman..."), BorderLayout.SOUTH);
        getContentPane().add(gDirector, BorderLayout.CENTER);
        pack();

        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screensize.width - getWidth()) / 2, (screensize.height - getHeight()) / 2);

        setVisible(true);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** We transmit Key events to our Woman character.
     */
    public void eventDispatched(AWTEvent e) {
        if (e instanceof KeyEvent) {
            myWoman.eventDispatched((KeyEvent) e);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Our tick method. The game time is composed of ticks where we update positions,
     *  refresh the screen, etc... We create a thread to call these tick() regularly.
     */
    public void tick() {

        // We tick all our women...
        myWoman.tick();

        for (int i = 0; i < 30; i++) {
            women[i].tick();
        }

        // We tick our GraphicsDirector ( the tick is propagated on all Drawable objects
        // and our WindowPolicy, we then call a repaint on ourselves )
        gDirector.tick();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    public static void main(String argv[]) {

        // 1 - We create our GraphicsDemo Frame...     
        GraphicsDemo gDemo = new GraphicsDemo();

        // 2 - ... and we tick it regularly
        Object lock = new Object();

        while (true) {
            // 2.1 - tick on our demo
            gDemo.tick();

            // 2.2 - MANDATORY : we wait 20ms minimum to let eventual other tasks be processed
            // If you don't wait some time your application might become VERY slow ( task scheduler
            // saturated ).
            synchronized (lock) {
                try {
                    lock.wait(20);
                } catch (Exception e) {
                }
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

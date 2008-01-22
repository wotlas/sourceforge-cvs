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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import wotlas.libs.graphics2d.Animation;
import wotlas.libs.graphics2d.Drawable;
import wotlas.libs.graphics2d.GraphicsDirector;
import wotlas.libs.graphics2d.ImageIdentifier;
import wotlas.libs.graphics2d.ImageLibrary;
import wotlas.libs.graphics2d.drawable.AuraEffect;
import wotlas.libs.graphics2d.drawable.ShadowSprite;
import wotlas.libs.graphics2d.drawable.Sprite;
import wotlas.libs.graphics2d.drawable.SpriteDataSupplier;
import wotlas.libs.graphics2d.drawable.TextDrawable;
import wotlas.libs.graphics2d.filter.ColorImageFilter;

/** Class representing a woman in our Demo. A Woman has a graphical representation of
 *  herself ( myWoman.getDrawable() ) and supplies the data for it via the 
 *  SpriteDataSupplier interface.
 */
class Woman implements SpriteDataSupplier {

    /*------------------------------------------------------------------------------------*/
    /** Our player animation (image ID selected to be displayed on screen).
     */
    private Animation animation;
    /** Our sprite (our 2D representation).
     */
    private Sprite sprite;
    /** Our shadow (2D representation of the shadow).
     */
    private ShadowSprite shadow;
    /** Our textDrawable (text that appears on screen when our player is clicked)
     */
    private TextDrawable textDrawable;
    /** Aura Effect that appears when this player is being clicked.
     */
    private AuraEffect auraEffect;
    /** Is this instance player controlled ?
     */
    private boolean isPlayerControlled;
    /** Player position and orientation (in radian).
     */
    private float x,  y,  orientation;
    /** Is the player moving ?
     */
    private boolean isMoving;
    /** Dimension of the map.
     */
    private Dimension backgroundDim;

    /*------------------------------------------------------------------------------------*/
    /** Constructor.
     * @param isPlayerControlled set to false for computer controlled character.
     * @param imageLib image library
     */
    public Woman(boolean isPlayerControlled, ImageLibrary imageLib) {
        // 1 - Some inits
        this.isPlayerControlled = isPlayerControlled;

        // 2 - We create an image animation for the image of this Woman walking.
        // All the images of the animation are in the 'players-0/woman-0/walking-woman-0' directory.
        animation = new Animation(new ImageIdentifier("players-0/woman-0/walking-woman-0"), // directory to use for the animation
                imageLib // ImageLibrary where to take the images
                );

        // 3 - A sprite is an image that can move on screen and rely on a 'SpriteDataSupplier' (ourselves)
        // to get its position, orientation, scale, etc... Also we call useAntialiasing(true)
        // to have our sprite anti-aliased ( You can call this method on any Drawable object
        // but be aware that anti-aliased objects take more time to render ).
        sprite = new Sprite((SpriteDataSupplier) this, ImageLibRef.PLAYER_PRIORITY);
        sprite.useAntialiasing(true);
    }

    /*------------------------------------------------------------------------------------*/
    /** Called after graphicsDirector's init to add some visual effects to the master player
     *  or to show other players.
     */
    public void initVisualProperties(GraphicsDirector gDirector) {

        // 1 - We change some colors of the sprite... this filter is used in real time.
        // ImageFilter can only be used with Sprite objects.
        ColorImageFilter filter = new ColorImageFilter();
        sprite.setDynamicImageFilter(filter);

        if (isPlayerControlled) {
            // blue dress is changed to green, golden hair is changed to brown
            filter.addColorChangeKey(ColorImageFilter.blue, ColorImageFilter.green);
            filter.addColorChangeKey(ColorImageFilter.yellow, ColorImageFilter.brown);
        } else {
            // blue dress remains blue, golden hair is changed to darkgray
            filter.addColorChangeKey(ColorImageFilter.yellow, ColorImageFilter.darkgray);
        }

        // 2 - Shadow Creation for this Woman.
        shadow = new ShadowSprite(
                this, // SpriteDataSupplier for any data we might need
                new ImageIdentifier("players-0/woman-0/walking-woman-shadow-1"), // Animation to use for the shadow
                ImageLibRef.SHADOW_PRIORITY, // draw priority, lower than our Sprite !
                4, // shadow deltaX from Sprite position
                4 // shadow deltaY from Sprite position
                );

        shadow.useAntialiasing(true);

        // 3 - We pick a random location and orientation
        backgroundDim = gDirector.getBackgroundDimension();

        x = (float) Math.random() * backgroundDim.width;
        y = (float) Math.random() * backgroundDim.height;
        orientation = (float) (Math.random() * 6.3);

        // 4 - Finally, we add Sprite & Shadow to the GraphicsDirector
        if (!isPlayerControlled) {
            gDirector.addDrawable(sprite);
            gDirector.addDrawable(shadow);
        } else {
            gDirector.addDrawable(shadow);
        } // Our Woman sprite has already been added to our GraphicsDirector
    // when we gave it as the drawable reference in the gDirector.init() method
    }

    /*------------------------------------------------------------------------------------*/
    /*** SpriteDataSupplier implementation ***/
    /** To get the X image position.
     * @return x image coordinate
     */
    public int getX() {
        return (int) x;
    }

    /** To get the Y image position.
     * @return y image cordinate
     */
    public int getY() {
        return (int) y;
    }

    /** To get the image identifier to use.
     * @return image identifier.
     */
    public ImageIdentifier getImageIdentifier() {
        return animation.getCurrentImage();
    }

    /** To get the eventual rotation angle. 0 means no rotation.
     * @return angle in radians.
     */
    public double getAngle() {
        return orientation;
    }

    /** To get the X factor for scaling... 1.0 means no X scaling
     * @return X scale factor
     */
    public double getScaleX() {
        return 1.0;
    }

    /** To get the Y factor for scaling... 1.0 means no Y scaling
     * @return Y scale factor
     */
    public double getScaleY() {
        return 1.0;
    }

    /** To get the image's transparency ( 0.0 means invisible, 1.0 means fully visible ).
     * @return alpha
     */
    public float getAlpha() {
        return 1.0f;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Tick Method.
     */
    public void tick() {

        // 1 - Position Update needed ?
        if (!isPlayerControlled) {
            // Computer Controlled Player
            x = (float) (x + 3 * Math.cos(orientation));
            y = (float) (y + 3 * Math.sin(orientation));
            orientation += 0.01f;
            isMoving = true;
        }

        // 2 - Player out of the limit of the screen ?
        if (x < 0) {
            x = (float) (backgroundDim.width - 5);
        } else if (x >= backgroundDim.width) {
            x = 5.0f;
        }

        if (y < 0) {
            y = (float) (backgroundDim.height - 5);
        } else if (y >= backgroundDim.height) {
            y = 5.0f;
        }

        // 3 - Animation Update
        if (isMoving) {
            animation.tick();
        } // select next image
        else {
            animation.reset();
        } // return to first image

        isMoving = false;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** What do we do with Key events ?
     */
    public void eventDispatched(KeyEvent key) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_UP:
                isMoving = true;
                x = (float) (x + 2 * Math.cos(orientation));
                y = (float) (y + 2 * Math.sin(orientation));
                break;

            case KeyEvent.VK_DOWN:
                isMoving = true;
                x = (float) (x - 2 * Math.cos(orientation));
                y = (float) (y - 2 * Math.sin(orientation));
                break;

            case KeyEvent.VK_LEFT:
                isMoving = true;
                orientation -= 0.1;
                break;

            case KeyEvent.VK_RIGHT:
                isMoving = true;
                orientation += 0.1;
                break;
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To get the drawable of this woman.
     */
    public Drawable getDrawable() {
        return sprite;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** To get a text drawable representing the name of this woman.
     */
    public Drawable getTextDrawable() {
        if (textDrawable != null) {
            if (textDrawable.isLive()) {
                return null;
            }  // text already displayed on screen

            textDrawable.resetTimeLimit();
            return textDrawable;
        }

        if (isPlayerControlled) {
            textDrawable = new TextDrawable("Woman in Green", (Drawable) sprite, Color.white,
                    13.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, 5000);
        } else {
            textDrawable = new TextDrawable("Woman in Blue", (Drawable) sprite, Color.blue,
                    13.0f, "Lucida Blackletter", ImageLibRef.TEXT_PRIORITY, 5000);
        }
        return textDrawable;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Return an aura drawable.
     */
    public Drawable getAura() {
        if (auraEffect != null) {
            if (auraEffect.isLive()) {
                return null;
            } // aura still displayed on screen

            auraEffect.reset();
            return auraEffect;
        }

        if (isPlayerControlled) {
            auraEffect = new AuraEffect(this, new ImageIdentifier("players-0/woman-symbols-1/seal-0.gif"),
                    ImageLibRef.AURA_PRIORITY, 5000);
        } else {
            auraEffect = new AuraEffect(this, new ImageIdentifier("players-0/woman-symbols-1/broom-1.gif"),
                    ImageLibRef.AURA_PRIORITY, 5000);
            auraEffect.setAmplitudeLimit(0.6f);
        }

        auraEffect.useAntialiasing(true);

        return auraEffect;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

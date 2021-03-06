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

package wotlas.libs.graphics2D.drawable;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import wotlas.libs.graphics2D.Drawable;
import wotlas.libs.graphics2D.ImageIdentifier;

/** 
 *  A MultiRegionImage is especially useful for HUGE background images that can't be
 *  loaded directly into memory. Instead we split the image into a set of smaller images
 *  mapped on a grid. The grid cells have a fixed width ( deltaX parameter ) and a fixed
 *  height ( deltaY parameter ).<p><br>
 *
 *  To compute which regions are visible we refer to a Drawable object that must be given
 *  in the MultiRegionImage constructor. You must also give a perception radius that will
 *  represent the zone the drawable is able to see on screen.<p>
 *
 *  With a drawable & perception radius we are able to load/unload the region's small
 *  images as the drawable moves. To match the screen movements the given reference
 *  drawable should be the same reference drawable used by the Graphics Director.<p>
 *
 *  The small images are taken from the same ImageDatabase "Action" directory. The
 *  associated "Set" directory MUST have the "-exc" option ( see the ImageLibrary for
 *  details on all that ). This way we can load/unload only the images we want.<p><br>
 *
 *  The association between the regions and the image indexes is done this way :<p><br>
 *
 *   0    1     2 <br>
 *   3    4     5 <br><p>
 *
 *  In the above example we have 6 regions and the 0, 1, ..., 5 numbers represent
 *  the small image indexes.
 *
 * @author MasterBob, Aldiss
 * @see wotlas.libs.graphics2D.Drawable
 * @see wotlas.libs.graphics2D.ImageLibrary
 */

public class MultiRegionImage extends Drawable {

    /*------------------------------------------------------------------------------------*/

    /** Our reference drawable for movements
     */
    private Drawable refDrawable;

    /** Perception radius for the refDrawable. This helps to determine which images
     *  we must display.
     */
    private int perceptionRadius;

    /** Images we use for each region.
     */
    private ImageIdentifier image[][];

    /** X delta between two regions ( x dim of of a region )
     */
    private int deltaX;

    /** Y delta between two regions ( y dim of of a region )
     */
    private int deltaY;

    /** Number of regions on the x cordinates.
     */
    private int nbRegionX;

    /** Number of regions on the y cordinates.
     */
    private int nbRegionY;

    /** Interval added to the perceptionRadius to compute which images
     *  are not used and can be deleted. This way we avoid nasty oscillations
     *  between image loading & image destruction.
     */
    private int destructionDelta;

    /** Our base image identifier we use for image loading.
     */
    private ImageIdentifier imBase;

    /*------------------------------------------------------------------------------------*/

    /** Constructor. A MultiRegionImage priority is always 0. The default destructionDelta 
     *  value is 30.
     *
     * @param refDrawable reference drawable which position helps us to compute region
     *        visibility
     * @param perceptionRadius tells in which rectangular zone around our refDrawable we
     *        must display the images. Helps to compute visible regions.
     * @param deltaX X delta between two regions ( x dim of of a region )
     * @param deltaY Y delta between two regions ( y dim of of a region )
     * @param width entire width of the whole image, MUST be a multiple of deltaX
     * @param height entire height of the whole image, MUST be a multiple of deltaY
     * @param imBase image identifier to use as a base for the images in the library.
     */
    public MultiRegionImage(Drawable refDrawable, int perceptionRadius, int deltaX, int deltaY, int width, int height,
            ImageIdentifier imBase) {
        super();

        this.r.x = 0;
        this.r.y = 0;
        this.r.width = width;
        this.r.height = height;

        this.nbRegionX = width / deltaX;
        this.nbRegionY = height / deltaY;

        this.destructionDelta = 30;

        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.refDrawable = refDrawable;
        this.perceptionRadius = perceptionRadius;
        this.imBase = imBase;

        this.image = new ImageIdentifier[this.nbRegionX][this.nbRegionY]; // our grid

        this.priority = 0; // always first drawable to be displayed
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To set a new value for the destructionDelta. The destructionDelta is the interval
     *  that is added to the perceptionRadius to compute which images are not used and
     *  can be deleted. This way we avoid nasty oscillations between image loading &
     *  image destruction.
     *
     * @param destructionDelta new value
     */
    public void setDestructionDelta(int destructionDelta) {
        this.destructionDelta = destructionDelta;
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

        // We draw all the images...
        for (int i = 0; i < this.nbRegionX; i++)
            for (int j = 0; j < this.nbRegionY; j++)
                if (this.image[i][j] != null)
                    gc.drawImage(this.imageLib.getImage(this.image[i][j]), i * this.deltaX - screen.x, j * this.deltaY - screen.y, null);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Tick method called by the GraphicsDirector. We load/unload images as needed.
     *
     *  @return always return true as a MultiRegionImage is always "live".
     */
    @Override
    public boolean tick() {

        // 1 - some inits
        int x = this.refDrawable.getRectangle().x;
        int y = this.refDrawable.getRectangle().y;

        // 2 - we remove unused images
        int indIMin = (x - this.perceptionRadius - this.destructionDelta) / this.deltaX;
        int indIMax = (x + this.perceptionRadius + this.destructionDelta) / this.deltaX;

        int indJMin = (y - this.perceptionRadius - this.destructionDelta) / this.deltaY;
        int indJMax = (y + this.perceptionRadius + this.destructionDelta) / this.deltaY;

        if (indIMin < 0)
            indIMin = 0;
        else if (indIMin >= this.nbRegionX)
            indIMin = this.nbRegionX - 1;

        if (indJMin < 0)
            indJMin = 0;
        else if (indJMin >= this.nbRegionY)
            indJMin = this.nbRegionY - 1;

        if (indIMax < 0)
            indIMax = 0;
        else if (indIMax >= this.nbRegionX)
            indIMax = this.nbRegionX - 1;

        if (indJMax < 0)
            indJMax = 0;
        else if (indJMax >= this.nbRegionY)
            indJMax = this.nbRegionY - 1;

        boolean outOfRange;

        for (int i = 0; i < this.nbRegionX; i++) {
            if (i < indIMin || indIMax < i)
                outOfRange = true;
            else
                outOfRange = false;

            for (int j = 0; j < this.nbRegionY; j++) {
                if (!outOfRange && (indJMin <= j && j <= indJMax))
                    continue;

                if (this.image[i][j] != null) {
                    this.imageLib.unloadImage(this.image[i][j]); // unload
                    this.image[i][j] = null;
                }
            }
        }

        // 3 - we load any needed new image
        indIMin = (x - this.perceptionRadius) / this.deltaX;
        indIMax = (x + this.perceptionRadius) / this.deltaX;

        indJMin = (y - this.perceptionRadius) / this.deltaY;
        indJMax = (y + this.perceptionRadius) / this.deltaY;

        if (indIMin < 0)
            indIMin = 0;
        else if (indIMin >= this.nbRegionX)
            indIMin = this.nbRegionX - 1;

        if (indJMin < 0)
            indJMin = 0;
        else if (indJMin >= this.nbRegionY)
            indJMin = this.nbRegionY - 1;

        if (indIMax < 0)
            indIMax = 0;
        else if (indIMax >= this.nbRegionX)
            indIMax = this.nbRegionX - 1;

        if (indJMax < 0)
            indJMax = 0;
        else if (indJMax >= this.nbRegionY)
            indJMax = this.nbRegionY - 1;

        for (int i = indIMin; i <= indIMax; i++)
            for (int j = indJMin; j <= indJMax; j++)
                if (this.image[i][j] == null) {
                    this.image[i][j] = new ImageIdentifier(this.imBase);
                    this.image[i][j].setImageId((short) (this.nbRegionX * j + i));
                    this.imageLib.loadImage(this.image[i][j]);
                }

        return true; // a MultiRegionImage is always "live" by default.
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

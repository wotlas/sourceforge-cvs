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

import java.io.Serializable;

/** Identifies an image in the ImageLibrary. It is created by giving the name of
 *  sub-directories in the disk space that represents the Image library (where the images are stored).
 *
 *  For example if the image library contains a directory "animals-0/cats-0-jit/" that
 *  have  'cat-0.jpg', 'cat-1.jpg' and 'cat-2.gif' images, the following ImageIdentifier :<br>
 *  <pre>
 *     String path[] = { "animals-0", "cats-0" };
 *     ImageIdentifier im = new ImageIdentifier( path );
 *
 *    or
 *
 *     String path[] = { "animals-0", "cats-0", "cat-0.jpg" };
 *     ImageIdentifier im = new ImageIdentifier( path );
 *
 *    or
 *
 *     ImageIdentifier im = new ImageIdentifier( "animals-0/cats-0" );
 *
 *    or
 *
 *     ImageIdentifier im = new ImageIdentifier( "animals-0/cats-0/cat-0.jpg" );
 *
 *  </pre><br>
 *   will always represent the first image of the "cats-0" directory, i.e. 'cats-0.jpg'. Note that
 *   directory options are optional here ( the directory name was "cats-0-jit", but we gave "cats-0").<br>
 *
 *   This other ImageIdentifier :
 *  <pre>
 *     String path[] = { "animals-0", "cats-0", "cat-1.jpg" };
 *     ImageIdentifier im = new ImageIdentifier( path );
 *  </pre><br>
 *   will represent the specified image "cats-1.jpg" of the "cat" directory. The extension ".jpg" is
 *   mandatory.<br>
 *
 *   This other ImageIdentifier :
 *  <pre>
 *     String path[] = { "animals-0", "cats-0" };
 *     ImageIdentifier im = new ImageIdentifier( path, true );
 *  </pre>
 *
 *  <p>will represent the image "cat-0.jpg" of the "cat" directory, but will also tell that
 *  this ImageIdentifier is an animation. This means that all the images of the "cat" directory
 *  are the images of a same animation. For example, if you then give this ImageIdentifier to a
 *  MotionlessSprite ( Drawable object ) it will create an Animation object to change the current
 *  image at each application tick(). We'll start with 'cat-0.jpg' then continue with 'cat-1.jpg',
 *  'cat-2.gif', 'cat-0.jpg', etc...</p>
 *
 *  <p><b>Important</b> : When you create an ImageIdentifier you give "String" objects as arguments.
 *  These strings are not kept in memory, they are replaced by an internal integer representation.
 *  Thus once created an ImageIdentifier represents directly a valid entry of the ImageLibrary.</p>
 *
 *  <p>To create an ImageIdentifier we don't need the ImageLibrary : from a "<name>-<number>"
 *  directory name or <name>-<number>.<jpg|gif> image name we retrieve the <number> part.
 *  For more examples take a look at the ImageLibrary.</p>
 *
 *
 * @author MasterBob, Aldiss, Diego
 * @see wotlas.libs.graphics2d.ImageLibrary
 */

public class ImageIdentifier implements Serializable {

    /*------------------------------------------------------------------------------------*/

    /** Integer identifiers pointing to the directory in the ImageLibrary where
     *  our image is.
     */
    protected short dirIds[];

    /** This is a declaration for persistence only.
     *  It's the same ids as above (dirIds) but as a 'Short' array for persistence.
     */
    protected Short dirPersistenceIds[];

    /** Image ID in the selected directory. We could have regroup dirIds[] and imageId
     *  in a same array but it would'nt have been practical to use. This way it's much easier.
     */
    protected short imageId;

    /** Use this ImageIdentifier as an animation ? (it's a hint for other classes)
     */
    protected boolean isAnimation;

    /*------------------------------------------------------------------------------------*/

    /** Constructor for persistence.
     */
    public ImageIdentifier() {
        this.dirIds = new short[0];
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with path & no animation.
     * @param path sub-directories (plus eventual specified image) pointing to the image.
     */
    public ImageIdentifier(String path[]) {
        constructImageIdentifier(path, false);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with path & animation. Important : our internal representation is
     *  always a full one : directory IDs + final image ID.
     *
     * @param path sub-directories (plus eventual specified image) pointing to the image.
     * @param hasAnimation use the path base for an animation ?
     */
    public ImageIdentifier(String path[], boolean isAnimation) {
        constructImageIdentifier(path, isAnimation);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To construct the imageIdentifier.
     * @param path sub-directories (plus eventual specified image) pointing to the image.
     * @param hasAnimation use the path base for an animation ?
     */
    protected void constructImageIdentifier(String path[], boolean isAnimation) {
        this.isAnimation = isAnimation;

        if (path == null || path.length == 0) {
            this.dirIds = new short[0]; // no dir and
            this.imageId = 0; // first image as default
        }

        // Is the last entry of the path an Image ?
        short ID = (short) ImageLibrary.getImageID(path[path.length - 1]);

        if (ID < 0) {
            this.dirIds = new short[path.length]; // last ID in the path is a
            // directory
            this.imageId = 0; // image ID set to 0 (default).
        } else {
            this.dirIds = new short[path.length - 1]; // last ID is an image, we don't need to keep some
            this.imageId = ID; // place for it, we just save it's ID..
        }

        // Save directory IDs
        for (int i = 0; i < this.dirIds.length; i++) {
            this.dirIds[i] = (short) ImageLibrary.getDirectoryID(path[i]);

            if (this.dirIds[i] < 0 && ImageLibrary.DEBUG_MODE)
                System.out.println("WARNING: ImageIdentifier has bad id: (" + path[i] + ")");
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with path & no animation.
     * @param path pointing to the image (relative path, don't enter the image database path).
     */
    public ImageIdentifier(String path) {
        this(path, false);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Constructor with path & animation. Important : our internal representation is
     *  always a full one : directory IDs + final image ID.
     *
     * @param path path pointing to the image.
     * @param hasAnimation use the path base for an animation ?
     */
    public ImageIdentifier(String path, boolean isAnimation) {

        // Null path ?
        if (path == null || path.length() == 0 || path.equals("/")) {
            constructImageIdentifier(null, isAnimation);
            return;
        }

        // We suppress an eventual ending '/'
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 2);

        // We count the number of '/'
        int pos = 0;
        int count = 0;

        while ((pos = path.indexOf('/', pos) + 1) > 0)
            count++;

        String splitPath[] = new String[count + 1];

        if (count == 0)
            splitPath[0] = path;
        else {
            pos = 0;
            int pos2 = 0;
            count = 0;

            while ((pos2 = path.indexOf('/', pos)) > 0) {
                splitPath[count] = path.substring(pos, pos2);
                count++;
                pos = pos2 + 1;
            }

            splitPath[count] = path.substring(pos, path.length());
        }

        constructImageIdentifier(splitPath, isAnimation);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Construction from an other ImageIdentifier.
     *
     * @param otherIm other ImageIdentifier to copy
     */
    public ImageIdentifier(ImageIdentifier otherIm) {
        this.dirIds = new short[otherIm.dirIds.length];

        for (short i = 0; i < this.dirIds.length; i++)
            this.dirIds[i] = otherIm.dirIds[i];

        this.imageId = otherIm.imageId;
        this.isAnimation = otherIm.isAnimation;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Construction from an array.
     *
     * @param dirIds internal directory identifiers to set (set by copy).
     * @param imageId internal image identifier to set.
     * @param isAnimation use the path base for an animation ?
     */
    public ImageIdentifier(short dirIds[], short imageID, boolean isAnimation) {
        if (dirIds != null)
            this.dirIds = new short[dirIds.length];
        else
            this.dirIds = new short[0];

        for (short i = 0; i < this.dirIds.length; i++)
            this.dirIds[i] = dirIds[i];

        this.imageId = imageID;
        this.isAnimation = isAnimation;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    // Getters & Setters for persistence
    /*
        public short[] getDirIds() {
           return dirIds;
        }

        public void setDirIds(short dirIds[]) {
           this.dirIds = dirIds;
        }
    */

    /** Getter for persistence purpose only.
     */
    public Short[] getDirPersistenceIds() {

        Short toReturn[] = new Short[this.dirIds.length];

        for (int i = 0; i < this.dirIds.length; i++)
            toReturn[i] = new Short(this.dirIds[i]);

        return toReturn;
    }

    /** Setter for persistence purpose only.
     */
    public void setDirPersistenceIds(Short dirPersistenceIds[]) {

        this.dirIds = new short[dirPersistenceIds.length];

        for (int i = 0; i < dirPersistenceIds.length; i++)
            this.dirIds[i] = dirPersistenceIds[i].shortValue();
    }

    /** Getter for internal use only.
     */
    public short getImageId() {
        return this.imageId;
    }

    /** Setter for internal use only.
     */
    public void setImageId(short imageId) {
        this.imageId = imageId;
    }

    /** To know if this ImageIdentifier represents an animation.
     */
    public boolean getIsAnimation() {
        return this.isAnimation;
    }

    /** To set if this ImageIdentifier represents an animation.
     */
    public void setIsAnimation(boolean isAnimation) {
        this.isAnimation = isAnimation;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To String method.
     * @return string representation
     */
    @Override
    public String toString() {
        String str = "ImageId: dir[";

        for (short i = 0; i < this.dirIds.length; i++)
            str += this.dirIds[i] + ", ";

        return str + "] image:" + this.imageId + " animation:" + this.isAnimation;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

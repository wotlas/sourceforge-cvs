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

import java.awt.Image;

/** To locate resources needed by the ImageLibrary.
 *
 * @author Aldiss
 */

public interface ImageResourceLocator {

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**  To get the Image Library Directory.
     */
    public String getImageLibraryDir();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To list all the files of a directory ( on one level only ).
     *
     *  @param dirName directory name (must be a complete path)
     *  @return all the files of the specified directory (not sub-dirs).
     */
    public String[] listFiles(String dirName);

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To list all the directories of a directory ( on one level only ).
     *
     *  @param dirName directory name (must be a complete path)
     *  @return the sub-dirs of the given dirName (on one level only).
     */
    public String[] listDirectories(String dirName);

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the wanted image from the Image Library directory.
     *
     *  @param imagePath image name from the library with FULL image library path.
     *  @return Image, null if the image was not found.
     */
    public Image getLibraryImage(String imagePath);

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

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

package wotlas.libs.graphics2D.filter;

import java.awt.image.BufferedImage;
import wotlas.libs.graphics2D.DynamicImageFilter;
import wotlas.libs.graphics2D.filter.color.BlueColor;
import wotlas.libs.graphics2D.filter.color.BrownColor;
import wotlas.libs.graphics2D.filter.color.Converter;
import wotlas.libs.graphics2D.filter.color.DarkGrayColor;
import wotlas.libs.graphics2D.filter.color.GrayColor;
import wotlas.libs.graphics2D.filter.color.GreenColor;
import wotlas.libs.graphics2D.filter.color.LightGrayColor;
import wotlas.libs.graphics2D.filter.color.LightYellowColor;
import wotlas.libs.graphics2D.filter.color.RedColor;
import wotlas.libs.graphics2D.filter.color.WhiteColor;
import wotlas.libs.graphics2D.filter.color.YellowColor;

/** A DynamicImageFilter that can change the colors of a BufferefImage. You can only
 *  change 'types' of colors : all blue pixels, all green pixels, ... many changes
 *  can be performed at the same time as the 'addColorChange' method can be called
 *  more than one time.
 *
 *  Actually this filter only works with fixed source colors and target colors. See
 *  the addColorChange javadoc for more details.
 *
 * @author Aldiss
 * @see wotlas.libs.graphics2D.DynamicImageFilter
 */

public class ColorImageFilter implements DynamicImageFilter {

    /*------------------------------------------------------------------------------------*/

    /** Blue Color Type
     */
    public final static ColorType blue = new BlueColor();

    /** Green Color Type
     */
    public final static ColorType green = new GreenColor();

    /** Yellow Color Type
     */
    public final static ColorType yellow = new YellowColor();

    /** Light Yellow Color Type
     */
    public final static ColorType lightYellow = new LightYellowColor();

    /** Red Color Type
     */
    public final static ColorType red = new RedColor();

    /** Brown Color Type
     */
    public final static ColorType brown = new BrownColor();

    /** White Color Type
     */
    public final static ColorType white = new WhiteColor();

    /** Light Gray Color Type
     */
    public final static ColorType lightgray = new LightGrayColor();

    /** Gray Color Type
     */
    public final static ColorType gray = new GrayColor();

    /** Dark Gray Color Type
     */
    public final static ColorType darkgray = new DarkGrayColor();

    /*------------------------------------------------------------------------------------*/

    /** Color Type couples (source & target) for our color change.
     */
    private ColorType colorChangeKey[][];

    /*------------------------------------------------------------------------------------*/

    /** To add a ColorChangeKey to this ColorImageFilter. How does it work ?
     *  well, this is simple. Here is an example :
     *
     *  addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.green );
     *
     *  With this key we'll transform all the blue pixels in green pixels.
     *
     * @param colorSourceId source ColorType
     * @param colorTargetId target ColorType
     */
    public void addColorChangeKey(ColorType colorSourceId, ColorType colorTargetId) {
        ColorType key[] = new ColorType[2];
        key[0] = colorSourceId;
        key[1] = colorTargetId;

        if (this.colorChangeKey == null) {
            this.colorChangeKey = new ColorType[1][];
            this.colorChangeKey[0] = key;
        } else {
            ColorType tmp[][] = new ColorType[this.colorChangeKey.length + 1][];
            System.arraycopy(this.colorChangeKey, 0, tmp, 0, this.colorChangeKey.length);
            tmp[this.colorChangeKey.length] = key;
            this.colorChangeKey = tmp;
        }
    }

    /*------------------------------------------------------------------------------------*/

    /** To create a new filtered image from an image source.
     *
     * @param srcIm source BufferedImage we take our data from (not modified).
     * @return new BufferedImage constructed from the given image.
     */
    public BufferedImage filterImage(BufferedImage srcIm) {

        if (srcIm == null)
            return null;

        int width = srcIm.getWidth();
        int height = srcIm.getHeight();

        // 1 - New Buffered Image
        if (this.colorChangeKey == null)
            return srcIm;

        BufferedImage dstIm = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 2 - Color Filter
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                dstIm.setRGB(i, j, filterPixel(srcIm.getRGB(i, j)));

        return dstIm;
    }

    /*------------------------------------------------------------------------------------*/

    /** To filter a pixel according to our ColorChange keys...
     *  @param argb pixel color
     */
    private int filterPixel(int argb) {

        short alpha = Converter.getAlpha(argb);

        if (alpha == 0)
            return argb; // transparent pixel

        short min = Converter.getRed(argb), mid = Converter.getGreen(argb), max = Converter.getBlue(argb), tmp;

        // 1 - Color to remplace ?
        byte keyID = -1;

        for (byte i = 0; i < this.colorChangeKey.length; i++)
            if (this.colorChangeKey[i][0].isFromThisColorType(min, mid, max)) {
                keyID = i;
                break;
            }

        if (keyID == -1)
            return argb;

        // 2 - We sort our min, mid, max luminosity components
        if (min > mid) {
            tmp = min;
            min = mid;
            mid = tmp;
        }

        if (max < mid) {
            tmp = max;
            max = mid;
            mid = tmp;
        }

        if (min > mid) {
            tmp = min;
            min = mid;
            mid = tmp;
        }

        // 3 - Color replace
        return this.colorChangeKey[keyID][1].setToColorType(min, mid, max) | (alpha << 24);
    }

    /*------------------------------------------------------------------------------------*/

}

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

package wotlas.common.character;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import wotlas.common.universe.WotlasLocation;
import wotlas.libs.graphics2D.ImageIdentifier;

/** A Human Wotlas Character.
 *
 * @author Aldiss
 * @see wotlas.common.character.WotCharacter
 */

public abstract class Human implements WotCharacter {

    /*------------------------------------------------------------------------------------*/

    /** Hair color
     */
    public final static String hairColors[] = { "bald", "golden", "brown", "black", "gray", "white", "reddish", };

    /*------------------------------------------------------------------------------------*/

    /** Hair color [PUBLIC INFO]
     */
    protected String hairColor;

    /** Speed [RECONSTRUCTED INFO - NOT REPLICATED]
     */
    transient protected float speed;

    /** TO ADD : other common human fields ( force, dexterity, etc ... ) */

    /*------------------------------------------------------------------------------------*/

    /** To get the hair color of the human player.
     */
    public String getHairColor() {
        return this.hairColor;
    }

    /** To set the hair color of the human player. If the hair color given
     *  doesn't exist in our list we set it as "unknown".
     */
    public void setHairColor(String hairColor) {
        if (hairColor != null)
            for (int i = 0; i < Human.hairColors.length; i++)
                if (hairColor.equals(Human.hairColors[i])) {
                    this.hairColor = hairColor;
                    return;
                }

        this.hairColor = "unknown";
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns an image for this character.
     *
     *  @param playerLocation player current location
     *  @return image identifier of this character.
     */
    public ImageIdentifier getImage(WotlasLocation playerLocation) {

        // Default image for towns & worlds
        if (playerLocation.isTown() || playerLocation.isWorld())
            return new ImageIdentifier("players-0/players-small-images-1/player-small-0");

        return null; // null otherwise, we let sub-classes redefine the rest...
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** Returns the speed of this character.
     *
     *  @param playerLocation player current location
     *  @return speed in pixel/s
     */
    public float getSpeed(WotlasLocation playerLocation) {
        if (playerLocation.isRoom())
            return 60.0f; // Default human speed ( 60pixel/s = 2m/s )
        else if (playerLocation.isTown())
            return 10.0f;
        else
            return 5.0f;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To put the WotCharacter's data on the network stream. You don't need
      * to invoke this method yourself, it's done automatically.
      *
      * @param ostream data stream where to put your data (see java.io.DataOutputStream)
      * @param publicInfoOnly if false we write the player's full description, if true
      *                     we only write public info
      * @exception IOException if the stream has been closed or is corrupted.
      */
    public void encode(DataOutputStream ostream, boolean publicInfoOnly) throws IOException {
        ostream.writeUTF(this.hairColor);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To retrieve your WotCharacter's data from the stream. You don't need
     * to invoke this method yourself, it's done automatically.
     *
     * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
     * @param publicInfoOnly if false it means the available data is the player's full description,
     *                     if true it means we only have public info here.
     * @exception IOException if the stream has been closed or is corrupted.
     */
    public void decode(DataInputStream istream, boolean publicInfoOnly) throws IOException {
        this.hairColor = istream.readUTF();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

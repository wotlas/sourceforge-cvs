/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
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

import java.io.*;

import wotlas.common.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;


/** An Aes Sedai character.
 *
 * @author Aldiss
 * @see wotlas.common.character.Female
 */

public class AesSedai extends Female {

 /*------------------------------------------------------------------------------------*/

  /** Ajah & Aes Sedai status
   */
    public final static byte AES_NOVICE       = 0;
    public final static byte AES_ACCEPTED     = 1;
    public final static byte AES_BROWN_AJAH   = 2;
    public final static byte AES_WHITE_AJAH   = 3;
    public final static byte AES_BLUE_AJAH    = 4;
    public final static byte AES_GREEN_AJAH   = 5;
    public final static byte AES_RED_AJAH     = 6;
    public final static byte AES_GRAY_AJAH    = 7;
    public final static byte AES_YELLOW_AJAH  = 8;
    public final static byte AES_AMYRLIN      = 9;

 /*------------------------------------------------------------------------------------*/

  /** Aes Sedai status ( ajah, novice, accepted, amyrlin ). [PUBLIC INFO]
   */
    private byte aesSedaiStatus;

 /*------------------------------------------------------------------------------------*/

  /** Current Sprite.
   */
    transient private Sprite aesSedaiSprite;

 /*------------------------------------------------------------------------------------*/

  /** Getters & Setters for persistence
   */
    public byte getAesSedaiStatus() {
       return aesSedaiStatus; 
    }

    public void setAesSedaiStatus( byte aesSedaiStatus ) {
       this.aesSedaiStatus = aesSedaiStatus;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get a Drawable for this character. This is can not be used on the
    *  server side : if no ImageLibrary has been created we return null.
    *
    * @param player the player to chain the drawable to. If a XXXDataSupplier is needed
    *               we sets it to this player object.
    * @return a Drawable for this character, null if no ImageLibrary is present.
    */
      public Drawable getDrawable( Player player ) {
      	 if( ImageLibrary.getDefaultImageLibrary() == null )
      	     return null;

         if(aesSedaiSprite!=null)
             return (Drawable) aesSedaiSprite; 

         return new Sprite( (SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns an image for this character.
   *
   *  @return image identifier of this character.
   */
     public ImageIdentifier getImage() {
     
       // default for now. TO ADD : return a different image by considering the
       // hair color and aesSedaiStatus.
         return new ImageIdentifier( ImageLibRef.PLAYERS_CATEGORY ,
                                     ImageLibRef.AES_SEDAI_SET ,
                                     ImageLibRef.AES_BLUE_GOLDH_WALKING_ACTION
                                   );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Tests if the given int is a valid aesSedaiStatus.
    *  @param aesSedaiStatus
    *  @return true if the aesSedaiStatus is valid, false otherwise.
    */
    public static boolean isValidAesSedaiStatus( byte aesSedaiStatus ) {
    	if( aesSedaiStatus>=0 && aesSedaiStatus<=9 )
    	    return true;
    	return false;
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
     public void encode( DataOutputStream ostream, boolean publicInfoOnly ) throws IOException {
     	super.encode( ostream, publicInfoOnly );
     	ostream.writeByte( aesSedaiStatus );
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
     public void decode( DataInputStream istream, boolean publicInfoOnly ) throws IOException {
        super.decode( istream, publicInfoOnly );
     	aesSedaiStatus = istream.readByte();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

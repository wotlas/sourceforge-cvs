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
import java.awt.Color;

import wotlas.common.*;
import wotlas.common.universe.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.filter.*;


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

  /** Current Shadow.
   */
    transient private ShadowSprite aesSedaiShadowSprite;

  /** Current Aura.
   */
    transient private AuraEffect aesSedaiAuraEffect;

  /** ColorImageFilter for InteriorMap Sprites.
   */
    transient private ColorImageFilter filter;

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
    *  The returned Drawable is unique : we always return the same drawable per
    *  AesSedai instance.
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

       // 1 - Sprite Creation + Filter
          aesSedaiSprite = new Sprite( (SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY );
          aesSedaiSprite.useAntialiasing(true);

          filter = new ColorImageFilter();

          switch( aesSedaiStatus ) {
              case AES_NOVICE :
                      filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.white );
                      break;
              case AES_ACCEPTED :
                      filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.white );
                      break;
              case AES_BROWN_AJAH :
                      filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.brown );
                      break;
              case AES_WHITE_AJAH:
                      filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.white );
                      break;
              case AES_BLUE_AJAH :
                      break;
              case AES_GREEN_AJAH :
                      filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.green );
                      break;
              case AES_RED_AJAH :
                      filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.red );
                      break;
              case AES_GRAY_AJAH :
                      filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.gray );
                      break;
              case AES_YELLOW_AJAH :
                      filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.yellow );
                      break;
              case AES_AMYRLIN :
                      filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.lightgray );
                      break;
          }

      // 2 - Hair Color
          switch( hairColor ) {
              case BALD :
                      break;
              case GOLDEN_HAIR :
                      break;
              case BROWN_HAIR :
                      filter.addColorChangeKey( ColorImageFilter.yellow, ColorImageFilter.brown );
                      break;
              case BLACK_HAIR:
                      filter.addColorChangeKey( ColorImageFilter.yellow, ColorImageFilter.darkgray );
                      break;
              case GREY_HAIR :
                      filter.addColorChangeKey( ColorImageFilter.yellow, ColorImageFilter.gray );
                      break;
              case WHITE_HAIR :
                      filter.addColorChangeKey( ColorImageFilter.yellow, ColorImageFilter.lightgray );
                      break;
              case REDDISH_HAIR :
                      filter.addColorChangeKey( ColorImageFilter.yellow, ColorImageFilter.red );
                      break;
          }

         aesSedaiSprite.setDynamicImageFilter( filter );
         return aesSedaiSprite;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's shadow. Important: a character Drawable MUST have been created
   *  previously ( via a getDrawable call ). You don't want to create a shadow with no
   *  character, do you ?
   *
   *  @return character's Shadow Drawable.
   */
     public Drawable getShadow(){
      	 if( ImageLibrary.getDefaultImageLibrary() == null )
      	     return null;

         if(aesSedaiShadowSprite!=null)
             return (Drawable) aesSedaiShadowSprite; 

      // Shadow Creation
         aesSedaiShadowSprite = new ShadowSprite( aesSedaiSprite.getDataSupplier(),
                                                  new ImageIdentifier(
                                                       ImageLibRef.PLAYERS_CATEGORY,
                                                       ImageLibRef.PLAYER_SHADOW_IMAGES_SET,
                                                       ImageLibRef.AES_SEDAI_WALK_SHADOW_ACTION ),
                                                  ImageLibRef.SHADOW_PRIORITY, 4, 4 );
         aesSedaiShadowSprite.useAntialiasing(true);
         return aesSedaiShadowSprite;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's aura.
   *  @return character's Aura Drawable.
   */
     public Drawable getAura(){
      	 if( ImageLibrary.getDefaultImageLibrary() == null )
      	     return null;

         if(aesSedaiAuraEffect!=null) {
             if(aesSedaiAuraEffect.isLive()) {
                return null; // aura still displayed on screen
             }

             aesSedaiAuraEffect.setAlpha( 0.0f );
             return (Drawable) aesSedaiAuraEffect;
         }

      // symbol selection
         short imageIndex = 0;

          switch( aesSedaiStatus ) {
              case AES_NOVICE :
                      imageIndex = ImageLibRef.AES_NOVICE_SYMBOL_INDEX;
                      break;
              case AES_ACCEPTED :
                      imageIndex = ImageLibRef.AES_ACCEPTED_SYMBOL_INDEX;
                      break;
              case AES_BROWN_AJAH :
                      imageIndex = ImageLibRef.AES_BROWN_SYMBOL_INDEX;
                      break;
              case AES_WHITE_AJAH:
                      imageIndex = ImageLibRef.AES_WHITE_SYMBOL_INDEX;
                      break;
              case AES_BLUE_AJAH :
                      imageIndex = ImageLibRef.AES_BLUE_SYMBOL_INDEX;
                      break;
              case AES_GREEN_AJAH :
                      imageIndex = ImageLibRef.AES_GREEN_SYMBOL_INDEX;
                      break;
              case AES_RED_AJAH :
                      imageIndex = ImageLibRef.AES_RED_SYMBOL_INDEX;
                      break;
              case AES_GRAY_AJAH :
                      imageIndex = ImageLibRef.AES_GREY_SYMBOL_INDEX;
                      break;
              case AES_YELLOW_AJAH :
                      imageIndex = ImageLibRef.AES_YELLOW_SYMBOL_INDEX;
                      break;
              case AES_AMYRLIN :
                      imageIndex = ImageLibRef.AES_AMYRLIN_SYMBOL_INDEX;
                      break;
          }

      // Aura Creation
         ImageIdentifier auraImage = new ImageIdentifier( ImageLibRef.PLAYERS_CATEGORY,
                                                          ImageLibRef.PLAYER_SYMBOL_IMAGES_SET,
                                                          ImageLibRef.AES_SEDAI_SYMBOL_ACTION,
                                                          imageIndex );

         aesSedaiAuraEffect = new AuraEffect( aesSedaiSprite.getDataSupplier(), auraImage,
                                              ImageLibRef.AURA_PRIORITY, false, 5000 );
         aesSedaiAuraEffect.useAntialiasing(true);
         return aesSedaiAuraEffect;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's color.
   *  @return character's color
   */
     public Color getColor(){
          switch( aesSedaiStatus ) {
              case AES_BROWN_AJAH :
                   return new Color(180,158,80);
              case AES_BLUE_AJAH :
                   return new Color(119,152,213);
              case AES_GREEN_AJAH :
                   return new Color(128,206,113);
              case AES_RED_AJAH :
                   return new Color(223,83,65);
              case AES_GRAY_AJAH :
                   return new Color(184,184,184);
              case AES_YELLOW_AJAH :
                   return new Color(209,203,99);
              case AES_NOVICE :
              case AES_ACCEPTED :
              case AES_WHITE_AJAH:
              case AES_AMYRLIN :
          }

        return Color.white;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns an image for this character.
   *
   *  @param playerLocation player current location
   *  @return image identifier of this character.
   */
     public ImageIdentifier getImage( WotlasLocation playerLocation ) {

         ImageIdentifier imID = super.getImage(playerLocation);

         if( imID==null ) {
              if(aesSedaiSprite!=null && filter!=null)
                 aesSedaiSprite.setDynamicImageFilter(filter);

           // We return the default Aes Sedai Sprite...
              return new ImageIdentifier( ImageLibRef.PLAYERS_CATEGORY ,
                                          ImageLibRef.AES_SEDAI_SET ,
                                          ImageLibRef.AES_BLUE_GOLDH_WALKING_ACTION );
         }

         if(aesSedaiSprite!=null)
            aesSedaiSprite.setDynamicImageFilter( null ); // no filter for player small image

         return imID;
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

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

  /** Black Ajah ?
   */
    private boolean blackAjah = false;

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
          updateColorFilter();
         return aesSedaiSprite;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Updates the color filter that is used for the AesSedai sprite.
   */
      private void updateColorFilter() {
         if(aesSedaiSprite==null)
      	     return;
      	
         filter = new ColorImageFilter();

         if(!blackAjah)
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
                      filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.white );
                      break;
            }
          else
              filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.darkgray ); // black dress

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
         String path[] = { "players-0", "shadows-3", "aes-sedai-walking-0" };
         aesSedaiShadowSprite = new ShadowSprite( aesSedaiSprite.getDataSupplier(),
                                                  new ImageIdentifier( path ),
                                                  ImageLibRef.SHADOW_PRIORITY, 4, 4 );
         //aesSedaiShadowSprite.useAntialiasing(true);
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

             aesSedaiAuraEffect.reset();
             return (Drawable) aesSedaiAuraEffect;
         }

      // symbol selection
         String symbolName = null;

          switch( aesSedaiStatus ) {
              case AES_NOVICE :
                      symbolName = "novice-9";
                      break;
              case AES_ACCEPTED :
                      symbolName = "accepted-8";
                      break;
              case AES_BROWN_AJAH :
                      symbolName = "brown-2";
                      break;
              case AES_WHITE_AJAH:
                      symbolName = "white-6";
                      break;
              case AES_BLUE_AJAH :
                      symbolName = "blue-4";
                      break;
              case AES_GREEN_AJAH :
                      symbolName = "green-5";
                      break;
              case AES_RED_AJAH :
                      symbolName = "red-3";
                      break;
              case AES_GRAY_AJAH :
                      symbolName = "gray-7";
                      break;
              case AES_YELLOW_AJAH :
                      symbolName = "yellow-1";
                      break;
              case AES_AMYRLIN :
                      symbolName = "amyrlin-0";
                      break;
          }

      // Aura Creation
         String path[] = { "players-0", "symbols-2", "aes-sedai-symbols-0", symbolName+".jpg" };
         ImageIdentifier auraImage = new ImageIdentifier( path );

         aesSedaiAuraEffect = new AuraEffect( aesSedaiSprite.getDataSupplier(), auraImage,
                                              ImageLibRef.AURA_PRIORITY, 5000 );
         aesSedaiAuraEffect.useAntialiasing(true);

         if(aesSedaiStatus==AES_NOVICE)
            aesSedaiAuraEffect.setAmplitudeLimit( 0.6f );

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

  /** To get the WotCharacter community name.
   * @return the name of the community.
   */
     public String getCommunityName() {
     	return "Aes Sedai";
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the rank of this WotCharacter in his/her community.
   * @return the rank of this wotcharacter in his/her community.
   */
     public String getCharacterRank() {
          switch( aesSedaiStatus ) {
              case AES_NOVICE :
                   return "Novice";
              case AES_ACCEPTED :
                   return "Accepted";
              case AES_BROWN_AJAH :
                   return "Brown Ajah";
              case AES_WHITE_AJAH:
                   return "White Ajah";
              case AES_BLUE_AJAH :
                   return "Blue Ajah";
              case AES_GREEN_AJAH :
                   return "Green Ajah";
              case AES_RED_AJAH :
                   return "Red Ajah";
              case AES_GRAY_AJAH :
                   return "Gray Ajah";
              case AES_YELLOW_AJAH :
                   return "Yellow Ajah";
              case AES_AMYRLIN :
                   return "Amyrlin";
          }

        return "Unknown";
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

           // We return the default Aes Sedai Image...
              String path[] = { "players-0", "aes-sedai-0", "aes-sedai-walking-0" };
              return new ImageIdentifier( path );
         }

         if(aesSedaiSprite!=null)
            aesSedaiSprite.setDynamicImageFilter( null ); // no filter for player small image

         return imID;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To toggle the Black Ajah status...
    * @return the new blackAjah state.
    */
    public boolean toggleBlackAjah() {
    	blackAjah = !blackAjah;
    	updateColorFilter();
    	return blackAjah;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Get Black ajah state : for persistence only. Use toggleBlackAjah().
    */
    public boolean getBlackAjah() {
    	return blackAjah;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Set Black ajah state : for persistence only. Use toggleBlackAjah().
    */
    public void setBlackAjah(boolean blackAjah) {
    	this.blackAjah = blackAjah;
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
     	ostream.writeBoolean( blackAjah );
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
     	blackAjah = istream.readBoolean();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

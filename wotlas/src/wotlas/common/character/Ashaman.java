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

import java.io.*;
import java.awt.Color;

import wotlas.common.*;
import wotlas.common.universe.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.filter.*;


/** A Ashaman character.
 *
 * @author Aldiss
 * @see wotlas.common.character.Male
 */

public class Ashaman extends Male {

 /*------------------------------------------------------------------------------------*/

  /** Ashaman rank
   */
    public final static String ashamanRank[][] = {
          //        Rank Name              Rank Symbol
                {   "Soldier",             "soldier-0",     },
                {   "Dedicated",           "dedicated-1",   },
                {   "Asha'man",            "ashaman-2",     },
                {   "M'Hael",              "mhael-3",       },
    };

  /** Ashaman rank
   */
    public final static Color ashamanColor[] = {
         //        Rank Color
                   Color.white,
                   new Color(184,184,184),
                   new Color(100,100,100),
                   new Color(10,10,10),
    };


 /*------------------------------------------------------------------------------------*/

  /** Ashaman status ( soldier, ... ). [PUBLIC INFO]
   */
    private String characterRank;

 /*------------------------------------------------------------------------------------*/

  /** Current Sprite.
   */
    transient private Sprite ashamanSprite;

  /** Current Shadow.
   */
    transient private ShadowSprite ashamanShadowSprite;

  /** Current Aura.
   */
    transient private AuraEffect ashamanAuraEffect;

  /** ColorImageFilter for InteriorMap Sprites.
   */
    transient private ColorImageFilter filter;

 /*------------------------------------------------------------------------------------*/

   /** Constructor
    */
    public Ashaman() {
    }

 /*------------------------------------------------------------------------------------*/

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

         if(ashamanSprite!=null)
             return (Drawable) ashamanSprite;

       // 1 - Sprite Creation + Filter
          ashamanSprite = new Sprite( (SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY );
          ashamanSprite.useAntialiasing(true);
          updateColorFilter();
         return ashamanSprite;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Updates the color filter that is used for the AesSedai sprite.
   */
      private void updateColorFilter() {
         if(ashamanSprite==null)
             return;

         filter = new ColorImageFilter();

      // 2 - Hair Color
         if( hairColor.equals("brown") ) {
                   filter.addColorChangeKey( ColorImageFilter.lightYellow, ColorImageFilter.brown );
         }
         else if( hairColor.equals("black") ) {
                   filter.addColorChangeKey( ColorImageFilter.lightYellow, ColorImageFilter.darkgray );
         }
         else if( hairColor.equals("gray") ) {
                   filter.addColorChangeKey( ColorImageFilter.lightYellow, ColorImageFilter.gray );
         }
         else if( hairColor.equals("white") ) {
                   filter.addColorChangeKey( ColorImageFilter.lightYellow, ColorImageFilter.lightgray );
         }
         else if( hairColor.equals("reddish") ) {
                   filter.addColorChangeKey( ColorImageFilter.lightYellow, ColorImageFilter.red );
         }
         else if( hairColor.equals("golden") ) {
                   filter.addColorChangeKey( ColorImageFilter.lightYellow, ColorImageFilter.yellow );
         }

       // 3 - Set Filter
         ashamanSprite.setDynamicImageFilter( filter );
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

         if(ashamanShadowSprite!=null)
             return (Drawable) ashamanShadowSprite;

      // Shadow Creation
         String path = null;

         path = "players-0/shadows-3/guard-walking-2"; // same shadow as the tower guard

         ashamanShadowSprite = new ShadowSprite( ashamanSprite.getDataSupplier(),
                                                  new ImageIdentifier( path ),
                                                  ImageLibRef.SHADOW_PRIORITY, 4, 4 );
         return ashamanShadowSprite;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's aura.
   *  @return character's Aura Drawable.
   */
     public Drawable getAura(){
         if( ImageLibrary.getDefaultImageLibrary() == null )
             return null;

         if(ashamanAuraEffect!=null) {
             if(ashamanAuraEffect.isLive()) {
                return null; // aura still displayed on screen
             }

             ashamanAuraEffect.reset();
             return (Drawable) ashamanAuraEffect;
         }

      // Aura Creation
         ashamanAuraEffect = new AuraEffect( ashamanSprite.getDataSupplier(), getAuraImage(),
                                            ImageLibRef.AURA_PRIORITY, 5000 );
         ashamanAuraEffect.useAntialiasing(true);
         ashamanAuraEffect.setAuraMaxAlpha(0.65f);

         if(characterRank.equals("Asha'man"))
            ashamanAuraEffect.setAmplitudeLimit( 1.6f );
         else if( characterRank.equals("M'Hael") )
            ashamanAuraEffect.setAmplitudeLimit( 1.4f );

         return ashamanAuraEffect;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Aura Image Identifier.
    */
    private ImageIdentifier getAuraImage() {
      // symbol selection
         String symbolName = null;

            for( int i=0; i<ashamanRank.length; i++ )
              if( characterRank.equals(ashamanRank[i][0]) ) {
                  symbolName = ashamanRank[i][1];
                  break;
              }

         if(symbolName==null) symbolName=ashamanRank[0][0]; // default if not found

      // Aura Creation
         return new ImageIdentifier( "players-0/symbols-2/ashaman-symbols-4/"+symbolName+".gif" );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's color.
   *  @return character's color
   */
     public Color getColor(){
         for( int i=0; i<ashamanRank.length; i++ )
              if( characterRank.equals(ashamanRank[i][0]) )
                  return ashamanColor[i];

        return Color.black;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the WotCharacter community name.
   * @return the name of the community.
   */
     public String getCommunityName() {
        return "Asha'man";
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the rank of this WotCharacter in his/her community.
   * @return the rank of this wotcharacter in his/her community.
   */
     public String getCharacterRank() {
        return characterRank;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the rank of this WotCharacter in his/her community.
   *  IMPORTANT : if the rank doesnot exist it is  set to "unknown".
   *
   * @param rank the rank of this wotcharacter in his/her community.
   */
     public void setCharacterRank( String rank ) {

         if(rank!=null)
            for( int i=0; i<ashamanRank.length; i++ )
              if( rank.equals(ashamanRank[i][0]) ) {
                  characterRank = rank;
                  return; // success
              }

         characterRank="unknown"; // not found
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
              if(ashamanSprite!=null && filter!=null)
                 ashamanSprite.setDynamicImageFilter(filter);

           // We return the default Ashaman Image...
              String path = null;

              path = "players-0/ashaman-8/ashaman-walking-0";

              return new ImageIdentifier( path );
         }

         if(ashamanSprite!=null)
            ashamanSprite.setDynamicImageFilter( null ); // no filter for player small image

         return imID;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the fanfare sound of this character class.
   *  @return fanfare sound file name
   */
     public String getFanfareSound() {
        if(characterRank.equals("M'Hael"))
           return "fanfare-special.wav";

        return "fanfare-asha.wav";
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
        ostream.writeUTF( characterRank );
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
        characterRank = istream.readUTF();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

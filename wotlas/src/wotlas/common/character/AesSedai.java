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

  /** Ajah & Aes Sedai rank
   */
    public final static String aesSedaiRank[][] = {
          //        Rank Name                Rank Symbol
                {   "Stilled",               "stilled-11",  },
                {   "Novice",                "novice-9",    },
                {   "Accepted",              "accepted-8",  },
                {   "Blue Ajah",             "blue-4",      },
                {   "Green Ajah",            "green-5",     },
                {   "Yellow Ajah",           "yellow-1",    },
                {   "Red Ajah",              "red-3",       },
                {   "Brown Ajah",            "brown-2",     },
                {   "White Ajah",            "white-6",     },
                {   "Gray Ajah",             "gray-7",      },
                {   "Keeper of the Chronicles",  "keeper-10",   },
                {   "Amyrlin",               "amyrlin-0",   },
                {   "Black Ajah",            "black-12",    },
    };

  /** Ajah & Aes Sedai rank
   */
    public final static Color aesSedaiColor[] = {
         //        Rank Color
                   new Color(160,115,130),
                   Color.white,
                   Color.white,
                   new Color(119,152,213),
                   new Color(128,206,113),
                   new Color(209,203,99),
                   new Color(223,83,65),
                   new Color(180,158,80),
                   Color.white,
                   new Color(184,184,184),
                   new Color(230,220,240),
                   new Color(243,228,175),
                   Color.black,
    };

 /*------------------------------------------------------------------------------------*/

  /** Aes Sedai status ( ajah, novice, accepted, amyrlin ). [PUBLIC INFO]
   */
    private String characterRank;

  /** Do we have to wear a black Ajah dress ? [PUBLIC INFO]
   */
    private boolean blackAjah = false;

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

   /** Constructor
    */
    public AesSedai() {
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

         if(!blackAjah) {
              if(characterRank.equals("Brown Ajah")) {
                   filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.brown );
              }
              else if(characterRank.equals("Blue Ajah")) {
                   // no filter needed
              }
              else if(characterRank.equals("Green Ajah")) {
                   filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.green );
              }
              else if(characterRank.equals("Red Ajah")) {
                   filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.red );
              }
              else if(characterRank.equals("Gray Ajah")) {
                   filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.gray );
              }
              else if(characterRank.equals("Yellow Ajah")) {
                   filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.yellow );
              }
              else
                   filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.white );
          }
          else
              filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.darkgray ); // black dress

      // 2 - Hair Color
         if( hairColor.equals("brown") ) {
                   filter.addColorChangeKey( ColorImageFilter.yellow, ColorImageFilter.brown );
         }
         else if( hairColor.equals("black") ) {
                   filter.addColorChangeKey( ColorImageFilter.yellow, ColorImageFilter.darkgray );
         }
         else if( hairColor.equals("gray") ) {
                   filter.addColorChangeKey( ColorImageFilter.yellow, ColorImageFilter.gray );
         }
         else if( hairColor.equals("white") ) {
                   filter.addColorChangeKey( ColorImageFilter.yellow, ColorImageFilter.lightgray );
         }
         else if( hairColor.equals("reddish") ) {
                   filter.addColorChangeKey( ColorImageFilter.yellow, ColorImageFilter.red );
         }

       // 3 - Set Filter
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

      // Aura Creation
         aesSedaiAuraEffect = new AuraEffect( aesSedaiSprite.getDataSupplier(), getAuraImage(),
                                              ImageLibRef.AURA_PRIORITY, 5000 );
         aesSedaiAuraEffect.useAntialiasing(true);

         if(characterRank.equals("Novice"))
            aesSedaiAuraEffect.setAmplitudeLimit( 0.6f );
         else if(characterRank.equals("Stilled"))
            aesSedaiAuraEffect.setAmplitudeLimit( 3.1f );
         else if(characterRank.equals("Keeper of the Chronicles")) {
            aesSedaiAuraEffect.setAuraMaxAlpha(0.75f);
            aesSedaiAuraEffect.setAmplitudeLimit(0.0f);
         }

         return aesSedaiAuraEffect;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Aura Image Identifier.
    */
    private ImageIdentifier getAuraImage() {
      // symbol selection
         String symbolName = null;

         if(!blackAjah) {
            for( int i=0; i<aesSedaiRank.length; i++ )
              if( characterRank.equals(aesSedaiRank[i][0]) ) {
                  symbolName = aesSedaiRank[i][1];
                  break;
              }
         } else
            symbolName="blackajah-12";

         if(symbolName==null) symbolName=aesSedaiRank[0][1]; // default if not found

      // Aura Creation
         String path[] = { "players-0", "symbols-2", "aes-sedai-symbols-0", symbolName+".gif" };
         return new ImageIdentifier( path );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's color.
   *  @return character's color
   */
     public Color getColor(){
       if(!blackAjah)
         for( int i=0; i<aesSedaiRank.length; i++ )
              if( characterRank.equals(aesSedaiRank[i][0]) )
                  return aesSedaiColor[i];

        return Color.black;
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
            for( int i=0; i<aesSedaiRank.length; i++ )
              if( rank.equals(aesSedaiRank[i][0]) ) {
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

        if(aesSedaiAuraEffect!=null)
           aesSedaiAuraEffect.setImage(getAuraImage());

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
        characterRank = istream.readUTF();
        blackAjah = istream.readBoolean();
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

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


/** A Warder character.
 *
 * @author Aldiss
 * @see wotlas.common.character.Male
 */

public class Warder extends Male {

 /*------------------------------------------------------------------------------------*/

  /** Warder rank
   */
    public final static String warderRank[][] = {
          //        Rank Name                Rank Symbol
                {   "Youngling",             "youngling-0",  },
                {   "Tower Guard",           "guard-1",      },
                {   "Warder",                "warder-2",     },
                {   "Blade Master",          "blade-3",      },
    };

  /** Warder rank
   */
    public final static Color warderColor[] = {
         //        Rank Color
                   Color.white,
                   new Color(184,184,184),
                   new Color(128,206,113),
                   new Color(209,203,99),
    };

 /*------------------------------------------------------------------------------------*/

  /** Warder status ( youngling, guard, ... ). [PUBLIC INFO]
   */
    private String characterRank;

 /*------------------------------------------------------------------------------------*/

  /** Current Sprite.
   */
    transient private Sprite warderSprite;

  /** Current Shadow.
   */
    transient private ShadowSprite warderShadowSprite;

  /** Current Aura.
   */
    transient private AuraEffect warderAuraEffect;

  /** ColorImageFilter for InteriorMap Sprites.
   */
    transient private ColorImageFilter filter;

 /*------------------------------------------------------------------------------------*/

   /** Constructor
    */
    public Warder() {
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

         if(warderSprite!=null)
             return (Drawable) warderSprite;

       // 1 - Sprite Creation + Filter
          warderSprite = new Sprite( (SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY );
          warderSprite.useAntialiasing(true);
          updateColorFilter();
         return warderSprite;
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Updates the color filter that is used for the AesSedai sprite.
   */
      private void updateColorFilter() {
         if(warderSprite==null)
             return;

         filter = new ColorImageFilter();

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
         warderSprite.setDynamicImageFilter( filter );
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

         if(warderShadowSprite!=null)
             return (Drawable) warderShadowSprite;

      // Shadow Creation
         String path = null;

              if(characterRank.equals("Youngling")) {
                 path = "players-0/shadows-3/youngling-walking-0";
              }
              else if(characterRank.equals("Tower Guard")) {
                 path = "players-0/shadows-3/guard-walking-1";
              }
              else {
                 path = "players-0/shadows-3/warder-walking-2";
              }

         warderShadowSprite = new ShadowSprite( warderSprite.getDataSupplier(),
                                                  new ImageIdentifier( path ),
                                                  ImageLibRef.SHADOW_PRIORITY, 4, 4 );
         return warderShadowSprite;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's aura.
   *  @return character's Aura Drawable.
   */
     public Drawable getAura(){
         if( ImageLibrary.getDefaultImageLibrary() == null )
             return null;

         if(warderAuraEffect!=null) {
             if(warderAuraEffect.isLive()) {
                return null; // aura still displayed on screen
             }

             warderAuraEffect.reset();
             return (Drawable) warderAuraEffect;
         }

      // Aura Creation
         warderAuraEffect = new AuraEffect( warderSprite.getDataSupplier(), getAuraImage(),
                                            ImageLibRef.AURA_PRIORITY, 5000 );
         warderAuraEffect.useAntialiasing(true);

         if(characterRank.equals("Tower Guard"))
            warderAuraEffect.setAmplitudeLimit( 0.6f );

         return warderAuraEffect;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Aura Image Identifier.
    */
    private ImageIdentifier getAuraImage() {
      // symbol selection
         String symbolName = null;

            for( int i=0; i<warderRank.length; i++ )
              if( characterRank.equals(warderRank[i][0]) ) {
                  symbolName = warderRank[i][1];
                  break;
              }

         if(symbolName==null) symbolName=warderRank[0][1]; // default if not found

      // Aura Creation
         return new ImageIdentifier( "players-0/symbols-2/warder-symbols-0/"+symbolName+".gif" );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's color.
   *  @return character's color
   */
     public Color getColor(){
         for( int i=0; i<warderRank.length; i++ )
              if( characterRank.equals(warderRank[i][0]) )
                  return warderColor[i];

        return Color.black;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the WotCharacter community name.
   * @return the name of the community.
   */
     public String getCommunityName() {
        if( characterRank.equals("Tower Guard") )
            return "Tar Valon Army";
        return "Warder";
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
            for( int i=0; i<warderRank.length; i++ )
              if( rank.equals(warderRank[i][0]) ) {
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
              if(warderSprite!=null && filter!=null)
                 warderSprite.setDynamicImageFilter(filter);

           // We return the default Warder Image...
              String path = null;

              if(characterRank.equals("Youngling")) {
                 path = "players-0/warder-4/youngling-walking-0";
              }
              else if(characterRank.equals("Tower Guard")) {
                 path = "players-0/warder-4/guard-walking-1";
              }
              else {
                 path = "players-0/warder-4/warder-walking-2";
              }

              return new ImageIdentifier( path );
         }

         if(warderSprite!=null)
            warderSprite.setDynamicImageFilter( null ); // no filter for player small image

         return imID;
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

/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2003 WOTLAS Team
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
import wotlas.common.objects.inventories.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.graphics2D.filter.*;

import wotlas.common.environment.*;

/** A Children Of The Light character.
 *
 * @author Aldiss, Elann, Diego
 * @see wotlas.common.character.Male
 */

public class ChildrenOfTheLight extends Male {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

 /*------------------------------------------------------------------------------------*/

  /** White Cloak rank
   */
    public final static String childrenRank[][] = {
          //        Rank Name                Rank Symbol
                {   "Soldier of the Light",  "soldier-0",  },
    };

  /** White Cloak rank
   */
    public final static Color childrenColor[] = {
         //        Rank Color
                   Color.white,
    };

 /*------------------------------------------------------------------------------------*/

  /** Children status ( soldier, ... ). [PUBLIC INFO]
   */
    private String characterRank;

 /*------------------------------------------------------------------------------------*/

  /** Current Sprite.
   */
    transient private Sprite childrenSprite;

  /** Current Shadow.
   */
    transient private ShadowSprite childrenShadowSprite;

  /** Current Aura.
   */
    transient private AuraEffect childrenAuraEffect;

  /** ColorImageFilter for InteriorMap Sprites.
   */
    transient private ColorImageFilter filter;

 /*------------------------------------------------------------------------------------*/

   /** Constructor
    */
    public ChildrenOfTheLight() {
        InitCharData();
        InitWotData();

        classes[0] = CLASSES_WOT_CHILDREN_OF_THE_LIGHT;
        
        this.charAttributes[this.ATTR_STR][this.IDX_ACTUAL] = 10;
        this.charAttributes[this.ATTR_STR][this.IDX_MAX]    = 10;
        this.charAttributes[this.ATTR_INT][this.IDX_ACTUAL] = 10;
        this.charAttributes[this.ATTR_INT][this.IDX_MAX]    = 10;
        this.charAttributes[this.ATTR_WIS][this.IDX_ACTUAL] = 10;
        this.charAttributes[this.ATTR_WIS][this.IDX_MAX]    = 10;
        this.charAttributes[this.ATTR_CON][this.IDX_ACTUAL] = 10;
        this.charAttributes[this.ATTR_CON][this.IDX_MAX]    = 10;
        this.charAttributes[this.ATTR_DEX][this.IDX_ACTUAL] = 10;
        this.charAttributes[this.ATTR_DEX][this.IDX_MAX]    = 10;
        this.charAttributes[this.ATTR_CHA][this.IDX_ACTUAL] = 10;
        this.charAttributes[this.ATTR_CHA][this.IDX_MAX]    = 10;
    }

 /*------------------------------------------------------------------------------------*/

   /** To get a Drawable for this character. This should not be used on the
    *  server side.
    *
    *  The returned Drawable is unique : we always return the same drawable per
    *  AesSedai instance.
    *
    * @param player the player to chain the drawable to. If a XXXDataSupplier is needed
    *               we sets it to this player object.
    * @return a Drawable for this character.
    */
    public Drawable getDrawable( Player player ) {
        if( !player.getLocation().isTileMap() ){
             if(childrenSprite!=null)
                 return (Drawable) childrenSprite;

           // 1 - Sprite Creation + Filter
              childrenSprite = new Sprite( (SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY );
              childrenSprite.useAntialiasing(true);
              updateColorFilter();
             return childrenSprite;
        }
        else {
            if(fakeSprite!=null)
                return (Drawable) fakeSprite;
            int imageNr = 0;
            switch( EnvironmentManager.whtGraphicSetIs() ){
                case EnvironmentManager.GRAPHICS_SET_ROGUE:
                    imageNr = 0;
                    break;
                default:
                    imageNr = EnvironmentManager.getDefaultNpcImageNr();
            }

            fakeSprite = new FakeSprite( (FakeSpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY
            , EnvironmentManager.getServerEnvironment().getGraphics(EnvironmentManager.SET_OF_NPC
            )[ EnvironmentManager.getDefaultPlayerImage() ], imageNr  );
            return fakeSprite;
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Updates the color filter that is used for the AesSedai sprite.
   */
      private void updateColorFilter() {
         if(childrenSprite==null)
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
         childrenSprite.setDynamicImageFilter( filter );
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's shadow. Important: a character Drawable MUST have been created
   *  previously ( via a getDrawable call ). You don't want to create a shadow with no
   *  character, do you ?
   *
   *  @return character's Shadow Drawable.
   */
     public Drawable getShadow(){

         if(childrenShadowSprite!=null)
             return (Drawable) childrenShadowSprite;

      // Shadow Creation
         String path = null;

         path = "players-0/shadows-3/children-walking-5";

         childrenShadowSprite = new ShadowSprite( childrenSprite.getDataSupplier(),
                                                  new ImageIdentifier( path ),
                                                  ImageLibRef.SHADOW_PRIORITY, 4, 4 );
         return childrenShadowSprite;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's aura.
   *  @return character's Aura Drawable.
   */
     public Drawable getAura(){

         if(childrenAuraEffect!=null) {
             if(childrenAuraEffect.isLive()) {
                return null; // aura still displayed on screen
             }

             childrenAuraEffect.reset();
             return (Drawable) childrenAuraEffect;
         }

      // Aura Creation
         childrenAuraEffect = new AuraEffect( childrenSprite.getDataSupplier(), getAuraImage(),
                                            ImageLibRef.AURA_PRIORITY, 5000 );
         childrenAuraEffect.useAntialiasing(true);
         childrenAuraEffect.setAuraMaxAlpha(0.7f);
         childrenAuraEffect.setAmplitudeLimit( 0.0f );
         return childrenAuraEffect;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the Aura Image Identifier.
    */
    private ImageIdentifier getAuraImage() {
      // symbol selection
         String symbolName = null;

            for( int i=0; i<childrenRank.length; i++ )
              if( characterRank.equals(childrenRank[i][0]) ) {
                  symbolName = childrenRank[i][1];
                  break;
              }

         if(symbolName==null) symbolName=childrenRank[0][1]; // default if not found

      // Aura Creation
         return new ImageIdentifier( "players-0/symbols-2/children-symbols-2/"+symbolName+".gif" );
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's color.
   *  @return character's color
   */
     public Color getColor(){
         for( int i=0; i<childrenRank.length; i++ )
              if( characterRank.equals(childrenRank[i][0]) )
                  return childrenColor[i];

        return Color.black;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the WotCharacter community name.
   * @return the name of the community.
   */
     public String getCommunityName() {
        return "Children of the Light";
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
            for( int i=0; i<childrenRank.length; i++ )
              if( rank.equals(childrenRank[i][0]) ) {
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
              if(childrenSprite!=null && filter!=null)
                 childrenSprite.setDynamicImageFilter(filter);

           // We return the default White Cloak Image...
              String path = null;

                 path = "players-0/children-6/children-walking-0";

              return new ImageIdentifier( path );
         }

         if(childrenSprite!=null)
            childrenSprite.setDynamicImageFilter( null ); // no filter for player small image

         return imID;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the fanfare sound of this character class.
   *  @return fanfare sound file name
   */
     public String getFanfareSound() {
        return "fanfare-child.wav";
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get a new Inventory for this WotCharacter.<br>
   * In this case, it is a ChildrenOfTheLightInventory.
   * @return a new inventory for this char
   */
     public Inventory createInventory()
	 {
	  return new ChildrenOfTheLightInventory();
	 }	 
	 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data with serialize.
   */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        super.writeExternal( objectOutput );
        objectOutput.writeUTF( characterRank );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal( objectInput );
            characterRank = objectInput.readUTF();
        } else {
            // to do.... when new version
        }
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** id version of data, used in serialized persistance.
   */
    public int ExternalizeGetVersion(){
        return 1;
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data to a stream to send data.
   */
    public void writeObject(java.io.ObjectOutputStream objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        super.writeExternal( objectOutput );
        objectOutput.writeUTF( characterRank );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data from a stream to recive data.
   */
    public void readObject(java.io.ObjectInputStream objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal( objectInput );
            characterRank = objectInput.readUTF();
        } else {
            // to do.... when new version
        }
    }

   /** used to store the drawable for tilemaps after creating it.
    */
    transient private FakeSprite fakeSprite;
    
}

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

/** A Warder character.
 *
 * @author Aldiss, Elann, Diego
 * @see wotlas.common.character.Male
 */

public class Warder extends Male {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

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
                   new Color(160,160,180),
                   new Color(140,140,160),
    };

  /** Warder Cloak Color
   */
    public final static String warderCloakColor[] = {
         //        Cloak Color
                   "gray",
                   "blue",
                   "yellow",
                   "red",
                   "green",
                   "brown",
                   "black",
    };


 /*------------------------------------------------------------------------------------*/

  /** Warder status ( youngling, guard, ... ). [PUBLIC INFO]
   */
    private String characterRank;

  /** Warder cloak color ( for warders only ). [PUBLIC INFO]
   *  possible values are listed in the static array WarderCloakColor.
   */
    private String cloakColor;

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
        InitCharData();
        InitWotData();

        this.setCharClass( CharData.CLASSES_WOT_WARDER );
        
        this.setCharAttr(CharData.ATTR_STR,10);
        this.setCharAttr(CharData.ATTR_INT,10);
        this.setCharAttr(CharData.ATTR_WIS,10);
        this.setCharAttr(CharData.ATTR_CON,10);
        this.setCharAttr(CharData.ATTR_DEX,10);
        this.setCharAttr(CharData.ATTR_CHA,10);
        
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
             if(warderSprite!=null)
                 return (Drawable) warderSprite;

           // 1 - Sprite Creation + Filter
              warderSprite = new Sprite( (SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY );
              warderSprite.useAntialiasing(true);
              updateColorFilter();
             return warderSprite;
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
         if(warderSprite==null)
             return;

         filter = new ColorImageFilter();


      // 1 - Cloak Color
         if( cloakColor!=null) {
             if( cloakColor.equals("brown") ) {
                   filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.brown );
             }
             else if( cloakColor.equals("gray") ) {
                   filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.gray );
             }
             else if( cloakColor.equals("green") ) {
                   filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.green );
             }
             else if( cloakColor.equals("yellow") ) {
                   filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.yellow );
             }
             else if( cloakColor.equals("red") ) {
                   filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.red );
             }
             else if( cloakColor.equals("black") ) {
                   filter.addColorChangeKey( ColorImageFilter.blue, ColorImageFilter.darkgray );
             }
         }

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

         if(warderShadowSprite!=null)
             return (Drawable) warderShadowSprite;

      // Shadow Creation
         String path = null;

              if(characterRank.equals("Youngling")) {
                 path = "players-0/shadows-3/youngling-walking-1";
              }
              else if(characterRank.equals("Tower Guard")) {
                 path = "players-0/shadows-3/guard-walking-2";
              }
              else {
                 path = "players-0/shadows-3/warder-walking-3";
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
         warderAuraEffect.setAuraMaxAlpha(0.75f);

         if(characterRank.equals("Tower Guard"))
            warderAuraEffect.setAmplitudeLimit( 2.6f );
         else if( characterRank.equals("Youngling") )
            warderAuraEffect.setAmplitudeLimit( 3.1f );

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
         return new ImageIdentifier( "players-0/symbols-2/warder-symbols-1/"+symbolName+".gif" );
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

  /** To get the cloak color of the warder. If there is no cloak we return null.
   * @return color name.
   */
     public String getCloakColor() {
        return cloakColor;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the cloak color of the warder. If the warder is a Tower Guard or a
   *  youngling it won't be set.
   *
   * @param cloakColor cloak color name
   */
     public void setCloakColor( String cloakColor ) {

         if( characterRank==null || (!characterRank.equals(warderRank[2][0]) &&
             !characterRank.equals(warderRank[3][0])) )
            return;

            for( int i=0; i<warderCloakColor.length; i++ )
              if( cloakColor.equals(warderCloakColor[i]) ) {
                  this.cloakColor = cloakColor;
                  return; // success
              }

         // not found
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

  /** Returns the fanfare sound of this character class.
   *  @return fanfare sound file name
   */
     public String getFanfareSound() {
        if(characterRank.equals("Tower Guard"))
           return "fanfare-tower.wav";

        return "fanfare.wav";
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get a new Inventory for this WotCharacter.<br>
   * In this case, it is a WarderInventory.
   * @return a new inventory for this char
   */
     public Inventory createInventory()
	 {
	  return new WarderInventory();
	 }	 
	 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data with serialize.
   */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        super.writeExternal( objectOutput );
        objectOutput.writeUTF( characterRank );
        objectOutput.writeBoolean(cloakColor!=null);
        if(cloakColor!=null)
           objectOutput.writeUTF( cloakColor );
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
            if(objectInput.readBoolean())
                cloakColor = objectInput.readUTF();
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

  /** write object data with serialize.
   */
    public void writeObject(java.io.ObjectOutputStream objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        super.writeExternal( objectOutput );
        objectOutput.writeUTF( characterRank );
        objectOutput.writeBoolean(cloakColor!=null);
        if(cloakColor!=null)
           objectOutput.writeUTF( cloakColor );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readObject(java.io.ObjectInputStream objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal( objectInput );
            characterRank = objectInput.readUTF();
            if(objectInput.readBoolean())
                cloakColor = objectInput.readUTF();
        } else {
            // to do.... when new version
        }
    }

   /** used to store the drawable for tilemaps after creating it.
    */
    transient private FakeSprite fakeSprite;
}

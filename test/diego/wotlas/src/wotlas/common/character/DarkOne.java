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

import wotlas.common.*;
import wotlas.common.universe.*;
import wotlas.common.objects.inventories.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;

import wotlas.common.environment.*;

import java.io.*;
import java.awt.Color;

/** The Dark One.
 *
 * @author Aldiss, Elann, Diego
 * @see wotlas.common.Player
 * @see wotlas.libs.graphics2D.Drawable
 */

public class DarkOne extends WotCharacter {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Current Sprite.
   */
    transient private Sprite doSprite;

  /** Current Shadow.
   */
    transient private ShadowSprite doShadowSprite;

    /** Constructor
    */
    public DarkOne() {
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get a Drawable for this character. This should not be used on the
    *  server side.
    *
    * @param player the player to chain the drawable to. If a XXXDataSupplier is needed
    *               we sets it to this player object.
    * @return a Drawable for this character
    */
    public Drawable getDrawable( Player player ) {
        if( !player.getLocation().isTileMap() ){
             if(doSprite!=null)
                 return (Drawable) doSprite;

              doSprite = new Sprite( (SpriteDataSupplier) player, ImageLibRef.PLAYER_PRIORITY );
              doSprite.useAntialiasing(true);
             return doSprite;
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

  /** Returns an image for this character, depending on the Map type.
   *
   *  @param playerLocation player current location
   *  @return image identifier of this character.
   */
     public ImageIdentifier getImage( WotlasLocation playerLocation ) {

           // We return the default DO Image...
              String path = "players-0/dark-one-5";
              return new ImageIdentifier( path );
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's shadow. Important: a character Drawable MUST have been created
   *  previously ( via a getDrawable call ). You don't want to create a shadow with no
   *  character, do you ?
   *  @return character's Shadow Drawable.
   */
     public Drawable getShadow(){

         if(doShadowSprite!=null)
             return (Drawable) doShadowSprite;

      // Shadow Creation
         String path = "players-0/shadows-3/dark-one-4";

         doShadowSprite = new ShadowSprite( doSprite.getDataSupplier(),
                                                  new ImageIdentifier( path ),
                                                  ImageLibRef.SHADOW_PRIORITY, 0, 0 );
         return doShadowSprite;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's aura.
   *  @return character's Aura Drawable.
   */     
     public Drawable getAura(){
     	return null;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Return the character's representative color.
   *  @return character's color.
   */     
     public Color getColor() {
        return Color.black;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the WotCharacter community name.
   * @return the name of the community.
   */
     public String getCommunityName() {
       return "Darkness";
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the rank of this WotCharacter in his/her community.
   * @return the rank of this wotcharacter in his/her community.
   */
     public String getCharacterRank() {
       return "Great Lord";
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To set the rank of this WotCharacter in his/her community.
   *  IMPORTANT : if the rank doesnot exist it is set to "unknown".
   *
   * @param rank the rank of this wotcharacter in his/her community.
   */
     public void setCharacterRank( String rank ) {
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the speed of this character.
   *
   *  @param playerLocation player current location
   *  @return speed in pixel/s
   */
     public float getSpeed( WotlasLocation playerLocation ) {
            return 30.0f;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the fanfare sound of this character class.
   *  @return fanfare sound file name
   */
     public String getFanfareSound() {
        return "fanfare-dark.wav";
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get a new Inventory for this WotCharacter.<br>
   * In this case, it is null. The Dark One has no Inventory. 
   * @return null
   */
     public Inventory createInventory()
	 {
	  return null;
	 }
	 
	 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data with serialize.
   */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        super.writeExternal( objectOutput );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal( objectInput );
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
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data from a stream to recive data.
   */
    public void readObject(java.io.ObjectInputStream objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal( objectInput );
        } else {
            // to do.... when new version
        }
    }

   /** used to store the drawable for tilemaps after creating it.
    */
    transient private FakeSprite fakeSprite;
    
    /** used to init vars
     */
    public void init() {
        InitCharData();
        InitWotData();

        setCharClass( CLASSES_WOT_DARK_ONE );
        
        this.setCharAttr(CharData.ATTR_STR,10);
        this.setCharAttr(CharData.ATTR_INT,10);
        this.setCharAttr(CharData.ATTR_WIS,10);
        this.setCharAttr(CharData.ATTR_CON,10);
        this.setCharAttr(CharData.ATTR_DEX,10);
        this.setCharAttr(CharData.ATTR_CHA,10);
    }
}

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
import wotlas.libs.graphics2D.*;

import java.io.*;

/** A Human Wotlas Character.
 *
 * @author Aldiss, Diego
 * @see wotlas.common.character.WotCharacter
 */

public abstract class Human extends WotCharacter {

 /*------------------------------------------------------------------------------------*/

  /** Hair color
   */
    public final static String hairColors[] = {
    	           "bald",
    	           "golden",
    	           "brown",
    	           "black",
    	           "gray",
    	           "white",
    	           "reddish",
    };

 /*------------------------------------------------------------------------------------*/

  /** Hair color [PUBLIC INFO]
   */
    protected String hairColor;

  /** Speed [RECONSTRUCTED INFO - NOT REPLICATED]
   */
    transient protected float speed;

 /*------------------------------------------------------------------------------------*/

  /** To get the hair color of the human player.
   */
    public String getHairColor() {
       return hairColor; 
    }

  /** To set the hair color of the human player. If the hair color given
   *  doesn't exist in our list we set it as "unknown".
   */
    public void setHairColor( String hairColor ) {
       if(hairColor!=null)
          for( int i=0; i<hairColors.length; i++ )
               if( hairColor.equals(hairColors[i]) ) {
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
     public ImageIdentifier getImage( WotlasLocation playerLocation ) {

         // Default image for towns & worlds
            if( playerLocation.isTown() || playerLocation.isWorld() )
                return new ImageIdentifier( "players-0/players-small-images-1/player-small-0" );

            return null; // null otherwise, we let sub-classes redefine the rest...
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Returns the speed of this character.
   *
   *  @param playerLocation player current location
   *  @return speed in pixel/s
   */
     public float getSpeed( WotlasLocation playerLocation ) {
         if ( playerLocation.isRoom() )
              return 60.0f;  // Default human speed ( 60pixel/s = 2m/s )
         else if ( playerLocation.isTown() )
              return 10.0f;
         else
              return 5.0f;
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data with serialize.
   */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        super.writeExternal( objectOutput );
        objectOutput.writeUTF( hairColor );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal( objectInput );
            hairColor = objectInput.readUTF();
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
        objectOutput.writeUTF( hairColor );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data from a stream to recive data.
   */
    public void readObject(java.io.ObjectInputStream objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal( objectInput );
            hairColor = objectInput.readUTF();
        } else {
            // to do.... when new version
        }
    }
}

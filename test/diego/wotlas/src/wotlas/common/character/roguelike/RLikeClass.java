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

package wotlas.common.character.roguelike;

import wotlas.common.*;
import wotlas.common.character.*;
import wotlas.libs.persistence.*;

import wotlas.utils.*;

import java.io.*;
import java.awt.Color;
import java.util.*;

/** Super class of Rogue Like Class character, it's added into a RLikeCharacter
 *
 * @author Diego
 */
public abstract class RLikeClass implements BackupReady {
    
    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;
    
    transient protected RLikeCharacter myChar;
    
    public void init(RLikeCharacter myChar) {
        this.myChar = myChar;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data with serialize.
   */
    
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
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

    public void writeObject(java.io.ObjectOutputStream objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
    }
    
    public void readObject(java.io.ObjectInputStream objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
        } else {
            // to do.... when new version
        }
    }
    
    public void reConnect(RLikeCharacter myChar) {
        this.myChar = myChar;
    }
    
    static protected short[] rollStat() {
        initRoll();

        short[] stats = new short [6];
        short[] prevStats = new short [6];
        int prevTotal = 0,total = 0;
        short stat;
        for(int a=0; a < 6 ; a++){
            stat = roll(3,6);
            prevStats[a] = stat;
            prevTotal += stat;
        }
        for(int b=0; b < 4 ; b++){
            for(int i=0; i < 6 ; i++){
                stat = roll(3,6);
                stats[i] = stat;
                total += stat;
            }
            if(prevTotal < total)
                prevStats = stats;
        }

        // order 'em
        stats =  (short[]) prevStats.clone();
        boolean choosed;
        for(int a=0; a < 6 ; a++){
            choosed = false;
            prevStats[a] = stats[0];
            for(int b=1; b < 6 ; b++){
                if( prevStats[a] < stats[b] ){
                    prevStats[a] = stats[b];
                    stats[b] = -1;
                    choosed = true;
                }
            }
            if(!choosed)
                stats[0] = -1;
        }
        return prevStats;
    }
    
    static private final short roll(int dices, int diceSize) {
        short value = 0;
        for(int i=0; i < dices ; i++){
            value += new Double( 1+(Dice.nextDouble()*(diceSize) ) ).shortValue() ;;
        }
        return value;
    }

    static private Random Dice;
    static private boolean needInit = true;
    
    static private void initRoll(){
        if(!needInit)
            return;
        Dice = new Random( System.currentTimeMillis() ) ;
        needInit = false;        
    }
}
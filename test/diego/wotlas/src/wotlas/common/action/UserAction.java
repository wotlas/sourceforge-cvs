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
package wotlas.common.action;


import java.io.*;
import wotlas.libs.persistence.*;
import wotlas.utils.*;
import wotlas.common.screenobject.*;

/** basic class for actions
 * in the FAR future new cast action can be CREATED by players
 * with MANY RULES and CHECKs, then STORED to in the SERVER 
 * under ther PLAYER data.
 *
 *
 * @author  Diego
 */
public abstract class UserAction implements SendObjectReady {
        
//    static public final byte TARGET_NO_TARGET   = 0;
    static public final byte TARGET_TYPE_SELF       = 0;
    static public final byte TARGET_TYPE_NPC        = 1;
    static public final byte TARGET_TYPE_PLAYER     = 2;
    static public final byte TARGET_TYPE_ITEM       = 3;
    static public final byte TARGET_TYPE_GROUND     = 4;
    static public final byte TARGET_TYPE_INVENTORY  = 5;

    static public final byte TARGET_RANGE_NONE      = 1;
    static public final byte TARGET_RANGE_TOUCH     = 2;
    static public final byte TARGET_RANGE_SHORT     = 3;
    static public final byte TARGET_RANGE_MEDIUM    = 4;
    static public final byte TARGET_RANGE_LONG      = 5;
//    static public final byte TARGET_RANGE_SEE_AND_SHORT  = 6;
//    static public final byte TARGET_RANGE_SEE_AND_MEDIUM = 7;
//    static public final byte TARGET_RANGE_SEE_AND_LONG   = 8;
    static public final byte TARGET_RANGE_SAME_MAP          = 9;
    static public final byte TARGET_RANGE_ONE_MAP           = 10;
    static public final byte TARGET_RANGE_MAP_ON_SAME_WORLD = 11;
    static public final byte TARGET_RANGE_ANY               = 12;

    static public final byte EFFECT_RANGE_NONE          = 1;
    static public final byte EFFECT_RANGE_SELF          = 2;
    static public final byte EFFECT_RANGE_1SQUARE       = 3;
    static public final byte EFFECT_RANGE_SHORT_AREA    = 4;
    static public final byte EFFECT_RANGE_MEDIUM_AREA   = 5;
    static public final byte EFFECT_RANGE_LONG_AREA     = 6;
    static public final byte EFFECT_RANGE_MAP           = 7;
    static public final byte EFFECT_RANGE_WORLD         = 8;
    static public final byte EFFECT_RANGE_ANY           = 9;
    
//  static public final byte INFO_NONE              = 1;
    static public final byte INFO_SELF              = 0;
    static public final byte INFO_TARGET            = 1;
    static public final byte INFO_ALL_IN_SAME_MAP   = 2;
    static public final byte INFO_WORLD             = 3;
    static public final byte INFO_SYSOP             = 4;
    static public final byte INFO_LOG               = 5; // should be implemented:
                                                        // people using this shoudl be logged.....

    protected int id;
//    protected SecurityLevel securityLevel;  NOT USED AT THE MOMENT
    protected byte maskTarget;
    protected byte targetRange;
    protected byte effectRange;
    protected String name;
    protected String description;
    protected byte maskInform;
    protected boolean ostileAction; // OSTILE ACTION CAUSE ATTACKS.
    
    /** used by server to check if it's possible to execute
     *
     */
    abstract public boolean CanExecute( ScreenObject user, byte targetType, byte range);

    /** used by client to check if it's possible to execute
     *
     */
    public boolean CanExecute(byte targetType, byte range) {
        return isValidTarget(targetType, range);
    }

    public int getId() {
        return id;
    }
    
    public boolean isValidTarget(byte targetType, byte range) {
        if( !MaskTools.isSet(maskTarget,targetType) ){
            System.out.println(" type is not valid  ");
            return false;
        }
        if( targetRange < range ){
            System.out.println(" range is not valid  ");
            return false;
        }
        System.out.println(" action is valid");
        return true;
    }
    
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
        objectOutput.writeInt( id );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data from a stream to recive data.
   */
    public void readObject(java.io.ObjectInputStream objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            id = objectInput.readInt();
        } else {
            // to do.... when new version
        }
    }
    
    
    /** should be called at start by Server and Client 
     * to initialized array of actions
     * 
     * this initializa BasicAction, AbilityAction, CastAction
     *
     */
    static public void InitAllActions() {
        BasicAction.InitBasicActions();
        CastAction.InitCastActions();
        AbilityAction.InitAbilityAction();
    }
}
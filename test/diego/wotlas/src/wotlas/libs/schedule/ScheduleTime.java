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

package wotlas.libs.schedule;

import wotlas.libs.persistence.*;

/** Manages the schedule of encounters on maps
 * @author Diego
 */
public class ScheduleTime implements BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    private boolean isEnvironmentTime;
    private short minutesTimeBegin;
    private short minutesTimeDuration;
    // transient boolean isStarted;
  
    /*
    public ScheduleTime(short minutesBegin, short duration, boolean isEnvironmentTime){
        this.minutesTimeBegin = begin;
        this.minutesTimeDuration = duration;
        this.isEnvironmentTime = isEnvironmentTime;
    }
    */    
    
    public ScheduleTime(byte hourBegin, short minutesDuration, boolean isEnvironmentTime){
        this.minutesTimeBegin = (short) (hourBegin*60);
        this.minutesTimeDuration = minutesDuration;
        this.isEnvironmentTime = isEnvironmentTime;
    }

    public ScheduleTime(byte hourBegin, byte hourDuration, boolean isEnvironmentTime){
        this.minutesTimeBegin = (short) (hourBegin*60);
        this.minutesTimeDuration = (short) (hourDuration*60);
        this.isEnvironmentTime = isEnvironmentTime;
    }

    public ScheduleTime(byte dayOfWeek, boolean isEnvironmentTime){
    }
    
    public boolean isTime(){
        return false;
    }

  /** write object data with serialize.
   */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        objectOutput.writeBoolean( isEnvironmentTime );
        objectOutput.writeShort( minutesTimeBegin );
        objectOutput.writeShort( minutesTimeDuration );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            isEnvironmentTime = objectInput.readBoolean();
            minutesTimeBegin = objectInput.readShort();
            minutesTimeDuration = objectInput.readShort();
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
}
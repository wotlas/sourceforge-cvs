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

import wotlas.libs.persistence.BackupReady;

/** Manages the schedule of encounters on maps
 * @author Diego
 */
public class EncounterSchedule implements BackupReady {

    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

    private String name;
    private boolean disabled;
    private ScheduleTime timeOfActivity;

    transient private int id;
    transient private boolean isRunning;
    transient private long lastSpawn;
    transient private int marker;
    transient private short resetEveryMinutes;

    /*
    String name;
    disabled;
    monsters[number of types][number of one type];
    reset every
    transient counter of last reset
    transient marker for this schedule to put on mob 
    */

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled(boolean value) {
        this.disabled = value;
    }

    public EncounterSchedule(String name, ScheduleTime timeOfActivity) {
        this.name = name;
        this.timeOfActivity = timeOfActivity;
        this.disabled = false;
    }

    public EncounterSchedule() {
        this.disabled = false;
    }

    public boolean isTime() {
        if (!this.disabled)
            return false;
        if (this.isRunning)
            return false;
        return this.timeOfActivity.isTime();
    }

    /** write object data with serialize.
     */
    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(ExternalizeGetVersion());
        objectOutput.writeUTF(this.name);
        objectOutput.writeBoolean(this.disabled);
        objectOutput.writeObject(this.timeOfActivity);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** read object data with serialize.
     */
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if (IdTmp == ExternalizeGetVersion()) {
            this.name = objectInput.readUTF();
            this.disabled = objectInput.readBoolean();
            this.timeOfActivity = (ScheduleTime) objectInput.readObject();
        } else {
            // to do.... when new version
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** id version of data, used in serialized persistance.
     */
    public int ExternalizeGetVersion() {
        return 1;
    }
}
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
 
package wotlas.common.universe;

import wotlas.common.*;
import wotlas.common.router.*;
import wotlas.utils.*;
import wotlas.libs.persistence.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.graphics2D.drawable.*;
import wotlas.libs.schedule.*;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.*;

/** Used to check load status of tilemaps
 *
 * @author Diego
 * @see wotlas.common.universe.TileMaps
 */
public abstract class PreloaderEnabled extends ScreenRectangle implements BackupReady {
    
    /** id used in Serialized interface.
     */
    private static final long serialVersionUID = 556565L;

//    final static public byte LOAD_NOTHING = 0; // nothing loaded
    final static public byte LOAD_MINIMUM_DATA = 1; // wotlocation,id,name,full name, exits(?).
    final static public byte LOAD_CLIENT_DATA = 2;  // no encounterSchedule nor npc, nor items
    final static public byte LOAD_SERVER_DATA = 3;  // no graphics[][] or groupOfGraphics
    final static public byte LOAD_ALL = 4; // all loaded

    transient static byte loadStatus;
    transient protected String fileName;
    transient protected int useCounter;
    transient protected long lastUse;

    /*
    abstract public void InternalUnloadAll();
    abstract public void InternalLoadMinimum();
    abstract public void InternalLoadClient();
    abstract public void InternalLoadServer();
    abstract public void InternalLoadAll();
    */
    
    /** used to know if map can be loaded
     * : used to disable the map
     */
    protected boolean enabled = true;

    /**  used to controll revision of the maps : so the player can't have it
     *   different from the server 
     */
    protected long timeStamp = 0;
    
    /** used to controll remote creation of maps
     * creator of map : it can be the online player creator
     */
    protected String creator = "Wotlas Team"; 
    
    /** used to controll remote creation of maps
     * new maps should not have it
     */
    protected String approved = "Wotlas Team";
    
    /** used to controll remote creation of maps
     *  name of last user that change it
     */
    protected String lastChange = ""; 

    /**  reserved means only player with security >= builder can get there
     *   reserved is true then map is created from remote.
     *   get to false then admin approved it
     */
    protected boolean reserved = false;
  /** id version of data, used in serialized persistance.
   */
    
    public PreloaderEnabled() {
    }
    
    public PreloaderEnabled(int x, int y, int width, int height) {
        super(x,y,width,height);
    }
    
    public int ExternalizeGetVersion(){
        return 1;
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** write object data with serialize.
   */
    public void writeExternal(java.io.ObjectOutput objectOutput)
    throws java.io.IOException {
        objectOutput.writeInt( ExternalizeGetVersion() );
        super.writeExternal( objectOutput );
        objectOutput.writeBoolean( enabled );
        objectOutput.writeLong( System.currentTimeMillis() );
        this.timeStamp = System.currentTimeMillis();
        objectOutput.writeObject( creator );
        objectOutput.writeObject( approved );
        objectOutput.writeObject( lastChange );
        objectOutput.writeBoolean( reserved );
    }
    
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** read object data with serialize.
   */
    public void readExternal(java.io.ObjectInput objectInput)
    throws java.io.IOException, java.lang.ClassNotFoundException {
        int IdTmp = objectInput.readInt();
        if( IdTmp == ExternalizeGetVersion() ){
            super.readExternal(objectInput);
            enabled = objectInput.readBoolean();
            timeStamp = objectInput.readLong();
            creator = ( String ) objectInput.readObject();
            approved = ( String ) objectInput.readObject();
            lastChange = ( String ) objectInput.readObject();
            reserved = objectInput.readBoolean();
        } else {
            // to do.... when new version
        }
    }
    
    public void SetPreloader(String fileName, byte loadStatus){
        this.fileName = fileName;
        this.loadStatus = loadStatus;
        useCounter = 0;
        lastUse = -1;
    }

    public void initPreloader(){
        switch(loadStatus){
            case LOAD_MINIMUM_DATA:
                break;
            case LOAD_CLIENT_DATA:
                break;
            case LOAD_SERVER_DATA:
                break;
            case LOAD_ALL:
                break;
        }
    }

    /*
    public void Unload(){
        if(loadStatus != this.LOAD_NOTHING)
            return;
        InternalUnloadAll();
        loadStatus = this.LOAD_NOTHING;
    }

    public void LoadMinimumData(){
        if(loadStatus != this.LOAD_MINIMUM)
            return;
        InternalLoadMinimum();
        loadStatus = this.LOAD_MINIMUM;
    }
    
    public void LoadClientData(){
        if(loadStatus != this.LOAD_CLIENT_DATA)
            return;
        InternalLoadClient();
        loadStatus = this.LOAD_CLIENT_DATA;
    }
    
    public void LoadServerData(){
        if(loadStatus != this.LOAD_SERVER_DATA)
            return;
        InternalLoadServer();
        loadStatus = this.LOAD_SERVER_DATA;
    }
    
    public void LoadAllData(){
        if(loadStatus != this.LOAD_ALL)
            return;
        InternalLoadAll();
        loadStatus = this.LOAD_ALL;
    }

    public boolean Save(){
        if(loadStatus != this.LOAD_ALL)
            return false ;        
        // .....
        return true;
    }
    */
}
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

package wotlas.common.environment;

import wotlas.utils.Debug;

import java.util.*;

/** Manages the schedule of encounters on maps
 * @author Diego
 */
public class EnvironmentManager {

    /** Show debug information ?
    */
    public static boolean SHOW_DEBUG = true;

    static private EnvironmentManager serverEnvironment = WotEnvironment();
    
    public static final byte ENVIRONMENT_WOT        = 0;
    public static final byte ENVIRONMENT_ROGUE_LIKE = 1;
    
    public static final String[] environmentNames = { "Wheel of Time"
    ,"Rogue Like"};

    private byte oneHourEveryThisMinutes;
    private byte environmentType;
    
    public EnvironmentManager( byte environmentType, byte oneHourEveryThisMinutes ){
        this.oneHourEveryThisMinutes = oneHourEveryThisMinutes;
        this.environmentType = environmentType;
    }
    
    static public EnvironmentManager getServerEnvironment(){
        return serverEnvironment;
    }
    
    static public void setServerEnvironment(EnvironmentManager serverEnvironment){
        serverEnvironment = serverEnvironment;
    }
    
    static public String getEnvironmentName(){
        return environmentNames[serverEnvironment.environmentType];
    }
    
    static public EnvironmentManager WotEnvironment(){
        return new EnvironmentManager( ENVIRONMENT_WOT , (byte)15 );
    }
    
    static public EnvironmentManager RLikeEnvironment(){
        return new EnvironmentManager( ENVIRONMENT_ROGUE_LIKE , (byte)15 );
    }
    
    static public byte getEnvironmentHour(){
        byte value = new Integer( ((Calendar.getInstance().get(Calendar.HOUR)*60)+Calendar.getInstance().get(Calendar.MINUTE) )
        / serverEnvironment.oneHourEveryThisMinutes
        ).byteValue();
        if(SHOW_DEBUG){
            Debug.signal( Debug.NOTICE, null, "System Time : " + Calendar.getInstance().get(Calendar.HOUR) );
            Debug.signal( Debug.NOTICE, null, "Environment Time : " + value );
        }
        // xxxx = System.currentTimeMillis();
        return value;
    }
}
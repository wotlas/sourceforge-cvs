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
import wotlas.libs.graphics2D.*;

import java.util.*;

/** Manages the environment of the server.
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

    public static final byte GRAPHICS_SET_ROGUE = 0;
    
    public static final byte SET_OF_NPC  = 0;
    public static final byte SET_OF_ITEM = 1;
    public static final byte SET_OF_EFFECT = 2;

    private byte oneHourEveryThisMinutes;
    private byte environmentType;
    private byte graphicsSet;

    /**  make the server know if i had initialized the graphics,
     *   so it did it only once.
     */
    static private boolean justInitGraphics = false;

    /**  Create an enviroment.
     *  enviroment manages tile graphics of items,effects and npc/players
     *  and the time of game : how many minutes of real life
     *  makes an hour in the game : usually are 15 minutes
     */
    public EnvironmentManager( byte environmentType, byte oneHourEveryThisMinutes, byte graphicsSet ){
        this.oneHourEveryThisMinutes = oneHourEveryThisMinutes;
        this.environmentType = environmentType;
        this.graphicsSet = graphicsSet;
    }

    /** return the graphicSet used on this server.
     *
     */
    static public GroupOfGraphics[] getGraphics( byte graphicSet ){
        if( serverEnvironment.graphicsSet == GRAPHICS_SET_ROGUE ){
            switch( graphicSet ){
                case SET_OF_NPC:
                    return GroupOfGraphics.ROGUE_NPC_SET;
                case SET_OF_ITEM:
                    return GroupOfGraphics.ROGUE_ITEM_SET;
                case SET_OF_EFFECT:
                    return GroupOfGraphics.ROGUE_EFFECT_SET;
            } 
        } 
        else 
            Debug.signal( Debug.ERROR, null, "Error initializing graphics : request of non existing graphics set." );
        return GroupOfGraphics.ROGUE_EFFECT_SET;
    }

    /** return the enviroment of this server.
     *
     */
    static public EnvironmentManager getServerEnvironment(){
        return serverEnvironment;
    }
    
    /** set the enviroment of this server.
     *
     */
    static public void setServerEnvironment(EnvironmentManager serverEnvironment){
        serverEnvironment = serverEnvironment;
    }
    
    /** Returns enviroment name used.
     *
     */
    static public String getEnvironmentName(){
        return environmentNames[serverEnvironment.environmentType];
    }

    /*
     *  enables and create Wheel of Time environemnt, however
     *  someone from the wotlas team, should choose what picture
     *  every class Warder,Ashaman,etc should use
     *   inside getDrawableForTileMaps() of every class.
     * (cause actually all have the same picture :
     * i dont know what classes are caster and what warriors.
     *
     */
    static public EnvironmentManager WotEnvironment(){
        return new EnvironmentManager( ENVIRONMENT_WOT , (byte)15, GRAPHICS_SET_ROGUE );
    }

    /** return the default Rogue Like environment
     */
    static public EnvironmentManager RLikeEnvironment(){
        return new EnvironmentManager( ENVIRONMENT_ROGUE_LIKE , (byte)15, GRAPHICS_SET_ROGUE );
    }
    
    /**  Used to get the game time. It's used to manage schedule.
     * It returns no minutes, but only the hour.
     *
     */
    static public byte getEnvironmentHour(){
        byte value = new Integer( ((Calendar.getInstance().get(Calendar.HOUR)*60)+Calendar.getInstance().get(Calendar.MINUTE) )
        / serverEnvironment.oneHourEveryThisMinutes
        ).byteValue();
        if(SHOW_DEBUG){
            Debug.signal( Debug.NOTICE, null, "System Time : " + Calendar.getInstance().get(Calendar.HOUR) );
            Debug.signal( Debug.NOTICE, null, "Environment Time : " + value );
        }
        return value;
    }
    
    /** It's used to initialized tile graphis, however it's called only 
     * from TileMapData, so it's initialized only/if the server have tilemaps
     * and the player get there. At that point, the client initialize the data.
     *
     */
    static public void initGraphics( GraphicsDirector gDirector ){
        if( justInitGraphics )
            return;
        if( serverEnvironment.graphicsSet == GRAPHICS_SET_ROGUE ){
            GroupOfGraphics.initGroupOfGraphics( gDirector, GroupOfGraphics.ROGUE_NPC_SET );
            GroupOfGraphics.initGroupOfGraphics( gDirector, GroupOfGraphics.ROGUE_ITEM_SET );
            GroupOfGraphics.initGroupOfGraphics( gDirector, GroupOfGraphics.ROGUE_EFFECT_SET );
        } 
        else 
            Debug.signal( Debug.ERROR, null, "Error initializing graphics : request of non existing graphics set." );
        justInitGraphics = true;
    }
    
    /** Returns what's the graphicsSet choosed.
     *  
     */
    static public byte whtGraphicSetIs(){
        return serverEnvironment.graphicsSet;
    }
    
    static public byte getDefaultNpcImageNr(){
        if( serverEnvironment.graphicsSet == GRAPHICS_SET_ROGUE )
            return (byte)0;
        else 
            Debug.signal( Debug.ERROR, null, "Error initializing graphics : request of non existing graphics set." );
        return (byte)0;
    }

    static public int getDefaultPlayerImage(){
        if( serverEnvironment.graphicsSet == GRAPHICS_SET_ROGUE )
            return 0;
        else 
            Debug.signal( Debug.ERROR, null, "Error initializing graphics : request of non existing graphics set." );
        return 0;
    }
}
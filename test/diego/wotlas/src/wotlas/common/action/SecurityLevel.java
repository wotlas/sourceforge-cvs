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

/** it's done to have a centralized management of security:
 * this class is done to make possible an implement of security in the future
 *
 * for example a player can have the possibility to restart the server
 * but nothing else(?)
 *
 * @author  Diego
 */
public class SecurityLevel{
    
//    static final public byte ITEM = 10;
//    static final public byte NPC = 20;
    static final public byte PLAYER = 40;
    static final public byte BUILDER = 30;
    static final public byte SYSOP = 90;

    protected byte needed;

    public SecurityLevel(){
    
    }

    public boolean CheckSecurityLevel( SecurityLevel havingthis ){
        if(needed == PLAYER) // player needs can reached by all
            return true;
        else if(havingthis.needed == SYSOP) // sysop do all
            return true;
        else if(needed == BUILDER && havingthis.needed == BUILDER) // builder can do builder act
            return true;
        return false;
    }

}
/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
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

package wotlas.server;


/** Wotlas Lie Memory
 * remembers player's meets
 *
 * @author Petrus
 */
public class LieMemory
{

 /*------------------------------------------------------------------------------------*/

    /** key of the player we meet
     */
    public String otherPlayerKey;
    
    /** fake name of other player
     */
    public short otherPlayerFakeNameIndex;
    
    /** Number of times we meet a player
     */
    public int meetsNumber;
    
    /** Last time we meet a player
     */
    public long lastMeetTime;

 /*------------------------------------------------------------------------------------*/

    /** To get lastMeetTime
     */
    public long getLastMeetTime() {
      return lastMeetTime;
    }
    
    /** To set lastMeetTime
     */
    public void setLastMeetTime(long lastMeetTime) {
      this.lastMeetTime = lastMeetTime;
    }
    
    /** To get meetsNumber
     */
    public int getMeetsNumber() {
      return meetsNumber;
    }  
    
    /** To set meetsNumber
     */
    public void setMeetsNumber(int meetsNumber) {
      this.meetsNumber = meetsNumber;
    }
    
    /** To get otherPlayerKey
     */
    public String getOtherPlayerKey() {
      return otherPlayerKey;
    }
    
    /** To set otherPlayerKey
     */
    public void setOtherPlayerKey(String otherPlayerKey) {
      this.otherPlayerKey = otherPlayerKey;
    }  
    
    /** To get otherPlayerFakeNameIndex
     */
    public short getOtherPlayerFakeNameIndex() {
      return otherPlayerFakeNameIndex;
    }
    
    /** To set otherPlayerFakeNameIndex
     */
    public void setOtherPlayerFakeNameIndex(short otherPlayerFakeNameIndex) {
      this.otherPlayerFakeNameIndex = otherPlayerFakeNameIndex;
    }
    
    /** Empty constructor for persistence
     */
    public LieMemory() {
      ;
    }
    
    /** Constructor
     */
    public LieMemory(String otherPlayerKey, short otherPlayerFakeNameIndex, int meetType) {
      this.otherPlayerKey = otherPlayerKey;
      this.otherPlayerFakeNameIndex = otherPlayerFakeNameIndex;
      this.meetsNumber = meetType;
      this.lastMeetTime = System.currentTimeMillis();
    }
    
     /** Constructor
     */
    public LieMemory(String otherPlayerKey, short otherPlayerFakeNameIndex, int meetType, long lastMeetTime) {
      this.otherPlayerKey = otherPlayerKey;
      this.otherPlayerFakeNameIndex = otherPlayerFakeNameIndex;
      this.meetsNumber = meetType;
      this.lastMeetTime = lastMeetTime;
    }
    
    /** Constructor
     */
    public LieMemory(String otherPlayerKey, short otherPlayerFakeNameIndex) {
      this(otherPlayerKey, otherPlayerFakeNameIndex, LieManager.MEET_SIMPLE);
    }
    
}

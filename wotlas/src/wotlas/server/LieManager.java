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

package wotlas.server;

import wotlas.utils.Tools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.util.Calendar;
import java.util.Date;

/** Wotlas Lie Manager.
 *
 * @author Petrus
 */

public class LieManager 
{
 
 /*------------------------------------------------------------------------------------*/

  /** Number of player fake names 
   */
  public final static short FAKENAMES_NUMBER = 5;
  
  /** Number of meets to remember
   */
  public final static int MEETS_NUMBER = 50;
  
  /** when we leave an InteriorMap, we forget all players
   * that we met less than MEET_CHANGEINTERIORMAP times
   */
  public final static int MEET_CHANGEINTERIORMAP = 30;
  
  /** when we leave an TownMap, we forget all players
   * that we met less than MEET_CHANGEINTERIORMAP times
   */
  public final static int MEET_CHANGETOWNMAP = 90;
  
  /** when we leave an WorldMap, we forget all players
   * that we met less than MEET_CHANGEINTERIORMAP times
   */
  public final static int MEET_CHANGEWORLDMAP = 100;
  
  /** Simple meet (weight 2)
   */
  public final static int MEET_SIMPLE = 2;
  
  /** Chat meet (weight 1)
   */
  public final static int MEET_CHATMESSAGE = 1;
  
 /*------------------------------------------------------------------------------------*/

  /** Number of meetsNumber to forget when player leave an interiormap
   */
  public final static int FORGET_INTERIORMAP = 5;
  
  /** Number of meetsNumber to forget when player leave an interiormap
   */
  public final static int FORGET_TOWNMAP = 10;
  
  /** Number of meetsNumber to forget when player leave an interiormap
   */
  public final static int FORGET_WORLDMAP = 20;
  
  /** Number of meetsNumber to forget when player has not connected for a long time
   */
  public final static int FORGET_RECONNECT_LONG = 40;
  
  /** Number of meetsNumber to forget when player reconnect
   */
  public final static int FORGET_RECONNECT = 2;
  
 /*------------------------------------------------------------------------------------*/
  
  /** List of player's fake names
   */
  private String[] fakeNames = new String[FAKENAMES_NUMBER];
  
  /** List of other players current player has met
   * ascending order
   */
  private LieMemory[] memoriesArray = new LieMemory[0];
  
  transient LieMemoryIterator memories = new LieMemoryIterator();
  
  public LieMemoryIterator getMemoriesIterator() {
    return memories;
  }
  
  /** Index of current fake name
   */
  private short currentFakeNameIndex = 0;
  
 /*------------------------------------------------------------------------------------*/

  /** To get fake names
   */
  public String[] getFakeNames() {
    return fakeNames;
  }
  
  /** To set fake names
   * @param fakeNames array of fake names
   */
  public void setFakeNames(String[] fakeNames) {
    this.fakeNames = fakeNames;
  }
  
  /** To get current fake name index
   */
  public short getCurrentFakeNameIndex() {
    return currentFakeNameIndex;
  }
  
  /** To set current fake name index
   */
  public void setCurrentFakeNameIndex(short currentFakeNameIndex) {
    this.currentFakeNameIndex = currentFakeNameIndex;
  }
  
  /** To get memories
   */
  public LieMemory[] getMemoriesArray() {
    if (memories==null) {
      return null;
    }
    memories.resetIterator();
    LieMemory[] myMemoriesArray = new LieMemory[memories.getSize()];
    for (int k=0; k<memories.getSize(); k++) {
      myMemoriesArray[k] = memories.next();        
    }
    return myMemoriesArray;
  }
  
  /** To set memories
   */
  public void setMemoriesArray(LieMemory[] memoriesArr) {
    if (memories==null) {
      memories = new LieMemoryIterator();
    } else {
      memories.clear();
      memories.resetIterator();
    }
    for (int k=0; k<memoriesArr.length; k++) {
      memories.add(memoriesArr[k]);
    }
  }
  
 /*------------------------------------------------------------------------------------*/
  
  public String getFakeName(short index) {
    return fakeNames[index];
  }
  
  /** To get player fake name
   *
   * @param otherPlayer player who requested the fake name
   */
  public String getFakeName(PlayerImpl otherPlayer) {  
    if (ServerDirector.SHOW_DEBUG)
      System.out.println("LieManager::getFakeName(otherPlayer="+otherPlayer.getPrimaryKey()+")");
    String otherPlayerKey = otherPlayer.getPrimaryKey();
    return addMeet(otherPlayer, MEET_SIMPLE);
  }
  
  /** To get current fake name
   */
  public String getCurrentFakeName() {    
    return fakeNames[currentFakeNameIndex];
  }
  
  /** To add a player to our memory
   *
   * @param otherPlayer other player we want to remember
   * @return other player fake name
   */
  public String addMeet(PlayerImpl otherPlayer) {
    return addMeet(otherPlayer, MEET_SIMPLE);
  }
  
  /** To add a player to our memory
   *
   * @param otherPlayer other player we want to remember
   * @param meetType type of meet
   * @return other player fake name
   */
  public String addMeet(PlayerImpl otherPlayer, int meetType) {
    if (ServerDirector.SHOW_DEBUG)
      System.out.println("\naddMeet(otherPlayer="+otherPlayer.getPrimaryKey()+", meetType="+meetType+")");
    String otherPlayerKey = otherPlayer.getPrimaryKey();
    LieMemory memory;
    int k;
    synchronized(memories) {
      k=0;
      memories.resetIterator();      
      while ( memories.hasNext() ) {
        k++;
        memory = memories.next();
        if (memory.otherPlayerKey.equals(otherPlayerKey)) {           
          // player already met
          if (ServerDirector.SHOW_DEBUG)
            System.out.println("\tplayer already met");
          if (k>MEETS_NUMBER) {            
            // should never happen
            return null; 
          } else {
            // 
            int newMeetsNumber = memory.meetsNumber + meetType;            
            short oldOtherPlayerFakeNameIndex = memory.otherPlayerFakeNameIndex;
            if (ServerDirector.SHOW_DEBUG)
              System.out.println("\tremoving entry");            
            k--;
            memories.remove();
                                      
            while ( memories.hasNext() ) {
              k++;
              memory = memories.next();              
              if (memory.meetsNumber<newMeetsNumber) {
                LieMemory myMemory = new LieMemory(otherPlayerKey, oldOtherPlayerFakeNameIndex, newMeetsNumber);
                if (ServerDirector.SHOW_DEBUG)
                  System.out.println("\tinserting new entry");
                memories.insert(myMemory);
                if (k>MEETS_NUMBER) {
                  // must remove the first element
                  memories.resetIterator();
                  memories.remove();
                }           
                return otherPlayer.getLieManager().getFakeName(oldOtherPlayerFakeNameIndex);                                
              }
            }
            // Add player at the end of the LieMemoryIterator
            if (ServerDirector.SHOW_DEBUG)
              System.out.println("\tadding new entry");            
            LieMemory myMemory = new LieMemory(otherPlayerKey, oldOtherPlayerFakeNameIndex, newMeetsNumber);
            memories.add(myMemory);   
            if (k>MEETS_NUMBER) {
              // must remove the first element
              memories.resetIterator();
              memories.remove();
            }  
            return otherPlayer.getLieManager().getFakeName(oldOtherPlayerFakeNameIndex);                                
          }
        }
      }
      
      // Player not found in the LieMemoryIterator
      if (ServerDirector.SHOW_DEBUG)
        System.out.println("\tplayer never met");      
      if (k>MEETS_NUMBER) {
        // not enough place to remember player
        return otherPlayer.getLieManager().getCurrentFakeName();
      } 
      // Add player at the beginning of the LieMemoryIterator
      if (ServerDirector.SHOW_DEBUG)
        System.out.println("\tinserting player");      
      memories.resetIterator();
      LieMemory myMemory = new LieMemory(otherPlayerKey, otherPlayer.getLieManager().getCurrentFakeNameIndex(), 0);
      memories.insert(myMemory);         
      return addMeet(otherPlayer, meetType);
    }
  }
    
  /** To forget players
   *
   * @param meetType number of meetsNumber to forget
   */
  public void removeMeet(int meetType) {
    if (ServerDirector.SHOW_DEBUG)
      System.out.println("LieManager::removeMeet(meetType="+meetType+")");
    if (memories==null)
      return;
    synchronized(memories) {
      LieMemory memory;
      memories.resetIterator();      
      while ( memories.hasNext() ) {
        memory = memories.next();
        memory.meetsNumber -= meetType;
        if (memory.meetsNumber<0)
          memories.remove();
      }
    }
  }
  
  /** To forget players we don't meet often
   *
   * @param meetType minium of meetsNumber to remember
   */
  public void forget(int meetType) {  
    if (ServerDirector.SHOW_DEBUG)
      System.out.println("LieManager::forget(meetType="+meetType+")");
    if (memories==null)
      return;
    synchronized(memories) {
      LieMemory memory;
      memories.resetIterator();      
      while ( memories.hasNext() ) {
        memory = memories.next();
        if (memory.meetsNumber < meetType) {
          memories.remove();
        } else {
          return;
        }
      }
    }    
  }

 /*------------------------------------------------------------------------------------*/

  /** To set a fake name
   *
   * @param index index of fake name
   * @param fakeName fake name ot set
   */
  public boolean setFakeName(short index, String fakeName) {
    if ( (index<0) || (index>FAKENAMES_NUMBER) )
      return false;
      
    if (fakeNames[index].length() == 0) {      
      fakeNames[index] = fakeName;
      return true;
    } else {
      return false;
    }
  }
  
  /** To set full player name
   */
  public boolean setFullPlayerName(String fakeName) {
    fakeNames[(short)0] = fakeName;
    return true;    
  }

  /** To create a new fake name
   * @param fakeName new fake name
   * @return index of new fake name
   */
  public short createFakeName(String fakeName) {    
    for (short i=0; i<FAKENAMES_NUMBER;) {
      if (fakeNames[i].length() == 0) {
        fakeNames[i] = fakeName;        
        return i;
      } else {
        i++;
      }
    }
    return -1;
  }
 
  /** To update last time we meet another player
   */
  public void setLastMeetPlayer(PlayerImpl otherPlayer) {
    if (ServerDirector.SHOW_DEBUG)
      System.out.println("LieManager::setLastMeetPlayer(otherPlayer="+otherPlayer+")");
    if (memories==null)
      return;
    String otherPlayerKey = otherPlayer.getPrimaryKey();
    synchronized(memories) {
      LieMemory memory;
      memories.resetIterator();      
      while ( memories.hasNext() ) {
        memory = memories.next();
        if (memory.otherPlayerKey.equals(otherPlayerKey)) {
          memory.lastMeetTime = System.currentTimeMillis();
          return;
        }
      }
    }
  }
  
  public String getLastMeetPlayer(PlayerImpl otherPlayer) {
    if (ServerDirector.SHOW_DEBUG)
      System.out.println("LieManager::getLastMeetPlayer(otherPlayer="+otherPlayer+")");
    if (memories==null)
      return "player never met\n";
    String otherPlayerKey = otherPlayer.getPrimaryKey();
    synchronized(memories) {
      LieMemory memory;
      memories.resetIterator();      
      while ( memories.hasNext() ) {
        memory = memories.next();
        if (memory.otherPlayerKey.equals(otherPlayerKey)) {      
          Calendar lastTime = Calendar.getInstance();
          lastTime.setTime(new Date(memory.lastMeetTime));             
          return "player already met on " + Tools.getLexicalDate(lastTime) + "\n";
        }
      }
      return "player never met\n";
    }
  }
  
 /*------------------------------------------------------------------------------------*/
 
  /** To show debug information
   */
  public String toString() {
    String result;
    result = "LieManager::toString for player "+fakeNames[0]+"\n";
    if (memories==null) {
      result += " \tmemories null\n";
      return result;
    }
    synchronized(memories) {
      memories.resetIterator();
      int k=0;
      LieMemory memory;
      while ( memories.hasNext() ) {        
        memory = memories.next();
        result += "\tk = " + k + " meetsNumber = " + memory.meetsNumber + " otherPlayerKey = " + memory.otherPlayerKey + " otherPlayerFakeNameIndex = " + memory.otherPlayerFakeNameIndex + "\n";
        k++;
      }    
      return result;
    }
  }
 
 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we put your message data on the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param ostream data stream where to put your data (see java.io.DataOutputStream)
   * @exception IOException if the stream has been closed or is corrupted.
   */
    public void encode( DataOutputStream ostream ) throws IOException {     
      int fakeNamesLength = fakeNames.length;
      // Transfering fakeNames  
      ostream.writeInt(fakeNamesLength);       
      for (int i=0; i<fakeNamesLength; i++) {
        ostream.writeUTF(fakeNames[i]);
      }
      
      // Transfering currentFakeNameIndex
      ostream.writeShort(currentFakeNameIndex);
      
      // Transfering memories 
      if (memories==null) {
        ostream.writeInt(0);
      } else {
        ostream.writeInt(memories.getSize());
        LieMemory memory;
        synchronized(memories) {
          memories.resetIterator();
          while (memories.hasNext()) {
            memory = memories.next();
            ostream.writeUTF(memory.otherPlayerKey);
            ostream.writeShort(memory.otherPlayerFakeNameIndex);
            ostream.writeInt(memory.meetsNumber);
            ostream.writeLong(memory.lastMeetTime);
          }
        }
      }
       
    }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This is where we retrieve our message data from the stream. You don't need
   * to invoke this method yourself, it's done automatically.
   *
   * @param istream data stream where you retrieve your data (see java.io.DataInputStream)
   * @param myPlayer player who owns this lieManager
   * @exception IOException if the stream has been closed or is corrupted.
   */
    public void decode( DataInputStream istream , PlayerImpl myPlayer ) throws IOException {
      //LieManager myLieManager = new LieManager();      
      LieManager myLieManager = myPlayer.getLieManager();
      
      int fakeNamesLength = istream.readInt();      
      for (int i=0; i<fakeNamesLength; i++) {
        myLieManager.fakeNames[i] = istream.readUTF();
      }
      
      currentFakeNameIndex = istream.readShort();
      
      int myMemoriesSize = istream.readInt();
      
      if (myMemoriesSize==0) {
        ;
      } else {
        LieMemoryIterator myMemories = myLieManager.getMemoriesIterator();
        myMemories.resetIterator();
        LieMemory myMemory;
        for (int i=0; i<myMemoriesSize; i++) {
          myMemory = new LieMemory(istream.readUTF(), istream.readShort(), istream.readInt(), istream.readLong());
          myMemories.add(myMemory);
        }          
      }       
    }
  
 /*------------------------------------------------------------------------------------*/
  
  /** Constructor.
   */
  public LieManager() {
    for (short i=0;i<FAKENAMES_NUMBER;i++) {
      fakeNames[i] = "";      
    }
    currentFakeNameIndex = 0;
  }


}

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
  
  /** if we meet other player less than MEETS_MIN_NUMBER
   * then we remove player from our memory
   */
  public final static int MEETS_MIN_NUMBER = 5;
  
  /** Simple meet (weight 1)
   */
  public final static int MEET_SIMPLE = 1;
  
  /** List of player's fake names
   */
  private String[] fakeNames = new String[FAKENAMES_NUMBER];
  
  /** List of other players current player has met
   * ascending order
   */
  private LieMemory[] memories;

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
  public LieMemory[] getMemories() {
    return memories;
  }
  
  /** To set memories
   */
  public void setMemories(LieMemory[] memories) {
    this.memories = memories;
  }
  
 /*------------------------------------------------------------------------------------*/
  
  /** To get player fake name
   *
   * @param otherKey primary key of player who requested the full name
   */
  public String getFakeName(String otherKey) {
    if (memories==null) {
      // Add player to our memory      
      addMeet(otherKey);
      return fakeNames[currentFakeNameIndex];
    }    
    for (int i=0; i<memories.length; i++) {      
      if (memories[i].otherPlayerKey.equals(otherKey)) {
        // player already met
        memories[i].meetsNumber += MEET_SIMPLE;
        return fakeNames[memories[i].otherPlayerFakeNameIndex];
      }
    }
    // player never met
    // Add player to our memory    
    addMeet(otherKey);    
    return fakeNames[currentFakeNameIndex];
  }
  
  /** To get current fake name
   */
  public String getCurrentFakeName() {    
    return fakeNames[currentFakeNameIndex];
  }
  
  /** To add a player to our memory
   */
  public void addMeet(String otherKey) {
    addMeet(otherKey, MEET_SIMPLE);
  }
  
  /** To add a player to our memory
   */
  synchronized public void addMeet(String otherKey, int meetType) {
    
    if (memories == null) {
      // Create a new memory array      
      LieMemory myMemory = new LieMemory(otherKey, currentFakeNameIndex);
      memories = new LieMemory[1];
      memories[0] = myMemory;
      return;
    } else {
      // memory array not null           
      int i; // old index
      int j; // new index
      for (i=0; (i<memories.length) && !(memories[i].otherPlayerKey.equals(otherKey)); i++) {;}      
      if (i>MEETS_NUMBER) {
        // not enough place to remember player
        return;
      } else {
        // there is enough place to remember player
        if (i<memories.length) {
          // player already met                      
          int newMeetsNumber = memories[i].meetsNumber + meetType;
          for (j=i+1; (j<memories.length) && (memories[j].meetsNumber<newMeetsNumber); j++) {;}          
          if (j>MEETS_NUMBER) {
            // not enough place to remember player
            // free first element of memory array
            // add new element at the end of memory array
            LieMemory[] myMemories = new LieMemory[memories.length];
            LieMemory myMemory = new LieMemory(otherKey, currentFakeNameIndex, newMeetsNumber);
            System.arraycopy(memories, 1, myMemories, 0, memories.length-1);
            myMemories[memories.length] = myMemory;
            memories = myMemories;
            return;
          } else {
            if (j<memories.length) {                       
              LieMemory[] myMemories = new LieMemory[memories.length];
              if (i>0) {
                // copy from 0 to i-1                      
                System.arraycopy(memories, 0, myMemories, 0, i);
              }
              // copy from i+1 to j-1
              if (j-i-1 > 0) {                
                System.arraycopy(memories, i+1, myMemories, i, j-i-1);
                // insert new element before position j
                myMemories[j-1] = memories[i];
                myMemories[j-1].meetsNumber = newMeetsNumber;
                // copy from j to tab length                    
                System.arraycopy(memories, j, myMemories, j, memories.length-j);
                memories = myMemories;
                return;
              } else {
                // if j=i+1
                // just swap rows at index i and j
                LieMemory memTemp = memories[i];
                memories[i] = memories[j];
                memories[j] = memTemp;
                memories[j].meetsNumber = newMeetsNumber;               
                return;                 
              }              
            } else {                           
              // add new element at the end of memory array              
              LieMemory[] myMemories = new LieMemory[memories.length+1];
              LieMemory myMemory = new LieMemory(otherKey, currentFakeNameIndex, newMeetsNumber);
              System.arraycopy(memories, 0, myMemories, 0, memories.length);
              myMemories[memories.length] = myMemory;
              memories = myMemories;
              return;                            
            }          
          }
        } else {
          // i >= memories.length && i<MEETS_NUMBER
          // player never met
          // add new element at the beginning of memory array
          LieMemory[] myMemories = new LieMemory[memories.length+1];
          LieMemory myMemory = new LieMemory(otherKey, currentFakeNameIndex, 0);
          myMemories[0] = myMemory;
          System.arraycopy(memories, 0, myMemories, 1, memories.length);
          memories = myMemories;
          // update player meets informations
          addMeet(otherKey, meetType);
          return;
        }
      }
    }
  }     
  
  /** To forgive players we don't meet often
   */
  synchronized public void forgive() {
    if (memories==null)
      return;      
    int i;
    for (i=0; (i<memories.length) && (memories[i].meetsNumber<MEETS_MIN_NUMBER); i++) {;}
    LieMemory[] myMemories = new LieMemory[memories.length-i];
    System.arraycopy(memories, i, myMemories, 0, memories.length-i);
    memories = myMemories;
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
 
 /*------------------------------------------------------------------------------------*/
 
  /** To show debug information
   */
  public String toString() {
    String result;
    result = "LieManager::toString";
    if (memories==null) {
      result += "memories null";
      return result;
    }
    for (int k=0; k<memories.length; k++) {
      result += "\tk = " + k + " meetsNumber = " + memories[k].meetsNumber + " otherPlayerKey = " + memories[k].otherPlayerKey + " otherPlayerFakeNameIndex = " + memories[k].otherPlayerFakeNameIndex;
    }
    return result;
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

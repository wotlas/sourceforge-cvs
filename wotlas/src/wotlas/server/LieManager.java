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

  /** Static Link to Server Config File.
   */
  public final static int FAKENAMES_NUMBER = 5;
  
  /** Number of players to remember
   */
  private final static int MEMORY_LENGTH = 30;

  /** List of player's fake names
   */
  private String[] fakeNames = new String[FAKENAMES_NUMBER];

  /** Index of current fake name
   */
  private int currentFakeName = 0;
  
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
  
  /** To get current fake name
   * @return the index of current fake name
   */
  public int getCurrentFakeName() {
    return currentFakeName;
  }
  
  /** To set the current fake name
   * @param index index of current fake name
   */
  public void setCurrentFakeName(int index) {
    this.currentFakeName = index;
  }

 /*------------------------------------------------------------------------------------*/
  
  /** Get a specific fake name
   *
   * @param index index of fake name
   */
  public String getFakeName(int index) {
    return fakeNames[index];
  }
  
  /** Get current fake name
   */
  public String getFakeName() {
    return fakeNames[currentFakeName];
  }
  
  public String getPlayerName() {
    return fakeNames[0];
  }
  
  public boolean setFakeName(int index, String fakeName) {
    if ( (index<0) || (index>FAKENAMES_NUMBER) )
      return false;
      
    if (fakeNames[index].length() == 0) {      
      fakeNames[index] = fakeName;
      return true;
    } else {
      return false;
    }
  }
  
  public boolean setPlayerName(String fakeName) {
    return setFakeName(0, fakeName);
  }

  /** To create a new fake name
   * @param fakeName new fake name
   * @return index of new fake name
   */
  public int createFakeName(String fakeName) {    
    for (int i=0; i<FAKENAMES_NUMBER;) {
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
  
  /** Constructor.
   */
  public LieManager() {
    for (int i=0;i<FAKENAMES_NUMBER;i++) {
      fakeNames[i] = "";      
    }
    currentFakeName = 0;
  }

 /*------------------------------------------------------------------------------------*/

}

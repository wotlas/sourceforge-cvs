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

  /** List of player's fake names
   */
  private String[] fakeNames = new String[FAKENAMES_NUMBER];

  /** Index of current fake name
   */
  private int currentFakeName;
  
 /*------------------------------------------------------------------------------------*/

  public String[] getFakeNames() {
    return fakeNames;
  }
  
  public void setFakeNames(String[] fakeNames) {
    this.fakeNames = fakeNames;
  }
  
  public String getFakeName(int index) {
    return fakeNames[index];
  }
  
  public boolean setFakeName(int index, String fakeName) {
    if (fakeNames[index].length() == 0) {
      System.out.println("index = " + index);
      System.out.println("fakeName = " + fakeName);    
      fakeNames[index] = fakeName;
      return true;
    } else {
      System.out.println("index = " + index);
      System.out.println("fakeName already set");
      return false;
    }
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

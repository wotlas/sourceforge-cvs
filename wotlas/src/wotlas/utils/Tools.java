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
 
package wotlas.utils;


/** Various useful tools...
 *
 * @author Aldiss
 */

public class Tools
{
 /*------------------------------------------------------------------------------------*/

  /** Waits ms milliseconds with a very low CPU use.
   *
   * @param ms number of milliseconds to wait.
   */
    static public void waitTime( int ms )
    {
      Object o = new Object();
    
       synchronized( o ) {
          try{
               o.wait(ms);
          }
          catch(InterruptedException e) {}
       }
    }

 /*------------------------------------------------------------------------------------*/

  /** Is the Java version higher than the "min_required_version" string ?
   *  If it's not the case we return false and signal an ERROR to the Debug utility.
   *
   * @param required_min_version the minimum version number acceptable for this JVM
   *        ("1.2.2" for example).
   * @return true if the JVM version is higher, false otherwise.
   */
    static public boolean javaVersionHigherThan( String min_required_version )
    {
       String version = System.getProperty("java.version");

       if( version==null ) {
         Debug.signal( Debug.ERROR, null, "Could not obtain JVM version..." );
         return false;
       }

       if( version.compareTo(min_required_version) < 0) {
          Debug.signal( Debug.ERROR, null, "Your Java version is "+version
                       +". The minimum required version is "+min_required_version+" !" );
          return false;
       }

       return true;
    }

 /*------------------------------------------------------------------------------------*/ 
}

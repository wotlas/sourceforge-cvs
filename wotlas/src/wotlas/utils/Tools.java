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

import java.util.Calendar;

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

  /** To get a date formated in a lexical way ( year-month-day).
   *  Example: "2001-09-25". Note that we write "09" instead of "9".
   *
   * @return date
   */
   static public String getLexicalDate()
   {
      Calendar rightNow = Calendar.getInstance();
 
      String year = ""+rightNow.get(Calendar.YEAR);
      String month = null;
      String day = null;
      
      if( rightNow.get(Calendar.MONTH) <= 9 )
           month = "0"+(rightNow.get(Calendar.MONTH)+1);
      else
           month = ""+(rightNow.get(Calendar.MONTH)+1);

      if( rightNow.get(Calendar.DAY_OF_MONTH) <= 9 )
           day = "0"+rightNow.get(Calendar.DAY_OF_MONTH);
      else
           day = ""+rightNow.get(Calendar.DAY_OF_MONTH);

      return year+"-"+month+"-"+day;
   }

 /*------------------------------------------------------------------------------------*/ 

   /** To get an instance of an object from its class name. We assume that the
    *  object has an empty constructor.
    *
    *  @param className a string representing the class name of the filter
    *  @return an instance of the object, null if we cannot get an instance.
    */
      public static Object getInstance( String className ) {
          try{
               Class myClass = Class.forName(className);
               return myClass.newInstance();
          }catch(Exception ex) {
               Debug.signal( Debug.ERROR, null, "Failed to create new instance of "+className+", "+ex );
               return null;
          }
      }

 /*------------------------------------------------------------------------------------*/ 

}


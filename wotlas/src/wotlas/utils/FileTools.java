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

import java.io.*;

import java.util.Properties;

/** Various useful tools to manipulate files...
 *
 * @author Aldiss
 */

public class FileTools
{
 /*------------------------------------------------------------------------------------*/

  /** Finds the file/directory having the highest (or lowest) name (lexical order) in the
   * specified directory. The file/directory must have the XXXsometingYYY format, where
   * is XXX is the "beg" parameter and YYY the "end" parameter.<br><p>
   *
   *  For example if the "/home/test" directory contains :<br><p>
   *
   *  ah-ah.cfg <br>
   *  save-2001-09-01.cfg <br>
   *  save-2001-09-02.cfg <br>
   *  save-2001-09-03.cfg <br>
   *  zzz-zzz.dat <br><p>
   *
   *  the call findSave( "/home/test", "save-",".cfg", true) will return "save-2001-03.cfg".
   *  the call findSave( "/home/test", "save-",".cfg", false) will return "save-2001-01.cfg".
   *
   *  Note that in the example we paid attention to the file names : no "9" but "09" to keep
   *  a useful lexical order.
   *
   * @param path directory to inspect.
   * @param beg  file name prefix.
   * @param end file name suffix.
   * @param latest latest or oldest save ?
   * @return file name with the highest or lowest lexical order and matching the specified format.
   */
   static public String findSave( String path, String beg, String end, boolean latest )
   {
       File fileList[] = new File( path ).listFiles();

       int index = -1;

       if( fileList==null )
           return null;

       for( int i=0; i<fileList.length; i++ )
       {
          if( !fileList[i].getName().startsWith(beg) || !fileList[i].getName().endsWith(end) )
               continue;

          if( index==-1 )
              index = i;
          else{
             int compare = fileList[i].getName().compareTo( fileList[index].getName() );

             if( ( latest && compare>0 ) || ( !latest && compare<0 ) )
                  index = i;
          }
       }

       if( index==-1 )
           return null;
       else
           return fileList[index].getName();
   }

 /*------------------------------------------------------------------------------------*/

  /** To load a simple properties file...
   *
   * @param pathname properties complete filename
   * @return properties file
   */
   static public Properties loadPropertiesFile( String pathname )
   {
      try {
         BufferedInputStream is = new BufferedInputStream( new FileInputStream(pathname) );
         Properties props = new Properties();
         props.load( is );

         return props;
      }
      catch(Exception e) {
         Debug.signal(Debug.WARNING, null, e);
         return null;
      }
   }

 /*------------------------------------------------------------------------------------*/

  /** To save a simple properties file... For more complex use PREFER the
   *  use of the persistence library.
   *
   * @param props properties to save
   * @param pathname file name to save the properties to.
   * @param header header to put in the file.
   * @return true if success, false otherwise
   */
   static public boolean savePropertiesFile( Properties props, String pathname, String header )
   {
      try{
         BufferedOutputStream os = new BufferedOutputStream( new FileOutputStream(pathname) );
         props.store( os, header );

         return true;
      }
      catch(Exception e) {
         Debug.signal(Debug.WARNING, null, e);
         return false;
      }
   }

 /*------------------------------------------------------------------------------------*/

}

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

package wotlas.server.setup;


import java.io.*;
import java.util.*;

import wotlas.utils.FileTools;
import wotlas.utils.Debug;

/** A small utility to update the version number in the server table.
 *
 * @author Aldiss
 */

class UpdateServerTable {

  /** Static path to the server configs
   */
  static public final String SERVER_HOME = "../base/servers/";

  /** Static path to the local server table.
   */
  static public final String SERVER_TABLE = "../src/config/remote/server-table.cfg";

  /******************************************************************************/


  static public void main(String argv[]){
     //Open the files
       
       File files[] = new File(SERVER_HOME).listFiles();
       String serverTable = FileTools.loadTextFromFile(SERVER_TABLE);

       if(files==null) {
       	  Debug.signal(Debug.CRITICAL,null, "no server config files in "+SERVER_HOME);
       	  return;
       }

       if(serverTable==null) {
          Debug.signal(Debug.CRITICAL,null,"server table not found at "+SERVER_TABLE);
       	  return;
       }
     
       for(int i=0; i<files.length; i++) {
       	   if(files[i].isDirectory() || !files[i].getName().endsWith(".cfg"))
       	      continue;

           int index = files[i].getName().indexOf("server-");

           if(index<0) continue;

           try{
             // we get the ID from "server-ID.cfg"
               String name = files[i].getName();
               int id = Integer.parseInt( name.substring( 7, name.length()-4) );
               
               if(id==0) continue;

              // we parse the file for 'configVersion='
               String config = FileTools.loadTextFromFile(SERVER_HOME+name);
               
               index = config.indexOf("configVersion=");
               int lineEnd = config.indexOf("\n",index);
               
               String version = config.substring(index+14,lineEnd);
               
              // we insert the version number in the server table
               name = "Server-"+id+"-Version =";
               index = serverTable.indexOf(name);           // note the SPACE before the '='
               lineEnd = serverTable.indexOf("\n",index);
               
               serverTable = serverTable.substring(0,index+name.length())
                             +" "+version
                             +serverTable.substring(lineEnd,serverTable.length());
                             
               System.out.println( "Updated successfully server config "+id+" to server table.");
           }
           catch(Exception e) { 
                Debug.signal(Debug.CRITICAL,null, "Server config with bad format detected : "+e);
           	continue;
           }
       }

      // We save the server table
         if( !FileTools.saveTextToFile( SERVER_TABLE, serverTable ) ) {
       	     Debug.signal(Debug.CRITICAL,null,"failed to save server table in "+SERVER_HOME );
       	     return;
       	 }

         Debug.signal(Debug.NOTICE,null," Server table saved." );
   }


  /********************************************************************************/

}
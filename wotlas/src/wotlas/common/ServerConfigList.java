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

package wotlas.common;

import wotlas.utils.*;


 /** Server Config List. Loaded using the provided persistence manager.
  *
  * @author Aldiss, Petrus
  * @see wotlas.common.ServerConfig
  */
 
public class ServerConfigList
{
 /*------------------------------------------------------------------------------------*/

    /** ServerConfig List ( not sorted !!! use getServerID() to retrieve ID )
     */
       private ServerConfig configs[];

    /** Remote server home URL : where the server list is stored on the internet.
     */
       private String remoteServerConfigHomeURL;

    /** Remote Server Table giving the list of available remote servers.
     */
       private String remoteServerTable;

 /*------------------------------------------------------------------------------------*/

  /** Constructor with persistence manager.
   *
   * @param pm persistence manager to use
   */
   public ServerConfigList( wotlas.common.PersistenceManager pm ) {

      // attempt to load the configs from persistenceManager...
         configs = pm.loadServerConfigs();
         if(configs==null)
            Debug.signal( Debug.WARNING, null, "No Server Configs loaded !" ); 
   }

 /*------------------------------------------------------------------------------------*/

  /** To get the URL where are stored the remote server configs. This URL can also contain
   *  a news.html file to display some news.
   *
   * @param remoteServerConfigHomeURL urlname to use to find the servers home.
   */
   public void setRemoteServerConfigHomeURL( String remoteServerConfigHomeURL ) {
      this.remoteServerConfigHomeURL = remoteServerConfigHomeURL;
   }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get the remote server table.
    * @return the previously loaded server table, the just-loaded server table or null
    *         if we cannot reach our central site.
    */
    public String getRemoteServerTable() {
          if(remoteServerConfigHomeURL==null)
             return null;

          if( remoteServerTable!= null )
             return remoteServerTable;

       // We load the file from its URL
          remoteServerTable = FileTools.getTextFileFromURL( remoteServerConfigHomeURL+"server-table.cfg" );
          return remoteServerTable;
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To get a ServerConfig file from its associated serverID.
    *
    *  NOTE : serverID != originalServerID. For a player the originalServerID is the
    *  serverID of the server that created his game account. But as his account moves
    *  from one server to another, the player's current serverID ( where he now is )
    *  can be different from his originalServerID.
    *
    * @param serverID server ID.
    * @return null if the serverconfig was not found, the ServerConfig object otherwise.
    */
     public ServerConfig getServerConfig( int serverID ) {
     
         if(configs==null)
            return null;
     
         for( int i=0; i<configs.length; i++ )
             if( configs[i].getServerID()==serverID )
                 return configs[i];

         return null;
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
 
  /** To get the number of servers.
   */  
   public int size() {
    if (configs==null)
      return 0;
    return configs.length;
   }
  
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** To get a ServerConfig file from its index in array <b>configs</b>
   *
   * @param index server index.
   */
   public ServerConfig ServerConfigAt(int index) {
      return configs[index];
   }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
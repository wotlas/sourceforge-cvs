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

    /** ServerConfig List ( sorted by getServerID() )
     */
       private ServerConfig configs[];

    /** Remote server home URL : where the server list is stored on the internet.
     */
       private String remoteServerConfigHomeURL;

    /** Remote Server Table giving the list of available remote servers.
     */
       private String remoteServerTable;

    /** Our persistenceManager
     */
       private PersistenceManager pm;

    /** Home of Servers unreachable ?
     */
       private boolean serversRemoteHomeUnreachable;

 /*------------------------------------------------------------------------------------*/

  /** Constructor with persistence manager.
   *
   * @param pm persistence manager to use
   */
   public ServerConfigList( PersistenceManager pm ) {
         this.pm = pm;
         serversRemoteHomeUnreachable = false;

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

          if( serversRemoteHomeUnreachable ) return null;

       // We load the file from its URL
          remoteServerTable = FileTools.getTextFileFromURL( remoteServerConfigHomeURL+"server-table.cfg" );
          
          if(remoteServerTable==null)
            serversRemoteHomeUnreachable = true;

          return remoteServerTable;
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To update a server config if there is an updated remote config.
    * @param config config to update
    */
    public void updateServerConfig( ServerConfig config ) {
    	if(getRemoteServerTable()==null)
    	   return;
    	
    	int ind = remoteServerTable.indexOf( "Server-"+config.getServerID()+"-Version" );
    	
    	if(ind>=0) {
           ind = remoteServerTable.indexOf( " = ", ind );

           if(ind>=0) {
              ind++; // to avoid the '=' char
              int end = remoteServerTable.indexOf( "\n", ind ); // seeking the end of line
              
              if( end>=0 ) {
              	// Extracting server config latest version
                  String version = remoteServerTable.substring( ind, end ).trim();

                  if( version.length()==0 ) {
                      Debug.signal( Debug.ERROR, this, "There is no Server "+config.getServerID()+" any more ! Trying to revert to previous config...");
                      return;
                  }

                // has the version changed ?
                  if( !config.getConfigVersion().equals( version ) ) {                  	
                     // a new server config is available
                        Debug.signal( Debug.WARNING, this, "A new config is available for server "+config.getServerID()+". Trying to load it.");

                        String newConfig = FileTools.getTextFileFromURL( remoteServerConfigHomeURL
                                   +PersistenceManager.SERVERS_PREFIX+config.getServerID()+PersistenceManager.SERVERS_SUFFIX );
                        
                        if( newConfig ==null ) {
                            Debug.signal( Debug.CRITICAL, this, "Failed to get new Server "+config.getServerID()+" config. Reverting to previous one.");
                            return;
                        }

                        if( pm.updateServerConfig( newConfig, config ) ){
                            Debug.signal( Debug.WARNING, this, "Updated successfully Server "+config.getServerID()+" config.");
                            return;
                        }

                     return; // failed to update... ( an error has been displayed by the PersistenceManager )
                  }

                  return; // config is up-to-date !
              }
           }
    	}

      // Not Found
         Debug.signal( Debug.ERROR, this, "Server Config "
                       +config.getServerID()+" not found in remote server table !");
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To retrieve the latest server config files.
    */
    public void getLatestConfigFiles() {
         if(getRemoteServerTable()==null)
    	    return;

         int ind=0;

         while( ( ind = remoteServerTable.indexOf( "Server-", ind ) ) >0 ) {
             ind += 7; // to skip the "Server-";
             
             int end = remoteServerTable.indexOf( "-", ind );             
             if(end<0) return; // premature end of table

          // We retrieve the serverID
             int serverID = -1;
             try{
                serverID = Integer.parseInt( remoteServerTable.substring( ind, end ) );
             }catch( Exception e ) {
             }

             ind ++; // to skip the last '-' char

          // Update or Creation ?
             if( serverID>0 ) {  // negative or null IDs are skipped
             	 ServerConfig config = getServerConfig( serverID );
             	 
             	 if( config!=null ) {
             	     updateServerConfig( config ); // update
             	 }
             	 else {
             	   // creation
                      Debug.signal( Debug.WARNING, this, "A new server ("+serverID+") is available. Trying to load its config.");

                      String newConfig = FileTools.getTextFileFromURL( remoteServerConfigHomeURL
                                         +PersistenceManager.SERVERS_PREFIX+serverID+PersistenceManager.SERVERS_SUFFIX );
                        
                      if( newConfig ==null ) {
                          Debug.signal( Debug.CRITICAL, this, "Failed to get new Server "+config.getServerID()+" config. Reverting to previous one.");
                          continue;
                      }

                      config = pm.createServerConfig( newConfig, serverID );

                      if( config!=null ){
                      	// We save it in our table
                           Debug.signal( Debug.NOTICE, this, "Retrieved successfully new server ("+config.getServerID()+") config.");
                           addConfig( config );
                      }
             	 }
             }

            // going to next line...
         }
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To add a new ServerConfig to our list.
    * @param newConfig config to add
    */
      public void addConfig( ServerConfig newConfig ) {
          if (configs == null) {
              configs = new ServerConfig[1];
              configs[0] = newConfig;
          } else {
              ServerConfig myConfigs[] = new ServerConfig[configs.length+1];
              System.arraycopy(configs, 0, myConfigs, 0, configs.length);
              myConfigs[configs.length] = newConfig;
              configs = myConfigs;
          }
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
             if( configs[i].getServerID()==serverID ) {
                 updateServerConfig( configs[i] ); // trying to update
                 return configs[i];
             }
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
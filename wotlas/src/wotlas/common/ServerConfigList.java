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

import java.awt.*;
import javax.swing.*;

 /** Server Config List. Loaded using the provided persistence manager.
  *
  * @author Aldiss, Petrus
  * @see wotlas.common.ServerConfig
  */
 
public class ServerConfigList
{
 /*------------------------------------------------------------------------------------*/

   /** Update period for server config address... (in ms)
    *
    *  Our cache system works on a "fail first" mode...
    *  If a server is reachable we don't check for a new update on its Internet address.
    *  If a server becomes unreachable then we check if its UPDATE_PERIOD has been reached
    *  if true we check for a new address.
    */
       public final long UPDATE_PERIOD = 1000*60*20;  // every twenty minutes


   /** Update period for the server table... (in ms). This is a straight cache mode.
    *  Each time the serverTable is accessed we check its lastServerTableUpdateTime
    *  and compare it to this period...
    */
       public final long UPDATE_TABLE_PERIOD = 1000*3600*6;  // every 6 hours


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

    /** Last time we updated the server table.
     */
       private long lastServerTableUpdateTime;

    /** A local ID of a server config we don't want to update. On the server side it's
     *  the server's ID. On the client side it should remain equal to -1 : all configs
     *  are updated.
     */
       private int localServerID;

 /*------------------------------------------------------------------------------------*/

  /** Constructor with persistence manager.
   *
   * @param pm persistence manager to use
   */
   public ServerConfigList( PersistenceManager pm ) {
         this.pm = pm;
         serversRemoteHomeUnreachable = false;
         lastServerTableUpdateTime=0; // none
         localServerID=-1; // no config to spare from updates...

      // attempt to load the configs from persistenceManager...
         configs = pm.loadServerConfigs();
         if(configs==null)
            Debug.signal( Debug.WARNING, null, "No Server Configs loaded !" );
   }

 /*------------------------------------------------------------------------------------*/

  /** To set the local server ID that is used on this machine and should never be used.
   * @param serverID serverID of the config to never update
   */
   public void setLocalServerID( int localServerID ) {
         this.localServerID = localServerID;
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

          if( serversRemoteHomeUnreachable ) return null;

       // Update Period Reached ?
          long now = System.currentTimeMillis();

          if( remoteServerTable== null || (lastServerTableUpdateTime+UPDATE_TABLE_PERIOD)<=now ) {
             // yes ! we erase the old server table.
                Debug.signal( Debug.NOTICE, null, "Loading server table..." );
                remoteServerTable = null;
                lastServerTableUpdateTime=now;
          }

          if( remoteServerTable!= null )
             return remoteServerTable;

          Debug.signal(Debug.NOTICE, null,"Trying to load Server Table from network...");
       // We load the file from its URL
          remoteServerTable = FileTools.getTextFileFromURL( remoteServerConfigHomeURL+"server-table.cfg" );
          
          if(remoteServerTable==null || remoteServerTable.length()==0 ) {
              Debug.signal(Debug.CRITICAL, this,"Try to load Server Table from network failed...");
              serversRemoteHomeUnreachable = true;
          }

          return remoteServerTable;
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** Get Max Value for ProgressMonitor
    */
    public int getMaxValue() {
    	if(getRemoteServerTable()==null)
    	   return 0;

         int ind=0, nb=0;

         while( ( ind = remoteServerTable.indexOf( "Server-", ind ) ) >0 ) {
             nb++;
             ind += 7; // to skip the 'Server-'
         }

        return nb;
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To update a server config if there is an updated remote config.
    * @param config config to update
    */
    public void updateServerConfig( ServerConfig config ) {
        if(config.getServerID()==0) // server config is local
           return;

    	if(getRemoteServerTable()==null)
    	   return;
    	
    	if(localServerID==config.getServerID())
    	   return;  // this config musn't be updated
    	
    	int ind = remoteServerTable.indexOf( "Server-"+config.getServerID()+"-Version" );
    	
    	if(ind>=0) {
           ind = remoteServerTable.indexOf( "=", ind );

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

                        String fileURL = remoteServerConfigHomeURL
                               + PersistenceManager.SERVERS_PREFIX+config.getServerID()+PersistenceManager.SERVERS_SUFFIX;

                        String newConfig = FileTools.getTextFileFromURL( fileURL );

                        if( newConfig ==null ) {
                            Debug.signal( Debug.CRITICAL, this, "Failed to get new Server "+config.getServerID()+" config. Reverting to previous one.");
                            return;
                        }

                        String newAdr = FileTools.getTextFileFromURL( fileURL+PersistenceManager.SERVERS_ADDRESS_SUFFIX );

                        if( newAdr==null ) {
                            Debug.signal( Debug.CRITICAL, this, "Failed to get new Server "+config.getServerID()+" address. Reverting to previous one.");
                            return;
                        }

                        if( pm.updateServerConfig( newConfig, newAdr, config ) ) {
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
    * @param parent parent component for the javax.swing.ProgressMonitor...
    */
    public void getLatestConfigFiles( Component parent ) {
         if(getRemoteServerTable()==null)
    	    return;

         int value=0;

       // Progress Monitor
          ProgressMonitor pMonitor = null;

          if(parent!=null) {
             pMonitor = new ProgressMonitor( parent, "Loading Server List", "", 0, getMaxValue() );
             pMonitor.setMillisToPopup(500);
             pMonitor.setMillisToDecideToPopup(100);
          }

          int ind=0;

         while( ( ind = remoteServerTable.indexOf( "Server-", ind ) ) >0 ) {
             ind += 7; // to skip the "Server-";
             value++;

             if(parent!=null)
                pMonitor.setProgress(value);

             if( parent!=null && pMonitor.isCanceled()) {
                pMonitor.close();
                return;
             }

             int end = remoteServerTable.indexOf( "-", ind );
             if(end<0) return; // premature end of table

          // We retrieve the serverID
             int serverID = -1;
             try{
                serverID = Integer.parseInt( remoteServerTable.substring( ind, end ) );
             }catch( Exception e ) {
             }

             ind ++; // to skip the last '-' char

             if(serverID<=0) continue; // negative or null IDs are skipped

             int versionBeg = remoteServerTable.indexOf( "=", ind );
             int versionEnd = remoteServerTable.indexOf( "\n",ind );

             if( versionBeg<0 || versionEnd<0 ) {
                 break; // end of file
             }

             if( remoteServerTable.substring(versionBeg+1,versionEnd).trim().length()==0 )
                 continue; // we skip the empty server entry...

          // Update or Creation ?
             ServerConfig config = findServerConfig( serverID );
             	 
             if( config!=null ) {
                 updateServerConfig( config ); // update
             }
             else {
               // creation
                 if( parent!=null && (pMonitor.isCanceled() || remoteServerTable.indexOf("WOT",ind)<0) ) {
                     pMonitor.close();
                     return;
                 }

                 Debug.signal( Debug.WARNING, this, "A new server ("+serverID+") is available. Trying to load its config.");

                 String fileURL = remoteServerConfigHomeURL
                               + PersistenceManager.SERVERS_PREFIX+serverID+PersistenceManager.SERVERS_SUFFIX;

                 String newConfig = FileTools.getTextFileFromURL( fileURL );

                 if( newConfig ==null ) {
                     Debug.signal( Debug.ERROR, this, "Failed to get new Server "+serverID+" config. Reverting to previous one.");
                     continue;
                 }

                 String newAdr = FileTools.getTextFileFromURL( fileURL+PersistenceManager.SERVERS_ADDRESS_SUFFIX );

                 if( newAdr==null ) {
                     Debug.signal( Debug.ERROR, this, "Failed to get new Server "+serverID+" address. Reverting to previous one.");
                     continue;
                 }

                 config = pm.createServerConfig( newConfig, newAdr, serverID );

                 if( config!=null ){
                    // We save it in our table
                       Debug.signal( Debug.NOTICE, this, "Retrieved successfully new server ("+serverID+") config.");
                       addConfig( config );
                 }
             }

            // going to next line...
         }

         if(parent!=null)
            pMonitor.close();
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

   /** We search for the wanted server config without performing any updates.
    */
     private ServerConfig findServerConfig( int serverID ) {
     
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
      if(configs==null) return null;
      return configs[index];
   }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To report a dead server. We'll then try to check the server address.
   * @param serverID server id which represents the dead server
   * @return another address you can try for this server, if this address fails
   *     you can really consider the server dead. If we return null it means
   *     that we don't where able to update the server address...
   */
    public String reportDeadServer( int serverID ) {

        ServerConfig currentConfig = findServerConfig(serverID);

        if(currentConfig==null) {
            Debug.signal( Debug.ERROR, this, "Failed to find local config of server "+currentConfig.getServerID()+".");
            return null;
        }

     // We check the cache timestamp
        if( currentConfig.getLastUpdateTime()+UPDATE_PERIOD > System.currentTimeMillis() )
            return null; // we recently checked the address

        String fileURL = remoteServerConfigHomeURL
                      + PersistenceManager.SERVERS_PREFIX+currentConfig.getServerID()+PersistenceManager.SERVERS_SUFFIX;

     // We load the address file
        String newAdr = FileTools.getTextFileFromURL( fileURL+PersistenceManager.SERVERS_ADDRESS_SUFFIX );

        if( newAdr==null ) {
            Debug.signal( Debug.ERROR, this, "Failed to get new Server "+currentConfig.getServerID()+" address. Reverting to previous one.");
            return null;
        }

        if( !pm.updateServerConfig( null, newAdr, currentConfig ) )
            Debug.signal(Debug.ERROR,this,"For some reason we failed to save the new address...");

        return newAdr; // new address the user can try...
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
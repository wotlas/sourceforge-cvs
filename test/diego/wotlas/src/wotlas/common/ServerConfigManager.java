/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2002 WOTLAS Team
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
import java.io.File;

import java.awt.*;
import javax.swing.*;


 /** Server Config Manager.
  *
  * @author Aldiss, Petrus
  * @see wotlas.common.ServerConfig
  * @see wotlas.libs.persistence.PropertiesConverter
  */
 
public class ServerConfigManager {

 /*------------------------------------------------------------------------------------*/

   /** Format of the Server Config File Names
    */
       public final static String SERVERS_PREFIX = "server-";
       public final static String SERVERS_SUFFIX = ".cfg";
       public final static String SERVERS_ADDRESS_SUFFIX = ".adr"; // this suffix is added after the
                                                               // SERVERS_SUFFIX : "server-0.cfg.adr"

   /** Update period for server config address... (in ms)
    *
    *  Our cache system works on a "fail first" mode...
    *  If a server is reachable we don't check for a new update on its Internet address.
    *  If a server becomes unreachable then we check if its UPDATE_PERIOD has been reached
    *  if true we check for a new address.
    */
       public final long UPDATE_PERIOD = 1000*60*5;  // every five minutes


   /** Update period for the server table... (in ms). This is a straight cache mode.
    *  Each time the serverTable is accessed we check its lastServerTableUpdateTime
    *  and compare it to this period...
    */
       public final long UPDATE_TABLE_PERIOD = 1000*3600*4;  // every 4 hours

 /*------------------------------------------------------------------------------------*/

    /** ServerConfig List ( sorted by getServerID() )
     */
       private ServerConfig configs[];

    /** Our resource manager/
     */
       private ResourceManager rManager;

    /** Remote server home URL : where the server list is stored on the internet.
     */
       private String remoteServerConfigHomeURL;

    /** Remote Server Table giving the list of available remote servers.
     */
       private String remoteServerTable;

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

  /** Constructor with Resource manager.
   *
   * @param rManager our ResourceManager
   */
   public ServerConfigManager( ResourceManager rManager ) {
         this.rManager = rManager;
         serversRemoteHomeUnreachable = false;
         lastServerTableUpdateTime=0; // none
         localServerID=-1; // no config to spare from updates...

      // Attempt to load the local server configs
         configs = loadServerConfigs();

         if(configs==null)
            Debug.signal( Debug.WARNING, null, "No Server Configs loaded !" );
   }

 /*------------------------------------------------------------------------------------*/

  /** To set the local server ID that is used on this machine and which configs should
   *  never be updated.
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
    protected String getRemoteServerTable() {
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

   /** To know if we have the necessary information on remote servers.
    *  @return true if everything is ok
    */
    public boolean hasRemoteServersInfo() {
    	if(remoteServerTable!=null)
    	   return true;
    	return false;
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To update a server config if there is an updated remote config.
    * @param config config to update
    */
    protected void updateServerConfig( ServerConfig config ) {
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
                      String fileURL = remoteServerConfigHomeURL+SERVERS_PREFIX+config.getServerID()+SERVERS_SUFFIX;
                      String newConfig = FileTools.getTextFileFromURL( fileURL );

                      if( newConfig ==null ) {
                          Debug.signal( Debug.CRITICAL, this, "Failed to get new Server "+config.getServerID()+" config. Reverting to previous one.");
                          return;
                      }

                      String newAdr = FileTools.getTextFileFromURL( fileURL+SERVERS_ADDRESS_SUFFIX );
                      newAdr = checkAddressFormat(newAdr);

                      if( newAdr.length()==0 ) {
                          Debug.signal( Debug.CRITICAL, this, "Failed to get new Server "+config.getServerID()+" address. Reverting to previous one.");
                          return;
                      }

                      if( updateServerConfig( newConfig, newAdr, config ) ) {
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

   /** Get Max Number of server configs (in the server table) for the ProgressMonitor
    */
    private int getMaxValue() {
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

   /** To retrieve the latest server config files from the net. Only the server configs
    *  that have been changed are downloaded.
    * @param parent parent component for the javax.swing.ProgressMonitor... if you
    *        enter null no ProgressMonitor will be used.
    */
    public void getLatestConfigFiles( Component parent ) {
         if(getRemoteServerTable()==null)
    	    return;

         int value=0;

       // Progress Monitor
          ProgressMonitor pMonitor = null;

          if(parent!=null) {
             pMonitor = new ProgressMonitor( parent, "Loading Server List", "", 0, getMaxValue() );
             pMonitor.setMillisToDecideToPopup(100);
             pMonitor.setMillisToPopup(500);
             pMonitor.setProgress(0);
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

                 String fileURL = remoteServerConfigHomeURL+SERVERS_PREFIX+serverID+SERVERS_SUFFIX;
                 String newConfig = FileTools.getTextFileFromURL( fileURL );

                 if( newConfig ==null ) {
                     Debug.signal( Debug.ERROR, this, "Failed to get new Server "+serverID+" config. Reverting to previous one.");
                     continue;
                 }

                 String newAdr = FileTools.getTextFileFromURL( fileURL+SERVERS_ADDRESS_SUFFIX );
                 newAdr = checkAddressFormat( newAdr );

                 if( newAdr.length()==0 ) {
                     Debug.signal( Debug.ERROR, this, "Failed to get new Server "+serverID+" address. Reverting to previous one.");
                     continue;
                 }

                 config = createServerConfig( newConfig, newAdr, serverID );

                 if( config!=null ){
                    // We save it in our table
                       Debug.signal( Debug.NOTICE, this, "Retrieved successfully new server ("+serverID+") config.");
                       addConfig( config );
                 }
             }

            // going to next line of the server table...
         }

         if(parent!=null)
            pMonitor.close();
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To add a new ServerConfig to our list.
    * @param newConfig config to add
    */
      protected void addConfig( ServerConfig newConfig ) {
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
     protected ServerConfig findServerConfig( int serverID ) {
     
         if(configs==null)
            return null;
     
         for( int i=0; i<configs.length; i++ )
             if( configs[i].getServerID()==serverID )
                 return configs[i];

         return null;
     }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To get the following server ID.
     * @param previousServerID the previous ID we tried
     * @return the immediately following server ID, -1 if there is none
     */
     public int getNextServerID(int previousServerID) {

          if(configs==null || configs.length==0) return -1;

          int min=-1;

       // Search the minimum
          for( int i=0; i<configs.length; i++ ) {
             int id = configs[i].getServerID();

             if( id>previousServerID && (id<min || min==-1) )
                 min=id;
          }
          
          return min;
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
  
  /** To get a ServerConfig file from its index in the array <b>configs</b>.
   *
   * @param index server index.
   */
   public ServerConfig serverConfigAt(int index) {
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

        String fileURL = remoteServerConfigHomeURL+SERVERS_PREFIX+currentConfig.getServerID()+SERVERS_SUFFIX;

     // We load the address file
        String newAdr = FileTools.getTextFileFromURL( fileURL+SERVERS_ADDRESS_SUFFIX );
        newAdr = checkAddressFormat(newAdr); // we check the format

        if( newAdr.length()==0 ) {
            Debug.signal( Debug.ERROR, this, "Failed to get new Server "+currentConfig.getServerID()+" address. Reverting to previous one.");
            return null;
        }

        if( !updateServerConfig( null, newAdr, currentConfig ) )
            Debug.signal(Debug.ERROR,this,"For some reason we failed to save the new address...");

        return newAdr; // new address the user can try...
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Check the format of a DNS name or IP address.
   *  @param adr address to check
   *  @return the address without any ending special chars.
   */
    public String checkAddressFormat( String adr ) {
    	 if(adr==null)
    	    return "";
    	
      // We search for bad chars at the end of the file.
      // the end chars must be either letters (end of a domain name fr, com, net etc...)
      // or numbers (end of an IP address).
         adr = adr.trim();

         for( int i=adr.length()-1; i!=0; i-- ) {
              char c = adr.charAt(i);
              
              if( ('a'<=c && c<='z') || ('A'<=c && c<='Z') ||
                  ('0'<=c && c<='9') || c=='.' )
                  return adr.substring(0,i+1);
         }
         
         return ""; // not a valid address !!!
    }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

                      /******** PERSISTENCE METHODS **********/

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Loads the server config associated to the given serverID.
   *
   * @param serverID id of the server which config is to be loaded.
   * @return server config
   */
   public ServerConfig loadServerConfig( int serverID ) {

        String serverFile = rManager.getExternalServerConfigsDir()
                                      +SERVERS_PREFIX+serverID+SERVERS_SUFFIX;

        ServerConfig config = (ServerConfig) rManager.loadObject( serverFile );
        String serverAdr = rManager.loadText( serverFile+SERVERS_ADDRESS_SUFFIX );
        serverAdr = checkAddressFormat( serverAdr );

        if( config==null || serverAdr.length()==0 ) {
            Debug.signal( Debug.ERROR, this, "Failed to load server config: "
                                   +"no valid server config"+serverID+" files found !");
            return null;
        }
          
        config.setServerName( serverAdr );
        return config;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Saves the server config to the SERVERS_HOME directory.
   *
   *  @param serverConfig server config
   *  @return true in case of success, false if an error occured.
   */
   public boolean saveServerConfig( ServerConfig serverConfig ) {

        String serverFile = rManager.getExternalServerConfigsDir()
                            +SERVERS_PREFIX+serverConfig.getServerID()+SERVERS_SUFFIX;

        if( !rManager.saveObject( serverConfig, serverFile ) || ( serverConfig.getServerName()!=null &&
            !rManager.saveText( serverFile+SERVERS_ADDRESS_SUFFIX, serverConfig.getServerName() ) ) ) {
            Debug.signal( Debug.ERROR, this, "Failed to save server config: "
                         +"couldn't save "+serverFile+SERVERS_ADDRESS_SUFFIX+" file !");
            return false;
        }

       return true;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Updates a server config of the SERVERS_HOME directory.
   *  The oldServerConfig fields are updated with the new ones.
   *  The newConfigText paramter can be null : we then only save the new address.
   *
   *  @param newConfigText new config loaded from an URL. Can be null.
   *  @param newAdrText new server address loaded from an URL
   *  @param oldServerConfig server config
   *  @return true in case of success, false if an error occured.
   */
   public boolean updateServerConfig( String newConfigText, String newAdrText, ServerConfig oldServerConfig ) {

        String serverFile = rManager.getExternalServerConfigsDir()
                              +SERVERS_PREFIX+oldServerConfig.getServerID()+SERVERS_SUFFIX;

        if( newConfigText!=null && !rManager.saveText( serverFile, newConfigText ) ) {
            Debug.signal( Debug.ERROR, this, "Failed to update server config: "
                                              +"failed to save "+serverFile+" file !");
            return false;
        }

        if( !rManager.saveText( serverFile+SERVERS_ADDRESS_SUFFIX, newAdrText ) ) {
            Debug.signal( Debug.ERROR, this, "Failed to update server config: "
                                   +"failed to save "+serverFile+SERVERS_ADDRESS_SUFFIX+" file !");
            return false;
        }

     // We load the newly saved config...
        if( newConfigText!=null ) {
            ServerConfig newConfig = (ServerConfig) rManager.loadObject( serverFile );

            if( newConfig.getServerID()!= oldServerConfig.getServerID() ) {
                Debug.signal( Debug.ERROR, this, "Failed to update server config: "
                            +"new Config was not saved: it hasn't the expected Server ID !");
                saveServerConfig( oldServerConfig );
                return false;
            }

            newConfig.setServerName( newAdrText );
            oldServerConfig.update( newConfig );
        }
        else
            oldServerConfig.setServerName( newAdrText );

      return true;
   }

 /*------------------------------------------------------------------------------------*/

  /** To create a server config from a text file representing a previously saved ServerConfig.
   *
   *  @param newConfigText new config loaded from an URL.
   *  @param newAdrText new server address loaded from an URL
   *  @param serverID server Id
   *  @return the created ServerConfig in case of success, null if an error occured.
   */
   public ServerConfig createServerConfig( String newConfigText, String newAdrText, int serverID ) {

        String serverFile = rManager.getExternalServerConfigsDir()
                                            +SERVERS_PREFIX+serverID+SERVERS_SUFFIX;

        File serversHomeDir = new File( rManager.getExternalServerConfigsDir() );

        if(!serversHomeDir.exists()) {
            serversHomeDir.mkdir();
            Debug.signal(Debug.WARNING,this,"Server configs dir was not found. Created dir...");
        }

        if( !rManager.saveText( serverFile, newConfigText ) )
            return null; // Save Failed

        if( !rManager.saveText( serverFile+SERVERS_ADDRESS_SUFFIX, newAdrText ) ) {
            new File(serverFile).delete();
            return null; // Save Failed
        }

        return loadServerConfig(serverID); // We load the newly saved config...
   }

 /*------------------------------------------------------------------------------------*/

  /** Loads all the server config files found in SERVERS_HOME.
   *
   * @param serverID id of the server which config is to be loaded.
   * @return server config
   */
   public ServerConfig[] loadServerConfigs() {

      String serversHome = rManager.getExternalServerConfigsDir();

      File serversHomeDir = new File(serversHome);

      if(!serversHomeDir.exists()) {
      	 serversHomeDir.mkdir();
      	 Debug.signal(Debug.WARNING,this,"Server configs dir was not found. Created dir...");
      	 return null;
      }

      File configFileList[] = serversHomeDir.listFiles();

      if(configFileList==null) {
      	 Debug.signal(Debug.CRITICAL,this,"No server file loaded...");
      	 return null;
      }

     // We count how many server config files we have...
       int nbFiles=0;

        for( int i=0; i<configFileList.length; i++ )
           if(configFileList[i].isFile() && configFileList[i].getName().endsWith(SERVERS_SUFFIX) )
              nbFiles++;
       
     // create ServerConfig array
        if(nbFiles==0)
           return null;

        ServerConfig configList[] = new ServerConfig[nbFiles];
        int index=0;

        for( int i=0; i<configFileList.length; i++ )
           if(configFileList[i].isFile() && configFileList[i].getName().endsWith(SERVERS_SUFFIX) ){

               String serverFile = serversHome + configFileList[i].getName();
               configList[index] = (ServerConfig) rManager.loadObject( serverFile );

               if(configList[index]==null) {
                  Debug.signal(Debug.ERROR, this, "Failed to load "+serverFile);
                  index++;
                  continue;
               }

               String serverName = rManager.loadText( serverFile+SERVERS_ADDRESS_SUFFIX );
               serverName = checkAddressFormat(serverName);

               if( serverName.length()==0 )
                   Debug.signal( Debug.ERROR, this, "Failed to load server config: no"
                                 +serverFile+SERVERS_ADDRESS_SUFFIX+" file found !");

               configList[index].setServerName( serverName );
               configList[index].clearLastUpdateTime(); // clear timestamp set by this operation
               index++;
           }

        return configList;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}
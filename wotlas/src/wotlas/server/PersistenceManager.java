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

import wotlas.common.*;
import wotlas.common.universe.*;

import wotlas.libs.persistence.*;
import wotlas.utils.Debug;
import wotlas.utils.FileTools;
import wotlas.utils.Tools;

import java.io.File;

 /** Persistence Manager for Wotlas Servers. The persistence manager is the central
  * class where are saved/loaded data for the game. Mainly, it deals with Game Accounts
  * and World data ( wotlas.common.universe ).
  *
  * @author Aldiss
  * @see wotlas.libs.persistence.PropertiesConverter
  */
 
public class PersistenceManager extends wotlas.common.PersistenceManager
{
 /*------------------------------------------------------------------------------------*/

  /** Client Accounts
   */
   public final static String CLIENT_PROFILE = "profile.cfg";
   public final static String ACCOUNTS_HOME  = "home";
   public final static String PLAYER_PREFIX  = "player-save-";
   public final static String PLAYER_SUFFIX  = ".cfg";

 /*------------------------------------------------------------------------------------*/

   /** Our Default PersistenceManager.
    */
      private static wotlas.server.PersistenceManager persistenceManager;

 /*------------------------------------------------------------------------------------*/

   /** Maximum number of save in a client account. If this number is reached we delete
    *  the oldest entry.
    */
      public final static int MAX_NUMBER_OF_SAVE = 5;

 /*------------------------------------------------------------------------------------*/
  
  /** Constructor.
   *
   * @param databasePath path to the local server database
   */
   private PersistenceManager( String databasePath ) {
          super(databasePath);
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** Creates a persistence manager.
   *
   * @param databasePath path to the local server database
   * @return the created (or previously created) persistence manager.
   */
   public static wotlas.server.PersistenceManager createPersistenceManager( String databasePath ) {
         if( persistenceManager == null )
             persistenceManager = new wotlas.server.PersistenceManager( databasePath );
         
         return persistenceManager;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the default persistence manager.
   *
   * @return the default persistence manager.
   */
   public static wotlas.server.PersistenceManager getDefaultPersistenceManager() {
         return persistenceManager;
   }

 /*------------------------------------------------------------------------------------*/

  /** To load all the client Accounts. Warning: the returned array can have null entries.
   *
   * @return all the client accounts found in the databasePath+"/"+ACCOUNTS_HOME
   */
   public GameAccount[] loadAccounts() 
   {
      String accountHome =  databasePath+File.separator+ACCOUNTS_HOME;
      File accountList[] = new File( accountHome ).listFiles();

     // no accounts ?
        if( accountList==null ) {
           Debug.signal( Debug.WARNING, this, "No accounts found in: "+accountHome );
           return null;
        }

     // We load the different accounts...
        GameAccount accounts[] = new GameAccount[accountList.length];

        for( int i=0; i<accountList.length; i++ )
        {
           if( !accountList[i].isDirectory() || accountList[i].getName().indexOf('-')<0 )
               continue;

           try
           {
             // we load the client's profile
                accounts[i] = (GameAccount) PropertiesConverter.load( accountHome + File.separator
                                              + accountList[i].getName() + File.separator
                                              + CLIENT_PROFILE );

             // we now search for the latest saved player file.
                String latest = FileTools.findSave( accountHome + File.separator
                                                          + accountList[i].getName(),
                                                          PLAYER_PREFIX, PLAYER_SUFFIX, true );

              // have we found the latest saved file ?
                 if( latest==null ) {
                  // nope, then it's an invalid account, we delete it...
                     Debug.signal( Debug.ERROR, this, "Failed to load account: "
                                     + accountHome + File.separator
                                     + accountList[i].getName() );
                     accounts[i] = null;
                     continue;
                 }

                 PlayerImpl player = (PlayerImpl) PropertiesConverter.load( accountHome + File.separator
                                              + accountList[i].getName() + File.separator
                                              + latest );

              accounts[i].setPlayer( player );
           }
           catch( PersistenceException pe ) {
              Debug.signal( Debug.ERROR, this, "Failed to load account: "
                            + accountHome + File.separator
                            + accountList[i].getName() +"\n Message:"+pe.getMessage() );
              accounts[i] = null;
           }
        }

      return accounts;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To save a client Account. Deletes the oldest entry if the account has too many.
   *
   * @param account client account
   * @return true if the account has been saved succesfully.
   */
   public boolean saveAccount( GameAccount account ) 
   {
      String accountHome =  databasePath+File.separator+ACCOUNTS_HOME;

      try{
            PropertiesConverter.save( account.getPlayer(),
                                    accountHome + File.separator
                                    + account.getAccountName() + File.separator
                                    + PLAYER_PREFIX+Tools.getLexicalDate()+PLAYER_SUFFIX );

         // ok, the save went ok... do we have to erase the oldest file ?
            File accountFiles[] = new File( accountHome + File.separator
                                            + account.getAccountName() ).listFiles();

            if( accountFiles.length > MAX_NUMBER_OF_SAVE+1 )
            {
               // ok, let's delete the oldest save	
                  String oldest = FileTools.findSave( accountHome + File.separator
                                                      + account.getAccountName(),
                                                      PLAYER_PREFIX, PLAYER_SUFFIX, false );

              // have we found the latest saved file ?
                 if( oldest!=null ) {
                 	
                    if( oldest.equals(PLAYER_PREFIX+Tools.getLexicalDate()+PLAYER_SUFFIX) )
                        return true;  // it's the file we've just saved, so we don't delete it

                    String oldestpath = accountHome + File.separator + account.getAccountName()
                                        + File.separator + oldest;

                    if( new File( oldestpath ).delete() )
                        return true;
                 }
                      
                 Debug.signal( Debug.WARNING, this, "Failed to find old account entry : "
                                     + oldest );
            }
      }
      catch( PersistenceException pe ) {
          Debug.signal( Debug.ERROR, this, "Failed to save account: "
                            + accountHome + File.separator
                            + account.getAccountName() +"\n Message:"+pe.getMessage() );
          return false;
      }
      
      return true;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To create a client Account.
   *
   * @param account client account
   * @return true if the account has been created succesfully.
   */
   public boolean createAccount( GameAccount account ) 
   {
      String accountHome =  databasePath+File.separator+ACCOUNTS_HOME;
      File accountDir = new File( accountHome+File.separator+account.getAccountName() );

      if( !accountDir.mkdir() ) {
          Debug.signal( Debug.ERROR, this, "Failed to create account: "
                            + accountDir.getName() +"\n Account already exists!" );
          return false;
      }

    // we create the client's "profile.cfg"
      try{
          PropertiesConverter.save( account,
                                    accountHome + File.separator
                                    + account.getAccountName() + File.separator
                                    + CLIENT_PROFILE );
      }
      catch( PersistenceException pe ) {
          Debug.signal( Debug.ERROR, this, "Failed to save account: "
                            + accountHome + File.separator
                            + account.getAccountName() +"\n Message:"+pe.getMessage() );
          if( !deleteAccount( account.getAccountName() ) )          
               Debug.signal( Debug.WARNING, this, "Failed to delete bad account" );

          return false;
      }

    // we save the player data in something like "player-save-2001-09-23.cfg"
       if( !saveAccount( account ) ) {
           if( !deleteAccount( account.getAccountName() ) )          
              Debug.signal( Debug.WARNING, this, "Failed to delete bad account" );

           return false;
       }
       
       return true;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To delete a client Account.
   *
   * @param accountName account name, use GameAccount.getAccountName().
   * @return true if the account has been deleted succesfully.
   */
   public boolean deleteAccount( String accountName ) 
   {
      String accountHome =  databasePath+File.separator+ACCOUNTS_HOME;
      File accountDir = new File( accountHome+File.separator+accountName );

      // account doesnot exists
         if( !accountDir.exists() )
             return true;
     
         File list[] = accountDir.listFiles();
      
      // we delete the account's file
         if( list!=null )
             for( int i=0; i<list.length; i++ )
                  if( !list[i].delete() )
                      return false;

      return accountDir.delete();
   }

 /*------------------------------------------------------------------------------------*/

}

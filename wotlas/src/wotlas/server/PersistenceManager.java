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

import wotlas.libs.persistence.*;

 /** Persistence Manager for Wotlas Servers.
  *
  * @author Aldiss
  * @see wotlas.libs.persistence.PropertiesConverter
  */
 
public class PersistenceManager
{
 /*------------------------------------------------------------------------------------*/

   /** Maximum number of save in a client account. If this number is reached we delete
    *  the oldest entry.
    */
      public final static int MAX_NUMBER_OF_SAVE = 5;
 
   /** Our Default PersistenceManager.
    */
      private static PersistenceManager persistenceManager;

 /*------------------------------------------------------------------------------------*/

   /** Path to the local server database.
    */
      private String databasePath;

 /*------------------------------------------------------------------------------------*/
  
  /** Constructor.
   *
   * @param databasePath path to the local server database
   */
   private PersistenceManager( String databasePath ) {
          this.databasePath = databasePath;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
  
  /** Creates a persistence manager.
   *
   * @param databasePath path to the local server database
   * @return the created (or previously created) persistence manager.
   */
   public static PersistenceManager createPersistenceManager( String databasePath ) {
         if( persistenceManager == null )
             persistenceManager = new PersistenceManager( String databasePath );
         
         return persistenceManager;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To get the default persistence manager.
   *
   * @return the default persistence manager.
   */
   public static PersistenceManager getDefaultPersistenceManager() {
         return persistenceManager;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To load the client Accounts
   *
   * @return client accounts...
   */
   public GameAccount[] loadAccounts() 
   {
      String accountHome =  databasePath+File.separator+"home";
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
           if( !accountList[i].isFile() )
               continue;

           try
           {
             // we load the client's profile
                accounts[i] = PropertiesConverter.load( accountHome + File.separator
                                              + accountList[i].getName() + File.separator
                                              + "profile.cfg" );

             // we now search for the latest saved player file.
                String latest = FileTools.findSave( accountHome + File.separator
                                                          + accountList[i].getName(),
                                                          "player-save", ".cfg", true );

              // have we found the latest saved file ?
                 if( latest==null ) {
                  // nope, then it's an invalid account, we delete it...
                     Debug.signal( Debug.ERROR, this, "Failed to load account: "
                                     + accountHome + File.separator
                                     + accountList[i].getName() );
                     accounts[i] = null;
                     continue;
                 }

                 PlayerImpl player = PropertiesConverter.load( accountHome + File.separator
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
      String accountHome =  databasePath+File.separator+"home";

      try{
            PropertiesConverter.save( account.getPlayer(),
                                    accountHome + File.separator
                                    + account.getAccountName() + File.separator
                                    + "player-save-"+Tools.getLexicalDate()+".cfg" );

         // ok, the save went ok... do we have to erase the oldest file ?
            File accountFiles[] = new File( accountHome + File.separator
                                            + account.getAccountName() ).listFiles();

            if( accountFiles > MAX_NUMBER_OF_SAVE+1 )
            {
               // ok, let's delete the oldest save	
                  String oldest = FileTools.findSave( accountHome + File.separator
                                                      + account.getAccountName(),
                                                      "player-save", ".cfg", false );

              // have we found the latest saved file ?
                 if( oldest!=null ) {

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
      String accountHome =  databasePath+File.separator+"home";
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
                                    + "profile.cfg" );
      }
      catch( PersistenceException pe ) {
          Debug.signal( Debug.ERROR, this, "Failed to save account: "
                            + accountHome + File.separator
                            + account.getAccountName() +"\n Message:"+pe.getMessage() );
          return false;
      }

    // we save the player data in something like "player-save-2001-09-23.cfg"
       if( !saveAccount( account ) ) {
           if( !deleteAccount( account ) )          
              Debug.signal( Debug.WARNING, this, "Failed to delete bad account" );

           return false;
       }
       
       return true;
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To delete a client Account.
   *
   * @param account client account
   * @return true if the account has been deleted succesfully.
   */
   public boolean deleteAccount( GameAccount account ) 
   {
      String accountHome =  databasePath+File.separator+"home";
      File accountDir = new File( accountHome+File.separator+account.getAccountName() );

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

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

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

import wotlas.libs.wizard.*;
import wotlas.utils.Debug;

import java.util.Hashtable;
import java.io.*;

/** A factory that stores and builds the JWizardStepParameters objects. It is used by
 *  the AccountBuilder.<br>
 *
 *  This factory is thread safe.
 *
 * @author Aldiss
 */

public class AccountStepFactory {

 /*------------------------------------------------------------------------------------*/

  /** Name of the Wizard home in the database.
   */
    public static final String WIZARD_HOME = "wizard";

  /** Name of the first step to be started by the AccountBuilder. This step must always
   *  exist.
   */
    public static final String FIRST_STEP = "index.wiz";

 /*------------------------------------------------------------------------------------*/

  /** Our JWizardStep Buffer where we store static JWizard steps.
   *  The hashtable's key is the file name that contains the persistent version
   *  of the JWizardStepParameters.
   */
   private Hashtable staticStepParameters;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. We load
   * @param databasePath path to the database where all kind of data is stored.
   */
   public AccountStepFactory(String databasePath) {
         staticStepParameters = new Hashtable(20);

         String accountWizardHome = databasePath+File.separator+WIZARD_HOME;

      // We load all the step parameters in our hashtable
         File list[] = new File(accountWizardHome).listFiles();
         
         if(list==null) {
            Debug.signal(Debug.CRITICAL,this,"Failed to load account wizard steps !");
            return;
         }

         int nbSteps=0;

         for( int i=0; i<list.length; i++ ) {
            if(!list[i].isFile() || !list[i].getName().endsWith(".wiz") )
               continue;
            
            JWizardStepParameters parameters = JWizardStepParameters.loadFromFile(
                                  accountWizardHome+File.separator+list[i].getName() );

            if(parameters==null) {
               Debug.signal(Debug.ERROR,this,"Failed to load wizard step "+list[i].getName());
               continue;
            }            

            staticStepParameters.put( list[i].getName(), parameters );
            nbSteps++;
         }

        Debug.signal( Debug.NOTICE, null, "Loaded "+nbSteps+" account wizard steps...");
   }

 /*------------------------------------------------------------------------------------*/

  /** To get a JWizardStepParameters object from the file name of its persistent state.
   *
   *  @param stepFileName file name
   *  @return the wanted JWizardStepParameters instance, null if we failed to retrieve it.
   */
   public JWizardStepParameters getStep(String stepFileName) {

     // Class already in our buffer ?
        if( staticStepParameters.containsKey(stepFileName) )
            return (JWizardStepParameters) staticStepParameters.get(stepFileName);
     
        return null; // not found
   }

 /*------------------------------------------------------------------------------------*/

}

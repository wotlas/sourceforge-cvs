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

package wotlas.libs.wizard.step;

import wotlas.libs.wizard.*;
import wotlas.utils.aswing.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


/** A JWizard for testing wizard steps. Just modify the JWizarStepParameters code
 *  to load the appropriate JWizardStep.
 *
 * @author Aldiss
 * @see wotlas.libs.wizard.JWizardStep
 */

public class JWizardTestingBench extends JWizard {

 /*------------------------------------------------------------------------------------*/

  /** ID for step selection
   */
   private int stepID;

 /*------------------------------------------------------------------------------------*/

  /** Called when wizard is finished (after last step's end).
   */
   protected void onFinished(Object context) {
   	new JWizardTestingBench(stepID+1);
   }

  /** Called when wizard is canceled ('cancel' button pressed).
   */
   protected void onCanceled(Object context) {
   	System.exit(0);
   }

 /*------------------------------------------------------------------------------------*/

  /** Constructor of a wizard. We call the initSteps() method. We don't call any
   *  show() or setVisible().
   *
   * @param title wizard title
   * @param width wizard width
   * @param height wizard height
   */
   public JWizardTestingBench( int stepID ) {

        super("Wizard Testing Bench",420,410);
        setLocation(200,100);
        this.stepID = stepID;

        JWizardStepParameters parameters = new JWizardStepParameters();

     /**
      **  Modify the code below to display the step you want :
      **/
       
        switch ( stepID ) {

           case 0 :  /* STEP INFO */

           parameters.setStepClass("wotlas.libs.wizard.step.JWizardStepInfo");
           parameters.setStepTitle("Information Step (JWizardStepInfo)");
           parameters.setProperty("init.info0", "Please note this important information : "
                                           +"This important information you will please note : "
                                           +"Please note this important information and of course "
                                           +"this important information please note : one two three four\nfive six "
                                           +"that's it I said it !"
                                           +"\n\nIf you can see this it means the step was successfull!"
                                  );
           break;

           case 1 :  /* STEP 1 TEXT FIELD */

           parameters.setStepClass("wotlas.libs.wizard.step.JWizardStep1TextField");
           parameters.setStepTitle("Information Input Step (JWizardStep1TextField)");

           parameters.setProperty("init.label0", "Kyzophrenic Proton :");
           parameters.setProperty("init.text0", "krypton A");

           parameters.setProperty("init.info0", "\nPlease note these fields are very important and should "
                                           +"not be set without a good understanding of neutronic computing.");
           break;

           case 2 : /* STEP 2 TEXT FIELD */

           parameters.setStepClass("wotlas.libs.wizard.step.JWizardStep2TextField");
           parameters.setStepTitle("Information Input Step (JWizardStep2TextField)");

           parameters.setProperty("init.label0", "Kyzophrenic Proton :");
           parameters.setProperty("init.text0", "krypton A");

           parameters.setProperty("init.label1", "Triple Recursive Quanta :");
           // no default value for text field1

           parameters.setProperty("init.info0", "\nPlease note these fields are very important and should "
                                           +"not be set without a good understanding of neutronic computing.");
           break;

           case 3 : /* STEP COMBO BOX */

           parameters.setStepClass("wotlas.libs.wizard.step.JWizardStepComboBox");
           parameters.setStepTitle("Information Choice Step (JWizardStepComboBox)");
 
           parameters.setProperty("init.label0", "Kyzophrenic Proton :");

           parameters.setProperty("init.nbChoices", "4");
           parameters.setProperty("init.choice0", "Proton");
           parameters.setProperty("init.choice1", "Neutron");
           parameters.setProperty("init.choice2", "Krypton");
           parameters.setProperty("init.choice3", "Trypton");

           parameters.setProperty("init.info0", "\nPlease note these fields are very important and should "
                                              +"not be set without a good understanding of neutronic computing.");
           break;

           case 4 : /* STEP LIST */

           parameters.setStepClass("wotlas.libs.wizard.step.JWizardStepList");
           parameters.setStepTitle("Information Choice Step (JWizardStepList)");

           parameters.setProperty("init.label0", "Kyzophrenic Proton vs Psychotic Deuterium :");

           parameters.setProperty("init.nbChoices", "4");
           parameters.setProperty("init.choice0", "Proton");
           parameters.setProperty("init.choice1", "Neutron");
           parameters.setProperty("init.choice2", "Krypton");
           parameters.setProperty("init.choice3", "Trypton Titan Jupiter");

           parameters.setProperty("init.info0", "\nPlease note these fields are very important and should "
                                              +"not be set without a good understanding of neutronic computing.");
           break;

           case 5 :  /* STEP PASSWORD */

           parameters.setStepClass("wotlas.libs.wizard.step.JWizardStepPassword");
           parameters.setStepTitle("Login & Password Step (JWizardStepPassword)");

           parameters.setProperty("init.info0", "\nPlease note these fields are very important and should "
                                           +"not be set without a good understanding of neutronic computing.");
           break;

           case 6 : /* STEP TEXT AREA */

           parameters.setStepClass("wotlas.libs.wizard.step.JWizardStepTextArea");
           parameters.setStepTitle("Story Input Step (JWizardStepTextArea)");

           parameters.setProperty("init.info0", "Please note these fields are very important and should "
                                           +"not be set without a good understanding of neutronic computing.");

           parameters.setProperty("init.text0", "My Kyzophrenic Proton !");
           parameters.setProperty("init.option0", "kryptonit forever"); // without this line no checkbox is created

           break;

           case 7 : /* STEP COMBO BOX */

           parameters.setStepClass("wotlas.libs.wizard.step.JWizardStepRadio");
           parameters.setStepTitle("Information Choice Step (JWizardStepRadio)");
 
           parameters.setProperty("init.label0", "Your Kyzophrenic possibilities :");

           parameters.setProperty("init.nbChoices", "3");
           parameters.setProperty("init.choice0", "Protons forever young");
           parameters.setProperty("init.choice1", "Neutrons incrementaly aging");
           parameters.setProperty("init.choice2", "Neutrinos shining every day");

           parameters.setProperty("init.info0", "Please note these fields are very important and should "
                                              +"not be set without a good understanding of neutronic computing.");
           parameters.setProperty("init.info1", "Try important things and should "
                                              +"not be set without a good understanding of neutronic computing.");
           parameters.setProperty("init.info2", "This really should "
                                              +"not be set without a good understanding of this: 21=12.");
           break;

           default:
                 System.exit(0);
        }

     /**
      **  End of your code modification.
      **/

        parameters.setIsPrevButtonEnabled(false);
        parameters.setIsLastStep(true);

        try{
            init( parameters );
        }
        catch( WizardException we ) {
            we.printStackTrace();
            System.exit(1); // init failed !
        }
  }

 /*------------------------------------------------------------------------------------*/

  /** Main for starting the bench
   */
   public static void main(String argv[]) {
   	new JWizardTestingBench(0);
   }

 /*------------------------------------------------------------------------------------*/

}
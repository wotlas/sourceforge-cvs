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

package wotlas.libs.wizard.step;

import wotlas.common.message.account.*;
import wotlas.libs.net.NetConnection;
import wotlas.libs.wizard.*;
import wotlas.libs.aswing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** A step of a wizard with a ALabel, ATextField, ATextArea (info)<br>
 *
 * Note that onShow onNext and onPrevious are not abstract anymore but their
 * implementation does nothing.
 *
 *  IMPORTANT :<br>
 *  
 *  We need some properties to initialize properly : ( see parameters.getProperty() ).<br>
 *  <pre>
 *    - "init.label0"       ( label for the first text field - MANDATORY )
 *    - "init.text0"        ( default text for the first text field - OPTIONAL )
 *    - "init.info0"        ( information text to display - OPTIONAL )
 *  </pre>
 *
 *  Optional properties are set to "" by default.
 *
 * @author Petrus
 * @see wotlas.libs.wizard.JWizardStep
 */

public class JWizardStep1TextFieldNet extends JWizardStep1TextField {

 /*------------------------------------------------------------------------------------*/

  /** Called each time the step is shown on screen.
   */
   protected void onShow(Object context, JWizard wizard) {
   }

  /** Called when the "Next" button is clicked.
   *  Use the wizard's setNextStep() method to set the next step to be displayed.
   *  @return return true to validate the "Next" button action, false to cancel it...
   */
   protected boolean onNext(Object context, JWizard wizard) {
    if( !super.onNext(context, wizard) )
      return false;
      
    JWizardStepParameters parameters = new JWizardStepParameters();
    //parameters.setStepClass(this.getClass().getName());

    parameters.setProperty("data.text0", getText0());
    
    NetConnection connection = (NetConnection) context;
    
    if( !connection.isConnected() ) {
       JOptionPane.showMessageDialog( null, "The account server seems to have shutdown !\nPlease cancel & restart this wizard later...", "Connection Closed", JOptionPane.ERROR_MESSAGE);
       return false;
    }

    connection.queueMessage(new AccountStepMessage(parameters)); 
    await();
     
    return true;
   }

  /** Called when Previous button is clicked.
   *  Use the wizard's setNextStep() method to set the next step to be displayed.
   *  @return return true to validate the "Previous" button action, false to cancel it...
   */
   protected boolean onPrevious(Object context, JWizard wizard) {
    NetConnection connection = (NetConnection) context;

    if( !connection.isConnected() ) {
       JOptionPane.showMessageDialog( null, "The account server seems to have shutdown !\nPlease cancel & restart this wizard later...", "Connection Closed", JOptionPane.ERROR_MESSAGE);
       return false;
    }

    connection.queueMessage(new PreviousStepMessage());
    await();
    
    return true;
   }

 /*------------------------------------------------------------------------------------*/
 
}

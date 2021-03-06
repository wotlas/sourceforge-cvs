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

import javax.swing.JOptionPane;
import wotlas.common.message.account.AccountStepMessage;
import wotlas.common.message.account.PreviousStepMessage;
import wotlas.libs.net.NetConnection;
import wotlas.libs.wizard.JWizard;
import wotlas.libs.wizard.JWizardStepParameters;

/** A step of a wizard with a ALabel, JRadioButton, AtextArea (associated
 * JRadioButton description).<br>
 *
 * Note that onShow onNext and onPrevious are not abstract anymore but their
 * implementation does nothing.
 *
 *  IMPORTANT :<br>
 *  
 *  We need some properties to initialize properly : ( see parameters.getProperty() ).<br>
 *  <pre>
 *    - "init.label0"      ( label for the combo box content - MANDATORY )
 *    - "init.nbChoices"  ( number of jradio button choices - MANDATORY )
 *
 *    - "init.choice0"             ( choice 0 : the text of the radio button - MANDATORY )
 *    - "init.choice1"             ( choice 1 : the text of the radio button - MANDATORY )
 *    - ...
 *    - "init.choice[nbChoices-1]" ( choice [nbChoices-1] : the text of the radio button - MANDATORY )
 *
 *    - "init.info0"             ( an optional information text for radio button 0 - OPTIONAL )
 *    - "init.info1"             ( an optional information text for radio button 1 - OPTIONAL )
 *    - ...
 *    - "init.info[nbChoices-1]" ( an optional information text for radio button [nbChoices-1] - OPTIONAL )
 *
 *  </pre>
 *
 *  Optional properties are set to "" by default.
 *
 * @author Aldiss
 * @see wotlas.libs.wizard.JWizardStep
 */

public class JWizardStepRadioNet extends JWizardStepRadio {

    /*------------------------------------------------------------------------------------*/

    /** Called each time the step is shown on screen.
     */
    @Override
    protected void onShow(Object context, JWizard wizard) {
    }

    /** Called when the "Next" button is clicked.
     *  Use the wizard's setNextStep() method to set the next step to be displayed.
     *  @return return true to validate the "Next" button action, false to cancel it...
     */
    @Override
    protected boolean onNext(Object context, JWizard wizard) {
        JWizardStepParameters parameters = new JWizardStepParameters();
        //parameters.setStepClass(this.getClass().getName());

        parameters.setProperty("data.choice", "" + getChoice());

        NetConnection connection = (NetConnection) context;

        if (!connection.isConnected()) {
            JOptionPane.showMessageDialog(null, "The account server seems to have shutdown !\nPlease cancel & restart this wizard later...", "Connection Closed", JOptionPane.ERROR_MESSAGE);
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
    @Override
    protected boolean onPrevious(Object context, JWizard wizard) {
        NetConnection connection = (NetConnection) context;

        if (!connection.isConnected()) {
            JOptionPane.showMessageDialog(null, "The account server seems to have shutdown !\nPlease cancel & restart this wizard later...", "Connection Closed", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        connection.queueMessage(new PreviousStepMessage());
        await();

        return true;
    }

    /*------------------------------------------------------------------------------------*/

}

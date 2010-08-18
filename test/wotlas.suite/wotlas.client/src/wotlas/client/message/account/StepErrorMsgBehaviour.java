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
package wotlas.client.message.account;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import wotlas.common.message.account.StepErrorMessage;
import wotlas.common.message.account.WishClientAccountNetMsgBehaviour;
import wotlas.libs.wizard.JWizard;
import wotlas.utils.Debug;

/**
 * Associated behaviour to the StepErrorMessage...
 *
 * @author Petrus
 */
public class StepErrorMsgBehaviour extends StepErrorMessage implements WishClientAccountNetMsgBehaviour {
    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public StepErrorMsgBehaviour() {
        super();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Associated code to this Message...
     *
     * @param sessionContext an object giving specific access to other objects needed to process
     *        this message.
     */
    public void doBehaviour(Object sessionContext) {

        // the sessionContext is here a JWizard
        JWizard wizard = (JWizard) sessionContext;

        wizard.awakeCurrentStep();
        SwingUtilities.invokeLater(new StepErrorJDisplay(wizard, this.info));
        Debug.signal(Debug.ERROR, null, "Step Error : " + this.info);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
    /** Class for message display. We use this class for async display.
     */
    public static class StepErrorJDisplay implements Runnable {

        private JWizard wizard;
        private String message;

        /** Empty constructor for NetMessageFactory (needed if we want an error display)
         */
        public StepErrorJDisplay() {
        }

        /** Constructor with parent wizard & message to display.
         */
        public StepErrorJDisplay(JWizard wizard, String message) {
            this.message = message;
            this.wizard = wizard;
        }

        /** Thread Action.
         */
        public void run() {
            JOptionPane.showMessageDialog(this.wizard, this.message, "Warning message!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}

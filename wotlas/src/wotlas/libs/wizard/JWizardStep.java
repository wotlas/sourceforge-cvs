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
 
package wotlas.libs.wizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** A step of the JWizard wizard. It's basically a JPanel that receives method calls
 *  when it is shown on screen and when one of the JWizard's button is pressed
 * ( "next" / "previous" ).
 *
 * All the parameters of this JWizardStep are aggregated by the JWizardStepParameters
 * which can be persistent or sent on a stream.
 *
 * To subclass this class, you'll need to define onShow(), onNext() and onPrevious().
 *
 * @author Petrus, Aldiss
 * @see wotlas.libs.wizard.JWizard
 */

public abstract class JWizardStep extends JPanel {

 /*------------------------------------------------------------------------------------*/

  /** JWizardStep parameters
   */
   protected JWizardStepParameters parameters;

 /*------------------------------------------------------------------------------------*/

  /*** ABSTRACT METHODS ***/

  /** Called each time the step is shown on screen.
   */
   protected abstract void onShow(Object context, JWizard wizard);

  /** Called when the "Next" button is clicked.
   *  Use the wizard's setNextStep() method to set the next step to be displayed.
   *  @return return true to validate the "Next" button action, false to cancel it...
   */
   protected abstract boolean onNext(Object context, JWizard wizard);

  /** Called when Previous button is clicked.
   *  Use the wizard's setNextStep() method to set the next step to be displayed.
   *  @return return true to validate the "Previous" button action, false to cancel it...
   */
   protected abstract boolean onPrevious(Object context, JWizard wizard);

 /*------------------------------------------------------------------------------------*/

  /** Empty Constructor.
   */
   public JWizardStep() {
       super();
       setBackground(Color.white);
   }

 /*------------------------------------------------------------------------------------*/

  /** Init method called to initilize this JWizardStep. You can redefine this method
   *  to add your JPanel's Swing components. Don't forget to call super.init(parameters);
   *
   * @param parameters parameters for this step
   * @exception thrown if the given parameters are wrong...
   */
   protected void init( JWizardStepParameters parameters ) throws WizardException {
      this.parameters = parameters;
   }

 /*------------------------------------------------------------------------------------*/

  /** To get the step's parameters.
   */
   protected JWizardStepParameters getParameters() {
   	return parameters;
   }

 /*------------------------------------------------------------------------------------*/

}
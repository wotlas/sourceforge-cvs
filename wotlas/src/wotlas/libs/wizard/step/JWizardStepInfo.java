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

import wotlas.libs.wizard.*;
import wotlas.utils.aswing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** A step of a wizard with a AtextArea (info).
 *
 * Note that onShow onNext and onPrevious are not abstract anymore but their
 * implementation does nothing.
 *
 *  IMPORTANT :<br>
 *  
 *  We need some properties to initialize properly : ( see parameters.getProperty() ).<br>
 *  <pre>
 *    - "init.info0"       ( information text to display - MANDATORY )
 *  </pre>
 *
 * @author Petrus
 * @see wotlas.libs.wizard.JWizardStep
 */

public class JWizardStepInfo extends JWizardStep {

  /** Swing components of this step
   */
   protected ATextArea tarea;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
  public JWizardStepInfo() {
    super();
    setBackground(Color.white);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(BorderFactory.createEmptyBorder(20,20,0,10));

    tarea = new ATextArea();
    tarea.setBackground(Color.white);
    tarea.setLineWrap(true);
    tarea.setWrapStyleWord(true);
    tarea.setEditable(false);
    tarea.setAlignmentX(LEFT_ALIGNMENT);  
    add(tarea);
      
    add(Box.createVerticalGlue());
  }

 /*------------------------------------------------------------------------------------*/

  /** Init method called to initilize this JWizardStep. You can redefine this method
   *  to add your JPanel's Swing components. Don't forget to call super.init(parameters);
   *
   * @param parameters parameters for this step
   * @exception thrown if the given parameters are wrong...
   */
   protected void init( JWizardStepParameters parameters ) throws WizardException {
        super.init(parameters);
      
     // 1 - We retrieve init properties
        String s_info = parameters.getProperty("init.info0");

     // 2 - We check the properties we have
        if(s_info==null)
           throw new WizardException("No information property found !");

     // 3 - We end the GUI init
        tarea.setText(s_info);
   }

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
   	return true;
   }

  /** Called when Previous button is clicked.
   *  Use the wizard's setNextStep() method to set the next step to be displayed.
   *  @return return true to validate the "Previous" button action, false to cancel it...
   */
   protected boolean onPrevious(Object context, JWizard wizard) {
   	return true;
   }

 /*------------------------------------------------------------------------------------*/

}

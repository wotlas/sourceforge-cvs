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
import java.awt.event.*;

import javax.swing.*;

/** A step of a wizard with a ALabel, ATextField, ALabel, ATextField, AtextArea (info)<br>
 *
 * Note that onShow onNext and onPrevious are not abstract anymore but their
 * implementation does nothing.
 *
 *  IMPORTANT :<br>
 *  
 *  We need some properties to initialize properly : ( see parameters.getProperty() ).<br>
 *  <pre>
 *    - "init.label0"      ( label for the first text field - MANDATORY )
 *    - "init.text0"       ( default text for the first text field - OPTIONAL )
 *    - "init.label1"      ( label for the second text field - MANDATORY )
 *    - "init.text1"       ( default text for the second text field - OPTIONAL )
 *    - "init.info0"        ( information text to display - OPTIONAL )
 *  </pre>
 *
 *  Optional properties are set to "" by default.
 *
 * @author Petrus
 * @see wotlas.libs.wizard.JWizardStep
 */

public class JWizardStep2TextField extends JWizardStep {

  /** Swing components of this step
   */
   private ALabel label0, label1;

   protected ATextField tfield0, tfield1;

   private ATextArea tarea;
   private JPanel formPanel;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
   public JWizardStep2TextField() {
      super();
      setBackground(Color.white);
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createEmptyBorder(20,20,0,20));
      
      formPanel = new JPanel(new GridLayout(2,2,5,5));
      formPanel.setBackground(Color.white);
        label0 = new ALabel();
        formPanel.add(label0);
        tfield0 = new ATextField(15);
        formPanel.add(tfield0);
        label1 = new ALabel();
        formPanel.add(label1);
        tfield1 = new ATextField(15);
        formPanel.add(tfield1);
      
      add(formPanel,BorderLayout.NORTH);
    
      tarea = new ATextArea();
      tarea.setBackground(Color.white);
      tarea.setLineWrap(true);
      tarea.setWrapStyleWord(true);
      tarea.setEditable(false);
      tarea.setAlignmentX(LEFT_ALIGNMENT);  
      add(tarea,BorderLayout.CENTER);
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
        String s_label0 = parameters.getProperty("init.label0");
        String s_text0  = parameters.getProperty("init.text0");
        String s_label1 = parameters.getProperty("init.label1");
        String s_text1  = parameters.getProperty("init.text1");
        String s_info   = parameters.getProperty("init.info0");

     // 2 - We check the properties we have
        if(s_label0==null || s_label1==null)
           throw new WizardException("No label property found !");

        if(s_text0==null) s_text0="";
        if(s_text1==null) s_text1="";
        if(s_info==null)  s_info="";

     // 3 - We end the GUI init
        label0.setText(s_label0);
        tfield0.setText(s_text0);
        label1.setText(s_label1);
        tfield1.setText(s_text1);

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
    if ( (tfield0.getText().length()==0) || (tfield1.getText().length()==0) ) {
      JOptionPane.showMessageDialog( null, "Please, fill the fields", "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
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

   /** To get the text entered in the first JTextField.
    */
   public String getText0() {
   	return tfield0.getText();
   }

 /*------------------------------------------------------------------------------------*/

   /** To get the text entered in the second JTextField.
    */
   public String getText1() {
   	return tfield1.getText();
   }

 /*------------------------------------------------------------------------------------*/

}

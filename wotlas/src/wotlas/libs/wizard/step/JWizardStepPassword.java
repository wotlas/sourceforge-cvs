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

/** A step of a wizard with a AtextArea (info), ALabel, ATextField,
 *  ALabel, APasswordTextField, ALabel, APasswordTextField <br>
 *
 * Note that onShow onNext and onPrevious are not abstract anymore but their
 * implementation does nothing apart onNext that checks your login & password length.
 *
 *  IMPORTANT :<br>
 *  
 *  We need some properties to initialize properly : ( see parameters.getProperty() ).<br>
 *  <pre>
 *    - "init.info0"        ( information text to display - OPTIONAL )
 *  </pre>
 *
 *  Optional properties are set to "" by default.
 *
 * @author Petrus
 * @see wotlas.libs.wizard.JWizardStep
 */

public class JWizardStepPassword extends JWizardStep {

  /** Swing components of this step
   */
   private ALabel label1, label2, label3;

   protected ATextField tfield1;
   protected APasswordField tfield2, tfield3;

   private ATextArea tarea;
   private JPanel formPanel;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   */
   public JWizardStepPassword() {
      super();
      setBackground(Color.white);
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createEmptyBorder(20,20,0,20));
      
      formPanel = new JPanel(new GridLayout(3,2,5,5));
      formPanel.setBackground(Color.white);
        label1 = new ALabel("Enter a login name :");
        formPanel.add(label1);

        tfield1 = new ATextField(15);
        formPanel.add(tfield1);

        label2 = new ALabel("Enter a password :");
        formPanel.add(label2);

        tfield2 = new APasswordField();
        formPanel.add(tfield2);

        label3 = new ALabel("Re-enter your password :");
        formPanel.add(label3);

        tfield3 = new APasswordField();
        formPanel.add(tfield3);
      
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
        String s_info   = parameters.getProperty("init.info0");

     // 2 - We check the properties we have
        if(s_info==null)  s_info="";

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
      String passwd = new String(tfield2.getPassword());
      String passwd2 = new String(tfield3.getPassword());

      char login[] = tfield1.getText().toCharArray();
      
      if(login.length>16) {
         JOptionPane.showMessageDialog( null, "Your login is too long ! (max 16 chars)", "Login", JOptionPane.ERROR_MESSAGE);
         return false;
      }

      if( login.length<3) {
          JOptionPane.showMessageDialog( null, "Your login must have at least 3 characters !", "Login", JOptionPane.ERROR_MESSAGE);
          return false;
      }

      for( int i=0; i<login.length; i++ )
           if( (login[i]<'a' || login[i]>'z') && (login[i]<'A' || login[i]>'Z') &&
               (login[i]<'0' || login[i]>'9') && login[i]!='-' ) {
               JOptionPane.showMessageDialog( null, "Sorry your login must only contain the following\ncharacters : 'a'-'z', 'A'-'Z', '0'-'9', '-'", "Login", JOptionPane.ERROR_MESSAGE);
               return false;
           }

      if( passwd.length() < 4 ) {
           JOptionPane.showMessageDialog( null, "Your password must have at least 5 characters !", "New Password", JOptionPane.ERROR_MESSAGE);
           return false;
      }

      if( !passwd.equals(passwd2) ) {
           JOptionPane.showMessageDialog( null, "Passwords are not equal !", "New Password", JOptionPane.ERROR_MESSAGE);
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

   /** To get the login entered in the JTextField.
    */
   public String getLogin() {
   	return tfield1.getText();
   }

 /*------------------------------------------------------------------------------------*/

   /** To get the password entered in the JPasswordField.
    */
   public String getPassword() {
   	return new String(tfield2.getPassword());
   }

 /*------------------------------------------------------------------------------------*/

}

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
 *    - "init.label"      ( label for the combo box content - MANDATORY )
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
 * @author Petrus
 * @see wotlas.libs.wizard.JWizardStep
 */

public class JWizardStepRadio extends JWizardStep implements ActionListener{

  /** Swing components of this step
   */
   private ALabel label1;

   protected ARadioButton buttons[];
   protected String aRadioInfo[];

   private ATextArea tarea;
   private JPanel formPanel;

   private ButtonGroup btGroup;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
   public JWizardStepRadio() {
      super();
      setBackground(Color.white);
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createEmptyBorder(20,20,0,20));

      label1 = new ALabel();
      add(label1,BorderLayout.NORTH);

        btGroup = new ButtonGroup();

      formPanel = new JPanel();
      formPanel.setAlignmentX(LEFT_ALIGNMENT);
      formPanel.setBackground(Color.white);      
      formPanel.setBorder(BorderFactory.createEmptyBorder(30,50,80,10));
      add(formPanel, BorderLayout.CENTER);

      tarea = new ATextArea();
      tarea.setBackground(Color.white);
      tarea.setLineWrap(true);
      tarea.setWrapStyleWord(true);
      tarea.setEditable(false);
      tarea.setAlignmentX(LEFT_ALIGNMENT);  
      add(tarea, BorderLayout.SOUTH);
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
        String s_label = parameters.getProperty("init.label");
        String s_nbChoices  = parameters.getProperty("init.nbChoices");

        String choices[] = null;

        try{
           int nb = Integer.parseInt(s_nbChoices);
           choices = new String[nb];
           aRadioInfo = new String[nb];
           buttons = new ARadioButton[nb];
        }
        catch(Exception e) {
           throw new WizardException("nbChoices property badly set! "+e.getMessage());
        }

     // 2 - We check the properties we have
        if(s_label==null)
           throw new WizardException("No label property found !");

        for( int i=0; i<choices.length; i++) {
             String choice = parameters.getProperty("init.choice"+i);

             if(choice==null)
                throw new WizardException("Property 'init.choice"+i+"' missing !");

             String s_info   = parameters.getProperty("init.info"+i);

             if(s_info==null) s_info="";

             choices[i] = choice;
             aRadioInfo[i] = s_info;
        }

     // 3 - We end the GUI init
        label1.setText(s_label);

        formPanel.setLayout( new GridLayout(choices.length,1,10,10) );

        for( int i=0; i<choices.length; i++) {
             buttons[i] = new ARadioButton(choices[i]);
             buttons[i].setActionCommand(""+i);
             buttons[i].setSelected(false);
             buttons[i].addActionListener(this);
             btGroup.add(buttons[i]);
             formPanel.add(buttons[i]);
        }

        if(choices.length>0) {
           buttons[0].setSelected(true);
           tarea.setText(aRadioInfo[0]);
        }
   }

 /*------------------------------------------------------------------------------------*/

   /** Action Performed when the user clicks a button.
    */
    public void actionPerformed(ActionEvent e) {

        int i=0;

        try{
           i = Integer.parseInt( e.getActionCommand() );
        }catch(Exception ex) {
          ex.printStackTrace();
          return;
	}

        if(i>=0) tarea.setText(aRadioInfo[i]);
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

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
import wotlas.libs.aswing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** A step of a wizard with a ALabel, JList, AtextArea (info).<br>
 *
 * Note that onShow onNext and onPrevious are not abstract anymore but their
 * implementation does nothing.
 *
 *  IMPORTANT :<br>
 *  
 *  We need some properties to initialize properly : ( see parameters.getProperty() ).<br>
 *  <pre>
 *    - "init.label0"      ( label for the JList content - MANDATORY )
 *    - "init.nbChoices"   ( number of JList choices - MANDATORY )
 *
 *    - "init.choice0"             ( choice 0 in the jlist - MANDATORY )
 *    - "init.choice1"             ( choice 1 in the jlist - MANDATORY )
 *    - ...
 *    - "init.choice[nbChoices-1]" ( choice [nbChoices-1] in the jlist - MANDATORY )
 *
 *    - "init.info0"       ( information text to display - OPTIONAL )
 *  </pre>
 *
 *  Optional properties are set to "" by default.
 *
 * @author Petrus, Aldiss
 * @see wotlas.libs.wizard.JWizardStep
 */

public class JWizardStepList extends JWizardStep {

  /** Swing components of this step
   */
   private ALabel label1;

   protected JList list;

   private ATextArea tarea;
   private JPanel formPanel;

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   */
   public JWizardStepList() {
      super();
      setBackground( Color.white );
      setLayout( new BorderLayout() );
      setBorder( BorderFactory.createEmptyBorder(20,20,0,20) );

        label1 = new ALabel();
        label1.setHorizontalAlignment( SwingConstants.CENTER );
        add( label1, BorderLayout.NORTH );

      formPanel = new JPanel( new GridLayout(1,1,10,2) );
      formPanel.setAlignmentX( LEFT_ALIGNMENT );
      formPanel.setBackground( Color.white );

        list = new JList();
        list.setCellRenderer( new AListCellRenderer() );
        list.setBorder( BorderFactory.createLineBorder( new Color(50,50,75) ) );
        list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        list.setAlignmentX( CENTER_ALIGNMENT );
        formPanel.add( list );
        formPanel.setBorder( BorderFactory.createEmptyBorder(10,80,10,80) );

      add( formPanel, BorderLayout.CENTER );

      tarea = new ATextArea();
      tarea.setBackground( Color.white );
      tarea.setLineWrap( true );
      tarea.setWrapStyleWord( true );
      tarea.setEditable( false );
      tarea.setAlignmentX( LEFT_ALIGNMENT );  
      add( tarea, BorderLayout.SOUTH );
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
        String s_label = parameters.getProperty("init.label0");
        String s_nbChoices  = parameters.getProperty("init.nbChoices");
        String s_info   = parameters.getProperty("init.info0");

        String choices[] = null;

        try{
           int nb = Integer.parseInt(s_nbChoices);
           choices = new String[nb];
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

             choices[i] = choice;
        }

        if(s_info==null)  s_info="";

     // 3 - We end the GUI init
        label1.setText(s_label);
        list.setListData(choices);
        
        if(choices.length!=0)
           list.setSelectedIndex(0);

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

   /** To get the selected choice : integer 0 to nbChoices, -1 if none selected.
    */
   public int getChoice() {
   	return list.getSelectedIndex();
   }

 /*------------------------------------------------------------------------------------*/

}

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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import wotlas.libs.aswing.ACheckBox;
import wotlas.libs.aswing.ATextArea;
import wotlas.libs.wizard.JWizard;
import wotlas.libs.wizard.JWizardStep;
import wotlas.libs.wizard.JWizardStepParameters;
import wotlas.libs.wizard.WizardException;

/** A step of a wizard with a ATextArea, ATextArea (editable), Checkbox <br>
 *
 * Note that onShow onNext and onPrevious are not abstract anymore but their
 * implementation does nothing.
 *
 *  IMPORTANT :<br>
 *  
 *  We need some properties to initialize properly : ( see parameters.getProperty() ).<br>
 *  <pre>
 *    - "init.info0"        ( information text to display first - MANDATORY )
 *    - "init.text0"        ( default text for the text area - OPTIONAL )
 *    - "init.option0"      ( optional checkbox text - OPTIONAL )
 *  </pre>
 *
 *  Optional properties are set to "" by default. If the text of the checkbox is not set
 *  the checkbox won't appear.
 *
 * @author Petrus
 * @see wotlas.libs.wizard.JWizardStep
 */

public class JWizardStepTextArea extends JWizardStep {

    /** Swing components of this step
     */
    private ATextArea tarea;

    protected ATextArea tareaInput;
    protected ACheckBox checkBox;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public JWizardStepTextArea() {
        super();
        setBackground(Color.white);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        this.tarea = new ATextArea();
        this.tarea.setBackground(Color.white);
        this.tarea.setLineWrap(true);
        this.tarea.setWrapStyleWord(true);
        this.tarea.setEditable(false);
        this.tarea.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(this.tarea, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.white);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        this.tareaInput = new ATextArea();
        this.tareaInput.setBackground(Color.white);
        this.tareaInput.setLineWrap(true);
        this.tareaInput.setWrapStyleWord(true);
        this.tareaInput.setEditable(true);
        this.tareaInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.tareaInput.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 75)));
        formPanel.add(this.tareaInput, BorderLayout.CENTER);
        add(formPanel, BorderLayout.CENTER);
    }

    /*------------------------------------------------------------------------------------*/

    /** Init method called to initilize this JWizardStep. You can redefine this method
     *  to add your JPanel's Swing components. Don't forget to call super.init(parameters);
     *
     * @param parameters parameters for this step
     * @exception thrown if the given parameters are wrong...
     */
    @Override
    protected void init(JWizardStepParameters parameters) throws WizardException {
        super.init(parameters);

        // 1 - We retrieve init properties
        String s_info = parameters.getProperty("init.info0");
        String s_text = parameters.getProperty("init.text0");
        String s_option = parameters.getProperty("init.option0");

        // 2 - We check the properties we have
        if (s_info == null)
            throw new WizardException("No information text property found !");

        if (s_text == null)
            s_text = "";

        // 3 - We end the GUI init
        if (s_option != null) {
            this.checkBox = new ACheckBox(s_option, false);
            add(this.checkBox, BorderLayout.SOUTH);
        }

        this.tarea.setText(s_info);
        this.tareaInput.setText(s_text);
    }

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
        return true;
    }

    /** Called when Previous button is clicked.
     *  Use the wizard's setNextStep() method to set the next step to be displayed.
     *  @return return true to validate the "Previous" button action, false to cancel it...
     */
    @Override
    protected boolean onPrevious(Object context, JWizard wizard) {
        return true;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the text entered in the JTextArea.
     */
    public String getText0() {
        return this.tareaInput.getText();
    }

    /*------------------------------------------------------------------------------------*/

    /** To get option0 state: true or false. If the checkBox doesn't exist we return false.
     */
    public boolean getOption0() {
        if (this.checkBox == null)
            return false;

        return this.checkBox.isSelected();
    }

    /*------------------------------------------------------------------------------------*/

}

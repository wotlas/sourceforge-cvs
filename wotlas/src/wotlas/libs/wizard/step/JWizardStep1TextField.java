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
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import wotlas.libs.aswing.ALabel;
import wotlas.libs.aswing.ATextArea;
import wotlas.libs.aswing.ATextField;
import wotlas.libs.wizard.JWizard;
import wotlas.libs.wizard.JWizardStep;
import wotlas.libs.wizard.JWizardStepParameters;
import wotlas.libs.wizard.WizardException;

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

public class JWizardStep1TextField extends JWizardStep {

    /** Swing components of this step
     */
    private ALabel label0;

    protected ATextField tfield0;

    private ATextArea tarea;
    private JPanel formPanel;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.
     */
    public JWizardStep1TextField() {
        super();
        setBackground(Color.white);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        this.formPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        this.formPanel.setBackground(Color.white);
        this.label0 = new ALabel();
        this.formPanel.add(this.label0);
        this.tfield0 = new ATextField(15);
        this.formPanel.add(this.tfield0);

        add(this.formPanel, BorderLayout.NORTH);

        this.tarea = new ATextArea();
        this.tarea.setBackground(Color.white);
        this.tarea.setLineWrap(true);
        this.tarea.setWrapStyleWord(true);
        this.tarea.setEditable(false);
        this.tarea.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(this.tarea, BorderLayout.CENTER);
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
        String s_label = parameters.getProperty("init.label0");
        String s_text = parameters.getProperty("init.text0");
        String s_info = parameters.getProperty("init.info0");

        // 2 - We check the properties we have
        if (s_label == null)
            throw new WizardException("No label property found !");

        if (s_text == null)
            s_text = "";
        if (s_info == null)
            s_info = "";

        // 3 - We end the GUI init
        this.label0.setText(s_label);
        this.tfield0.setText(s_text);
        this.tarea.setText(s_info);
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
        if (this.tfield0.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "Please, fill the fields", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
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

    /** To get the text entered in the first JTextField.
     */
    public String getText0() {
        return this.tfield0.getText();
    }

    /*------------------------------------------------------------------------------------*/

}

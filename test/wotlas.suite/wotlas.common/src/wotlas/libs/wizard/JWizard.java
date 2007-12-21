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

package wotlas.libs.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import wotlas.libs.aswing.ALabel;

/** A generic setup wizard that possesses buttons (previous/cancel/next) and displays
 *  a panel (JWizardStep) that can react when these buttons are pressed. <br>
 *  JWizard is abstract : the subclass must implement the method : onFinished() and
 *  onCanceled().
 *
 *  This class uses images found by using the provided WizardResourceLocator.
 *
 * @author Petrus, Aldiss
 * @see wotlas.libs.wizard.JWizardStep
 */

public abstract class JWizard extends JFrame {

    /*------------------------------------------------------------------------------------*/

    /** Where the images are stored.
     */
    protected WizardResourceLocator resourceLocator;

    /** The current wizard step
     */
    protected JWizardStep currentStep;

    /** The next wizard step
     */
    protected JWizardStep nextStep;

    /** Our factory for building JWizardStep.
     */
    protected JWizardStepFactory stepFactory;

    /** Title of the wizard
     */
    protected JPanel titlePanel;

    /** main panel of the wizard
     */
    protected JPanel mainPanel;

    /** panel of navigation buttons
     */
    protected JPanel buttonsPanel;

    /** title of the wizard
     */
    protected ALabel t_title;

    /** previous button
     */
    protected JButton b_previous;

    /** next button
     */
    protected JButton b_next;

    /** cancel button
     */
    protected JButton b_cancel;

    /** Context of the wizard.
     *  This object can be anything you want and is set by JWizard's subclass. 
     */
    protected Object context;

    /** pictures of the buttons
     */
    private ImageIcon im_okup, im_okdo, im_okun;
    private ImageIcon im_cancelup, im_canceldo, im_cancelun;
    private ImageIcon im_nextup, im_nextdo, im_nextun;
    private ImageIcon im_previousup, im_previousdo, im_previousun;

    /** Wizard Image
     */
    private Image wizardImage;

    /*------------------------------------------------------------------------------------*/

    /*** ABSTRACT METHODS ***/

    /** Called when wizard is finished (after last step's end).
     */
    protected abstract void onFinished(Object context);

    /** Called when wizard is canceled ('cancel' button pressed).
     */
    protected abstract void onCanceled(Object context);

    /*------------------------------------------------------------------------------------*/

    /** Constructor of a wizard. We call the initSteps() method. We don't call any
     *  show() or setVisible().
     *
     * @param title wizard title
     * @param resourceLocator gives access to the images we need
     * @param titleFont font to use for the title
     * @param width wizard width
     * @param height wizard height   
     */
    public JWizard(String title, WizardResourceLocator resourceLocator, Font titleFont, int width, int height) {
        super(title);
        this.resourceLocator = resourceLocator;

        this.stepFactory = new JWizardStepFactory();

        setSize(width + 100, height);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) ((screenSize.getWidth() - getWidth()) / 2), (int) ((screenSize.getHeight() - getHeight()) / 2));

        setBackground(Color.white);
        setIconImage(resourceLocator.getGuiImage("icon.gif"));

        JPanel wizardPanel = new JPanel();
        wizardPanel.setBackground(Color.white);
        wizardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        getContentPane().add(wizardPanel, BorderLayout.CENTER);

        this.titlePanel = new JPanel(new GridLayout(1, 1, 5, 5));
        this.titlePanel.setBackground(Color.white);
        this.titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.titlePanel.setPreferredSize(new Dimension(width - 10, 24));

        this.t_title = new ALabel(title);
        this.t_title.setFont(titleFont);

        this.t_title.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.titlePanel.add(this.t_title);
        wizardPanel.add(this.titlePanel, BorderLayout.NORTH);

        this.mainPanel = new JPanel(new GridLayout(1, 1));
        this.mainPanel.setBackground(Color.yellow);
        this.mainPanel.setPreferredSize(new Dimension(width, height - 110));
        wizardPanel.add(this.mainPanel, BorderLayout.CENTER);

        // We load the wizard image
        MediaTracker mediaTracker = new MediaTracker(this);
        this.wizardImage = resourceLocator.getGuiImage("wizard.jpg");
        mediaTracker.addImage(this.wizardImage, 0);

        try {
            mediaTracker.waitForAll(); // wait for all images to be in memory
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JPanel leftPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(JWizard.this.wizardImage, 0, 0, 100, this.getHeight(), this);
            }
        };

        leftPanel.setPreferredSize(new Dimension(100, height));
        leftPanel.setMinimumSize(new Dimension(100, height));
        leftPanel.setMaximumSize(new Dimension(100, height));
        getContentPane().add(leftPanel, BorderLayout.WEST);

        this.buttonsPanel = new JPanel(new GridLayout(1, 4, 0, 10));
        this.buttonsPanel.setBackground(Color.white);

        // *** Load images of buttons
        this.im_cancelup = resourceLocator.getImageIcon("cancel-up.gif");
        this.im_canceldo = resourceLocator.getImageIcon("cancel-do.gif");
        this.im_cancelun = resourceLocator.getImageIcon("cancel-un.gif");
        this.im_okup = resourceLocator.getImageIcon("ok-up.gif");
        this.im_okdo = resourceLocator.getImageIcon("ok-do.gif");
        this.im_okun = resourceLocator.getImageIcon("ok-un.gif");
        this.im_nextup = resourceLocator.getImageIcon("next-up.gif");
        this.im_nextdo = resourceLocator.getImageIcon("next-do.gif");
        this.im_nextun = resourceLocator.getImageIcon("next-un.gif");
        this.im_previousup = resourceLocator.getImageIcon("previous-up.gif");
        this.im_previousdo = resourceLocator.getImageIcon("previous-do.gif");
        this.im_previousun = resourceLocator.getImageIcon("previous-un.gif");

        // *** Previous ***
        this.b_previous = new JButton(this.im_previousup);
        this.b_previous.setRolloverIcon(this.im_previousdo);
        this.b_previous.setPressedIcon(this.im_previousdo);
        this.b_previous.setDisabledIcon(this.im_previousun);
        this.b_previous.setBorderPainted(false);
        this.b_previous.setContentAreaFilled(false);
        this.b_previous.setFocusPainted(false);

        this.b_previous.setActionCommand("previous");

        this.b_previous.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JWizard.this.nextStep = null;

                if (!JWizard.this.currentStep.onPrevious(getContext(), JWizard.this))
                    return;

                if (JWizard.this.nextStep == null)
                    return; // not set by onPrevious

                showNextStep();
            }
        });

        // *** Next ***
        this.b_next = new JButton(this.im_nextup);
        this.b_next.setRolloverIcon(this.im_nextdo);
        this.b_next.setPressedIcon(this.im_nextdo);
        this.b_next.setDisabledIcon(this.im_nextun);
        this.b_next.setBorderPainted(false);
        this.b_next.setContentAreaFilled(false);
        this.b_next.setFocusPainted(false);

        this.b_next.setActionCommand("next");
        this.b_next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JWizard.this.nextStep = null;

                if (!JWizard.this.currentStep.onNext(getContext(), JWizard.this))
                    return;

                if (JWizard.this.nextStep == null) {
                    // End of JWizard ?
                    if (JWizard.this.currentStep.getParameters().getIsLastStep()) {
                        onFinished(getContext()); // End of Wizard
                        setVisible(false);
                        JWizard.this.mainPanel.removeAll();
                        JWizard.this.stepFactory.clear();
                        dispose();
                        JWizard.this.currentStep = null;
                    }

                    return;
                }

                showNextStep();
            }
        });

        // *** Cancel ***
        this.b_cancel = new JButton(this.im_cancelup);
        this.b_cancel.setRolloverIcon(this.im_canceldo);
        this.b_cancel.setPressedIcon(this.im_canceldo);
        this.b_cancel.setDisabledIcon(this.im_cancelun);
        this.b_cancel.setBorderPainted(false);
        this.b_cancel.setContentAreaFilled(false);
        this.b_cancel.setFocusPainted(false);

        this.b_cancel.setActionCommand("cancel");
        this.b_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int value = JOptionPane.showConfirmDialog(null, "Are you sure ?", "Cancel", JOptionPane.YES_NO_OPTION);

                if (value != JOptionPane.YES_OPTION)
                    return;

                onCanceled(getContext());
                setVisible(false);
                JWizard.this.mainPanel.removeAll();
                JWizard.this.stepFactory.clear();
                dispose();
                JWizard.this.currentStep = null;
                JWizard.this.nextStep = null;
            }
        });

        // *** Add the buttons to buttonsPanel ***
        this.buttonsPanel.add(this.b_previous);
        this.buttonsPanel.add(new JLabel(" "));
        this.buttonsPanel.add(this.b_cancel);
        this.buttonsPanel.add(this.b_next);
        this.buttonsPanel.setPreferredSize(new Dimension(width - 10, 45));

        // *** Add buttonsPanel ***
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        southPanel.setBackground(Color.white);
        southPanel.add(this.buttonsPanel);
        southPanel.setPreferredSize(new Dimension(width - 10, 60));
        wizardPanel.add(southPanel, BorderLayout.SOUTH);

        // *** Window listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JWizard.this.b_cancel.doClick();
            }
        });

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // close is close ! not hide !
    }

    /*------------------------------------------------------------------------------------*/

    /** Initialize this JWizard with its first JWizarStep. Displays the JWizard.
     *  It is your responsablity to call this method.
     *
     * @param parameters parameters for the first step
     * @exception thrown if the given parameters are wrong...
     */
    protected void init(JWizardStepParameters parameters) throws WizardException {
        if (!setNextStep(parameters))
            throw new WizardException("failed to create first step.");

        showNextStep();
    }

    /*------------------------------------------------------------------------------------*/

    /** Initialize this JWizard with its first JWizarStep. Displays the JWizard.
     *  It is your responsablity to call this method. 
     *
     * @param parametersFile file containing the JWizardStepParameters for the first step
     * @exception thrown if the given parameters are wrong...
     */
    protected void init(String parametersFile) throws WizardException {
        if (!setNextStep(parametersFile))
            throw new WizardException("failed to create first step.");

        showNextStep();
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the current JWizardStep. Given its parameters we create the step using
     *  our JWizardStepFactory.
     *
     *  @param parameters step parameters
     *  @return true the current step was changed successfully, false otherwise.
     */
    public boolean setNextStep(JWizardStepParameters parameters) {

        this.nextStep = this.stepFactory.getJWizardStep(parameters);

        if (this.nextStep == null)
            return false;

        return true;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the current JWizardStep. Given a file containing the JWizardStepParameters
     *  we try to create the step using our JWizardStepFactory.
     *
     * @param parametersFile file containing the JWizardStepParameters to use for the step's
     *        creation.
     * @return true the current step was changed successfully, false otherwise.
     */
    public boolean setNextStep(String parametersFile) {

        this.nextStep = this.stepFactory.getJWizardStepFromFile(parametersFile);

        if (this.nextStep == null)
            return false;

        return true;
    }

    /*------------------------------------------------------------------------------------*/

    /** To show the next selected step on the wizard
     */
    protected void showNextStep() {

        // Step Cleaning
        if (this.nextStep == null)
            return; // no step !!!

        this.mainPanel.removeAll();

        // Step Update
        this.currentStep = this.nextStep;
        this.nextStep = null;

        this.mainPanel.add(this.currentStep, BorderLayout.SOUTH);

        // Parameters update
        this.t_title.setText(this.currentStep.getParameters().getStepTitle());
        this.b_next.setEnabled(this.currentStep.getParameters().getIsNextButtonEnabled());
        this.b_previous.setEnabled(this.currentStep.getParameters().getIsPrevButtonEnabled());

        if (this.currentStep.getParameters().getIsLastStep()) {
            this.b_next.setIcon(this.im_okup);
            this.b_next.setRolloverIcon(this.im_okdo);
            this.b_next.setPressedIcon(this.im_okdo);
            this.b_next.setDisabledIcon(this.im_okun);
        } else {
            this.b_next.setIcon(this.im_nextup);
            this.b_next.setRolloverIcon(this.im_nextdo);
            this.b_next.setPressedIcon(this.im_nextdo);
            this.b_next.setDisabledIcon(this.im_nextun);
        }

        // Screen Show
        this.mainPanel.repaint();
        show();

        this.currentStep.onShow(this.context, this);
    }

    /*------------------------------------------------------------------------------------*/

    /** To set the context object which is given to JWizardSteps onXXX methods.
     */
    public void setContext(Object context) {
        this.context = context;
    }

    /*------------------------------------------------------------------------------------*/

    /** To get the context object which is given to JWizardSteps onXXX methods.
     */
    protected Object getContext() {
        return this.context;
    }

    /*------------------------------------------------------------------------------------*/

    /** To set if the next button is enabled.
     */
    public void setIsNextButtonEnabled(boolean enabled) {
        this.b_next.setEnabled(enabled);
    }

    /*------------------------------------------------------------------------------------*/

    /** To awake the current step.
     */
    public void awakeCurrentStep() {
        if (this.currentStep != null)
            this.currentStep.awake();
    }

    /*------------------------------------------------------------------------------------*/

}
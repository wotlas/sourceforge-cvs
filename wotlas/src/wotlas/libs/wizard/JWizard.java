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

import wotlas.utils.aswing.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


/** A generic setup wizard that possesses buttons (previous/cancel/next) and displays
 *  a panel (JWizardStep) that can react when these buttons are pressed. <br>
 *  JWizard is abstract : the subclass must implement the method : onFinished() and
 *  onCanceled().
 *
 *  This class uses images found in the GUI_IMAGES_PATH.
 *
 * @author Petrus, Aldiss
 * @see wotlas.libs.wizard.JWizardStep
 */

public abstract class JWizard extends JFrame {

 /*------------------------------------------------------------------------------------*/

  /** Where the images are stored. Note the existence of an ending '/'.
   */
  public static final String GUI_IMAGES_PATH = "../base/gui/";

 /*------------------------------------------------------------------------------------*/

  /** The current wizard step
   */
  private JWizardStep currentStep;

  /** The next wizard step
   */
  private JWizardStep nextStep;

  /** Our factory for building JWizardStep.
   */
  private JWizardStepFactory stepFactory;

  /** Title of the wizard
   */
  private JPanel titlePanel;

  /** main panel of the wizard
   */
  private JPanel mainPanel;

  /** panel of navigation buttons
   */
  private JPanel buttonsPanel;

  /** title of the wizard
   */
  private ALabel t_title;

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
   * @param width wizard width
   * @param height wizard height
   */
  public JWizard(String title, int width, int height) {
      super(title);

      stepFactory = new JWizardStepFactory();

      setSize(width+100,height);
      setBackground(Color.white);
      setIconImage(Toolkit.getDefaultToolkit().getImage( GUI_IMAGES_PATH+"icon.gif" ));

      JPanel wizardPanel = new JPanel();
      wizardPanel.setBackground(Color.white);
      wizardPanel.setAlignmentX(LEFT_ALIGNMENT);
      getContentPane().add(wizardPanel, BorderLayout.CENTER);

      titlePanel = new JPanel(new GridLayout(1,1,5,5));
      titlePanel.setBackground(Color.white);
      titlePanel.setAlignmentX(LEFT_ALIGNMENT);
      titlePanel.setPreferredSize( new Dimension(width-10,24) );

      t_title = new ALabel(title);
      t_title.setFont(new Font("Serif",Font.PLAIN, 18) );
      t_title.setAlignmentX(LEFT_ALIGNMENT);
      titlePanel.add(t_title);
      wizardPanel.add(titlePanel, BorderLayout.NORTH);

      mainPanel = new JPanel(new GridLayout(1,1));
      mainPanel.setBackground(Color.yellow);
      mainPanel.setPreferredSize( new Dimension(width,height-110) );
      wizardPanel.add(mainPanel, BorderLayout.CENTER);

   // We load the wizard image
      MediaTracker mediaTracker = new MediaTracker(this);
      wizardImage  = getToolkit().getImage("../base/gui/wizard.jpg");
      mediaTracker.addImage(wizardImage,0);

      try{
            mediaTracker.waitForAll(); // wait for all images to be in memory
      }
      catch(InterruptedException e){
            e.printStackTrace();
      }

      JPanel leftPanel = new JPanel() {
      	  public void paintComponent(Graphics g) {
      	     g.drawImage( wizardImage, 0, 0, 100, getHeight(), this );
      	  }
      };

      leftPanel.setPreferredSize( new Dimension(100,height) );
      leftPanel.setMinimumSize( new Dimension(100,height) );
      leftPanel.setMaximumSize( new Dimension(100,height) );
      getContentPane().add( leftPanel, BorderLayout.WEST );


      buttonsPanel = new JPanel(new GridLayout(1,4,0,5));
      buttonsPanel.setBackground(Color.white);

    // *** Load images of buttons
      im_cancelup   = new ImageIcon(GUI_IMAGES_PATH+"cancel-up.gif");
      im_canceldo   = new ImageIcon(GUI_IMAGES_PATH+"cancel-do.gif");
      im_cancelun   = new ImageIcon(GUI_IMAGES_PATH+"cancel-un.gif");
      im_okup       = new ImageIcon(GUI_IMAGES_PATH+"ok-up.gif");
      im_okdo       = new ImageIcon(GUI_IMAGES_PATH+"ok-do.gif");
      im_okun       = new ImageIcon(GUI_IMAGES_PATH+"ok-un.gif");
      im_nextup     = new ImageIcon(GUI_IMAGES_PATH+"next-up.gif");
      im_nextdo     = new ImageIcon(GUI_IMAGES_PATH+"next-do.gif");
      im_nextun     = new ImageIcon(GUI_IMAGES_PATH+"next-un.gif");
      im_previousup = new ImageIcon(GUI_IMAGES_PATH+"previous-up.gif");
      im_previousdo = new ImageIcon(GUI_IMAGES_PATH+"previous-do.gif");
      im_previousun = new ImageIcon(GUI_IMAGES_PATH+"previous-un.gif");

    // *** Previous ***
      b_previous = new JButton(im_previousup);
      b_previous.setRolloverIcon(im_previousdo);
      b_previous.setPressedIcon(im_previousdo);
      b_previous.setDisabledIcon(im_previousun);
      b_previous.setBorderPainted(false);
      b_previous.setContentAreaFilled(false);
      b_previous.setFocusPainted(false);        
    
      b_previous.setActionCommand("previous");

      b_previous.addActionListener(
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e) {
          nextStep = null;

          if( !currentStep.onPrevious(getContext(),JWizard.this) )
              return;

          if(nextStep==null)
              return; // not set by onPrevious

          showNextStep();
        }
      });

    // *** Next ***
      b_next = new JButton(im_nextup);
      b_next.setRolloverIcon(im_nextdo);
      b_next.setPressedIcon(im_nextdo);
      b_next.setDisabledIcon(im_nextun);
      b_next.setBorderPainted(false);
      b_next.setContentAreaFilled(false);
      b_next.setFocusPainted(false);
    
      b_next.setActionCommand("next");
      b_next.addActionListener(
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e) {
            nextStep = null;

            if( !currentStep.onNext(getContext(),JWizard.this) )
                return;

            if(nextStep==null) {
              // End of JWizard ?
                 if( currentStep.getParameters().getIsLastStep() ) {
                     onFinished(getContext());  // End of Wizard
                     setVisible(false);
                     mainPanel.removeAll();
                     stepFactory.clear();
                     dispose();
                     currentStep=null;
                 }

                 return;
            }

            showNextStep();
        }
      });

    // *** Cancel ***
      b_cancel = new JButton(im_cancelup);
      b_cancel.setRolloverIcon(im_canceldo);
      b_cancel.setPressedIcon(im_canceldo);
      b_cancel.setDisabledIcon(im_cancelun);
      b_cancel.setBorderPainted(false);
      b_cancel.setContentAreaFilled(false);
      b_cancel.setFocusPainted(false);
      
      b_cancel.setActionCommand("cancel");
      b_cancel.addActionListener(
        new ActionListener()
        {
           public void actionPerformed(ActionEvent e) {
              onCanceled(getContext());
              setVisible(false);
              mainPanel.removeAll();
              stepFactory.clear();
              dispose();
              currentStep=null;
              nextStep=null;
           }
        });

    // *** Add the buttons to buttonsPanel ***
      buttonsPanel.add(b_previous);
      buttonsPanel.add(new JLabel(" "));
      buttonsPanel.add(b_cancel);
      buttonsPanel.add(b_next);
      buttonsPanel.setPreferredSize( new Dimension(width-10,45) );

    // *** Add buttonsPanel ***
      JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
      southPanel.setBackground(Color.white);
      southPanel.add(buttonsPanel);
      wizardPanel.add(southPanel, BorderLayout.SOUTH);

    // *** Window listener
      addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                b_cancel.doClick();
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
   protected void init( JWizardStepParameters parameters ) throws WizardException {
        if( !setNextStep(parameters) )
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
   protected void init( String parametersFile ) throws WizardException {
        if( !setNextStep(parametersFile) )
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
   public boolean setNextStep( JWizardStepParameters parameters ) {

       nextStep = stepFactory.getJWizardStep( parameters );
       
       if(nextStep==null)
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
   public boolean setNextStep( String parametersFile ) {

       nextStep = stepFactory.getJWizardStepFromFile( parametersFile );

       if(nextStep==null)
          return false;

       return true;
   }

 /*------------------------------------------------------------------------------------*/

  /** To show the next selected step on the wizard
   */
   protected void showNextStep() {

   // Step Cleaning
      if(nextStep==null)
         return; // no step !!!

      mainPanel.removeAll();

   // Step Update
      currentStep = nextStep;
      nextStep = null;

      mainPanel.add(currentStep, BorderLayout.SOUTH);

   // Parameters update
      t_title.setText( currentStep.getParameters().getStepTitle() );
      b_next.setEnabled( currentStep.getParameters().getIsNextButtonEnabled() );
      b_previous.setEnabled(  currentStep.getParameters().getIsPrevButtonEnabled() );

      if( currentStep.getParameters().getIsLastStep() ) {
          b_next.setIcon(im_okup);
          b_next.setRolloverIcon(im_okdo);
          b_next.setPressedIcon(im_okdo);
          b_next.setDisabledIcon(im_okun);
      }
      else {
          b_next.setIcon(im_nextup);
          b_next.setRolloverIcon(im_nextdo);
          b_next.setPressedIcon(im_nextdo);
          b_next.setDisabledIcon(im_nextun);
      }

   // Screen Show
      mainPanel.repaint();
      show();

      currentStep.onShow(context,this);
   }

 /*------------------------------------------------------------------------------------*/

  /** To set the context object which is given to JWizardSteps onXXX methods.
   */
   protected void setContext(Object context) {
     this.context = context;
   }

 /*------------------------------------------------------------------------------------*/

  /** To get the context object which is given to JWizardSteps onXXX methods.
   */
   protected Object getContext() {
     return context;
   }

 /*------------------------------------------------------------------------------------*/

  /** To awake the current step.
   */
   //protected void awakeCurrentStep() {
   public void awakeCurrentStep() {
        if(currentStep!=null)
           currentStep.awake();
   }

 /*------------------------------------------------------------------------------------*/

}
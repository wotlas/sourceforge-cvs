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

package wotlas.client.gui;

import wotlas.client.ClientManager;
import wotlas.client.DataManager;
import wotlas.common.message.account.*;
import wotlas.utils.aswing.*;

import wotlas.libs.net.*;

import java.awt.*;
import java.awt.event.*;

import java.util.Vector;

import javax.swing.*;

/** A generic wizard<br>
 * the subclass must implement the method : onFinished()
 *
 * @author Petrus
 * @see wotlas.client.gui.JWizardStep;
 */

public abstract class JWizard extends JFrame
{

 /*------------------------------------------------------------------------------------*/

  /** Vector of the panels
   */
  private Vector vPanels;

  /** Index of the current panel
   */
  private int currentIndex;

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

  /** Contexte of the wizard
   */
  protected Object context;
  
  /** pictures of buttons
   */
  private ImageIcon im_okup, im_okdo, im_okun;
  private ImageIcon im_cancelup, im_canceldo, im_cancelun;  
  private ImageIcon im_nextup, im_nextdo, im_nextun;
  private ImageIcon im_previousup, im_previousdo, im_previousun;

 /*------------------------------------------------------------------------------------*/

  /** Called when wizard is finished
   */
  protected abstract void onFinished(Object context);

 /*------------------------------------------------------------------------------------*/

  /** Constructor of a wizard
   *
   * @param title wizard title
   * @param width wizard width
   * @param height wizard height
   */
  public JWizard(String title, int width, int height) {
    super(title);
    setSize(width,height);
    setBackground(Color.white);

    this.context = context;

    
    
   


    vPanels = new Vector();

    titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    titlePanel.setBackground(Color.white);
    t_title = new ALabel(title);
    titlePanel.add(t_title);
    getContentPane().add(titlePanel, BorderLayout.NORTH);

    mainPanel = new JPanel(new GridLayout(1,1));
    mainPanel.setBackground(Color.yellow);
    getContentPane().add(mainPanel, BorderLayout.CENTER);

    buttonsPanel = new JPanel();
    buttonsPanel.setBackground(Color.white);

    // *** Load images of buttons
    im_cancelup   = new ImageIcon("../base/gui/cancel-up.gif");
    im_canceldo   = new ImageIcon("../base/gui/cancel-do.gif");
    im_cancelun   = new ImageIcon("../base/gui/cancel-un.gif");
    im_okup       = new ImageIcon("../base/gui/ok-up.gif");
    im_okdo       = new ImageIcon("../base/gui/ok-do.gif");
    im_okun       = new ImageIcon("../base/gui/ok-un.gif");
    im_nextup     = new ImageIcon("../base/gui/next-up.gif");
    im_nextdo     = new ImageIcon("../base/gui/next-do.gif");
    im_nextun     = new ImageIcon("../base/gui/next-un.gif");
    im_previousup = new ImageIcon("../base/gui/previous-up.gif");
    im_previousdo = new ImageIcon("../base/gui/previous-do.gif");
    im_previousun = new ImageIcon("../base/gui/previous-un.gif");
               
    // *** Previous ***
    b_previous = new JButton(im_previousup);
    b_previous.setRolloverIcon(im_previousdo);
    b_previous.setPressedIcon(im_previousdo);
    b_previous.setDisabledIcon(im_previousun);
    b_previous.setBorderPainted(false);
    b_previous.setContentAreaFilled(false);
    b_previous.setFocusPainted(false);        
    
    b_previous.setActionCommand("previous");
    //b_previous.setEnabled(false);
    b_previous.addActionListener(
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e) {
          JWizardStep step = (JWizardStep)vPanels.elementAt(currentIndex);

          if( !step.onPrevious(getContext()) )
              return;

          currentIndex--;
        // we restore the images of the next button
          b_next.setIcon(im_nextup);
          b_next.setRolloverIcon(im_nextdo);
          b_next.setPressedIcon(im_nextdo);
          b_next.setDisabledIcon(im_nextun);

          mainPanel.remove(step);
          showStep(currentIndex);
        }
      }
    );

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
          JWizardStep step = (JWizardStep)vPanels.elementAt(currentIndex);

            if( !step.onNext(getContext()) )
                return;

          if (currentIndex == (vPanels.size()-1)) { 
            //b_next.setEnabled(false);
            setVisible(false);
            mainPanel.removeAll();
            dispose();
            // End of Wizard
            onFinished(getContext());
          } else {
            currentIndex++;
            if (currentIndex == (vPanels.size()-1)) {
              //b_next.setText("Finish");
              b_next.setIcon(im_okup);
              b_next.setRolloverIcon(im_okdo);
              b_next.setPressedIcon(im_okdo);
              b_next.setDisabledIcon(im_okun);              
            }

            //b_previous.setEnabled(true);
            mainPanel.remove(step);
            showStep(currentIndex);
          }
        }
      }
    );

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
          dispose();
          NetPersonality personality = (NetPersonality) context;
          
          personality.queueMessage( new CancelAccountCreationMessage() );
          personality.closeConnection();
          ClientManager.getDefaultClientManager().start(10);

          /*
          mainPanel.remove((JWizardStep)vPanels.elementAt(currentIndex));
          currentIndex = 0;
          b_previous.setEnabled(false);
          //b_next.setText("Next >");
          b_next.setIcon(im_nextup);
          b_next.setRolloverIcon(im_nextdo);
          b_next.setPressedIcon(im_nextdo);
          b_next.setDisabledIcon(im_nextun);          
          b_next.setEnabled(true);
          showStep(currentIndex);
          */
        }
      }
    );

    // *** Add the buttons to buttonsPanel ***
    buttonsPanel.add(b_cancel);
    buttonsPanel.add(b_previous);
    buttonsPanel.add(b_next);

    // *** Add buttonsPanel to mainPanel ***
    JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    southPanel.setBackground(Color.white);
    southPanel.add(buttonsPanel);
    getContentPane().add(southPanel, BorderLayout.SOUTH);

    currentIndex = 0;
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the number of steps
   */
  public int getNSteps() {
    return vPanels.size();
  }

 /*------------------------------------------------------------------------------------*/

  /** To add a new step to the wizard
   */
  public int addStep(JWizardStep step) {
    vPanels.add(step);
    return vPanels.size();
  }

  /** To remove a step from the wizard
   */
  public boolean removeStep(JWizardStep step) {
    return vPanels.remove(step);
  }

 /*------------------------------------------------------------------------------------*/

  /** To show a step of the wizard
   *
   * @param indexPanel index of the panel to be shown
   */
  public void showStep(int indexStep) {
    if (DataManager.SHOW_DEBUG)
      System.out.println("showStep(" + indexStep + ")");
    JWizardStep step = (JWizardStep)vPanels.elementAt(indexStep);

    mainPanel.removeAll();
    mainPanel.add(step, BorderLayout.SOUTH);
    int index = currentIndex + 1;
    t_title.setText(step.getTitle());
    mainPanel.repaint();
    show();
    step.onShow(context);
  }

 /*------------------------------------------------------------------------------------*/

  /** To enable the next button
   */
  public void setNextEnabled(boolean value) {
    b_next.setEnabled(value);
  }

  /** To enable the previous button
   */
  public void setPreviousEnabled(boolean value) {
    b_previous.setEnabled(value);
  }

 /*------------------------------------------------------------------------------------*/

  /** To change the current index
   */
  public void setStepIndex(int index) {
    this.currentIndex = index;
  }

 /*------------------------------------------------------------------------------------*/

  /** To set the context
   */
  protected void setContext(Object context) {
    this.context = context;
  }

  /** To get the context
   */
  private Object getContext() {
    return context;
  }

 /*------------------------------------------------------------------------------------*/

}
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

package wotlas.client.screen;

import wotlas.client.DataManager;
import wotlas.client.gui.*;

import wotlas.common.message.account.*;
import wotlas.common.message.description.*;

import wotlas.libs.net.NetPersonality;

import wotlas.utils.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** A wizard to create an account
 *
 * @author Petrus
 * @see wotlas.client.gui.JWizard
 * @see wotlas.client.gui.JWizardStep
 */
public class JAccountWizard extends JWizard
{

 /*------------------------------------------------------------------------------------*/

  private NetPersonality personality;

  /** Constructor
   */
  public JAccountWizard(NetPersonality personality) {
    super("Account creation", 400, 400);
    setContext(personality);
    this.personality = personality;
  }

 /*------------------------------------------------------------------------------------*/

  /** Init the different steps of the wizard
   */
  public void init()
  {

    Step1 step1 = new Step1();
    Step2 step2 = new Step2();
    Step3 step3 = new Step3();

    addStep(step1);
    addStep(step2);
    addStep(step3);

  }

 /*------------------------------------------------------------------------------------*/

  /** Start the wizard
   */
  public void start() {
    showStep(0);
  }

 /*------------------------------------------------------------------------------------*/

  /** Called when the wizard is finished
   */
  protected void onFinished(Object context) {
    System.out.println("finished");
    personality.queueMessage( new AccountCreationMessage() );
  }

 /*------------------------------------------------------------------------------------*/

  /** step of wizard
   */
  class Step1 extends JWizardStep
  {
    private ALabel lbl_infos;
    private ARadioButton bt_char0, bt_char1, bt_char2, bt_char3, bt_char4, bt_char5, bt_char6, bt_char7, bt_char8, bt_char9;
    private ButtonGroup btGroup;
    private JPanel mainPanel, formPanel;
    private String className;
    private byte wotCharacterStatus;

    /** called when the step is to be shown
     */
    public void onShow(Object context) {}

    /** called when Next button is clicked
     */
    public void onNext(Object context) {
      personality.queueMessage( new WotCharacterClassMessage(btGroup.getSelection().getActionCommand(), wotCharacterStatus) );
    }

    /** called when Previous button is clicked
     */
    public void onPrevious(Object context) {}

    /** Constructor
     */
    public Step1()
    {
      super("Character");
      setBackground(Color.white);

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      lbl_infos = new ALabel("Please, choose your character:");
      lbl_infos.setAlignmentX(LEFT_ALIGNMENT);
      add(lbl_infos);

      mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      mainPanel.setBackground(Color.white);

      formPanel = new JPanel(new GridLayout(9,1,10,2));
      formPanel.setBackground(Color.white);
        bt_char0 = new ARadioButton("Aes Sedai, Novice");
        bt_char0.setActionCommand("wotlas.common.character.AesSedai");
        bt_char0.setSelected(true);
        bt_char0.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            className = "wotlas.common.character.AesSedai";
            wotCharacterStatus = wotlas.common.character.AesSedai.AES_NOVICE;
          }
        });
        formPanel.add(bt_char0);
        
        bt_char1 = new ARadioButton("Aes Sedai, Accepted");
        bt_char1.setActionCommand("wotlas.common.character.AesSedai");     
        bt_char1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            className = "wotlas.common.character.AesSedai";
            wotCharacterStatus = wotlas.common.character.AesSedai.AES_ACCEPTED;
          }
        });   
        formPanel.add(bt_char1);
        
        bt_char2 = new ARadioButton("Aes Sedai, Brown Ajah");
        bt_char2.setActionCommand("wotlas.common.character.AesSedai");  
        bt_char2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            className = "wotlas.common.character.AesSedai";
            wotCharacterStatus = wotlas.common.character.AesSedai.AES_BROWN_AJAH;
          }
        });      
        formPanel.add(bt_char2);
        
        bt_char3 = new ARadioButton("Aes Sedai, White Ajah");
        bt_char3.setActionCommand("wotlas.common.character.AesSedai");    
        bt_char3.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            className = "wotlas.common.character.AesSedai";
            wotCharacterStatus = wotlas.common.character.AesSedai.AES_WHITE_AJAH;
          }
        });    
        formPanel.add(bt_char3);
        
        bt_char4 = new ARadioButton("Aes Sedai, Blue Ajah");
        bt_char4.setActionCommand("wotlas.common.character.AesSedai");        
        bt_char4.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            className = "wotlas.common.character.AesSedai";
            wotCharacterStatus = wotlas.common.character.AesSedai.AES_BLUE_AJAH;
          }
        });    
        formPanel.add(bt_char4);
        
        bt_char5 = new ARadioButton("Aes Sedai, Green Ajah");
        bt_char5.setActionCommand("wotlas.common.character.AesSedai");        
        bt_char5.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            className = "wotlas.common.character.AesSedai";
            wotCharacterStatus = wotlas.common.character.AesSedai.AES_GREEN_AJAH;
          }
        });    
        formPanel.add(bt_char5);
        
        bt_char6 = new ARadioButton("Aes Sedai, Red Ajah");
        bt_char6.setActionCommand("wotlas.common.character.AesSedai");        
        bt_char6.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            className = "wotlas.common.character.AesSedai";
            wotCharacterStatus = wotlas.common.character.AesSedai.AES_RED_AJAH;
          }
        });    
        formPanel.add(bt_char6);
        
        bt_char7 = new ARadioButton("Aes Sedai, Gray Ajah");
        bt_char7.setActionCommand("wotlas.common.character.AesSedai");   
        bt_char7.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            className = "wotlas.common.character.AesSedai";
            wotCharacterStatus = wotlas.common.character.AesSedai.AES_GRAY_AJAH;
          }
        });         
        formPanel.add(bt_char7);
        
        bt_char8 = new ARadioButton("Aes Sedai, Yellow Ajah");
        bt_char8.setActionCommand("wotlas.common.character.AesSedai");    
        bt_char8.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            className = "wotlas.common.character.AesSedai";
            wotCharacterStatus = wotlas.common.character.AesSedai.AES_YELLOW_AJAH;
          }
        });        
        formPanel.add(bt_char8);
        
        bt_char9 = new ARadioButton("Aes Sedai, Amyrlin");
        bt_char9.setActionCommand("wotlas.common.character.AesSedai");        
        bt_char9.setEnabled(false);
        bt_char9.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            className = "wotlas.common.character.AesSedai";
            wotCharacterStatus = wotlas.common.character.AesSedai.AES_AMYRLIN;
          }
        });    
        formPanel.add(bt_char9);
        
        // Group the radio buttons.
        btGroup = new ButtonGroup();
        btGroup.add(bt_char0);
        btGroup.add(bt_char1);
        btGroup.add(bt_char2);
        btGroup.add(bt_char3);
        btGroup.add(bt_char4);
        btGroup.add(bt_char5);
        btGroup.add(bt_char6);
        btGroup.add(bt_char7);
        btGroup.add(bt_char8);
        btGroup.add(bt_char9);
        
      mainPanel.add(formPanel);

      mainPanel.setAlignmentX(LEFT_ALIGNMENT);
      add(mainPanel);

      add(Box.createVerticalGlue());

    }
  }

 /*------------------------------------------------------------------------------------*/

  /** step of wizard
   */
  class Step3 extends JWizardStep
  {
    private ALabel lbl_infos;
    private ALabel lbl_nickname, lbl_fullname, lbl_email;
    private ATextField tf_nickname, tf_fullname, tf_email;
    private JPanel mainPanel, formPanel;

    /** called when the step is to be shown
     */
    public void onShow(Object context) {}

    /** called when Next button is clicked
     */
    public void onNext(Object context) {
      personality.queueMessage( new PlayerNamesMessage(tf_nickname.getText(), tf_fullname.getText()));
    }

    /** called when Previous button is clicked
     */
    public void onPrevious(Object context) {}

    /** Consctructor
     */
    public Step3()
    {
      super("Informations (last step)");
      setBackground(Color.white);

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      lbl_infos = new ALabel("Complete these fields:");
      lbl_infos.setAlignmentX(LEFT_ALIGNMENT);
      add(lbl_infos);

      mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      mainPanel.setBackground(Color.white);
      formPanel = new JPanel(new GridLayout(3,2,10,2));
      formPanel.setBackground(Color.white);
        lbl_nickname = new ALabel("Nickname: ");
        formPanel.add(lbl_nickname);
        tf_nickname = new ATextField(10);
        formPanel.add(tf_nickname);
        lbl_fullname = new ALabel("Full name: ");
        formPanel.add(lbl_fullname);
        tf_fullname = new ATextField(10);
        formPanel.add(tf_fullname);
        lbl_email = new ALabel("Email: ");
        formPanel.add(lbl_email);
        tf_email = new ATextField(10);
        formPanel.add(tf_email);

      mainPanel.add(formPanel);

      mainPanel.setAlignmentX(LEFT_ALIGNMENT);
      add(mainPanel);

      add(Box.createVerticalGlue());

    }
  }

 /*------------------------------------------------------------------------------------*/

  /** step of wizard
   */
  class Step2 extends JWizardStep
  {
    private ALabel lbl_infos;
    private ARadioButton bt_color1, bt_color2, bt_color3, bt_color4, bt_color5, bt_color6;
    private ButtonGroup btGroup;
    private ActionListener myListener;
    private JPanel mainPanel, formPanel;
    private byte hairColor;

    /** called when the step is to be shown
     */
    public void onShow(Object context) {}

    /** called when Next button is clicked
     */
    public void onNext(Object context) {
      personality.queueMessage( new VisualPropertiesMessage(hairColor) );
    }

    /** called when Previous button is clicked
     */
    public void onPrevious(Object context) {}

    /** Consctructor
     */
    public Step2()
    {
      super("Description");
      setBackground(Color.white);

      hairColor = wotlas.common.character.Human.GOLDEN_HAIR;

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      lbl_infos = new ALabel("Choose your hair color:");
      lbl_infos.setAlignmentX(LEFT_ALIGNMENT);
      add(lbl_infos);

      mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      mainPanel.setBackground(Color.white);
      formPanel = new JPanel(new GridLayout(6,1,10,2));
      formPanel.setBackground(Color.white);
        bt_color1 = new ARadioButton("GOLDEN");
        bt_color1.setActionCommand("1");
        bt_color1.setSelected(true);
        bt_color1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            hairColor = wotlas.common.character.Human.GOLDEN_HAIR;
          }
        });
        formPanel.add(bt_color1);
        bt_color1.setSelected(true);

        bt_color2 = new ARadioButton("BROWN");
        bt_color2.setActionCommand("2");
        bt_color2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            hairColor = wotlas.common.character.Human.BROWN_HAIR;
          }
        });
        formPanel.add(bt_color2);        

        bt_color3 = new ARadioButton("BLACK");
        bt_color3.setActionCommand("3");
        bt_color3.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            hairColor = wotlas.common.character.Human.BLACK_HAIR;
          }
        });
        formPanel.add(bt_color3);        

        bt_color4 = new ARadioButton("GREY");
        bt_color4.setActionCommand("4");
        bt_color4.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            hairColor = wotlas.common.character.Human.GREY_HAIR;
          }
        });
        formPanel.add(bt_color4);        

        bt_color5 = new ARadioButton("WHITE");
        bt_color5.setActionCommand("5");
        bt_color5.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            hairColor = wotlas.common.character.Human.WHITE_HAIR;
          }
        });
        formPanel.add(bt_color5);        

        bt_color6 = new ARadioButton("REDDISH");
        bt_color6.setActionCommand("6");
        bt_color6.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            hairColor = wotlas.common.character.Human.REDDISH_HAIR;
          }
        });
        formPanel.add(bt_color6);        


        btGroup = new ButtonGroup();
        
        btGroup.add(bt_color1);
        btGroup.add(bt_color2);
        btGroup.add(bt_color3);
        btGroup.add(bt_color4);
        btGroup.add(bt_color5);
        btGroup.add(bt_color6);
      mainPanel.add(formPanel);

      mainPanel.setAlignmentX(LEFT_ALIGNMENT);
      add(mainPanel);

      add(Box.createVerticalGlue());

    }
  }

 /*------------------------------------------------------------------------------------*/

  /** Main. TEST
   *
   * @param argv this default param is not used.
   */
  /*static public void main(String argv[])
  {
    JAccountWizard host = new JAccountWizard();
    host.init();
    host.start();
  }*/

 /*------------------------------------------------------------------------------------*/

}
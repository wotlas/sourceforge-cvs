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

import wotlas.client.ClientManager;
import wotlas.client.*;
import wotlas.client.screen.JAccountConnectionDialog;

import wotlas.common.ServerConfig;
import wotlas.common.ServerConfigList;
import wotlas.common.ServerConfigListTableModel;

import wotlas.common.message.account.*;

import wotlas.libs.wizard.*;
import wotlas.libs.wizard.step.*;

import wotlas.libs.net.NetPersonality;

import wotlas.utils.*;
import wotlas.utils.aswing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/** A wizard to create an account
 *
 * @author Petrus
 */
public class JAccountCreationWizard extends wotlas.libs.wizard.JWizard
{
  /**  The current account wizard
   */
   static protected JAccountCreationWizard wizard;

  /** Called when wizard is finished (after last step's end).
   */
   protected void onFinished(Object context) {
   	 Debug.signal( Debug.NOTICE, this, "Wizard finished.");

         if(context!=null) {
            NetPersonality personality = (NetPersonality) context;
            personality.queueMessage( new CancelAccountCreationMessage() );
            personality.closeConnection();
         }

         ClientManager clientManager = ClientManager.getDefaultClientManager();
         clientManager.start(11);
   }

  /** Called when wizard is canceled ('cancel' button pressed).
   */
   protected void onCanceled(Object context) {
   	 Debug.signal( Debug.NOTICE, this, "Wizard canceled.");

         if(context!=null) {
            NetPersonality personality = (NetPersonality) context;
            personality.queueMessage( new CancelAccountCreationMessage() );
            personality.closeConnection();
         }

         ClientManager clientManager = ClientManager.getDefaultClientManager();
         clientManager.start(0);
   }
  
  /** Constructor
   */
  public JAccountCreationWizard() {
    super("Account creation wizard",460,460);
    wizard = this;

    // We display first step
    try {
      //setContext();
      init( wotlas.client.screen.JAccountCreationWizard$ServerSelectionStep.getStaticParameters() );
    } catch( WizardException we ) {
      we.printStackTrace();
      Debug.signal( Debug.ERROR, this, "Wizard initialisation failed");
    }
  }

 /*------------------------------------------------------------------------------------*/

 /**
  * First Step of our JWizard.
  * Choose a server.
  */
  public static class ServerSelectionStep extends JWizardStep {
    
    /** Our ServerConfigList file.
     */
    private ServerConfigList serverConfigList;
  
    /** Current serverConfig
     */
    private ServerConfig currentServerConfig;
    

    /*------------------------------------------------------------------------------------*/

    /** This is a static JWizardStep, to build it more simply this method
     *  returns the JWizardStepParameters needed for the JWizard.
     */
    public static JWizardStepParameters getStaticParameters() {
      JWizardStepParameters param = new JWizardStepParameters(
                            "wotlas.client.screen.JAccountCreationWizard$ServerSelectionStep",
                            "Server Selection" );
      param.setIsNextButtonEnabled(false);
      param.setIsPrevButtonEnabled(false);
      param.setIsDynamic(false); // we want the step to be buffered
      return param;
    }
    
    /** Consctructor
     */
    public ServerSelectionStep() {
      super();
      
      ClientManager clientManager = ClientManager.getDefaultClientManager();
      
      // JPanel inits
      setLayout(new BorderLayout());

      setAlignmentX(LEFT_ALIGNMENT);
      setBackground(Color.white);
      setBorder(BorderFactory.createEmptyBorder(10,20,10,20));      
      
      // Info on this Step
      JPanel group0 = new JPanel(new GridLayout(1,1,0,0));
      group0.setAlignmentX(LEFT_ALIGNMENT);
      group0.setBackground(Color.white);
      ATextArea text0 = new ATextArea("       Welcome to the Account Creation Wizard."
                                     +" Please, choose the server where you want to create your new account."
                                     +" If the list below is empty it means the central wotlas web server is not running"
                                     +" and you'll have to restart this wizard later.\n" );
      text0.setLineWrap(true);
      text0.setWrapStyleWord(true);
      text0.setEditable(false);    
      text0.setAlignmentX(LEFT_ALIGNMENT);
      group0.add( text0 );
      add(group0, BorderLayout.NORTH );

      // Loading Server Configs
      serverConfigList = clientManager.getServerConfigList();
      serverConfigList.getLatestConfigFiles(this);

      ServerConfigListTableModel serverConfigListTabModel = new ServerConfigListTableModel(serverConfigList);
      JTable serversTable = new JTable(serverConfigListTabModel);
      serversTable.setDefaultRenderer(Object.class, new ATableCellRenderer());
      serversTable.setBackground(Color.white);
      serversTable.setForeground(Color.black);
      serversTable.setSelectionBackground(Color.lightGray);
      serversTable.setSelectionForeground(Color.white);
      serversTable.setRowHeight(24);
      serversTable.getColumnModel().getColumn(2).setPreferredWidth(-1);
      // selection
      serversTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      ListSelectionModel rowServerSM = serversTable.getSelectionModel();
      rowServerSM.addListSelectionListener(new ListSelectionListener()
      {
        private JHTMLWindow htmlDescr;

        public void valueChanged(ListSelectionEvent e)
        {
           ListSelectionModel lsm = (ListSelectionModel) e.getSource();
           if (lsm.isSelectionEmpty())
               return; //no rows were selected

           int selectedRow = lsm.getMinSelectionIndex();

          //selectedRow is selected
            currentServerConfig = serverConfigList.ServerConfigAt(selectedRow);

            if(htmlDescr==null)
               htmlDescr = new JHTMLWindow( wizard, "Wotlas Server", "text:"+currentServerConfig.toHTML(), 350, 250, false );
            else {
               htmlDescr.setText( currentServerConfig.toHTML() );
               if( !htmlDescr.isShowing() ) htmlDescr.show();
            }
            
          //Ignore extra messages.
           if (e.getValueIsAdjusting())
              return;

            //selectedRow is selected
            currentServerConfig = serverConfigList.ServerConfigAt(selectedRow);
            wizard.setIsNextButtonEnabled(true);
            //currentProfileConfig.setOriginalServerID(currentServerConfig.getServerID());
            //currentProfileConfig.setServerID(currentServerConfig.getServerID());
        }
      });

      // show table
      JScrollPane scrollPane = new JScrollPane(serversTable);
      serversTable.setAlignmentX(LEFT_ALIGNMENT);
      //serversTable.setPreferredScrollableViewportSize(new Dimension(0, 200));
      scrollPane.getViewport().setBackground(Color.white);
      //scrollPane.setPreferredSize(new Dimension(100,220));
      scrollPane.setAlignmentX(LEFT_ALIGNMENT);
      scrollPane.setPreferredSize( new Dimension(430,300) );
      add(scrollPane, BorderLayout.CENTER);

      // Info on the server selected
      /*JPanel group1 = new JPanel(new GridLayout(1,1,0,0));
      group1.setAlignmentX(LEFT_ALIGNMENT);
      group1.setBackground(Color.white);
      ATextArea text1 = new ATextArea(".\n.\n");
      text1.setLineWrap(true);
      text1.setWrapStyleWord(true);
      text1.setEditable(false);
      text1.setAlignmentX(LEFT_ALIGNMENT);
      group1.add( text1 );
      stepPanel.add(group1);
      */
      //stepPanel.add(Box.createVerticalGlue());
      
    }
    
    /** Called each time the step is shown on screen.
     */
    protected void onShow(Object context, JWizard wizard) {
    }
  
    /** Called when the "Next" button is clicked.
     *  Use the wizard's setNextStep() method to set the next step to be displayed.
     *  @return return true to validate the "Next" button action, false to cancel it...
     */
    protected boolean onNext(Object context, JWizard wizard) {
      
      JAccountConnectionDialog jaconnect = new JAccountConnectionDialog( null,
                       currentServerConfig.getServerName(), currentServerConfig.getAccountServerPort(),
                       currentServerConfig.getServerID(), wizard);

      if ( jaconnect.hasSucceeded() ) {
        wizard.setContext(jaconnect.getPersonality());
        Debug.signal( Debug.NOTICE, null, "ClientManager connected to AccountServer");
        jaconnect.getPersonality().queueMessage(new AccountCreationMessage());
        await();
        
        return true;      
      } else {
        Debug.signal( Debug.NOTICE, null, "ClientManager ejected from AccountServer");
        ClientManager clientManager = ClientManager.getDefaultClientManager();
        clientManager.getScreenIntro().show(); // line added by Aldiss
        return false;
      }
    }
    
    /** Called when Previous button is clicked.
     *  Use the wizard's setNextStep() method to set the next step to be displayed.
     *  @return return true to validate the "Previous" button action, false to cancel it...
     */
    protected boolean onPrevious(Object context, JWizard wizard) {
      return false; // should never been reached
    }
  }

}
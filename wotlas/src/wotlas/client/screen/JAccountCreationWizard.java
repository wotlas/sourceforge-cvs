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

import wotlas.common.ServerConfig;
import wotlas.common.ServerConfigList;
import wotlas.common.ServerConfigListTableModel;

import wotlas.libs.wizard.*;
import wotlas.libs.wizard.step.*;

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
  
  /** Called when wizard is finished (after last step's end).
   */
   protected void onFinished(Object context) {
   	 Debug.signal( Debug.NOTICE, this, "Wizard finished.");
   	 //sendMessage(new AccountCreationEndedMessage());
   }

  /** Called when wizard is canceled ('cancel' button pressed).
   */
   protected void onCanceled(Object context) {
   	 Debug.signal( Debug.NOTICE, this, "Wizard canceled.");
   	 //sendMessage(new CancelAccountCreationMessage());
   }  
  
  /** Constructor
   */
  public JAccountCreationWizard() {
    super("Account creation wizard",420,410);
  }

 /*------------------------------------------------------------------------------------*/

 /**
  * First Step of our JWizard.
  * Choose a server.
  */
  public static class ServerSelectionStep extends JWizardStep {
    
    /** This is a static JWizardStep, to build it more simply this method
     *  returns the JWizardStepParameters needed for the JWizard.
     */
    public static JWizardStepParameters getStaticParameters() {
      JWizardStepParameters param = new JWizardStepParameters(
                            "wotlas.client.screen.JAccountWizard2$ServerSelectionStep",
                            "Step 1 - Server selection" );
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
      JPanel stepPanel = new JPanel();
      stepPanel.setAlignmentX(LEFT_ALIGNMENT);
      stepPanel.setBackground(Color.white);
      stepPanel.setPreferredSize( new Dimension(430,630) );
      
      setAlignmentX(LEFT_ALIGNMENT);
      JScrollPane scroll = new JScrollPane( stepPanel );
      scroll.setAlignmentX(LEFT_ALIGNMENT);
      scroll.setPreferredSize( new Dimension(450,430) );
      add(scroll);
      
      // List of servers
      ServerConfigListTableModel serverConfigListTabModel = new ServerConfigListTableModel(clientManager.getServerConfigList());
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
           /*ListSelectionModel lsm = (ListSelectionModel) e.getSource();
           if (lsm.isSelectionEmpty())
               return; //no rows were selected

           int selectedRow = lsm.getMinSelectionIndex();

          //selectedRow is selected
            currentServerConfig = serverConfigList.ServerConfigAt(selectedRow);

            if(htmlDescr==null)
               htmlDescr = new JHTMLWindow( screenIntro, "Wotlas Server", "text:"+currentServerConfig.toHTML(), 350, 250, false );
            else {
               htmlDescr.setText( currentServerConfig.toHTML() );
               if( !htmlDescr.isShowing() ) htmlDescr.show();
            }
          //Ignore extra messages.
            if (e.getValueIsAdjusting())
              return;

            //selectedRow is selected
            currentServerConfig = serverConfigList.ServerConfigAt(selectedRow);
            //serversTable.setToolTipText(currentServerConfig.getDescription());
            currentProfileConfig.setOriginalServerID(currentServerConfig.getServerID());
            currentProfileConfig.setServerID(currentServerConfig.getServerID());
            b_ok.setEnabled(true);
            */
        }
      });
      // show table
      JScrollPane scrollPane = new JScrollPane(serversTable);
      serversTable.setPreferredScrollableViewportSize(new Dimension(0, 100));
      scrollPane.getViewport().setBackground(Color.white);
      stepPanel.add(scrollPane);
      
      
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
      return true;      
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
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

import wotlas.libs.net.*;
import wotlas.utils.ALabel;
import wotlas.utils.Debug;
import wotlas.utils.SwingTools;
import wotlas.utils.Tools;

import java.awt.*;
import java.awt.event.*;

import javax.swing.ImageIcon;
import javax.swing.*;

/** A small utility to connect to a server using a JDialog.
 *
 * @author Aldiss, Petrus
 * @see wotlas.libs.net.NetClient
 */

public abstract class JConnectionDialog extends JDialog implements Runnable
{
 /*------------------------------------------------------------------------------------*/

    private ALabel l_info;    // information label
    private JButton b_cancel; // cancel button

    private boolean hasSucceeded; // has connection succeeded ?
    private NetPersonality personality;
    private NetClient client;

    private Frame frame;
    private String server, key;
    private int port;
    private Object context;

 /*------------------------------------------------------------------------------------*/

   /** Constructor. Displays the JDialog and immediately tries to connect to the specified
    *  server. It displays eventual error messages in pop-ups.
    *  The detail of the parameters is the following :
    * 
    * @param frame frame owner of this JDialog
    * @param server server name (DNS or IP address)
    * @param port server port
    * @param key server key for this connection ( see the javadoc header of this class ).
    * @param context context to set to messages ( see NetPersonality ).
    */

   public JConnectionDialog(Frame frame,String server,int port,String key, Object context) {
         super(frame,"Network Connection",true);

      // some inits
         this.frame = frame;
         this.server = server;
         this.port = port;
         this.key = key;
         this.context = context;

         hasSucceeded = false;
         getContentPane().setLayout( new BorderLayout() );
         getContentPane().setBackground(Color.white);
         
      // Top Label
         ALabel label1 = new ALabel("Trying to connect to "+server+":"+port+", please wait...",
                                    SwingConstants.CENTER );
         getContentPane().add( label1, BorderLayout.NORTH );

      // Center label
         l_info = new ALabel("Initializing Network Connection...", SwingConstants.CENTER);
         l_info.setPreferredSize( new Dimension( 200, 100 ) );
         getContentPane().add( l_info, BorderLayout.CENTER );

      // Cancel Button
         ImageIcon im_cancelup = new ImageIcon("..\\base\\gui\\cancel-up.gif");
         ImageIcon im_canceldo = new ImageIcon("..\\base\\gui\\cancel-do.gif");
         ImageIcon im_cancelun = new ImageIcon("..\\base\\gui\\cancel-un.gif");
         b_cancel = new JButton(im_cancelup);
         b_cancel.setRolloverIcon(im_canceldo);
         b_cancel.setPressedIcon(im_canceldo);
         b_cancel.setDisabledIcon(im_cancelun);
         b_cancel.setBorderPainted(false);
         b_cancel.setContentAreaFilled(false);
         b_cancel.setFocusPainted(false);
         //b_cancel = new JButton("Cancel Server Connection");
         getContentPane().add(b_cancel, BorderLayout.SOUTH );

          b_cancel.addActionListener(new ActionListener()
           {
              public void actionPerformed (ActionEvent e)
              {
                  if( client!=null )
                      client.stopConnectionProcess();
              }
           });

         pack();

         new Thread( this ).start();
         SwingTools.centerComponent(this);
         show();

   }

 /*------------------------------------------------------------------------------------*/

   /** Thread action
    */
    public void run() {

        do{
             Tools.waitTime( 1500 );
        }
        while( !isShowing() );


        tryConnection();
        dispose();
        System.out.println("fin");
    }

 /*------------------------------------------------------------------------------------*/

   /** To display en error message in a pop-up.
    */
    private void displayError( String error ) {
          JOptionPane.showMessageDialog( frame, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

 /*------------------------------------------------------------------------------------*/

   /** To try a connection
    */
    private void tryConnection() {
        client = new NetClient();

        String packages[] = getPackages();

        personality = client.connectToServer( server, port, key, context, packages );

        if(personality==null) {
             if( client.getErrorMessage()!=null )
                 displayError( client.getErrorMessage() );
             return;
         }

         l_info.setText( "Connection succeeded..." );
         hasSucceeded = true;

         Tools.waitTime( 1500 );
    }

 /*------------------------------------------------------------------------------------*/

   /** To retrieve a list of the NetMessage packages to use with this server.
    */
    abstract protected String[] getPackages();

 /*------------------------------------------------------------------------------------*/

   /** Has the connection succeeded ?
    *
    * @return true if we succesfully connected on the specified server.
    */
     public boolean hasSucceeded() {
     	 return hasSucceeded;
     }

 /*------------------------------------------------------------------------------------*/

   /** To get the created personality.
    *
    * @return the created NetPersonality.
    */
     public NetPersonality getPersonality() {
     	 return personality;
     }

 /*------------------------------------------------------------------------------------*/

}
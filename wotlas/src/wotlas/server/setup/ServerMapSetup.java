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
 
package wotlas.server.setup;

import wotlas.server.PersistenceManager;
import wotlas.common.universe.*;
import wotlas.common.*;
import wotlas.libs.log.*;
import wotlas.utils.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

 /** This a utility for server management: it displays all the buildings and let you
  * choose the server owner of each building.
  *
  * @author Aldiss
  */

public class ServerMapSetup extends WorldTree {

 /*------------------------------------------------------------------------------------*/

  /** Database Relative Path.
   */
    private final static String DATABASE_PATH = "../base";

  /** Our persistence manager
   */
    private static wotlas.server.PersistenceManager pm;

  /** Our worldMaps...
   */
    private static WorldMap worldMaps[];
    

  /** Static Init.
   */    
    static{
     // STEP 0 - Log Creation
        try {
           Debug.setPrintStream( new JLogStream( new javax.swing.JFrame(), "../log/server-map-setup.log", "../base/gui/log-title-dark.jpg" ) );
        } catch( java.io.FileNotFoundException e ) {
           e.printStackTrace();
           Debug.exit();
        }

        pm = wotlas.server.PersistenceManager.createPersistenceManager(DATABASE_PATH);
        worldMaps = pm.loadLocalUniverse(false);

        if( worldMaps==null ) {
            Debug.signal(Debug.FAILURE,null,"Error: couldn't load world data.");
            Debug.exit();
        }

        for( int i=0; i<worldMaps.length; i++ )
             if( worldMaps[i]!=null )
                 worldMaps[i].init();
    }

 /*------------------------------------------------------------------------------------*/

   /** Our JFrame
    */
     private JFrame frame;

 /*------------------------------------------------------------------------------------*/

  /** Constructor.
   * @param wManager worldManager to use to get the world data.
   */
    public ServerMapSetup( WorldManager wManager ) {
       super( wManager, BUILDING_LEVEL_ONLY );
       
       frame = new JFrame("Server Map Setup");

       frame.getContentPane().add(new JLabel("Double-click on a 'Building' to edit its serverID (server that owns it):"),BorderLayout.NORTH);
       frame.getContentPane().add( this, BorderLayout.CENTER );

       JPanel buttonPanel = new JPanel();
       frame.getContentPane().add( buttonPanel, BorderLayout.SOUTH );

         JButton bSave = new JButton("Save World Data.");

          bSave.addActionListener(new ActionListener() {
              public void actionPerformed (ActionEvent e) {
                  if( pm.saveLocalUniverse( worldMaps, true ) )
                      JOptionPane.showMessageDialog(frame, " World data saved", "Success", JOptionPane.INFORMATION_MESSAGE); 
                  else
                      JOptionPane.showMessageDialog(frame, " Failed to save world data.", "INFORMATION", JOptionPane.ERROR_MESSAGE);
              }
          });

         JButton bQuit = new JButton("Exit (without saving)");

          bQuit.addActionListener(new ActionListener() {
              public void actionPerformed (ActionEvent e) {
                  Debug.exit();
              }
          });

       buttonPanel.add( bSave, BorderLayout.WEST );
       buttonPanel.add( bQuit, BorderLayout.EAST );

       frame.pack();
       frame.setLocation(200,200);
       frame.show();
    }

 /*------------------------------------------------------------------------------------*/

   /** Method called when an element is clicked.
    *  Extend this method to provide here the process you want on the selected object.
    *  Note that the 'selected' is either a WorldMap, TownMap, Building, InteriorMap or Room.
    *
    *  By default here we don't do anything.
    *
    * @param selected object in the hierarchy.  You can cast it into a WorldMap, etc...
    */
    public void elementDoubleClicked( Object selectedObject ) {
    	if( !(selectedObject instanceof Building) )
    	    return;
    	
        String newID = JOptionPane.showInputDialog("Enter a new serverID for this building:"); 

        if(newID==null || newID.length()==0) return;

        try{
           int nID = Integer.parseInt( newID );

           Building b = (Building) selectedObject;
           b.setServerID(nID);        
        }
        catch(Exception e) {
           Debug.signal(Debug.ERROR, this,"ERROR: "+e.getMessage()+"\n  -> ServerID not saved.");
           return;
        }

        repaint();
    }

 /*------------------------------------------------------------------------------------*/

  /** Main
   */
    static public void main( String argv[] ) {

          Debug.signal(Debug.NOTICE,null,"Starting Server Map Setup...");

          WorldManager wManager = new WorldManager(worldMaps);
          new ServerMapSetup(wManager);
    }

 /*------------------------------------------------------------------------------------*/

}


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

package wotlas.client.screen;

import wotlas.client.ClientDirector;
import wotlas.utils.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;


/** JPanel that possesses & display various plugins located in the
 *  wotlas.client.screen.plugin package.
 *
 * @author Petrus, Aldiss
 */

public class JPlayerPanel extends JPanel implements MouseListener {

 /*------------------------------------------------------------------------------------*/ 

  /** Our TabbedPane where are stored the plug-ins.
   */
    private JTabbedPane playerTabbedPane;
  
 /*------------------------------------------------------------------------------------*/ 
  
  /** Consctructor.
   */
    public JPlayerPanel() {
      super();
      setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    
      playerTabbedPane = new JTabbedPane();
      add(playerTabbedPane);
    }

 /*------------------------------------------------------------------------------------*/ 

  /** To init the JPlayerPanel with the available plug-ins.
   */
    protected void init() {

       /** We load the available plug-ins
        *  WE ASSUME THAT WE ARE NOT IN A JAR FILE
        */
          File pluginFiles[] = new File( "wotlas/client/screen/plugin" ).listFiles();

          if( pluginFiles==null || pluginFiles.length==0 ) {
              Debug.signal( Debug.WARNING, this, "No plug-ins found in wotlas/client/screen/plugin !" );
              return;
          }

          for( int i=0; i<pluginFiles.length; i++ ) {

              if( !pluginFiles[i].isFile() || !pluginFiles[i].getName().endsWith(".class") 
                  || pluginFiles[i].getName().indexOf('$')>=0 )
                  continue;

           // We load the class file
              try{
                  String name = pluginFiles[i].getName();
                  Class cl = Class.forName( "wotlas.client.screen.plugin."
                                            + name.substring( 0, name.lastIndexOf(".class") ) );

                  if(cl==null || cl.isInterface())
                     continue;

                  Object o = cl.newInstance();

                  if( o==null || !(o instanceof JPanelPlugIn) )
                      continue;

                  JPanelPlugIn plugIn = (JPanelPlugIn) o;
                  
                  if( !plugIn.init() )
                      continue; // init failed

               // Ok, we have a valid plug-in
                  addPlugIn( plugIn, plugIn.getPlugInIndex() );
              }
              catch( Exception e ) {
                  Debug.signal( Debug.WARNING, this, e );
              }
          }

        Debug.signal(Debug.NOTICE,null,"Loaded "+playerTabbedPane.getTabCount()+" plug-ins...");
    }

 /*------------------------------------------------------------------------------------*/ 

  /** To add a plugin to our tabbed pane
   *  @param plugIn plug-in to add
   *  @param index index in the list, -1 means at the end.
   */
    protected void addPlugIn( JPanelPlugIn plugIn, int index ) {
         if(index<0 || index>playerTabbedPane.getTabCount()-1)
            playerTabbedPane.addTab( plugIn.getPlugInName(),
                                  ClientDirector.getResourceManager().getImageIcon("pin.gif"),
                                  plugIn,
                                  plugIn.getToolTipText() );
         else
            playerTabbedPane.insertTab( plugIn.getPlugInName(),
                                  ClientDirector.getResourceManager().getImageIcon("pin.gif"),
                                  plugIn,
                                  plugIn.getToolTipText(), index );
    }

 /*------------------------------------------------------------------------------------*/ 
  
  /** To get a PlugIn given its name.
   * @param plugInName plug-in name as returned by plugIn.getPlugInName()
   * @return plugIn, null if not found
   */
    public JPanelPlugIn getPlugIn( String plugInName ) {

        for(int i=0; i<playerTabbedPane.getTabCount();i++) {
            JPanelPlugIn plugIn = (JPanelPlugIn) playerTabbedPane.getComponentAt(i);

             if( plugIn.getPlugInName().equals(plugInName) )
                  return plugIn;
        }

        return null; // not found
    }

 /*------------------------------------------------------------------------------------*/

  /** To reset the JPlayerPanel and the state of all its plug-ins.
   */
    public void reset() {
        for(int i=0; i<playerTabbedPane.getTabCount();i++) {
            JPanelPlugIn plugIn = (JPanelPlugIn) playerTabbedPane.getComponentAt(i);
            plugIn.reset();
        }
    }
  
 /*------------------------------------------------------------------------------------*/

  /**
   * Invoked when the mouse button is clicked
   */
    public void mouseClicked(MouseEvent e) {}

  /**
   * Invoked when the mouse enters a component
   */
    public void mouseEntered(MouseEvent e) {}

  /**
   * Invoked when the mouse exits a component
   */
    public void mouseExited(MouseEvent e) {}

  /**
   * Invoked when a mouse button has been pressed on a component
   */
    public void mousePressed(MouseEvent e) {}

  /**
   * Invoked when a mouse button has been released on a component
   */
    public void mouseReleased(MouseEvent e) {}

 /*------------------------------------------------------------------------------------*/
}

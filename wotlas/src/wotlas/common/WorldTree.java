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
 
package wotlas.common;

import wotlas.common.universe.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;

 /** This class represents all the universe's locations as hierachical tree
  *  in a JPanel. To use it properly just extend this class and redefine the
  *  void elementClicked() method which is called when the user double-clicks
  *  on a location of the tree.
  *
  * @author Aldiss
  */

public class WorldTree extends JPanel {

 /*------------------------------------------------------------------------------------*/

  /** Level to display.
   */
    public final static byte WORLD_LEVEL_ONLY          = 0;
    public final static byte TOWN_LEVEL_ONLY           = 1;
    public final static byte BUILDING_LEVEL_ONLY       = 2;
    public final static byte INTERIORMAP_LEVEL_ONLY    = 3;
    public final static byte ALL_LEVELS                = 4;

 /*------------------------------------------------------------------------------------*/

  /** Our tree.
   */
    private JTree tree;

 /*------------------------------------------------------------------------------------*/

  /** Constructor. We display all the levels of the world hierarchy... ( ALL_LEVELS )
   * @param wManager worldManager to use to get the world data.
   */
    public WorldTree( WorldManager wManager ) {
       this( wManager, ALL_LEVELS );
    }

 /*------------------------------------------------------------------------------------*/

  /** Constructor. With an eventually limited hierarchy level.
   *
   * @param wManager worldManager to use to get the world data.
   * @param level level of the tree to display (see macros : ALL_LEVELS, etc... ).
   */
    public WorldTree( WorldManager wManager, byte level ) {
    	 super();

      // Tree Creation
         DefaultMutableTreeNode top = new DefaultMutableTreeNode("Universe");

         WorldMap wMaps[] = wManager.getWorldMaps();

         if(wMaps!=null)         
           for( int w=0; w<wMaps.length; w++ ) {
              if(wMaps[w]==null) continue;

              DefaultMutableTreeNode wNode = new DefaultMutableTreeNode(wMaps[w]);
              top.add(wNode);

              if(level==WORLD_LEVEL_ONLY) continue;

              TownMap tMaps[] = wMaps[w].getTownMaps();
              
              if(tMaps==null) continue;

            // We add TownMaps...
               for( int t=0; t<tMaps.length; t++ ) {
                  if(tMaps[t]==null) continue;
                  
                  DefaultMutableTreeNode tNode = new DefaultMutableTreeNode(tMaps[t]);
                  wNode.add(tNode);

                  if(level==TOWN_LEVEL_ONLY) continue;
             
                  Building bMaps[] = tMaps[t].getBuildings();
              
                  if(bMaps==null) continue;

               // We add TownMaps...
                   for( int b=0; b<bMaps.length; b++ ) {
                      if(bMaps[b]==null) continue;
                  
                      DefaultMutableTreeNode bNode = new DefaultMutableTreeNode(bMaps[b]);
                      tNode.add(bNode);

                      if(level==BUILDING_LEVEL_ONLY) continue;

                      InteriorMap iMaps[] = bMaps[b].getInteriorMaps();
              
                      if(iMaps==null) continue;

                   // We add TownMaps...
                       for( int i=0; i<iMaps.length; i++ ) {
                          if(iMaps[i]==null) continue;
                  
                          DefaultMutableTreeNode iNode = new DefaultMutableTreeNode(iMaps[i]);
                          bNode.add(iNode);

                          if(level==INTERIORMAP_LEVEL_ONLY) continue;
             
                          Room rMaps[] = iMaps[i].getRooms();
              
                          if(rMaps==null) continue;

                       // We add Rooms...
                           for( int r=0; r<rMaps.length; r++ ) {
                              if(rMaps[r]==null) continue;

                              DefaultMutableTreeNode rNode = new DefaultMutableTreeNode(rMaps[r]);
                              iNode.add(rNode);
                           }
                       }
                   }
               }
           }

      // Create a tree that allows one selection at a time.
         tree = new JTree(top);
         tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

         tree.addMouseListener( new MouseAdapter() {
              public void mousePressed(MouseEvent e) {
                  int selRow = tree.getRowForLocation(e.getX(), e.getY());
                  TreePath selPath = tree.getPathForLocation( e.getX(), e.getY() );

                  if(selRow==-1 || selPath==null) return;

                  DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();

                  if( e.getClickCount()==2 ) {
                      elementDoubleClicked( node.getUserObject() );
                  }
              }
         });
 

      // We add the components
         tree.setPreferredSize(new Dimension(500,500));
         add( new JScrollPane( tree ) );
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
    }

 /*------------------------------------------------------------------------------------*/

}


/* Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
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

package wotlas.editor;

import wotlas.client.screen.plugin.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import wotlas.utils.*;
import wotlas.libs.aswing.*;

import wotlas.libs.persistence.*;
import wotlas.libs.graphics2D.*;
import wotlas.common.*;

import wotlas.utils.Debug;

import wotlas.common.universe.*;

import wotlas.client.*;
import wotlas.client.screen.*;

import wotlas.libs.graphics2D.drawable.*;

import javax.swing.event.*;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeSelectionModel;

/** Plug In to add editor tools
 *
 * @author Aldiss, Fred McMaster, Diego
 */

public class EditorPlugIn extends JPanelPlugIn {
 /*------------------------------------------------------------------------------------*/ 
    
    transient static public byte selectedIsFree = 0;
    transient static public int selectedGroup = 0;
    transient static public int selectedGroupImgNr = 0;
    transient static private GraphicsDirector gDirector;
    transient static private WotlasLocation location;
    transient static public DefaultMutableTreeNode treeOfTileMapNode;

//    transient static public DefaultMutableTreeNode area = null;

  /** 'New' map button.
   */
    transient private AButton newMapButton;

  /** 'Save' map button.
   */
    transient private AButton saveMapButton;

  /** Center panel where the macros are set...
   */
    transient private JPanel centerPanel;
    
    
    /** Creates new form EditorPlugIn2 */
    public EditorPlugIn() {
        super();
        // treeOfTileMapNode = new DefaultMutableTreeNode("World : Tile Maps");
        initComponents();
        LoadTree();
        init();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jLabel14 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel34 = new javax.swing.JPanel();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        BigTree = new JTree( treeOfTileMapNode );
        mapData = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        buttonNew = new javax.swing.JButton();
        buttonSave = new javax.swing.JButton();
        buttonLoad = new javax.swing.JButton();
        buttonRefresh = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        DataAreaName = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        DataID = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        DataFullName = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        DataShortName = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        DataInsertionPoint = new javax.swing.JTextField();
        jPanel21 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        DataSmallImage = new javax.swing.JTextField();
        jPanel18 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        DataMusicName = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        DataGroupOfGraphics = GetNewGOGList();
        jPanel19 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        DataLength = new javax.swing.JTextField();
        jPanel22 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        DataHeight = new javax.swing.JTextField();
        jPanel16 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        DataMapSize = new javax.swing.JList();
        jPanel17 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        DataBasicSetId = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        DataBasicSetIdNr = new javax.swing.JTextField();
        jPanel32 = new javax.swing.JPanel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList1 = GetNewGOGList();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane1.setDividerLocation(100);
        jScrollPane2 = new javax.swing.JScrollPane();
        OneGroupList = OneGroupList();
        jScrollPane3 = new javax.swing.JScrollPane();
        HisTileList = HisTileList();
        jPanel1 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        Preview = new javax.swing.JLabel();
        TileNotFree = new javax.swing.JCheckBox();
        jPanel29 = new javax.swing.JPanel();
        jPanel31 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel25 = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        jPanel33 = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel24 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();

        jLabel14.setText("jLabel14");

        setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Editor Tools");
        jPanel2.add(jLabel1);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        jTabbedPane1.setName("");
        jPanel34.setLayout(new java.awt.BorderLayout());

        jTabbedPane5.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(BigTree);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane5.addTab("Map Tree", jPanel3);

        mapData.setLayout(new java.awt.BorderLayout());

        buttonNew.setText("New");
        buttonNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNewActionPerformed(evt);
            }
        });

        jPanel28.add(buttonNew);

        buttonSave.setText("Save");
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });

        jPanel28.add(buttonSave);

        buttonLoad.setText("Load");
        buttonLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLoadActionPerformed(evt);
            }
        });

        jPanel28.add(buttonLoad);

        buttonRefresh.setText("Refresh");
        buttonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshActionPerformed(evt);
            }
        });

        jPanel28.add(buttonRefresh);

        mapData.add(jPanel28, java.awt.BorderLayout.SOUTH);

        jLabel16.setText("Area Name");
        jPanel30.add(jLabel16);

        DataAreaName.setColumns(8);
        DataAreaName.setText("jTextField1");
        jPanel30.add(DataAreaName);

        jPanel4.add(jPanel30);

        jLabel2.setText("Id");
        jPanel12.add(jLabel2);

        DataID.setColumns(3);
        DataID.setEditable(false);
        DataID.setText("jTextField1");
        jPanel12.add(DataID);

        jPanel4.add(jPanel12);

        jLabel3.setText("Full Name");
        jPanel13.add(jLabel3);

        DataFullName.setColumns(8);
        DataFullName.setText("jTextField2");
        jPanel13.add(DataFullName);

        jPanel4.add(jPanel13);

        jLabel4.setText("Short Name");
        jPanel14.add(jLabel4);

        DataShortName.setColumns(8);
        DataShortName.setText("jTextField3");
        jPanel14.add(DataShortName);

        jPanel4.add(jPanel14);

        jLabel5.setText("Insertion Point");
        jPanel15.add(jLabel5);

        DataInsertionPoint.setColumns(8);
        DataInsertionPoint.setEditable(false);
        DataInsertionPoint.setText("jTextField4");
        jPanel15.add(DataInsertionPoint);

        jPanel4.add(jPanel15);

        jLabel6.setText("Small Image");
        jPanel21.add(jLabel6);

        DataSmallImage.setColumns(8);
        DataSmallImage.setText("jTextField5");
        DataSmallImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DataSmallImageActionPerformed(evt);
            }
        });

        jPanel21.add(DataSmallImage);

        jPanel4.add(jPanel21);

        jLabel7.setText("Music Name");
        jPanel18.add(jLabel7);

        DataMusicName.setColumns(8);
        DataMusicName.setText("jTextField6");
        jPanel18.add(DataMusicName);

        jPanel4.add(jPanel18);

        jPanel20.setLayout(new java.awt.GridLayout(1, 2));

        jLabel8.setText("Group Of Graphics");
        jPanel20.add(jLabel8);

        DataGroupOfGraphics.setPreferredSize(new java.awt.Dimension(100, 50));
        jPanel20.add(DataGroupOfGraphics);

        jPanel4.add(jPanel20);

        jLabel9.setText("Length");
        jPanel19.add(jLabel9);

        DataLength.setColumns(8);
        DataLength.setText("jTextField7");
        DataLength.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DataLengthActionPerformed(evt);
            }
        });

        jPanel19.add(DataLength);

        jPanel4.add(jPanel19);

        jLabel10.setText("Height");
        jPanel22.add(jLabel10);

        DataHeight.setText("jTextField8");
        DataHeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DataHeightActionPerformed(evt);
            }
        });

        jPanel22.add(DataHeight);

        jPanel4.add(jPanel22);

        jPanel16.setLayout(new java.awt.BorderLayout());

        jLabel11.setText("Map Tile Size");
        jPanel16.add(jLabel11, java.awt.BorderLayout.WEST);

        DataMapSize.setMaximumSize(new java.awt.Dimension(30, 20));
        DataMapSize.setMinimumSize(new java.awt.Dimension(50, 20));
        DataMapSize.setPreferredSize(new java.awt.Dimension(100, 50));
        jPanel16.add(DataMapSize, java.awt.BorderLayout.EAST);

        jPanel4.add(jPanel16);

        jLabel12.setText("Basic Tile Set Id");
        jPanel17.add(jLabel12);

        DataBasicSetId.setColumns(8);
        DataBasicSetId.setEditable(false);
        DataBasicSetId.setText("jTextField9");
        jPanel17.add(DataBasicSetId);

        jPanel4.add(jPanel17);

        jLabel13.setText("Nr. basic image");
        jPanel11.add(jLabel13);

        DataBasicSetIdNr.setColumns(8);
        DataBasicSetIdNr.setEditable(false);
        DataBasicSetIdNr.setText("jTextField10");
        jPanel11.add(DataBasicSetIdNr);

        jPanel4.add(jPanel11);

        mapData.add(jPanel4, java.awt.BorderLayout.CENTER);

        jTabbedPane5.addTab("TileMap", mapData);

        jPanel34.add(jTabbedPane5, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("TileMaps", jPanel34);

        jPanel32.setLayout(new java.awt.BorderLayout());

        jTabbedPane4.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane4.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel5.setEnabled(false);
        jPanel5.add(jPanel6, java.awt.BorderLayout.SOUTH);

        jPanel7.setLayout(new java.awt.BorderLayout());

        jPanel7.setEnabled(false);
        jList1.setLayoutOrientation(javax.swing.JList.VERTICAL_WRAP);
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(jList1);

        jPanel7.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        jPanel5.add(jPanel7, java.awt.BorderLayout.CENTER);

        jTabbedPane4.addTab("Group", jPanel5);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jPanel9.add(jPanel23);

        jPanel8.add(jPanel9, java.awt.BorderLayout.SOUTH);

        jPanel10.setLayout(new java.awt.BorderLayout());

        OneGroupList.setLayoutOrientation(javax.swing.JList.VERTICAL_WRAP);
        OneGroupList.setPreferredSize(new java.awt.Dimension(80, 100));
        OneGroupList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(OneGroupList);

        jSplitPane1.setLeftComponent(jScrollPane2);

        jScrollPane3.setPreferredSize(new java.awt.Dimension(80, 300));
        HisTileList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        HisTileList.setPreferredSize(new java.awt.Dimension(80, 100));
        HisTileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(HisTileList);

        jSplitPane1.setRightComponent(jScrollPane3);

        jPanel10.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jPanel8.add(jPanel10, java.awt.BorderLayout.CENTER);

        jTabbedPane4.addTab("Single", jPanel8);

        jPanel32.add(jTabbedPane4, java.awt.BorderLayout.CENTER);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Selected :");
        jPanel1.add(jLabel15);

        Preview.setPreferredSize(new java.awt.Dimension(50, 50));
        jPanel1.add(Preview);

        TileNotFree.setBackground(new java.awt.Color(255, 255, 255));
        TileNotFree.setText("Not Free");
        TileNotFree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TileNotFreeActionPerformed(evt);
            }
        });

        jPanel1.add(TileNotFree);

        jPanel32.add(jPanel1, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.addTab("Graphics", jPanel32);

        jTabbedPane1.addTab("Connect Exit", jPanel29);

        jPanel31.setLayout(new java.awt.BorderLayout());

        jTabbedPane2.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane2.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane2.addTab("Npc", jPanel25);

        jTabbedPane2.addTab("Item", jPanel27);

        jPanel31.add(jTabbedPane2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Create", null, jPanel31, "null");

        jPanel33.setLayout(new java.awt.BorderLayout());

        jTabbedPane3.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane3.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane3.addTab("Npc", jPanel24);

        jTabbedPane3.addTab("Item", jPanel26);

        jPanel33.add(jTabbedPane3, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Add to Map", jPanel33);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    private void buttonNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNewActionPerformed
        gDirector.removeAllDrawables();
        TileMap origin = EditTile.getDataManager().getWorldManager().getTileMap( location );
        TileMap destination = EditTile.getDataManager().getWorldManager().getWorldMapFromID(0).addNewTileMap();
        destination.initNewTileMap( EditTile.getDataManager().getWorldManager().getWorldMapFromID(0) );
        location = destination.getLocation();
        System.out.println( " new location "+location.toString() );
        destination.setAreaName("");
        destination.setFullName("new full name");
        destination.setShortName("shortname");
        destination.setInsertionPoint( origin.getInsertionPoint() );
        destination.setSmallTileMapImage( origin.getSmallTileMapImage() );
        destination.setMusicName( origin.getMusicName() );
        destination.selectGroupOfGraphics( GroupOfGraphics.ROGUE_SET );
        TileManagerFlat manager = new TileManagerFlat(  destination );
        manager.setMap( 10, 20, TileMap.PIXEL_32
        , origin.getManager().getBasicFloorId()
        , origin.getManager().getBasicFloorNr() );
        destination.setManager( (TileMapManager)manager );
        EditTile.workingOnThisTileMap = destination;
        EditTile.getDataManager().myMapData.initDisplayEditor( EditTile.getDataManager(), location );
    }//GEN-LAST:event_buttonNewActionPerformed

    private void buttonLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLoadActionPerformed
        gDirector.removeAllDrawables();
        EditTile.getDataManager().myMapData.initDisplayEditor( EditTile.getDataManager(), location );
    }//GEN-LAST:event_buttonLoadActionPerformed

    private void buttonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefreshActionPerformed
        gDirector.removeAllDrawables();
        EditTile.getDataManager().myMapData.initDisplayEditor( EditTile.getDataManager(), location );
    }//GEN-LAST:event_buttonRefreshActionPerformed

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        try{
            System.out.println( " saving location "+location.toString() );
            TileMap tileMap = EditTile.getDataManager().getWorldManager().getTileMap( location );
            EditTile.workingOnThisTileMap.setAreaName( DataAreaName.getText() );
            EditTile.workingOnThisTileMap.setFullName( DataFullName.getText() );
            EditTile.workingOnThisTileMap.setShortName( DataShortName.getText() );
            // DataInsertionPoint.setText("xxx");
            // DataSmallImage.setText("xxx");
            DataMusicName.setText(EditTile.workingOnThisTileMap.getMusicName());
            // DataBasicSetId.setText( "" + EditTile.workingOnThisTileMap.getManager().getBasicFloorId() );
            // DataBasicSetIdNr.setText( "" + EditTile.workingOnThisTileMap.getManager().getBasicFloorNr() );
        } catch (Exception e) {
            System.out.println("not saved as");
            return;
        }
        EditTile.letsTryToSave( location.getTileMapID() );
    }//GEN-LAST:event_buttonSaveActionPerformed

    private void TileNotFreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TileNotFreeActionPerformed
        if( !TileNotFree.isSelected() )
            setFreeTileOrNot( TileMap.TILE_FREE );
        else
            setFreeTileOrNot( TileMap.TILE_NOT_FREE );
    }//GEN-LAST:event_TileNotFreeActionPerformed

    private void DataHeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DataHeightActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_DataHeightActionPerformed

    private void DataSmallImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DataSmallImageActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_DataSmallImageActionPerformed

    private void DataLengthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DataLengthActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_DataLengthActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList DataGroupOfGraphics;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTextField DataBasicSetIdNr;
    private javax.swing.JTextField DataHeight;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JList DataMapSize;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JList OneGroupList;
    private javax.swing.JTextField DataBasicSetId;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel mapData;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JTree BigTree;
    private javax.swing.JList jList1;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JButton buttonSave;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JTextField DataMusicName;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList HisTileList;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel Preview;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JTextField DataFullName;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton buttonNew;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton buttonLoad;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField DataShortName;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField DataLength;
    private javax.swing.JTextField DataSmallImage;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JButton buttonRefresh;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JTextField DataID;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JTextField DataAreaName;
    private javax.swing.JTextField DataInsertionPoint;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JCheckBox TileNotFree;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JLabel jLabel10;
    // End of variables declaration//GEN-END:variables
    
 /*------------------------------------------------------------------------------------*/

  /** Called once to initialize the plug-in.
   *  @return if true we display the plug-in, return false if something fails during
   *          this init(), this way the plug-in won't be displayed.
   */
    public boolean init() {
        location = new WotlasLocation();
        location.WotlasLocationChangeToTileMap(0);
        EditTile.workingOnThisTileMap = EditTile.getDataManager().getWorldManager().getTileMap( location );
        DataID.setText("xxx");
        DataAreaName.setText(EditTile.workingOnThisTileMap.getAreaName());
        DataFullName.setText(EditTile.workingOnThisTileMap.getFullName());
        DataShortName.setText(EditTile.workingOnThisTileMap.getShortName());
        DataInsertionPoint.setText("xxx");
        DataSmallImage.setText("xxx");
        DataMusicName.setText(EditTile.workingOnThisTileMap.getMusicName());
        //  doing this lose the right object
        // DataGroupOfGraphics = GetNewGOGList();
        DataLength.setText( "" + EditTile.workingOnThisTileMap.getMapSize().width );
        DataHeight.setText( "" + EditTile.workingOnThisTileMap.getMapSize().height );
        // DataMapTileSize.setText();
        DataBasicSetId.setText( "" + EditTile.workingOnThisTileMap.getManager().getBasicFloorId() );
        DataBasicSetIdNr.setText( "" + EditTile.workingOnThisTileMap.getManager().getBasicFloorNr() );
        return true; // this plug-in always works...
    }

 /*------------------------------------------------------------------------------------*/

    /** Called when we need to reset the content of this plug-in.
    */
    public void reset() {
        // We remove the previous content
    	init();
    }

   /** Returns the name of the plug-in that will be displayed in the JPlayerPanel.
    * @return a short name for the plug-in
    */
      public String getPlugInName() {
      	  return "Editor";
      }

 /*------------------------------------------------------------------------------------*/

   /** Returns the name of the plug-in's author.
    * @return author name.
    */
      public String getPlugInAuthor() {
          return "Wotlas Team (Aldiss & Fred & Diego)";
      }

 /*------------------------------------------------------------------------------------*/

   /** Returns the tool tip text that will be displayed in the JPlayerPanel.
    * @return a short tool tip text
    */
      public String getToolTipText() {
          return "To create/change TileMap";
      }

 /*------------------------------------------------------------------------------------*/

   /** Eventual index in the list of JPlayerPanels
    * @return -1 if the plug-in has to be added at the end of the plug-in list,
    *         otherwise a positive integer for a precise location.
    */
      public int getPlugInIndex() {
          return -1;
      }

 /*------------------------------------------------------------------------------------*/

   /** Tells if this plug-in is a system plug-in that represents some base
    *  wotlas feature.
    * @return true means system plug-in, false means user plug-in
    */
    public boolean isSystemPlugIn() {
        return false;
    }
      
    DefaultListModel listModel1,listModel2,listModel3;
    
    public JList GetNewGOGList() {
        ImageLibrary imageLib = EditTile.getDataManager().getImageLibrary();
        JLabel tmp;

        listModel1 = new DefaultListModel();
        tmp = new JLabel("Demo Set", GroupOfGraphics.DEMO_SET[0].getAsIcon(0, imageLib), JLabel.LEFT);
        listModel1.addElement( tmp );
        tmp = new JLabel("Rogue Like", GroupOfGraphics.ROGUE_SET[0].getAsIcon(0, imageLib), JLabel.LEFT );
        listModel1.addElement( tmp );

      	JList list = new JList();
        list = new JList(listModel1);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.setCellRenderer(new RendIcon());
        list.setVisibleRowCount(5);
        list.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if( !lsm.getValueIsAdjusting() ) {
                    
                }
                // System.out.println( " diego: value: " + lsm.getLeadSelectionIndex() );
            }
        });
        return list;
    }

    class RendIcon extends JLabel implements ListCellRenderer {
        public Component getListCellRendererComponent(
        JList list,
        Object value,            // value to display
        int index,               // cell index
        boolean isSelected,      // is the cell selected
        boolean cellHasFocus)    // the list and the cell have the focus
        {
            JLabel tmp = (JLabel) value;
            setText( tmp.getText() );
            setIcon( tmp.getIcon() );
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            setBackground(isSelected ? Color.cyan : Color.white);
            setForeground(isSelected ? Color.black : Color.black);
            return this;
        }
    }

    public void SetModel2(int index) {
        ImageLibrary imageLib = EditTile.getDataManager().getImageLibrary();
        JLabel tmp;
        listModel2.removeAllElements();
        for( int i=0; i < GroupOfGraphics.ROGUE_SET.length; i++) {
            tmp = new JLabel("Rogue", GroupOfGraphics.ROGUE_SET[i].getAsIcon(0, imageLib), JLabel.LEFT );
            listModel2.addElement( tmp );
        }
    }

    public JList OneGroupList() {
        listModel2 = new DefaultListModel();
        SetModel2(0);
                
      	JList list = new JList();
        list = new JList(listModel2);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.setCellRenderer(new RendIcon());
        list.setVisibleRowCount(5);    
        list.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if( !lsm.getValueIsAdjusting() ) {
                    selectedGroup = lsm.getLeadSelectionIndex();
                    SetModel3( lsm.getLeadSelectionIndex() );
                }
            }
        });
        return list;
    }

    public void SetModel3(int index) {
        ImageLibrary imageLib = EditTile.getDataManager().getImageLibrary();
        JLabel tmp;
        listModel3.removeAllElements();
        for( int i=0; i < GroupOfGraphics.ROGUE_SET[index].totalImage(); i++) {
            tmp = new JLabel( GroupOfGraphics.ROGUE_SET[index].getAsIcon(i, imageLib), JLabel.LEFT );
            listModel3.addElement( tmp );
        }
    }

    public JList HisTileList() {
        listModel3 = new DefaultListModel();
        SetModel3(0);
        
      	JList list = new JList();
        list = new JList(listModel3);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.setCellRenderer(new RendIcon());
        list.setVisibleRowCount(5);
        list.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if( !lsm.getValueIsAdjusting() ) {
                    ImageLibrary imageLib = EditTile.getDataManager().getImageLibrary();
                    selectedGroupImgNr = lsm.getLeadSelectionIndex();
                    Preview.setIcon( GroupOfGraphics.ROGUE_SET[selectedGroup].getAsIcon(selectedGroupImgNr, imageLib) );
                }
            }
        });
        return list;
    }
    
    static public void AddIt(int x, int y){
        TileMap tileMap = EditTile.workingOnThisTileMap;
        Drawable background = null;             // background image
        background = (Drawable) new MotionlessSprite( x*tileMap.getMapTileDim().width, // ground x=0
            y*tileMap.getMapTileDim().height,       // ground y=0
            tileMap.getGroupOfGraphics()[tileMap.getManager().getMapBackGroundData()[x][y][0]],  // GroupOfGraphics
            tileMap.getManager().getMapBackGroundData()[x][y][1],        // number of internal tile
            ImageLibRef.SECONDARY_MAP_PRIORITY      // priority
        );
        gDirector.addDrawable( background );
    }

    static public void rememberTheGDirector(GraphicsDirector value){
        gDirector = value;
    }

    static public void setFreeTileOrNot(byte value){
        selectedIsFree = value;
    }

    public void LoadTree() {
        // createNodes(treeOfTileMapNode);
        BigTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        BigTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                   BigTree.getLastSelectedPathComponent();

                if (node == null) return;

                Object nodeInfo = node.getUserObject();
                if (node.isLeaf()) {
                    TreeMapInfo item = (TreeMapInfo)nodeInfo;
                    
                    location = new WotlasLocation();
                    location.WotlasLocationChangeToTileMap( item.Id );
                    gDirector.removeAllDrawables();
                    EditTile.getDataManager().myMapData.initDisplayEditor( EditTile.getDataManager(), location );
                } else {
                    // .....
                }
            }
        });        
//        treeView.setMinimumSize(minimumSize);
    }

    static public DefaultMutableTreeNode createNode( TileMap value ) {
        DefaultMutableTreeNode map = null;
        map = new DefaultMutableTreeNode( new TreeMapInfo( value.getFullName(), value.tileMapID ) );
        return map;
    }
}
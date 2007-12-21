/* Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2003 WOTLAS Team
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

package wotlas.client.screen.plugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import wotlas.utils.*;
import wotlas.libs.aswing.*;

import wotlas.common.*;
import wotlas.common.message.description.PlayerPastMessage;
import wotlas.client.*;
import wotlas.client.screen.*;
import wotlas.common.character.*;

/** Plug In that shows information on the selected player and enables the
 *  local player to set his/her past.
 *
 * @author Diego
 */
public class AttributesPlugIn extends JPanelPlugIn  {

    static public int BARSIZE = 80;
    
    /** Creates new form AttributesPlugIn */
    public AttributesPlugIn() {
        super();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jTabbArea = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        panAttrib = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jFlags = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        setBackground(new java.awt.Color(204, 204, 204));
        jTabbArea.setBackground(new java.awt.Color(204, 204, 204));
        jTabbArea.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panAttrib.setLayout(null);

        panAttrib.setBackground(new java.awt.Color(204, 204, 204));
        panAttrib.setMaximumSize(new java.awt.Dimension(200, 1000));
        jScrollPane3.setViewportView(panAttrib);

        jPanel1.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jTabbArea.addTab("Stats", jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setBackground(new java.awt.Color(204, 204, 204));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jFlags.setBackground(new java.awt.Color(204, 204, 204));
        jFlags.setForeground(new java.awt.Color(255, 102, 102));
        jScrollPane2.setViewportView(jFlags);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jTabbArea.addTab("Flags", jPanel2);

        add(jTabbArea, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea jFlags;
    private javax.swing.JTabbedPane jTabbArea;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panAttrib;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
    
   /** Returns the name of the plug-in that will be displayed in the JPlayerPanel.
    * @return a short name for the plug-in
    */
      public String getPlugInName() {
      	  return "Attributes";
      }

 /*------------------------------------------------------------------------------------*/

   /** Returns the name of the plug-in's author.
    * @return author name.
    */
      public String getPlugInAuthor() {
          return "Wotlas Team (Diego)";
      }

 /*------------------------------------------------------------------------------------*/

   /** Returns the tool tip text that will be displayed in the JPlayerPanel.
    * @return a short tool tip text
    */
      public String getToolTipText() {
          return "Selected Player Attributes Information";
      }

 /*------------------------------------------------------------------------------------*/

   /** Eventual index in the list of JPlayerPanels
    * @return -1 if the plug-in has to be added at the end of the plug-in list,
    *         otherwise a positive integer for a precise location.
    */
      public int getPlugInIndex() {
          return 0;
      }

 /*------------------------------------------------------------------------------------*/

   /** Tells if this plug-in is a system plug-in that represents some base
    *  wotlas feature.
    * @return true means system plug-in, false means user plug-in
    */
      public boolean isSystemPlugIn() {
      	  return true;
      }

  /** Called once to initialize the plug-in.
   *  @return if true we display the plug-in, return false if something fails during
   *          this init(), this way the plug-in won't be displayed.
   */
    public boolean init() {
        initComponents();
        PlayerImpl player = ClientDirector.getDataManager().getMyPlayer();

        /*  show attributes */
        int[] data = player.getBasicChar().showMaskCharAttributes();
        JPanel tmp;
        JPanel box;
        JPanel boxFill;
        JLabel text;
        int spaceCounter = 0;
        
        // only to debug
//        for(int index = 0; index < CharData.ATTR_LAST_ATTR; index++){
//            System.out.println( " debug this : "+player.getBasicChar().getCharAttrWihDescr(index) );
//        }

        for(int index = 0; index < CharData.ATTR_LAST_ATTR; index++){
            if( !MaskTools.isSet(data,index) )
                continue;
            // no means to show a stat with 0 : cause a division by zero.
            if( player.getBasicChar().getCharAttrMax(index) == 0 )
                continue;
            tmp = new JPanel();
            // tmp.setLayout( null );
            box = new JPanel();
            boxFill = new JPanel();
            // box.setBorder( new javax.swing.border.LineBorder(new java.awt.Color(0, 204, 204)) );
            box.setLayout( null );
            box.setForeground( Color.blue );
            box.setBackground( Color.black );
            box.setPreferredSize( new Dimension(BARSIZE,10) );
            box.setSize( new Dimension(BARSIZE,10) );
            boxFill.setForeground( Color.red );
            boxFill.setBackground( Color.red );
            boxFill.setSize( new Dimension( -1 + new Integer( player.getBasicChar().getCharAttrActual(index)*BARSIZE
            / player.getBasicChar().getCharAttrMax(index) ).intValue(),9) );
//            boxFill.setSize( new Dimension( 80, 9 ) );
            text = new JLabel(CharData.ATTR_NAMES[index]);
            text.setFont(new java.awt.Font("Dialog", 1, 12));
            tmp.add( text );
            tmp.add( box );
            box.add( boxFill );
            text = new JLabel(""+player.getBasicChar().getCharAttrActual(index));
            text.setFont(new java.awt.Font("Dialog", 1, 12));
            tmp.add( text );
            tmp.setLocation( 3, 5+(spaceCounter*18) );
            tmp.setSize( new Dimension(160,20) );
            panAttrib.add(tmp);
            spaceCounter++;
        }

        /*  show flags (if any) */
        String textToShow = new String("");
        for(int index = 0; index < CharData.FLAG_LAST_FLAG; index++){
            if( !player.getBasicChar().isFlagSet(index) )
                continue;
            textToShow += CharData.FLAG_NAMES[index]+"\n";
        }
        jFlags.setFont(new java.awt.Font("Dialog", 1, 12));
        jFlags.setText(textToShow);
        return true; // nothing special to init...
    }

 /*------------------------------------------------------------------------------------*/

  /** Called when we need to reset the content of this plug-in.
   */
    public void reset() {
        removeAll();
        revalidate();
        /*
        jTabbArea.removeAll();
        remove(jTabbArea);
        revalidate();
        jFlags = null;
        jTabbArea = null;
        jPanel2 = null;
        jScrollPane2 = null;
        jPanel1 = null;
        panAttrib = null;
        jScrollPane3 = null;
         */
        init();
        revalidate();
    }
}
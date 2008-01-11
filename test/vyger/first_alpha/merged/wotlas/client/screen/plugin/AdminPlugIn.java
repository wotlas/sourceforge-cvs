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

import java.awt.Cursor;
import wotlas.client.ClientDirector;
import wotlas.client.DataManager;
import wotlas.client.screen.JPanelPlugIn;
import wotlas.common.action.BasicAction;
import wotlas.common.action.CastAction;

/** Plug In that shows information on the selected player and enables the
 *  local player to set his/her past.
 *
 * @author Diego
 */
public class AdminPlugIn extends JPanelPlugIn {

    /** Creates new form AttributesPlugIn */
    public AdminPlugIn() {
        super();
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        this.jPanel1 = new javax.swing.JPanel();
        this.MobCrea1 = new javax.swing.JButton();
        this.ItemCrea1 = new javax.swing.JButton();
        this.summDwarfKing = new javax.swing.JButton();
        this.summDwarfCleric = new javax.swing.JButton();
        this.summDwarfWizard = new javax.swing.JButton();
        this.attackCommand = new javax.swing.JButton();
        this.plasmaBoltSpell = new javax.swing.JButton();
        this.moveHere = new javax.swing.JButton();
        this.jPanel21 = new javax.swing.JPanel();
        this.jLabel11 = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        this.jPanel1.setLayout(null);

        this.MobCrea1.setText("Monster creation");
        this.MobCrea1.setToolTipText("Monster Creation");
        this.MobCrea1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MobCrea1ActionPerformed(evt);
            }
        });

        this.jPanel1.add(this.MobCrea1);
        this.MobCrea1.setBounds(10, 10, 130, 20);

        this.ItemCrea1.setText("Item creation");
        this.ItemCrea1.setToolTipText("Item creation");
        this.ItemCrea1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemCrea1ActionPerformed(evt);
            }
        });

        this.jPanel1.add(this.ItemCrea1);
        this.ItemCrea1.setBounds(10, 30, 130, 20);

        this.summDwarfKing.setText("dwarf king");
        this.summDwarfKing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summDwarfKingActionPerformed(evt);
            }
        });

        this.jPanel1.add(this.summDwarfKing);
        this.summDwarfKing.setBounds(10, 70, 110, 20);

        this.summDwarfCleric.setText("dwarf cleric");
        this.summDwarfCleric.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summDwarfClericActionPerformed(evt);
            }
        });

        this.jPanel1.add(this.summDwarfCleric);
        this.summDwarfCleric.setBounds(10, 90, 110, 20);

        this.summDwarfWizard.setText("dwarf wizard");
        this.summDwarfWizard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summDwarfWizardActionPerformed(evt);
            }
        });

        this.jPanel1.add(this.summDwarfWizard);
        this.summDwarfWizard.setBounds(10, 110, 110, 20);

        this.attackCommand.setText("attack");
        this.attackCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attackCommandActionPerformed(evt);
            }
        });

        this.jPanel1.add(this.attackCommand);
        this.attackCommand.setBounds(10, 140, 70, 20);

        this.plasmaBoltSpell.setText("Plasma bolt");
        this.plasmaBoltSpell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plasmaBoltSpellActionPerformed(evt);
            }
        });

        this.jPanel1.add(this.plasmaBoltSpell);
        this.plasmaBoltSpell.setBounds(10, 170, 101, 20);

        this.moveHere.setText("come Here!");
        this.moveHere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveHereActionPerformed(evt);
            }
        });

        this.jPanel1.add(this.moveHere);
        this.moveHere.setBounds(10, 200, 99, 20);

        add(this.jPanel1, java.awt.BorderLayout.CENTER);

        this.jLabel11.setForeground(new java.awt.Color(255, 0, 0));
        this.jLabel11.setText("Use only for test");
        this.jPanel21.add(this.jLabel11);

        add(this.jPanel21, java.awt.BorderLayout.NORTH);

    }//GEN-END:initComponents

    private void moveHereActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveHereActionPerformed
        ClientDirector.getDataManager().commandRequest = DataManager.COMMAND_CAST;
        ClientDirector.getDataManager().getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        ClientDirector.getDataManager().commandAction = CastAction.getCastAction(CastAction.CAST_COMEHERE);
    }//GEN-LAST:event_moveHereActionPerformed

    private void plasmaBoltSpellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plasmaBoltSpellActionPerformed
        ClientDirector.getDataManager().commandRequest = DataManager.COMMAND_CAST;
        ClientDirector.getDataManager().getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        ClientDirector.getDataManager().commandAction = CastAction.getCastAction(CastAction.CAST_ADMIN_PLASMA);
    }//GEN-LAST:event_plasmaBoltSpellActionPerformed

    private void attackCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attackCommandActionPerformed
        ClientDirector.getDataManager().commandRequest = DataManager.COMMAND_BASIC;
        ClientDirector.getDataManager().getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        ClientDirector.getDataManager().commandAction = BasicAction.getBasicAction(BasicAction.BASIC_ATTACK);
    }//GEN-LAST:event_attackCommandActionPerformed

    private void summDwarfWizardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_summDwarfWizardActionPerformed
        ClientDirector.getDataManager().commandRequest = DataManager.COMMAND_CAST;
        ClientDirector.getDataManager().getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        ClientDirector.getDataManager().commandAction = CastAction.getCastAction(CastAction.CAST_ADMIN_SUMMON4);
    }//GEN-LAST:event_summDwarfWizardActionPerformed

    private void summDwarfClericActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_summDwarfClericActionPerformed
        ClientDirector.getDataManager().commandRequest = DataManager.COMMAND_CAST;
        ClientDirector.getDataManager().getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        ClientDirector.getDataManager().commandAction = CastAction.getCastAction(CastAction.CAST_ADMIN_SUMMON3);
    }//GEN-LAST:event_summDwarfClericActionPerformed

    private void summDwarfKingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_summDwarfKingActionPerformed
        ClientDirector.getDataManager().commandRequest = DataManager.COMMAND_CAST;
        ClientDirector.getDataManager().getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        ClientDirector.getDataManager().commandAction = CastAction.getCastAction(CastAction.CAST_ADMIN_SUMMON2);
    }//GEN-LAST:event_summDwarfKingActionPerformed

    private void ItemCrea1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemCrea1ActionPerformed
        // Add your handling code here:
        System.out.println("Calling item creation I");
        ClientDirector.getDataManager().commandRequest = DataManager.COMMAND_CAST;
        ClientDirector.getDataManager().getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        ClientDirector.getDataManager().commandAction = CastAction.getCastAction(CastAction.CAST_ADMIN_CREATE);
    }//GEN-LAST:event_ItemCrea1ActionPerformed

    private void MobCrea1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MobCrea1ActionPerformed
        // Add your handling code here:
        System.out.println("Calling mob creation I");
        // avvisare l'inventory del player e il panel della mappa.
        ClientDirector.getDataManager().commandRequest = DataManager.COMMAND_CAST;
        ClientDirector.getDataManager().getClientScreen().getMapPanel().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        ClientDirector.getDataManager().commandAction = CastAction.getCastAction(CastAction.CAST_ADMIN_SUMMON);
    }//GEN-LAST:event_MobCrea1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton summDwarfKing;
    private javax.swing.JButton summDwarfWizard;
    private javax.swing.JButton MobCrea1;
    private javax.swing.JButton plasmaBoltSpell;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JButton summDwarfCleric;
    private javax.swing.JButton moveHere;
    private javax.swing.JButton attackCommand;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton ItemCrea1;

    // End of variables declaration//GEN-END:variables

    /** Returns the name of the plug-in that will be displayed in the JPlayerPanel.
     * @return a short name for the plug-in
     */
    @Override
    public String getPlugInName() {
        return "Admin Tools";
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the name of the plug-in's author.
     * @return author name.
     */
    @Override
    public String getPlugInAuthor() {
        return "Wotlas Team (Diego)";
    }

    /*------------------------------------------------------------------------------------*/

    /** Returns the tool tip text that will be displayed in the JPlayerPanel.
     * @return a short tool tip text
     */
    @Override
    public String getToolTipText() {
        return "Admin tools to test program";
    }

    /*------------------------------------------------------------------------------------*/

    /** Eventual index in the list of JPlayerPanels
     * @return -1 if the plug-in has to be added at the end of the plug-in list,
     *         otherwise a positive integer for a precise location.
     */
    @Override
    public int getPlugInIndex() {
        return 0;
    }

    /*------------------------------------------------------------------------------------*/

    /** Tells if this plug-in is a system plug-in that represents some base
     *  wotlas feature.
     * @return true means system plug-in, false means user plug-in
     */
    @Override
    public boolean isSystemPlugIn() {
        return true;
    }

    /** Called once to initialize the plug-in.
     *  @return if true we display the plug-in, return false if something fails during
     *          this init(), this way the plug-in won't be displayed.
     */
    @Override
    public boolean init() {
        return true; // nothing special to init...
    }

    /*------------------------------------------------------------------------------------*/

    /** Called when we need to reset the content of this plug-in.
     */
    @Override
    public void reset() {
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
    }
}
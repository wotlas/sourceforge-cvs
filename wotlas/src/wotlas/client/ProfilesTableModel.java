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

package wotlas.client;

import javax.swing.table.AbstractTableModel;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.*;

/** An AbstractTableModel to diplay the client's profiles in a JTable
 *
 * @author Petrus
 * @see wotlas.client.Profile
 * @see wotlsa.client.ProfilesConfig
 */
public class ProfilesTableModel extends AbstractTableModel {
  final String[] columnNames = {"login", "server", "serial"};
  Profile profiles[];    
  
  ProfilesTableModel(Profile profiles[]) {
    this.profiles = profiles;
  }
  
  public int getColumnCount() {
    return columnNames.length;
  }
    
  public int getRowCount() {
    return profiles.length;
  }

  public String getColumnName(int col) {
    return columnNames[col];
  }  
    
  public Object getValueAt(int row, int col) {
    switch(col) {
      case 0:
        return this.profiles[row].getLogin();
        
      case 1:
        return this.profiles[row].getServerName();        
        
      case 2:
        return this.profiles[row].getSerial();
        
      default:
        return null;
    }
  }

  /* JTable uses this method to determine the default renderer/
   * editor for each cell
   */  
  /*public Class getColumnClass(int c) {
    String dummy = "";
    return dummy.getClass();
  }*/
  
}  
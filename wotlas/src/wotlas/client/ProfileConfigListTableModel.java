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

import wotlas.common.ServerConfig;
import wotlas.common.ServerConfigManager;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/** An AbstractTableModel to display the client's profiles in a JTable
 *
 * @author Petrus
 * @see wotlas.client.ProfileConfig
 * @see wotlas.common.ServerConfig
 */

public class ProfileConfigListTableModel extends AbstractTableModel {

 /*------------------------------------------------------------------------------------*/

  /** Array of client's profiles
   */
  private ProfileConfig profiles[];

  /** Array of servers
   */
  private ServerConfigManager servers;

  /** temporary server
   */
  private ServerConfig serverConfig;

  /** Names of the table columns
   */
  private final String[] columnNames = {"name", "server", "key"};

 /*------------------------------------------------------------------------------------*/

  /** Constructor
   *
   * @param profileConfigList client's accounts
   * @param serverConfigList list of servers
   */
  public ProfileConfigListTableModel( ProfileConfigList profileConfigList,
                                      ServerConfigManager serverConfigList) {
    this.profiles = profileConfigList.getProfiles();
    if(profiles==null) profiles = new ProfileConfig[0];
    
    this.servers  = serverConfigList;
  }

 /*------------------------------------------------------------------------------------*/

  /** To get the number of columns
   */
  public int getColumnCount() {
    return columnNames.length;
  }

  /** To get the number of rows
   */
  public int getRowCount() {
    return profiles.length;
  }

  /** To get a column name
   *
   * @param col index of column
   */
  public String getColumnName(int col) {
    return columnNames[col];
  }

  /** To get the value of a cell
   *
   * @param row index of cell row
   * @param col index of cell column
   */
  public Object getValueAt(int row, int col) {
    switch(col) {
      case 0:
        return profiles[row].getPlayerName();

      case 1:
        serverConfig = servers.getServerConfig(profiles[row].getServerID());
        return serverConfig.getServerName();

      case 2:
        return profiles[row].getLogin()+"-"+profiles[row].getKey();

      default:
        return null;
    }
  }

 /*------------------------------------------------------------------------------------*/

}  
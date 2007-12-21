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

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import wotlas.libs.net.NetConnection;

/** To display various system info...
 * 
 * @author petrus, aldiss
 */
public class JSystemInfoPanel extends JPanel {

    /*------------------------------------------------------------------*/

    /** Ping Panel
     */
    public static final byte GRAPHIC_PING_PANEL = 0;

    /** Memory Panel
     */
    public static final byte MEMORY_PANEL = 1;

    /*------------------------------------------------------------------*/

    /** Selected Panel Type.
     */
    private static byte selectedType = 0; // default 0

    /** The default JSystemInfoPanel used.
     */
    private static JSystemInfoPanel defaultSystemInfo;

    /*------------------------------------------------------------------*/

    /** Ping Panel (is appart because the ping panel is necessary to wotlas)
     */
    protected GraphicPingPanel pingPanel;

    /** Other system JPanel ( JMemory, etc... )
     */
    protected JPanel systemPanel;

    /*------------------------------------------------------------------*/

    /** To change the displayed SystemInfo panel. This method should be called
     *  from the Option Dialog.
     * @param SytemInfoPanel type : GRAPHIC_PING_PANEL, MEMORY_PANEL, ...
     */
    public static void setSelectedPanel(byte type) {
        if (JSystemInfoPanel.defaultSystemInfo == null || type == JSystemInfoPanel.selectedType)
            return; // nothing to change

        // Stop previous System Panel
        switch (JSystemInfoPanel.selectedType) {
            case GRAPHIC_PING_PANEL:
                break; // nothing to do the ping panel must stay alive...

            case MEMORY_PANEL:
                ((JMemory) JSystemInfoPanel.defaultSystemInfo.systemPanel).stop();
                break;
        }

        // Start new one
        switch (type) {
            case GRAPHIC_PING_PANEL:
                JSystemInfoPanel.defaultSystemInfo.add(JSystemInfoPanel.defaultSystemInfo.pingPanel, BorderLayout.CENTER);
                break;

            case MEMORY_PANEL:
                JSystemInfoPanel.defaultSystemInfo.systemPanel = new JMemory();
                JSystemInfoPanel.defaultSystemInfo.add(JSystemInfoPanel.defaultSystemInfo.systemPanel, BorderLayout.CENTER);
                break;
        }

        JSystemInfoPanel.selectedType = type;
    }

    /*------------------------------------------------------------------*/

    /** Constructor. Default System Panel is a Ping Panel.
     */
    public JSystemInfoPanel() {
        super(new BorderLayout());
        JSystemInfoPanel.defaultSystemInfo = this;
        setBackground(Color.white);
        this.pingPanel = new GraphicPingPanel();
        add(this.pingPanel, BorderLayout.CENTER);
    }

    /*------------------------------------------------------------------*/

    /** To initialize properly the current system info Panel.
     * @param connection a valid Network Connection
     */
    public void init(NetConnection connection) {
        connection.setPingListener(this.pingPanel);

        // extra inits
        switch (JSystemInfoPanel.selectedType) {
            case GRAPHIC_PING_PANEL:
                break;

            case MEMORY_PANEL:
                break;
        }
    }

    /*------------------------------------------------------------------*/

}

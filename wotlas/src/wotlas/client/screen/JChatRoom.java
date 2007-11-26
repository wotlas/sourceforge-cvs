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
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import wotlas.client.ClientDirector;
import wotlas.client.DataManager;
import wotlas.common.Player;
import wotlas.common.PlayerState;
import wotlas.common.chat.ChatRoom;

/** Swing Chat Room where messages are displayed...
 *
 * @author Petrus, Aldiss
 */

public class JChatRoom extends JPanel implements MouseListener {

    /*------------------------------------------------------------------------------------*/

    /** ChatRoom display.
     */
    private JPanel chatTab;

    /** Panel where messages appear.
     */
    private JChatDisplay chatDisplay;

    /** JList of ChatRoom players.
     */
    private JList playersJList;
    private DefaultListModel playersListModel;

    /** Array of players.
     */
    private Hashtable players;

    /** Key of selected player.
     */
    static public String selectedPlayerKey;

    /*------------------------------------------------------------------------------------*/

    /** Constructor.<br>
     *  To get ChatRoom component to display in player's interface
     *
     * @param chatRoom the chatRoom to display
     */
    JChatRoom(ChatRoom chatRoom) {
        //super(false);
        super(true);
        setName(chatRoom.getPrimaryKey());
        setLayout(new BorderLayout());
        this.players = new Hashtable(2);

        // EAST (List of ChatRoom players)
        this.playersListModel = new DefaultListModel();

        this.playersJList = new JList(this.playersListModel);

        this.playersJList.addMouseListener(this);

        this.playersJList.setFixedCellWidth(100);

        this.playersJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        PlayersListRenderer playersListRenderer = new PlayersListRenderer();
        //    if( selectedPlayer.getPlayerAwayMessage()!=null && !selectedPlayer.isConnectedToGame() ) {
        this.playersJList.setCellRenderer(playersListRenderer);

        JScrollPane listScroller = new JScrollPane(this.playersJList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        try {
            this.chatDisplay = new JChatDisplay(chatRoom);
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        add("Center", this.chatDisplay.getPanel());

        add("East", listScroller);

        appendText("<font color='green'><i>New chat created.</i></font>");

    }

    /*------------------------------------------------------------------------------------*/

    /** To add some players to the JList.
     */
    /*synchronized public void addPlayers(PlayerImpl players[]) {
      for (int i=0; i<players.length; i++) {
        // we detect non valid entries
       	 if(players[i]==null ||  this.players.containsKey( players[i].getPrimaryKey() ))
      	    continue;

        // ok, we add this one...
           playersListModel.addElement(players[i].getFullPlayerName());
           this.players.put( players[i].getPrimaryKey(), players[i] );
      }
    }*/

    /** To add a player to the JList.
     */
    synchronized public void addPlayer(String primaryKey, String senderFullName) {
        if (this.players.containsKey(primaryKey))
            return; // already in this chat
        if (DataManager.SHOW_DEBUG)
            System.out.println("ADDING PLAYER " + primaryKey);

        Hashtable playersTable = ClientDirector.getDataManager().getPlayers();
        Player newPlayer = (Player) playersTable.get(primaryKey);

        final PlayerState newPlayerItem;

        /*if(newPlayer!=null)
           newPlayerItem = new PlayerState(senderFullName, newPlayer.isConnectedToGame());
        else
           newPlayerItem = new PlayerState(senderFullName, true );
        */
        if (newPlayer != null)
            newPlayerItem = new PlayerState(senderFullName, newPlayer.getPlayerState().value);
        else {
            newPlayerItem = new PlayerState(senderFullName, PlayerState.CONNECTED);
        }

        this.players.put(primaryKey, newPlayerItem);

        if (newPlayer != null && newPlayer.isConnectedToGame()) {
            Runnable runnable = new Runnable() {
                public void run() {
                    if (!newPlayerItem.fullName.equals(ClientDirector.getDataManager().getMyPlayer().getFullPlayerName()))
                        appendText("<font color='green'>" + newPlayerItem.fullName + " entered the chat...</font>");
                    JChatRoom.this.playersListModel.addElement(newPlayerItem);
                    revalidate();
                    repaint();
                }
            };
            SwingUtilities.invokeLater(runnable);
        } else {
            Runnable runnable = new Runnable() {
                public void run() {
                    JChatRoom.this.playersListModel.addElement(newPlayerItem);
                    revalidate();
                    repaint();
                }
            };
            SwingUtilities.invokeLater(runnable);
        }
    }

    /** To remove a player from the JList.
     */
    synchronized public void removePlayer(String primaryKey) {
        if (!this.players.containsKey(primaryKey))
            return; // not in this chat
        if (DataManager.SHOW_DEBUG)
            System.out.println("REMOVING PLAYER " + primaryKey);

        final PlayerState oldPlayerItem = (PlayerState) this.players.get(primaryKey);
        this.players.remove(primaryKey);

        Runnable runnable = new Runnable() {
            public void run() {
                if (!oldPlayerItem.fullName.equals(ClientDirector.getDataManager().getMyPlayer().getFullPlayerName()))
                    appendText("<font color='green'><i>" + oldPlayerItem.fullName + " left the chat...</i></font>");
                JChatRoom.this.playersListModel.removeElement(oldPlayerItem);
                revalidate();
                repaint();
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    /** To update a player's full name from the JList.
     */
    synchronized public void updatePlayer(String primaryKey, String newName) {
        if (!this.players.containsKey(primaryKey))
            return; // not in this chat
        if (DataManager.SHOW_DEBUG)
            System.out.println("UPDATING PLAYER " + primaryKey);

        final PlayerState oldPlayerItem = (PlayerState) this.players.get(primaryKey);
        //final PlayerState newPlayerItem = new PlayerState(newName, oldPlayerItem.isNotAway);
        final PlayerState newPlayerItem = new PlayerState(newName, oldPlayerItem.value);
        this.players.put(primaryKey, newPlayerItem);

        Runnable runnable = new Runnable() {
            public void run() {
                JChatRoom.this.playersListModel.removeElement(oldPlayerItem);
                JChatRoom.this.playersListModel.addElement(newPlayerItem);
                revalidate();
                repaint();
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    /** To update a player's state from the JList.
     */
    /*synchronized public void updatePlayer(String primaryKey, boolean isNotAway) {
      if( !players.containsKey( primaryKey ) )
          return; // not in this chat
      if (DataManager.SHOW_DEBUG)
        System.out.println("UPDATING PLAYER "+primaryKey);

      final PlayerState oldPlayerItem = (PlayerState) players.get(primaryKey);
      final PlayerState newPlayerItem = new PlayerState(oldPlayerItem.fullName, isNotAway);
      players.put(primaryKey, newPlayerItem);

      Runnable runnable = new Runnable() {
        public void run() {
          playersListModel.removeElement(oldPlayerItem);
          playersListModel.addElement(newPlayerItem);
          revalidate();
          repaint();
        }
      };
      SwingUtilities.invokeLater( runnable );
    }*/

    /** To update a player's state from the JList.
     */
    synchronized public void updatePlayer(String primaryKey, byte value) {
        if (!this.players.containsKey(primaryKey))
            return; // not in this chat
        if (DataManager.SHOW_DEBUG)
            System.out.println("UPDATING PLAYER " + primaryKey);

        final PlayerState oldPlayerItem = (PlayerState) this.players.get(primaryKey);
        final PlayerState newPlayerItem = new PlayerState(oldPlayerItem.fullName, value);
        this.players.put(primaryKey, newPlayerItem);

        Runnable runnable = new Runnable() {
            public void run() {
                JChatRoom.this.playersListModel.removeElement(oldPlayerItem);
                JChatRoom.this.playersListModel.addElement(newPlayerItem);
                revalidate();
                repaint();
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    /** To remove all players from JList.
     */
    synchronized public void removeAllPlayers() {
        this.players.clear();

        Runnable runnable = new Runnable() {
            public void run() {
                JChatRoom.this.playersListModel.removeAllElements();
                revalidate();
                repaint();
            }
        };
        SwingUtilities.invokeLater(runnable);

    }

    public Hashtable getPlayers() {
        return this.players;
    }

    /*------------------------------------------------------------------------------------*/

    public void appendText(String s) {
        this.chatDisplay.appendText(s);
    }

    /*------------------------------------------------------------------------------------*/

    /**
     * Invoked when the mouse button is clicked
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Invoked when the mouse enters a component
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component
     */
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on a component
     */
    public void mouseReleased(MouseEvent e) {

        if (DataManager.SHOW_DEBUG)
            System.out.println("[JChatRoom] : clic sur (" + e.getX() + "," + e.getY() + ")");
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        } else {
            if (DataManager.SHOW_DEBUG)
                System.out.println("\tleft clic");
            String selectedPlayerName = ((PlayerState) this.playersJList.getSelectedValue()).fullName;
            ClientDirector.getDataManager().getClientScreen().getChatPanel().setInputBoxText("/to:" + selectedPlayerName + ":");
        }

    }

    class PlayersListRenderer extends JLabel implements ListCellRenderer {
        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.

        public Component getListCellRendererComponent(JList list, Object value, // value to display
        int index, // cell index
        boolean isSelected, // is the cell selected
        boolean cellHasFocus) // the list and the cell have the focus
        {
            String s = ((PlayerState) value).fullName;
            setText(s);

            this.setBackground(list.getBackground());

            /*
            if ( ((PlayerState) value).isNotAway )
            this.setForeground(list.getForeground());
            else
            this.setForeground(Color.gray);
            */

            if (((PlayerState) value).value == PlayerState.CONNECTED)
                this.setForeground(list.getForeground());
            else if (((PlayerState) value).value == PlayerState.DISCONNECTED)
                this.setForeground(Color.lightGray);
            else
                this.setForeground(Color.gray);

            this.setEnabled(list.isEnabled());
            this.setFont(list.getFont());
            return this;
        }
    }

}

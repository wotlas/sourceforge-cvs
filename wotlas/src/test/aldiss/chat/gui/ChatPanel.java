

package chat.gui;

import chat.common.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import wotlas.libs.net.*;


public class ChatPanel extends JPanel implements NetConnectionListener
{
  /** max number of messages to display on screen at the same time */
      static final private int MAX_DISPLAYED_MESSAGES = 25;

  /** Components of this chat */
      private JLabel label;
      private JTextField t_entry;
      private JButton b_ok;
      private JTextPane  t_chat;

  /** Chat document */
      private DefaultStyledDocument doc_chat;
      private SimpleAttributeSet attribut;

  /** messages number */
      private int mess_number;

  /** Reader nickname */
      private String pseudo;

  /** Our NetPersonality
   */
      NetPersonality personality;

  /****************************************************************************/
  /****************************** CONSTRUCTOR *********************************/
  /****************************************************************************/

    /** Creates a chat panel
     *
     * @param width chat's width
     * @param height chat's height
     */

    public ChatPanel(int width, int height )
    {
       super();

       // inits
          pseudo = null;
          mess_number = 0;

          Font f_text = new Font("Serif", Font.BOLD, 12);
          Font f_chat = new Font("Dialog", Font.BOLD, 12);

       // Panel creation
          setPreferredSize(new Dimension(width,height));
          setLayout(new BorderLayout());
       
       // Chat Title
          JPanel j_title = new JPanel();
          j_title.setLayout(new FlowLayout());
          j_title.setOpaque(false);

          JLabel l_title = new JLabel("Chat - Moteur Réseau Wotlas");
          l_title.setFont(new Font("Serif",Font.BOLD|Font.ITALIC,14));
          l_title.setForeground(new Color(70,70,120));
          l_title.setOpaque(false);

          j_title.add(l_title);
          add(j_title,BorderLayout.NORTH);
       
       // Chat messages space
          doc_chat = new DefaultStyledDocument();
          t_chat = new JTextPane( doc_chat );

          JScrollPane scroll = new JScrollPane(t_chat);
          add(scroll,BorderLayout.CENTER);

          attribut = new SimpleAttributeSet();
          StyleConstants.setFontSize(attribut,12);

          t_chat.setEditable(false);
          t_chat.setFont(f_chat);
       
       // User Panel
          JPanel user_panel = new JPanel();
          user_panel.setLayout(new BorderLayout());
          user_panel.setOpaque(false);
          add(user_panel,BorderLayout.SOUTH);
       
       // label
          label = new JLabel("Entrez votre pseudo:");
          label.setOpaque(false);
          user_panel.add(label,BorderLayout.NORTH);

       // textfield
          t_entry = new JTextField(20);
          t_entry.getCaret().setVisible(true);
          t_entry.setFont(f_text);
          t_entry.setForeground(new Color(50,100,150));
          user_panel.add(t_entry,BorderLayout.CENTER);

            t_entry.addKeyListener(new KeyAdapter()
            {
              public void keyReleased(KeyEvent e)
              {
                  if( e.getKeyCode()==KeyEvent.VK_ENTER )
                     okAction();
              }
	    });
       
       // button
          b_ok = new JButton("OK");
          user_panel.add(b_ok,BorderLayout.EAST);   

             b_ok.addActionListener(new ActionListener()
             {
                public void actionPerformed (ActionEvent e)
                {
                   okAction();
                }
             });

    }


 /******************************************************************************/

  /** Adds some text to the chat space...
   *
   * @param text the text to add...
   */

  synchronized public void addChatText(String text)
  {
    // too much messages displayed ?
       mess_number++;

       if( mess_number>MAX_DISPLAYED_MESSAGES )
         try
         {
            int pos = doc_chat.getText(0,doc_chat.getLength()).indexOf("\n");
            doc_chat.remove(0,pos+1);
            mess_number--;
	 }
         catch(BadLocationException e)  {
              System.out.println("Chat Error:"+e.getMessage());
         } 

    // text color
          StyleConstants.setForeground( attribut, Color.blue );

       try{
             doc_chat.insertString (doc_chat.getLength(), text+"\n", attribut );
       }
       catch(BadLocationException e) {
               e.printStackTrace();
               return;
       }

     // TRICK TRICK TRICK TRICK TRICK

     // we want the scrollbars to move when some text is added...
       if(isShowing())
         t_chat.setCaretPosition( doc_chat.getLength() );

     // TRICK TRICK TRICK TRICK TRICK
  }

 /******************************************************************************/

  /** action when the user wants to send a message
   */ 

   synchronized private void okAction()
   {
     String text = t_entry.getText();
     String message;

        if(text.length()==0 || personality==null)
           return;

      // first message ? (pseudo)
        if(pseudo==null) {
            pseudo = text+"> ";
            label.setText("Entrez vos messages :");

            message = pseudo+"---connected---";
        }
        else
            message = pseudo+text;


      // we send the message
         personality.queueMessage( new ChatClientMessage( message ) );

      // entry reset
         t_entry.setText("");
   }


  /****************************************************************************/

    public void connectionCreated( NetPersonality personality ) {
         this.personality = personality;
    }

    public void connectionClosed( NetPersonality personality ) {
         this.personality = null;
         System.exit(0);
    }

  /****************************************************************************/
 
}

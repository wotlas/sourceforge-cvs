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

package wotlas.libs.log;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

/** A JLogStream prints messages to a log file every three minutes and also prints
 *  messages on a JTextArea in a JDialog.
 *
 * @author Aldiss
 * @see wotlas.libs.log.LogStream
 */

public class JLogStream extends LogStream {
 /*------------------------------------------------------------------------------------*/

   /** Max number of messages we display...
    */
     private static final int MAX_MSG = 20;

 /*------------------------------------------------------------------------------------*/

   /** Our CroppedWindow
    */
     private JCroppedWindow dialog;
     
   /** Our JTextArea
    */
     private JTextArea logArea;

   /** Our image.
    */ 
     private Image image;

   /** Number of messages displayed
    */
     private int numberOfMsg;

 /*------------------------------------------------------------------------------------*/

   /** Constructor with file name. The log is saved to disk every 3 minutes.
    *  Example : new JLogStream( frame, "client.log", "back.jpg", "../data/gui" );<br>
    *
    *  The "back.jpg" image given in the example is taken from "../data/gui".
    *
    * @param owner frame parent
    * @param logFileName log file to create or use if already existing.
    * @param imageName image to display. The image is taken from the guiImagesPath.
    * @param guiImagesPath path to the gui images.
    * @exception FileNotFoundException if we cannot use or create the given log file.
    */
     public JLogStream( Frame owner, String logFileName, String imageFileName,
                        String guiImagesPath )
     throws FileNotFoundException {
          super( logFileName, false, 180*1000 );

            if(imageFileName.indexOf("dark")<0)
               dialog = new JCroppedWindow( owner, "Wotlas Log Window", false, guiImagesPath );
            else
               dialog = new JCroppedWindow( owner, "Wotlas Log Window", true, guiImagesPath );

       // 1 - image panel
          image = dialog.loadImage( guiImagesPath+File.separator+imageFileName );

          JPanel imPanel = new JPanel( true ) {
                public void paint( Graphics g ) {
                   g.drawImage( image, 0, 0, dialog );
                }          
          };

          imPanel.setPreferredSize( new Dimension( image.getWidth(null),
                                                   image.getHeight(null) ) );

          dialog.getContentPane().add( imPanel, BorderLayout.NORTH );
          dialog.setBackground( Color.white );

       // 2 - log text area
          logArea = new JTextArea("Starting log timer...\n");
          logArea.setFont( new Font( "Monospaced", Font.PLAIN, 10 ) );
          logArea.setForeground( new Color( 100,100,100 ) );
          logArea.setPreferredSize( new Dimension( image.getWidth(null), 90 ) );
          logArea.setEditable(false);

          JScrollPane scrollPane = new JScrollPane( logArea,
             JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );


          dialog.getContentPane().add( scrollPane, BorderLayout.CENTER );
          scrollPane.setPreferredSize( new Dimension( image.getWidth(null), 80 ) );

       // 3 - event management
          dialog.addWindowListener( new WindowAdapter() {
     	    public void windowClosing( WindowEvent e ) {
     	    	JLogStream.this.flush();
                dialog = null;
            }
          });

       // 4 - display
          dialog.pack();

          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          dialog.setLocation( (int) ((screenSize.getWidth() - dialog.getWidth()) / 2),
                   (int) ((screenSize.getHeight() - dialog.getHeight()) / 2) );

          dialog.show();
          waitTime(1000);
     }

 /*------------------------------------------------------------------------------------*/

  /** Method called each time text is added to the stream.
   *  Useful if you want to display the log somewhere else.
   *
   * @param x text just printed to log.
   */
    protected void printedText( final String x ) {
        if(logArea==null || dialog==null || !logArea.isShowing() || x==null || x.length()==0 )
           return;

    // how many lines in this message ?
       int nbLines=0;
       int cur=0;

       do{
          nbLines++;
          cur = x.indexOf( "\n", cur );
          
          if(cur<0) break;
          cur++;
       }while( cur>0 );

    // too much messages displayed ?
       numberOfMsg +=nbLines;

      Runnable runnable = new Runnable() {
        public void run() {
           while( numberOfMsg > MAX_MSG ) {
              int pos = logArea.getText().indexOf( "\n");

              if(pos>=0) {
                  logArea.setText( logArea.getText().substring(pos+1,logArea.getText().length() ) );
                  numberOfMsg--;
              }
              else
                  break;
           }

           if(logArea.isShowing()){
              logArea.append( x+"\n" );
              logArea.setPreferredSize( new Dimension( image.getWidth(dialog), numberOfMsg*16 ) );
           }

        // we want the scrollbars to move when some text is added...
           if(logArea.isShowing())
              logArea.setCaretPosition( logArea.getText().length() );
        }
      };

      SwingUtilities.invokeLater( runnable );
    }

 /*------------------------------------------------------------------------------------*/

  /** Waits ms milliseconds with a very low CPU use.
   *
   * @param ms number of milliseconds to wait.
   */
    public void waitTime( long ms ) {
      Object o = new Object();
    
       synchronized( o ) {
          try{
               o.wait(ms);
          }
          catch(InterruptedException e) {}
       }
    }

 /*------------------------------------------------------------------------------------*/

}

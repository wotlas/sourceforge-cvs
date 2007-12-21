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

package wotlas.client.gui;

import wotlas.common.ResourceManager;
import wotlas.utils.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import javax.swing.border.*;

import java.net.*;
import java.io.*;


 /** A JDialog to display HTML ( URL or text file ).
  *
  * @author Aldiss
  */

public class JHTMLWindow extends JDialog implements ActionListener {
 /*------------------------------------------------------------------------------------*/

  /** Max Repaints (in the JDK1.3 images are badly displayed, we need to call repaint
   *  a few times ).
   */
    public static int MAX_REPAINTS = 20;

 /*------------------------------------------------------------------------------------*/

  /** JEditorPane to display the HTML text.
   */
    private JEditorPane html;

  /** Timer to repaint the JEditorPaint a few times...
   */
    private Timer timer;

  /** number of repaints...
   */
    private int nbRepaints = 0;

 /*------------------------------------------------------------------------------------*/

   /** Constructor with HTML file name/URL. (no header, not modal)
    * @param frame parent frame
    * @param title JDialog title
    * @param fileName HTML filename. If the file begins with "http:" we assume it's an URL
    *        otherwise we consider its a local file.
    * @param width initial window width
    * @param height initial window height
    * @param center tells if the JDialog must be centered on screen
    * @param rManager where to get images resource
    */
    public JHTMLWindow( Frame frame, String title, String fileName, int width, int height,
                        boolean center, ResourceManager rManager ) {
        this( frame, null, title, fileName, width, height, center, false, rManager );
    }

 /*------------------------------------------------------------------------------------*/

   /** Constructor with HTML file name/URL header & modal mode.
    * @param frame parent frame
    * @param header text to display before the html document
    * @param title JDialog title
    * @param fileName HTML filename. If the file begins with "http:" we assume it's an URL
    *        otherwise we consider its a local file. If the file begins with "text:" we assume
    *        the fileName contains the text to display so we just display the "fileName" string.
    * @param width initial window width
    * @param height initial window height
    * @param center tells if the JDialog must be centered on screen
    * @param modal modal window ?
    * @param rManager where to get images resource
    */
    public JHTMLWindow(Frame frame, String header, String title, String fileName, int width, int height,
                       boolean center, boolean modal, ResourceManager rManager ) {
        super( frame, title, modal );
        String htmlText = null;
        URL url = null;

        if(header==null) header="";

     // We load the html file
        if( fileName.startsWith( "http:" ) ) {
           // URL
              try{
                 url = new URL( fileName );
              }
              catch(Exception e){
                 htmlText = "<b>ERROR</b><br>Failed to open URL: <i>"+fileName+"</i><p>An exception occured: <i>"+e.getMessage()+"</i>";
              }
        }
        else if( fileName.startsWith( "text:" ) )
              htmlText = fileName.substring(5,fileName.length());
        else if( rManager.inJar() )
              url = getClass().getResource(fileName);
        else try{
                url = new File(fileName).toURL();
             }catch(Exception e) {
                e.printStackTrace();
             }

        if( htmlText == null && url==null)
            htmlText = "<b>ERROR</b><br>Could not open file: <i>"+fileName+"</i>";

         htmlText = header+htmlText;

      // JDialog properties
         getContentPane().setLayout(new BorderLayout());
         getContentPane().setBackground(Color.white);
         setBackground(Color.white);

      // We load the images
         ImageIcon im_okup = rManager.getImageIcon("ok-up.gif");
         ImageIcon im_okdo = rManager.getImageIcon("ok-do.gif");

      // OK Button
         JButton b_ok = new JButton(im_okup);
         b_ok.setRolloverIcon(im_okdo);
         b_ok.setPressedIcon(im_okdo);
         b_ok.setBorderPainted(false);
         b_ok.setContentAreaFilled(false);
         b_ok.setFocusPainted(false);

         b_ok.addActionListener(new ActionListener() {
             public void actionPerformed (ActionEvent e) {
             	if(timer!=null)
                   timer.stop();
                dispose();
             }
         });

         getContentPane().add(b_ok,BorderLayout.SOUTH);

      // JEDITOR PANE
         try { 
         	 if(url==null)
                    html = new JEditorPane("text/html",htmlText);
                 else {
                    html = new JEditorPane();
                    html.setPage(url);
                 }

                 html.addHyperlinkListener(createHyperLinkListener());
         } catch (IOException e) { 
               html = new JEditorPane( "text/html","<b>ERROR</b><br><i>"+e.getMessage()+"</i> unreachable.");
         }

         html.setEditable(false);
         html.setBorder(new EmptyBorder(0, 0, 0, 0) );               
         html.setPreferredSize(new Dimension(width-30,height));

         JScrollPane scroller = new JScrollPane(html);
         scroller.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
         scroller.setPreferredSize(new Dimension(width,height));
         getContentPane().add(scroller,BorderLayout.CENTER);

      // Display
         pack();
         
         if(center)
            SwingTools.centerComponent( this );
         else
            setLocation( 100, 100 );

         show();

         timer = new Timer(500,this);
         timer.start();
    }

 /*------------------------------------------------------------------------------------*/

   /** To set the html text of the JEDitorPane
    */
     public void setText( String htmlText ) {
     	html.setText( htmlText );
     }

 /*------------------------------------------------------------------------------------*/

   /** To add a listener to get HyperLinks...
    */
     public HyperlinkListener createHyperLinkListener()
     { 
        return new HyperlinkListener()
                   { 
                        public void hyperlinkUpdate(HyperlinkEvent e)
                        { 
                           if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                           { 
                              if (e instanceof HTMLFrameHyperlinkEvent)
                              { 
                                ( (HTMLDocument) html.getDocument() ).processHTMLFrameHyperlinkEvent( 
                                                                      (HTMLFrameHyperlinkEvent) e ); 
                              }
                              else
                              { 
                                  try { 
                                          html.setPage(e.getURL());
                                          
                                          if(timer!=null) {
                                             timer.stop();
                                             nbRepaints=0;
                                             timer.start();
                                          }
                                  } catch (IOException ioe) { 
                                          html.setText( "<b>ERROR</b><br>Failed to open URL: <i>"
                                                       +e.getURL()+"</i><p>An exception occured: <i>"
                                                       +ioe.getMessage()+"</i>" );
                                  } 
                              } 
                           } 
                        } 
                    }; 
     } 

 /*------------------------------------------------------------------------------------*/

  public void actionPerformed( ActionEvent e) {
    if(e.getSource()!=timer)
       return;

     nbRepaints++;

      Runnable runnable = new Runnable() {
        public void run() {
          html.repaint();
        }
      };

      SwingUtilities.invokeLater( runnable );

     if( nbRepaints >= MAX_REPAINTS ) {
     	 timer.stop();
     	 nbRepaints=0;
     }
  }

 /*------------------------------------------------------------------------------------*/

}

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

package wotlas.client.gui;

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

public class JHTMLWindow extends JDialog
{
 /*------------------------------------------------------------------------------------*/

  /** JEditorPane to display the HTML text.
   */
    private JEditorPane html;

 /*------------------------------------------------------------------------------------*/

   /** Constructor with HTML file name/URL.
    * @param frame parent frame
    * @param title JDialog title
    * @param fileName HTML filename. If the file begins with "http:" we assume it's an URL
    *        otherwise we consider its a local file.
    * @param width initial window width
    * @param height initial window height
    * @param center tells if the JDialog must be centered on screen
    */
    public JHTMLWindow(Frame frame, String title, String fileName, int width, int height, boolean center ) {
        super( frame, title, false );
        String htmlText = null;
        URL url = null;

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
        else
              htmlText = FileTools.loadTextFromFile( fileName );

        if( htmlText == null && url==null)
            htmlText = "<b>ERROR</b><br>Could not open file: <i>"+fileName+"</i>";

      // JDialog properties
         getContentPane().setLayout(new BorderLayout());
         getContentPane().setBackground(Color.white);
         setBackground(Color.white);

      // We load the images
         ImageIcon im_okup = new ImageIcon("..\\base\\gui\\ok-up.gif");
         ImageIcon im_okdo = new ImageIcon("..\\base\\gui\\ok-do.gif");

      // OK Button
         JButton b_ok = new JButton(im_okup);
         b_ok.setRolloverIcon(im_okdo);
         b_ok.setPressedIcon(im_okdo);
         b_ok.setBorderPainted(false);
         b_ok.setContentAreaFilled(false);
         b_ok.setFocusPainted(false);

         b_ok.addActionListener(new ActionListener() {
             public void actionPerformed (ActionEvent e) {
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
    }

 /*------------------------------------------------------------------------------------*/

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

}

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

package wotlas.utils;

import wotlas.libs.graphics2D.ImageLibrary;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** A small utility to display a cropped window.
 *
 * @author Aldiss
 */

public class JCroppedWindow extends JWindow{

 /*------------------------------------------------------------------------------------*/

  // navigation bar images
     private Image leftBar, middleBar, rightBar;

  // Title
     private String title;

  // Font  
     private Font titleFont;

  // User Mouse Listener
     private MouseListener userMouseListener;

  // left mouse button pressed ?
     private boolean leftButtonPressed;

  // resizing window ? moving window ?
     private boolean resizingFromTop, resizingFromBottom, resizingFromLeft, resizingFromRight;
     private boolean movingWindow;

  // initial mouse position for window move
     private int iniX, iniY;

     private boolean updatedSize, updatedLocation;
     private int newX, newY, newW, newH;

 /*------------------------------------------------------------------------------------*/

  // Our user ContentPane... yes it's a JPanel...
     private JPanel userContentPane;

 /*------------------------------------------------------------------------------------*/

  /** Constructor with owner Frame and title.
   *
   * @param owner frame owner
   * @param title window title, set to "" if you want none
   */
   public JCroppedWindow( Frame owner, String title ) {
        super( owner );
        this.title = title;

     // We load the images
        leftBar = ImageLibrary.loadImage( "../base/gui/left-bar.gif" );
        middleBar = ImageLibrary.loadImage( "../base/gui/middle-bar.gif" );
        rightBar = ImageLibrary.loadImage( "../base/gui/right-bar.gif" );
        
     // Font
        titleFont = new Font( "Dialog", Font.PLAIN, 11 );

     // State inits
        leftButtonPressed = false;
        resizingFromTop = false;
        resizingFromBottom = false;
        resizingFromLeft = false;
        resizingFromRight = false;
        movingWindow = false;

     // Our Mouse Adapter        
        super.addMouseListener( new MouseAdapter() {
 
            public void mouseClicked(MouseEvent e) {
                if( e.getY()<rightBar.getHeight(null)
                    && e.getX()>JCroppedWindow.this.getWidth()-rightBar.getWidth(null) )
                    dispose();
                else
                    if(userMouseListener!=null)
                       userMouseListener.mouseClicked(e);
            }

            public void mouseEntered(MouseEvent e) {
                 if(userMouseListener!=null)
                    userMouseListener.mouseClicked(e);
            }

            public void mouseExited(MouseEvent e) {
                 if(userMouseListener!=null)
                    userMouseListener.mouseClicked(e);
            }

            public void mousePressed(MouseEvent e) {

                if(leftButtonPressed) { // if button already pressed
                    if(userMouseListener!=null)
                       userMouseListener.mousePressed(e);
                    return;                   
                }

            	leftButtonPressed = true;
                iniX = e.getX();
                iniY = e.getY();
                updatedSize=false;
                updatedLocation=false;

            	newX = JCroppedWindow.this.getX();
            	newY = JCroppedWindow.this.getY();
            	newW = JCroppedWindow.this.getWidth();
            	newH = JCroppedWindow.this.getHeight();

                movingWindow = false;
                resizingFromTop = false;
                resizingFromBottom = false;
                resizingFromLeft = false;
                resizingFromRight = false;

                if( e.getY() < 3 )
                    resizingFromTop = true;
                else if( e.getY() > JCroppedWindow.this.getHeight()-5 )
                    resizingFromBottom = true;
                else if( e.getY() < middleBar.getHeight(null)+2 )
                    movingWindow = true;

                if( !movingWindow && e.getX() < 5 )
                    resizingFromLeft = true;
                else if( !movingWindow && e.getX() > JCroppedWindow.this.getWidth()-5 )
                    resizingFromRight = true;

                if(userMouseListener!=null)
                    userMouseListener.mousePressed(e);
            }

            public void mouseReleased(MouseEvent e) {
                if(movingWindow) {
                    newX += e.getX()-iniX;
                    newY += e.getY()-iniY;
                    updatedLocation=true;
                }
                else{                
                  if(resizingFromTop) {
                    newY += e.getY()-iniY;
                    updatedLocation=true;

                    newH += iniY-e.getY();
                    updatedSize=true;
                  }
                  else if(resizingFromBottom) {
                    newH += -iniY+e.getY();
                    updatedSize=true;
                  }
   
                  if(resizingFromLeft) {
                    newX += e.getX()-iniX;
                    updatedLocation=true;

                    newW += iniX-e.getX();
                    updatedSize=true;
                  }
                  else if(resizingFromRight) {
                    newW += -iniX+e.getX();
                    updatedSize=true;
                  }
                }

                if(newH<middleBar.getHeight(null))
                   newH = middleBar.getHeight(null);

                if(newW<leftBar.getWidth(null)+rightBar.getWidth(null))
                   newW = leftBar.getWidth(null)+rightBar.getWidth(null);

                if(updatedLocation && updatedSize) {
                    JCroppedWindow.this.setSize( new Dimension( newW, newH ) );
                    userContentPane.setPreferredSize( new Dimension( newW, newH-middleBar.getHeight(null) ) );
                    JCroppedWindow.this.setSize( new Dimension( newW, newH ) );
                    JCroppedWindow.this.pack();
                    JCroppedWindow.this.setLocation(newX, newY);
                }
                else if(updatedSize) {
                    userContentPane.setPreferredSize( new Dimension( newW, newH-middleBar.getHeight(null) ) );
                    JCroppedWindow.this.setSize( new Dimension( newW, newH ) );
                    JCroppedWindow.this.pack();
                }
                else if( updatedLocation ) {
                    JCroppedWindow.this.setLocation(newX, newY);
                    JCroppedWindow.this.pack();
                }
                else if(userMouseListener!=null)
                        userMouseListener.mouseReleased(e);

                updatedLocation=false;
                updatedSize=false;
            	leftButtonPressed = false;
                movingWindow = false;
                resizingFromTop = false;
                resizingFromBottom = false;
                resizingFromLeft = false;
                resizingFromRight = false;
            }
        });


     // Default components
        super.setContentPane( new JPanel( true ) );
     
        super.getContentPane().setLayout( new BorderLayout() );

        super.getContentPane().add( new JBarPanel(), BorderLayout.NORTH );
        
        userContentPane = new JPanel(true);
        userContentPane.setLayout( new BorderLayout() );
        super.getContentPane().add( userContentPane, BorderLayout.CENTER );
   }

 /*------------------------------------------------------------------------------------*/

   /** To get the JWindow's Content Pane
    */
    public Container getContentPane() {
   	return (Container) userContentPane;
    }

 /*------------------------------------------------------------------------------------*/

   /** To set the JWindow's Content Pane. Only JPanel are accpeted.
    */
    public void setContentPane( Container contentPane ) {
        if(contentPane instanceof JPanel)
           userContentPane = (JPanel) contentPane;
    }

 /*------------------------------------------------------------------------------------*/

   /** To add a MouseListener to this JWindow.
    */
   public void addMouseListener(MouseListener l) {
        userMouseListener = l;
   }

 /*------------------------------------------------------------------------------------*/

  /** Our JBarPanel.
   */
   public class JBarPanel extends JPanel {

              public JBarPanel() {
              	super(true);
                setOpaque(false);
                setBackground( Color.white );
                setMinimumSize( new Dimension( 0, 0 ) );
                setPreferredSize( new Dimension( 100, 0 ) );
                setMaximumSize( new Dimension( 3000, 0 ) );
              }

              public void setMaximumSize(Dimension maximumSize) {
              	  maximumSize.height = middleBar.getHeight(null);
              	  super.setMaximumSize( maximumSize );
              }

              public void setPreferredSize(Dimension preferredSize) {
              	  preferredSize.height = middleBar.getHeight(null);
              	  
              	  if(preferredSize.width<leftBar.getWidth(null)+rightBar.getWidth(null))
              	     preferredSize.width=leftBar.getWidth(null)+rightBar.getWidth(null);

              	  super.setPreferredSize( preferredSize );
              }

              public void setMinimumSize(Dimension minimumSize) {
              	  minimumSize.height = middleBar.getHeight(null);
              	  
              	  if(minimumSize.width<leftBar.getWidth(null)+rightBar.getWidth(null))
              	     minimumSize.width=leftBar.getWidth(null)+rightBar.getWidth(null);

              	  super.setMinimumSize( minimumSize );
              }

              public void paint( Graphics g ) {
                 super.paint( g );

              // 1 - Left & middle bar images
                 g.drawImage( leftBar, 0, 0, this );

                 for( int i=leftBar.getWidth(null); i<=getWidth()-rightBar.getWidth(null);
                          i+=middleBar.getWidth(null) )
                      g.drawImage( middleBar, i, 0, this );

              // 2 - Title
                 Graphics2D g2D = (Graphics2D) g;
                 RenderingHints saveRenderHints = g2D.getRenderingHints(); // save
    
                 RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                        RenderingHints.VALUE_ANTIALIAS_ON);
                 renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    
                 g2D.setRenderingHints( renderHints );
                 g2D.setColor( Color.black );
                 g2D.setFont( titleFont );
                 g2D.drawString( title, leftBar.getWidth(null)+2, 11 );
                 g2D.setRenderingHints( saveRenderHints ); // restore

              // 4 - Right Bar image
                 g.drawImage( rightBar, getWidth()-rightBar.getWidth(null), 0, this );
            }
    }

 /*------------------------------------------------------------------------------------*/

 /** MAIN
  *
  public static void main( String argv[] ) {
       JCroppedWindow win = new JCroppedWindow( new Frame(), "Wotlas" );
       
       win.getContentPane().add( new JLabel("Cropped Window"), BorderLayout.NORTH );
       win.getContentPane().add( new JButton("Button"), BorderLayout.CENTER );
       win.getContentPane().setBackground( Color.white );

       win.setSize( 300, 200 );
       win.setLocation( 100, 100 );

       win.pack();
       win.show();
  }
  */
 /*------------------------------------------------------------------------------------*/

}

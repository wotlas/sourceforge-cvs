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

package wotlas.client.screen;

import wotlas.client.DataManager;
import wotlas.utils.*;
import wotlas.libs.graphics2D.*;
import wotlas.libs.net.NetPingListener;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.font.*;

/** A graphic panel to show ping info.
 *
 *  @author Aldiss
 */

public class GraphicPingPanel extends JPanel implements NetPingListener
{
 /*------------------------------------------------------------------------------------*/

 // The different ping background images red, yellow, green,
  private Image red, green, yellow;

 // Panel j_drawzone size
  private static int TEXT_X = 40;
  private static int TEXT_Y = 24;

 // Font for ping text
  private Font f_text;

 // double-buffer.
  private Image offScreenImage;

 // current ping value
  private int currentPing;
  
 // eventual "Please Wait" dialog
  private JPleaseWait pleaseWait;

 /*------------------------------------------------------------------------------------*/

 /** Constructor.
  *
  */
    public GraphicPingPanel()
    {
         setFont("Lucida Blackletter");

         MediaTracker mediaTracker = new MediaTracker(this);
         red  = getToolkit().getImage("../base/gui/ping-red.jpg");
         green = getToolkit().getImage("../base/gui/ping-green.jpg");
         yellow = getToolkit().getImage("../base/gui/ping-yellow.jpg");
         mediaTracker.addImage(red,0);
         mediaTracker.addImage(green,1);
         mediaTracker.addImage(yellow,2);

         try{
            mediaTracker.waitForAll(); // wait for all images to be in memory
         }
         catch(InterruptedException e){
            e.printStackTrace();
         }

      // Panel properties
         setBackground(Color.black);
         setPreferredSize( new Dimension(160,40) );
         setMinimumSize( new Dimension(160,40) );
         setMaximumSize( new Dimension(160,40) );
   }


/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To paint our panel...
    */
      public void paint(Graphics g) {
           try {
            // off screen image eventual creation
               if(offScreenImage==null)
                  offScreenImage = createImage(160,40);

               Graphics2D offScreen = (Graphics2D) offScreenImage.getGraphics();

            // we erase the previous content by redrawing the backgound
               if(currentPing<400 && currentPing>=0)
                  offScreen.drawImage(green,0,0,this);
               else if(currentPing<1000 && currentPing>=0)
                  offScreen.drawImage(yellow,0,0,this);
               else
                  offScreen.drawImage(red,0,0,this);

            // Anti aliasing
               RenderingHints savedRenderHints = offScreen.getRenderingHints(); // save
               RenderingHints antiARenderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                              RenderingHints.VALUE_ANTIALIAS_ON);
               antiARenderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

               offScreen.setRenderingHints( antiARenderHints );

               offScreen.setFont(f_text);
               offScreen.setColor(Color.black);

               if(currentPing<0)
                    offScreen.drawString( "No Response !",TEXT_X,TEXT_Y);
               else if(currentPing<100)
                    offScreen.drawString( "Excellent ("+currentPing+" ms)",TEXT_X,TEXT_Y);
               else if(currentPing<200)
                  offScreen.drawString( "Good ("+currentPing+" ms)",TEXT_X,TEXT_Y);
               else if(currentPing<400)
                  offScreen.drawString( "Medium ("+currentPing+" ms)",TEXT_X,TEXT_Y);
               else if(currentPing<1000)
                  offScreen.drawString( "Low ("+currentPing+" ms)",TEXT_X,TEXT_Y);
               else
                  offScreen.drawString( "Very Low ("+currentPing+" ms)",TEXT_X,TEXT_Y);

             // clean anti-aliasing
               offScreen.setRenderingHints( savedRenderHints );

             // we can now draw the whole result image on screen
      	       g.drawImage(offScreenImage,0,0,this);
          }
      	  catch (Exception e) {
          }
      }

      public void repaint() {
          paint(getGraphics());
      }

      public void update(Graphics g) {
          paint(g);
      }

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /**
  * To define the font for the title and the text
  */
  public void setFont(String fontName){
    f_text = FontFactory.getDefaultFontFactory().getFont(fontName);
    f_text = f_text.deriveFont(Font.PLAIN, 10f);
  }

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** This method is called when some ping information is available.
   *
   * @param ping if >=0 it's a valid ping value, if == PING_FAILED it means the
   *        last ping failed, if == PING_CONNECTION_CLOSED it means the connection
   *        has been closed.
   */
     public void pingComputed( int ping ) {
     	currentPing = ping;
     	repaint();
     	
     	if( ping==PING_FAILED && pleaseWait==null) {
            DataManager dManager = DataManager.getDefaultDataManager();
            dManager.getMyPlayer().getMovementComposer().resetMovement();
            pleaseWait = new JPleaseWait( dManager.getClientScreen() );
        }
        else if(ping!=PING_FAILED && pleaseWait!=null) {
            pleaseWait.dispose();
            pleaseWait = null;
        }
     }

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Internal Class : "Please Wait Window"
   */
     class JPleaseWait extends JDialog {
     	public JPleaseWait( Frame frame ){
     	    super( frame, "Network Connection", false );
     	    getContentPane().add( new JLabel("No response from server. Please Wait..."), BorderLayout.CENTER );
     	    pack();
     	    SwingTools.centerComponent( this );
     	    show();
     	}
     }

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}


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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/** Displays an image in a JWindow during a certain amount of time.
 *
 * @author Aldiss
 */

public class IntroductionWindow extends Window implements ActionListener
{
  /** image to display.
   */
      private Image back;

  /** Timer
   */
      private Timer timer;

 /*------------------------------------------------------------------------------------*/

  /** Creates a Window with the specified image in background.
   *
   * @param frame parent frame. 
   * @param image_path an image path...
   * @param duration display duration
   */

    public IntroductionWindow( Frame frame, String image_path, int duration )
    {
        super(frame);

          Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

       // We load the image...
          MediaTracker mediaTracker = new MediaTracker(this);
          back = getToolkit().getImage(image_path);
          mediaTracker.addImage(back,0);

           try{
               mediaTracker.waitForID(0);
           }
           catch(InterruptedException e){
               Debug.signal( Debug.WARNING, this, e );
           }

       // We center the windows on the screen
          int XO = ( screensize.width-back.getWidth(this) )/2;
          int YO = ( screensize.height-back.getHeight(this) )/2;

          setLayout(null);
          setBackground(Color.black);
          setBounds(XO,YO,back.getWidth(this),back.getHeight(this));

         setVisible(true);
         repaint();

      // Timer init
         timer = new Timer(duration,this);
         timer.start();
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /** Timer Event interception
  *
  * @param e supposed timer event
  */
   public void actionPerformed( ActionEvent e)
   {
     if(e.getSource()!=timer)
         return;

     dispose();
   }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** Paint Method. We draw the background image.
   * @param g graphics
   */
     public void paint(Graphics g) {
          g.drawImage(back,0,0,this);
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

  /** To avoid any flicks we redefine this method...
   */
     public void repaint() {
          paint(getGraphics());
     }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

   /** To avoid any flicks we redefine this method...
    * @param g graphics
    */
      public void update(Graphics g) {
          paint(g);
      }

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
}


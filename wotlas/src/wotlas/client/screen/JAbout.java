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

import wotlas.utils.*;
import wotlas.libs.graphics2D.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import java.awt.font.*;
import java.awt.geom.*;

/** JAbout to show a text scrolling in a JDialog
 *
 *  @author  Aldiss, MasterBob
 */

public class JAbout extends JDialog implements ActionListener
{
 // back= about.jpg, back2=about-back.jpg
  private Image back, back2;

 // Panel (zone of the screen) where we'll draw the text.
  private JPanel j_drawzone;

 // Panel j_drawzone size
  private static int DRAWZONE_WIDTH = 297;
  private static int DRAWZONE_HEIGHT = 147;


  private Font f_text, f_title;
  private Color c_text, c_title, c_shadow;

 // double-buffer. Off screen image for the j_drawzone panel.
  private Image offScreenImage;

  private Image backorigin;
  private Image backorigin2;


 // Text to display.
 // each line beginning with a space is considered as a title
  private String text[] =
    {
    	" ",
    	"Robert Jordan's",
  	" Wheel of Time",
  	"---",
  	" Light and Shadow",
  	"v1.1.2 - January 2002",
  	" ",
  	" ",
  	" Project Management",
  	"Valère",
  	"Aldiss",
        " ",
  	" Developers - Release 1",
  	"Aldiss",
  	"Petrus",
  	"MasterBob",
  	"Hari Coplin",
  	" ",
  	" Developers - 2nd Team",
  	"Valère",
        "Blackhole",
        "Vasaldo",
        " ",
        " Game Content",
        "Valère",
        "Klianwolf",
        "Severian",
        "Xeno yar litharr",
        "Mazarboul",
        "Great Lord of the Dark",
        "Logain",
        "Felherid",
        " ",
        " 2D Graphics",
        "Aldiss",
        " ",
        " Thanks To...",
        "Families & friends",
        "PouceOne & Anza",
        "Tibob",
        "Foreign Wotlas Fans",
        " ",
        "The French WoT mailing list",
        "roue-du-temps@yahoogroups.com",
        " ",
        "Tour Grise Internet Site",
        "www.chez.com/tourgrise",
        " ",
        "The Wheel Of Time PC Game",
        "www.wheeloftime.com",
        " ",
        "SourceForge.net",
        " ",
        " ",
        " ",
        " ",
        "No sprites were hurt during the game.",
        " ",
        " ",
        " ",
        " ",
        "Aes Sedai were dressed by Ejin Couturier",
        "26 E.Tishar Street - Tar Valon B536",
        " ",
        " ",
        " ",
        " ",
        "Wotlas will return in 'Bugs are forever'",
        " ",
        " ",
        " ",
        " ",
        "Thank you for playing wotlas !",
        " ",
        " ",
    };

  private int[] textWidth;


// a Timer generates a regular event (caught by the ActionListener)
// we use it to redraw the screen regularly, performin the text animation
 private javax.swing.Timer timer;

// current text y position relative to the j_drawzone panel.
 private int y0;


// In the previous "text" array not all the lines appear on screen at the same time.
// So the following variable indicates the first line number to display
 private int first_line;

 static final FontRenderContext frc = new FontRenderContext(new AffineTransform(),false,false);

 /**
  * constructor for this dialog
  * we need the same parameters :
  * @param owner the frame owner of this JDialog
  * @param title the title of this JDialog
  */
    public JAbout(Frame owner)
    {
         super(owner,"About");
         setFont("Lblack.ttf");
         calculTextWidth();
         c_text = new Color(40,50,60);
         c_title = new Color(80,100,190);
         c_shadow = new Color(50,50,70);

         y0 = DRAWZONE_HEIGHT;  // y0 initialisation on j_drawzone's screen bottom

       this.setImage("about.jpg","about-back.jpg");

      // Frame properties
         getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));
         //getContentPane().setBounds(0,0,640,480);
         getContentPane().setBackground(Color.black);
         setBackground(Color.black);

      // Jpanel where we draw the background image
          JPanel jp = new JPanel()
          {
             public void paintComponent(Graphics g){
                g.drawImage(back,0,0,this);
             }
          };

         jp.setLayout(null);
         jp.setBackground(Color.black);
         getContentPane().add(jp);
         jp.setPreferredSize( new Dimension(400,300) );
         jp.setMinimumSize( new Dimension(400,300) );
         jp.setMaximumSize( new Dimension(400,300) );
         pack();

      // J_drawzone Panel, where we display the text
          j_drawzone = new JPanel()
          {
             public void paintComponent(Graphics g)
             {
               try
               {
               	  // off screen image eventual creation
                    if(offScreenImage==null)
                           offScreenImage = createImage(DRAWZONE_WIDTH,DRAWZONE_HEIGHT);

                    Graphics2D offScreen = (Graphics2D) offScreenImage.getGraphics();

                  // we erase the previous content by redrawing the text-backgound.jpg
                    offScreen.drawImage(back2,0,0,this);

                  // Anti aliasing
                    RenderingHints savedRenderHints = offScreen.getRenderingHints(); // save    
                    RenderingHints antiARenderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                              RenderingHints.VALUE_ANTIALIAS_ON);
                    antiARenderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    offScreen.setRenderingHints( antiARenderHints );

                  // has the text roll ended ? yes if first_line = number of lines
                    if(first_line==text.length) {
                    	first_line=0;
                    	y0 = DRAWZONE_HEIGHT+50;//DRAWZONE_WIDTH+50;  // we reset animation
                    }

                  // text display
                     for(int i=first_line; i<text.length;i++)
                     {
                       // current line y position
                        int base_y = i*25+y0;

                       // if y<0 this line is out of screen, we don't draw it
                        if(base_y<0) {
                           first_line++;
                           continue;
                        }

                       // if this line is out of the bottom of the screen
                       // it means the other following lines are also out of
                       // screen. we stop to display the text.
                        if( base_y>DRAWZONE_HEIGHT+15)
                           break;

                       // is it an empty line ?
                        if( text[i].length()==0)
                           continue;

                       // if it begins with a space, it's a title.
                        if(text[i].charAt(0)==' ')
                        {
                         offScreen.setFont(f_title);

                          // text's shadow
                           offScreen.setColor(c_shadow);
                           offScreen.drawString( text[i],
                                 (DRAWZONE_WIDTH -textWidth[i])/2 +1,
                                 base_y +1);

                          // text
                           offScreen.setColor(c_title);
                           offScreen.drawString( text[i],
                                 (DRAWZONE_WIDTH -textWidth[i])/2,
                                 base_y );
                        }
                        else
                        {
                          // simple text
                           offScreen.setFont(f_text);
                           offScreen.setColor(c_text);
                           offScreen.drawString( text[i],
                                 (DRAWZONE_WIDTH - textWidth[i])/2,
                                 base_y );
                        }
                     }

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

          }; /// END OF J_DRAWZONE PANEL DEFINITION


      // some additional j_drawzone properties
         j_drawzone.setLayout(new BoxLayout(j_drawzone,BoxLayout.X_AXIS));
         j_drawzone.setOpaque(true);
         j_drawzone.setBounds(50,90,DRAWZONE_WIDTH,DRAWZONE_HEIGHT);
         jp.add(j_drawzone);


      // jframe properties
         setResizable(false);
/*
         this.addComponentListener(new ComponentListener(){
           public void componentHidden(ComponentEvent e){}
           public void componentMoved(ComponentEvent e){}
           public void componentResized(ComponentEvent e){
            resize();
           }
           public void componentShown(ComponentEvent e){}
         });
*/
      // Ok button
         JButton b_ok = new JButton(new ImageIcon("../base/gui/close-up.jpg"));
         b_ok.setBounds(158,258,80,40);
         b_ok.setRolloverIcon(new ImageIcon("../base/gui/close-do.jpg"));
         b_ok.setPressedIcon(new ImageIcon("../base/gui/close-do.jpg"));

         b_ok.setBorderPainted(false);
         b_ok.setContentAreaFilled(false);

         jp.add(b_ok);

          b_ok.addActionListener(new ActionListener()
           {
              public void actionPerformed (ActionEvent e) {
              	timer.stop();
                dispose();
              }
            });
         
         SwingTools.centerComponent( this );
         setVisible(true);

      // Timer init
         timer = new javax.swing.Timer(20,this);
         timer.start();
  }

/**
 * to get the image back and back2
 */
   public void setImage(String imageBack, String imageBack2)
    {
     // MediaTracker for efficient image loading.
     MediaTracker mediaTracker = new MediaTracker(this);
     //backorigin = getToolkit().getImage("../base/gui/"+imageBack);
     //backorigin2 = getToolkit().getImage("../base/gui/"+imageBack2);
     back  = getToolkit().getImage("../base/gui/"+imageBack);
     back2 = getToolkit().getImage("../base/gui/"+imageBack2);     mediaTracker.addImage(back,0);
     mediaTracker.addImage(back2,1);
//     back = backorigin.getScaledInstance(640,480,Image.SCALE_REPLICATE);
//     back2 = backorigin2.getScaledInstance(DRAWZONE_WIDTH,DRAWZONE_HEIGHT,Image.SCALE_REPLICATE);

     try{
         mediaTracker.waitForAll(); // wait for all images to be in memory
     }
     catch(InterruptedException e){
           e.printStackTrace();
     }
    }


 /**
  * action to be done if the jdialog is resized
  *
 private void resize()
  {
   back = backorigin.getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_REPLICATE);
   DRAWZONE_WIDTH = this.getWidth()/3;
   DRAWZONE_HEIGHT = this.getHeight()/3;
   back2 = backorigin2.getScaledInstance(DRAWZONE_WIDTH,DRAWZONE_HEIGHT,Image.SCALE_REPLICATE);
   j_drawzone.setBounds(this.getWidth()/3,this.getHeight()/3,DRAWZONE_WIDTH,DRAWZONE_HEIGHT);

   offScreenImage = null;//pour to recalculate it
  }
*/

 /**
  * we calculate the text width
  * (it would take too much time to recalculate it each time we draw the text)
  */
 private void calculTextWidth()
  {
   this.textWidth = new int[this.text.length];
   for(int i=0; i<textWidth.length; i++)
    {
     TextLayout t;
     if(text[i].charAt(0)==' ') t = new TextLayout(text[i],f_title,frc);
     else t = new TextLayout(text[i],f_text,frc);

     textWidth[i] = ((int)t.getBounds().getWidth());
    }
  }

 /**
  * to set the text
  */
 public void setText(String[] newText)
  {
   this.text = newText;
   calculTextWidth();
  }


/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

 /**
  * To define the font for the title and the text
  */
  public void setFont(String fontName){
   try {
      String fontPath = "../base/fonts";
      FileInputStream fis = new FileInputStream(fontPath+File.separator+fontName);
      f_text = Font.createFont(Font.TRUETYPE_FONT, fis);
      FileInputStream fis2 = new FileInputStream(fontPath+File.separator+fontName);
      f_title = Font.createFont(Font.TRUETYPE_FONT, fis2);
      //System.out.println("Font=" + font);
      f_text = f_text.deriveFont(Font.BOLD, 15f);
      f_title = f_title.deriveFont(Font.BOLD, 20f);
      //Map fontAttributes1 = f_text.getAttributes();
      //Map fontAttributes2 = f_title.getAttributes();
      //System.out.println("Attrihbutes=" + fontAttributes);
    } catch (Exception e) {
      f_text = new Font("dialog", Font.PLAIN, 15);
      f_title = new Font("dialog", Font.PLAIN, 20);
      e.printStackTrace();
    }
  }



/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/


 // Timer Event interception
   public void actionPerformed( ActionEvent e)
   {
     if(e.getSource()!=timer)
       return;

    // text position decreased
       y0-=1;

    // and repaint please !
       j_drawzone.repaint();
   }

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}


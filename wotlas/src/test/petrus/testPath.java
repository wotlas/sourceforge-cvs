import wotlas.libs.pathfinding.*;
import wotlas.utils.List;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.MediaTracker;
import java.awt.Graphics2D;
import java.util.Vector;
import java.awt.Point;
import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.*;
import java.text.*;

public class testPath extends JFrame implements MouseListener
{
  private BufferedImage maskBuffImg;        // buffered image of the mask
  private BufferedImage textureBuffImg;     // buffered image of the texture  
  
  private Image maskImg;                    // mask
  private Image textureImg;                 // map (texture)
  private int maskWidth;                    // width of the mask (in cells)
  private int maskHeight;                   // height of the mask (in cells)   
  
  private int mapOriginX;                   // x coordinate of the map
  private int mapOriginY;                   // y coordinate of the map
      
  private boolean bOrigin = true;           // true if mouse clic is the start point (false otherwise)  
  private int x1,y1;                        // coordinate of Start point
  private int x2,y2;                        // coordinate of Goal point
  
  private AStar AStarObject;                // A* Object for the algorithm
  
/* ------------------------------------------------------------------------------------------------------------------- */
  
  /**
   * Init the frame :
   * - load the images
   * - init the width and height of images
   * - create AStar object
   * - initialize the mask of AStar
   */
  public void init()
  {   
    // Gets the images
    System.out.println("Loading images...");
    maskImg    = getToolkit().getImage("mask.gif");
    textureImg = getToolkit().getImage("room.jpg");
    
    // Creates a media tracker to wait for the image
    System.out.println("Waiting for the images...");
    MediaTracker myTracker = new MediaTracker(this);
    myTracker.addImage(maskImg, 0);
    myTracker.addImage(textureImg, 0);
    try {
      myTracker.waitForAll();
    } catch (InterruptedException ie) {
      System.err.println("InterruptedException: " + ie);
      System.exit(1);
    }    
    
    // A* algorithm
    AStarObject = new AStar();
    
    // Init the mask of AStarObject
    maskWidth  = maskImg.getWidth(this);  // width of the mask (in cells)
    maskHeight = maskImg.getHeight(this); // height of the mask (in cells)
    maskBuffImg = new BufferedImage(maskWidth, maskHeight, BufferedImage.TYPE_INT_RGB);
    maskBuffImg.createGraphics().drawImage(maskImg, 0, 0, null);    
    AStarObject.initMask(maskBuffImg, maskWidth, maskHeight);
    System.out.println("Mask width: " + maskWidth + " , Mask height: " + maskHeight);
    
    // Init the map (1 cell = 10 pixels square)
    mapOriginX = maskWidth*10; // x coordinate of the origin of the map on the screen (in pixels)
    mapOriginY = 0;            // y coordinate of the origin of the map on the screen (in pixels)
    textureBuffImg = new BufferedImage(maskWidth*10, maskHeight*10, BufferedImage.TYPE_INT_RGB);          
    textureBuffImg.createGraphics().drawImage(textureImg, 0, 0, null);    
    
    // Listen to Mouse clics
    addMouseListener(this);
    
  }

/* ------------------------------------------------------------------------------------------------------------------- */
  
  /**
   * Starts the A* algorithm
   */
  public void startAlgo()
  {
    long startTime;      // Time
    long endTime;        // Time
    //Vector AStarPath;    // found path
    List AStarPath;    // found path

    // Refresh the image of the map
    textureBuffImg = null;
    textureBuffImg = new BufferedImage(maskWidth*10, maskHeight*10, BufferedImage.TYPE_INT_RGB);
    textureBuffImg.createGraphics().drawImage(textureImg, 0, 0, null);
    
    // Draw the start point
    Point startPoint = new Point(x1,y1);
    drawPoint(startPoint);
    
    // Draw the goal point
    Point goalPoint  = new Point(x2,y2);
    drawPoint(goalPoint);    

    startTime = System.currentTimeMillis();
    System.err.println("Start time : " + startTime);
    
    // Starts A* algorithm
    System.out.println("Starting AStar algo");
    AStarPath = AStarObject.findPath(startPoint, goalPoint);
    
    endTime = System.currentTimeMillis();
    System.err.println("End time : " + endTime);
    
    // Draws the found path on the screen
    if (AStarPath == null) {
      System.err.println("no path found!");
    } else {
      System.err.println("drawing found path!");      
      drawPath(AStarPath);
      AStarPath = null;
      repaint();
    }    
    
    System.err.print("Elapsed Time to find path between (" + startPoint.x + "," + startPoint.y + ") and (" + goalPoint.x+ "," + goalPoint.y + ") : ");
    System.err.println(endTime-startTime);
  }

/* -------------------------------------------------------------------------------------------------------------------- */

  /**
   * Draws the path on the buffered image "textureBuffImg"
   * @param path vector of the points to be drawn
   */
  //private void drawPath(Vector path)
  private void drawPath(List path)
  {
    Point pathPoint;
    for (int i=0; i<path.size(); i++)
    {
      pathPoint = (Point) path.elementAt(i);
      for (int k=0; k<10; k++)
      {
        for (int l=0; l<10; l++)
        {
          textureBuffImg.setRGB(pathPoint.x*10+k, pathPoint.y*10+l, -126305);
        }
      }
    }
  }

  /**
   * Draws a point on the buffered image "textureBuffImg"
   * @param point point to be drawn
   */
  private void drawPoint(Point point)
  {    
    for (int k=0; k<10; k++)
    {
      for (int l=0; l<10; l++) {
        textureBuffImg.setRGB(point.x*10+k, point.y*10+l, -126305);
      }
    }
  }

  /**
   * Draws the mask on the buffered image "textureBuffImg"
   */
  private void drawMask()
  {       
    for (int i=0; i<maskWidth; i++)
    {
      for (int j=0; j<maskHeight; j++)
      {
        if (AStarObject.isNotBlock(i,j)) {
          for (int k=0; k<10; k++)
          {
            for (int l=0; l<10; l++)
            {
              textureBuffImg.setRGB(i*10+k, j*10+l, -126305);
            }
          }
        }
      }
    }
  }
          
/* ------------------------------------------------------------------------------------------------------------------- */
  
  /**
   *
   */
  public void paint(Graphics g)
  {    
    Graphics2D g2 = (Graphics2D) g;    
    g2.drawImage(maskImg, 0, 0, maskWidth*10, maskHeight*10, this);
    g2.drawImage(textureBuffImg, mapOriginX, mapOriginY, maskWidth*10, maskHeight*10, this);
  }

/* ------------------------------------------------------------------------------------------------------------------- */
  
  /**
   */
  public void processKeyEvent(KeyEvent e)
  {
    
  }
  
  /**
   */
  public void processWindowEvent(WindowEvent e)
  {    
    if (e.getID() == java.awt.event.WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }

/* ------------------------------------------------------------------------------------------------------------------- */
  
  /**
   * Invoked when the mouse button is clicked
   */
  public void mouseClicked(MouseEvent e)
  {   
    e.translatePoint(-mapOriginX, -mapOriginY);
    if (e.getID() == java.awt.event.MouseEvent.MOUSE_CLICKED)
    {
      if (bOrigin == true)
      {
        System.out.println("Origine");
        x1 = e.getX()/10;
        y1 = e.getY()/10;
        System.out.println(x1 + " " + y1);
        bOrigin = false;
      } else {
        System.out.println("End");
        x2 = e.getX()/10;
        y2 = e.getY()/10;
        System.out.println(x2 + " " + y2);
        bOrigin = true;
        startAlgo();
      }
    }
  }  
  /**
   * Invoked when the mouse enters a component
   */
  public void mouseEntered(MouseEvent e)
  {
    
  }  
  /**
   * Invoked when the mouse exits a component
   */
  public void mouseExited(MouseEvent e)
  {
    
  }  
  /**
   * Invoked when a mouse button has been pressed on a component
   */ 
  public void mousePressed(MouseEvent e)
  {
    
  }
  /**
   * Invoked when a mouse button has been released on a component
   */
  public void mouseReleased(MouseEvent e)
  {
    
  }

/* ------------------------------------------------------------------------------------------------------------------- */
  
  /**
   * Main program
   */
  public static void main(String args[])
  {
    testPath frame;
           
    frame = new testPath();
    frame.init();    
    frame.setSize(1024,400);
    frame.setVisible(true);
    frame.show();    
  }
}
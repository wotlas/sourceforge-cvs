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
import java.awt.image.BufferedImage;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/** Draws a animated graph to monitor a value
 * cf. demos of jdk1.3
 *
 * @author Petrus
 */
 
public class JMonitor extends JPanel {

  public String monitorInfo = "";
  public float monitorValue;
  public float monitorScale;

  /** Panel default width.
   */
  private int w;
  
  /** Panel default height;
   */
  private int h;        
  
  private BufferedImage bimg;
  private Graphics2D big;
  
  private Font font = new Font("Times New Roman", Font.PLAIN, 11);
  
  private int columnInc;
  private int pts[];
  private int ptNum;
  private int ascent, descent;
  
  private Rectangle graphOutlineRect = new Rectangle();
  
  private Line2D graphLine = new Line2D.Float();
  private Color graphColor = new Color(46, 139, 87);
  private Color mfColor = new Color(0, 100, 0);
  
  /** Constructor.
   */
  public JMonitor(int width, int height) {
    this.w = width;
    this.h = height;            
    setBackground(Color.black);            
  }

  /** To set monitorValue.
   */
  public void setMonitorValue(float value) {
    monitorValue = value;
    repaint();
  }
  
  /** To set monitorScale.
   */
  public void setMonitorScale(float value) {
    monitorScale = value;
  }
  
  /** To get minimumSize.
   */
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  /** To get maximumSize.
   */
  public Dimension getMaximumSize() {
    return getPreferredSize();
  }

  /** To get preferredSize.
   */
  public Dimension getPreferredSize() {
    return new Dimension(w, h);
  }

  /** Paint.
   */          
  public void paint(Graphics g) {
    if (big == null) {
      bimg = (BufferedImage) createImage(w, h);
      big = bimg.createGraphics();
      big.setFont(font);
      FontMetrics fm = big.getFontMetrics(font);
      ascent = (int) fm.getAscent();
      descent = (int) fm.getDescent();
      big.setBackground(getBackground());
    }
    
    big.clearRect(0,0,w,h);

    // Draw string monitorInfo.
    big.setColor(Color.green);
    big.drawString(monitorInfo, 4.0f, (float) ascent+0.5f);
    //big.drawString(monitorInfo, 4, h-descent);

    // Calculate remaining size
    float ssH = ascent + descent;
    float remainingHeight = (float) (h - (ssH*2) - 0.5f);
    float blockHeight = remainingHeight/10;
    float blockWidth = 20.0f;
    float remainingWidth = (float) (w - blockWidth - 10);

    // .. Draw axes ..
    big.setColor(graphColor);
    // Origin x coordinate
    int graphX = 10;
    int graphY = (int) ssH;
    int graphW = w - graphX - 5;
    int graphH = (int) remainingHeight;
    graphOutlineRect.setRect(graphX, graphY, graphW, graphH);
    big.draw(graphOutlineRect);

    // .. Draw row ..
    int graphRow = graphH/10;
    big.setColor(mfColor);
    for (int j = graphY; j <= graphH+graphY; j += graphRow) {
      graphLine.setLine(graphX,j,graphX+graphW,j);
      big.draw(graphLine);
    }
        
    // .. Draw animated column movement ..
    int graphColumn = graphW/15;
    if (columnInc == 0) {
      columnInc = graphColumn;
    }
    for (int j = graphX+columnInc; j < graphW+graphX; j+=graphColumn) {
      graphLine.setLine(j,graphY,j,graphY+graphH);
      big.draw(graphLine);
    }
    --columnInc;

    if (pts == null) {
      pts = new int[graphW];
      ptNum = 0;
    } else if (pts.length != graphW) {
      int tmp[] = null;
      if (ptNum < graphW) {     
        tmp = new int[ptNum];
        System.arraycopy(pts, 0, tmp, 0, tmp.length);
    } else {        
        tmp = new int[graphW];
        System.arraycopy(pts, pts.length-tmp.length, tmp, 0, tmp.length);
        ptNum = tmp.length - 2;
      }
      pts = new int[graphW];
      System.arraycopy(tmp, 0, pts, 0, tmp.length);
    } else {
      big.setColor(Color.yellow);
      pts[ptNum] = (int)(graphY+graphH*(monitorValue/monitorScale));
      for (int j=graphX+graphW-ptNum, k=0;k < ptNum; k++, j++) {
          if (k != 0) {
              if (pts[k] != pts[k-1]) {
                  big.drawLine(j-1, pts[k-1], j, pts[k]);
              } else {
                  big.fillRect(j, pts[k], 1, 1);
              }
          }
      }
      if (ptNum+2 == pts.length) {
        // throw out oldest point
        for (int j = 1;j < ptNum; j++) {
          pts[j-1] = pts[j];
        }
        --ptNum;
      } else {
          ptNum++;
      }
  }
  g.drawImage(bimg, 0, 0, this);
}


        
}

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

package wotlas.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

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
        this.monitorValue = value;
        repaint();
    }

    /** To set monitorScale.
     */
    public void setMonitorScale(float value) {
        this.monitorScale = value;
    }

    /** To get minimumSize.
     */
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    /** To get maximumSize.
     */
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    /** To get preferredSize.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.w, this.h);
    }

    /** Paint.
     */
    @Override
    public void paint(Graphics g) {
        if (this.big == null) {
            this.bimg = (BufferedImage) createImage(this.w, this.h);
            this.big = this.bimg.createGraphics();
            this.big.setFont(this.font);
            FontMetrics fm = this.big.getFontMetrics(this.font);
            this.ascent = fm.getAscent();
            this.descent = fm.getDescent();
            this.big.setBackground(getBackground());
        }

        this.big.clearRect(0, 0, this.w, this.h);

        // Draw string monitorInfo.
        this.big.setColor(Color.green);
        this.big.drawString(this.monitorInfo, 4.0f, this.ascent + 0.5f);
        //big.drawString(monitorInfo, 4, h-descent);

        // Calculate remaining size
        float ssH = this.ascent + this.descent;
        float remainingHeight = (this.h - (ssH * 2) - 0.5f);
        float blockHeight = remainingHeight / 10;
        float blockWidth = 20.0f;
        float remainingWidth = (this.w - blockWidth - 10);

        // .. Draw axes ..
        this.big.setColor(this.graphColor);
        // Origin x coordinate
        int graphX = 10;
        int graphY = (int) ssH;
        int graphW = this.w - graphX - 5;
        int graphH = (int) remainingHeight;
        this.graphOutlineRect.setRect(graphX, graphY, graphW, graphH);
        this.big.draw(this.graphOutlineRect);

        // .. Draw row ..
        int graphRow = graphH / 10;
        this.big.setColor(this.mfColor);
        for (int j = graphY; j <= graphH + graphY; j += graphRow) {
            this.graphLine.setLine(graphX, j, graphX + graphW, j);
            this.big.draw(this.graphLine);
        }

        // .. Draw animated column movement ..
        int graphColumn = graphW / 15;
        if (this.columnInc == 0) {
            this.columnInc = graphColumn;
        }
        for (int j = graphX + this.columnInc; j < graphW + graphX; j += graphColumn) {
            this.graphLine.setLine(j, graphY, j, graphY + graphH);
            this.big.draw(this.graphLine);
        }
        --this.columnInc;

        if (this.pts == null) {
            this.pts = new int[graphW];
            this.ptNum = 0;
        } else if (this.pts.length != graphW) {
            int tmp[] = null;
            if (this.ptNum < graphW) {
                tmp = new int[this.ptNum];
                System.arraycopy(this.pts, 0, tmp, 0, tmp.length);
            } else {
                tmp = new int[graphW];
                System.arraycopy(this.pts, this.pts.length - tmp.length, tmp, 0, tmp.length);
                this.ptNum = tmp.length - 2;
            }
            this.pts = new int[graphW];
            System.arraycopy(tmp, 0, this.pts, 0, tmp.length);
        } else {
            this.big.setColor(Color.yellow);
            this.pts[this.ptNum] = (int) (graphY + graphH * (this.monitorValue / this.monitorScale));
            for (int j = graphX + graphW - this.ptNum, k = 0; k < this.ptNum; k++, j++) {
                if (k != 0) {
                    if (this.pts[k] != this.pts[k - 1]) {
                        this.big.drawLine(j - 1, this.pts[k - 1], j, this.pts[k]);
                    } else {
                        this.big.fillRect(j, this.pts[k], 1, 1);
                    }
                }
            }
            if (this.ptNum + 2 == this.pts.length) {
                // throw out oldest point
                for (int j = 1; j < this.ptNum; j++) {
                    this.pts[j - 1] = this.pts[j];
                }
                --this.ptNum;
            } else {
                this.ptNum++;
            }
        }
        g.drawImage(this.bimg, 0, 0, this);
    }

}

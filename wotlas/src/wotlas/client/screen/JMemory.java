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

import wotlas.utils.JMonitor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/**
 * Tracks Memory allocated & used, displayed in graph form.
 */
public class JMemory extends JPanel implements Runnable {

 /*------------------------------------------------------------------*/

  private JMonitor surf;
  
  /** Time between 2 pings.
   */
  private long sleepAmount = 1000;
  
  /** Our thread.
   */
  private Thread thread;
  
  /** Our runtime.
   */
  private Runtime r = Runtime.getRuntime();

 /*------------------------------------------------------------------*/
 
  /** Constructor.
   */
  public JMemory() {
    add(surf = new JMonitor(200, 200));        
  }
  
  /** Start thread.
   */
  public void start() {
    thread = new Thread(this);
    thread.setPriority(Thread.MIN_PRIORITY);
    thread.setName("MemoryMonitor");
    thread.start();
  }
  
  /** Stop thread.
   */
  public synchronized void stop() {
    thread = null;
    notify();
  }
  
  /** Run thread.
   */
  public void run() {
    Thread me = Thread.currentThread();
       
    while (thread == me && isPinging()) {
      ping();
      try {
        thread.sleep(sleepAmount);
      } catch (InterruptedException e) { break; }
    }
    thread = null;
  }
  
  /** True if the JPanel is shown.
   */
  private boolean isPinging() {
    return true;
  }

  /** Ping.
   */
  private void ping() {
    System.out.println("ping");
    
    float freeMemory = (float) r.freeMemory();
    float totalMemory = (float) r.totalMemory();
    
    surf.monitorInfo = String.valueOf(((int) (totalMemory - freeMemory))/1024) + "K used";
    
    surf.setMonitorScale(totalMemory);
    surf.setMonitorValue(freeMemory);
    
    //System.out.println("freeMemory = " + (freeMemory/1024) );
    //System.out.println("totalMemory = " + (totalMemory/1024) );
    
  }
  
  /** Main.
   */
    public void init() {
       final JMemory monitor = new JMemory();
       
       JFrame f = new JFrame("Java2D Demo - JMonitor");
       
        /*WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeiconified(WindowEvent e) { monitor.start(); }
            public void windowIconified(WindowEvent e) { monitor.stop(); }
        };
        f.addWindowListener(l);
        */
        
        f.getContentPane().add("Center", monitor);
        f.pack();
        f.setSize(new Dimension(200,200));
        f.setVisible(true);
        monitor.start();

    }
}

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

package wotlas.libs.aswing;

import java.awt.Frame;
import javax.swing.Timer;

/** Creates and manages a ProgressMonitor from a thread...
 *
 * @author Aldiss
 */

public class AProgressMonitor extends Thread {

    /*-------------------------------------------------------------------------------*/

    /** Progress Monitor
     */
    private AProgressDialog pMonitor;

    /** Value for the progress monitor, ranges from one to 100.
     */
    private int value;

    /** String message for the progress monitor...
     */
    private String note;

    /** Title.
     */
    private String title;

    /** Our timer for repaint...
     */
    private Timer timer;

    /** Our frame
     */
    private Frame frame;

    /** Quit ?
     */
    private boolean quit;

    /*-------------------------------------------------------------------------------*/

    /** Constructor with parent component and title.
     */
    public AProgressMonitor(Frame frame, String title) {
        super();
        this.title = title;
        this.frame = frame;
        this.note = "";
        this.quit = false;

        // We increase slightly the priority of this thread
        int priority = getPriority();

        if (priority < Thread.MAX_PRIORITY)
            setPriority(priority + 1);

        start();
    }

    /*-------------------------------------------------------------------------------*/

    /** Action !
     */
    @Override
    public synchronized void run() {
        this.pMonitor = new AProgressDialog(this.frame, this.title);

        do {
            this.pMonitor.setProgress(this.value);
            this.pMonitor.setNote(this.note);

            try {
                wait();
            } catch (Exception e) {
            }
        } while (!this.quit);

        this.pMonitor.hide();
    }

    /*-------------------------------------------------------------------------------*/

    /** Value for the progress monitor, ranges from one to 100.
     */
    public synchronized void setProgress(String note, int value) {
        if (value < 0)
            value = 0;
        if (value > 100)
            value = 100;

        this.value = value;
        this.note = note;
        notify();
    }

    /*-------------------------------------------------------------------------------*/

    /** To close the progress monitor.
     */
    public synchronized void close() {
        this.quit = true;
        notify();
    }

    /*-------------------------------------------------------------------------------*/
}

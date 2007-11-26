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

package wotlas.libs.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/** A NetThread is an abstract class representing a thread that manages
 *  a socket connection.
 * 
 * @author Aldiss
 * @see wotlas.libs.net.NetReceiver
 * @see wotlas.libs.net.NetSender
 */

abstract public class NetThread extends Thread {

    /*------------------------------------------------------------------------------------*/

    /** Our socket.
     */
    private Socket socket;

    /** tells the thread if it must stop.
     */
    private boolean stopThread;

    /*------------------------------------------------------------------------------------*/

    /** NetThread constructor with an opened socket.
     * @param socket an already opened socket. 
     */
    protected NetThread(Socket socket) {
        super("NetThread");

        this.socket = socket;
        this.stopThread = false;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**  To get a buffered socket input stream.
     *
     * @param buffer_size buffer size for the BufferedInputStream.
     * @return a buffered input stream linked to the socket input stream.
     * @exception IOException if the socket has not already been opened.
     */
    protected BufferedInputStream getBufferedInputStream(int buffer_size) throws IOException {
        return new BufferedInputStream(this.socket.getInputStream(), buffer_size);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**  To get a buffered socket output stream.
     *
     * @param buffer_size buffer size for the BufferedOutputStream.
     * @return a buffered output stream linked to the socket output stream.
     * @exception IOException if the socket has not already been opened.
     */
    protected BufferedOutputStream getBufferedOutputStream(int buffer_size) throws IOException {
        return new BufferedOutputStream(this.socket.getOutputStream(), buffer_size);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**  Use this method to stop this thread.
     *   This method does nothing if the thread has already been stopped.
     */
    synchronized public void stopThread() {
        this.stopThread = true;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**  To see if this thread should stop...
     *
     * @return true if the thread should stop.
     */
    synchronized protected boolean shouldStopThread() {
        return this.stopThread;
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /**  Method to close the socket connection.
     */
    public void closeSocket() {
        try {
            this.socket.getInputStream().close();
            this.socket.getOutputStream().close();
            this.socket.close();
        } catch (IOException e) {
            // socket probably already closed...
            // be just to be sure...
            try {
                this.socket.close();
            } catch (IOException ioe) {
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

}

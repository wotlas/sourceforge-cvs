/**
 * 
 */
package wotlas.libs.net;

import java.io.IOException;

/**
 * An IOServerChannel is an abstraction to encapsulate the behavior of a real socket server which are listening for new connections.
 * 
 * @author SleepingOwl
 *
 */
public interface IOServerChannel {

    /** @return a short description of the current io socket server. */

    public String getDescription();

    public void stopServer();

    /** should we stop ?
     *  @return true if the server must stop.
     */
    public boolean mustStop();

    /** To change the maximum number of opened sockets. This can be refused if more than
     *  the new 'maxOpenedSockets' are already opened.
     *  @param maxOpenedSockets maximum number of opened sockets
     */
    public void setMaximumOpenedSockets(int maxOpenedSockets);

    /**
     * @return
     */
    public IOChannel accept() throws IOException;

    /**
     * 
     */
    public void close() throws IOException;
}

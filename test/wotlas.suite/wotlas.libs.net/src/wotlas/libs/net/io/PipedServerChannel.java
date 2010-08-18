/**
 * 
 */
package wotlas.libs.net.io;

import java.io.IOException;
import java.net.ServerSocket;
import wotlas.libs.net.IOChannel;
import wotlas.libs.net.IOServerChannel;
import wotlas.libs.net.NetServerListener;
import wotlas.utils.Debug;

/**
 * @author SleepingOwl
 *
 */
public class PipedServerChannel implements IOServerChannel, NetServerListener {

    /*------------------------------------------------------------------------------------*/

    /** Period between two bind() tries if the network interface is not ready.
     */
    public static final long INTERFACE_BIND_PERIOD = 1000 * 60 * 3; // 3 min

    /** To prevent servers from accessing to netwok info at the same time
     */
    private static final byte systemNetLock[] = new byte[0];

    /** Stop server ?
     */
    protected boolean stopServer = false;

    /** ioHandler object storing client and server in and out streams. */
    private PipedStreamsHandler ioHandler;

    /** server side to communicate to the unique client */
    private PipedClientChannel ioClient;

    /*------------------------------------------------------------------------------------*/

    /**
     */
    public PipedServerChannel() {
        super();
    }

    /** To get a valid ServerSocket. If the interface is not ready we will display a warning
     *  message and wait INTERFACE_BIND_PERIOD milliseconds before retrying.
     * @param serverInterface 
     * @param serverPort 
     * @param listeners 
     *  @return a valid server socket
     */
    public ServerSocket initServerSocket(String serverInterface, int serverPort, NetServerListener listeners[]) {

        // Instantiating the server and client side.
        String pipedname = serverInterface + "#" + serverPort;

        try {
            reinitServerSocket(pipedname);
        } catch (IOException e) {
            Debug.signal(Debug.FAILURE, this, "Could not create server socket ! " + e); // FATAL !
            Debug.exit();
            return null;
        }

        // 2 - Has the state of the network interface changed ?
        for (int i = 0; i < listeners.length; i++)
            listeners[i].serverInterfaceIsUp(serverInterface, false); // state not changed

        return null;
    }

    private synchronized void reinitServerSocket(String pipedname) throws IOException {
        if (this.ioHandler != null) {
            // Not to be notified twice of io channel closing.
            this.ioHandler.setServerListener(null);
            this.ioHandler.closeServer();
            this.ioHandler = PipedClientServerManager.getMgr().closeStreams(pipedname);
        }

        this.ioHandler = PipedClientServerManager.getMgr().initStreams(pipedname);
        this.ioHandler.setServerListener(this);
        this.ioClient = new PipedClientChannel(this.ioHandler, false);
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** To stop this server
     */
    public synchronized void stopServer() {
        this.stopServer = true;
        notifyAll();
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

    /** should we stop ?
     *  @return true if the server must stop.
     */
    public synchronized boolean mustStop() {
        return this.stopServer;
    }

    /** To change the maximum number of opened sockets. This can be refused if more than
     *  the new 'maxOpenedSockets' are already opened.
     *  @param maxOpenedSockets maximum number of opened sockets
     */
    public synchronized void setMaximumOpenedSockets(int maxOpenedSockets) {
        // Not used : only one connection available.
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetServerSocket#getDescription()
     */
    public String getDescription() {
        return this.ioHandler.getName();
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetServerSocket#accept()
     */
    public synchronized IOChannel accept() throws IOException {
        if (this.ioHandler != null && this.ioHandler.isClientConnecting()) {
            return this.ioClient;
        }
        // Waiting for stop
        try {
            wait(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetServerSocket#close()
     */
    public void close() throws IOException {
        if (this.ioHandler == null)
            return;
        String pipedname = this.ioHandler.getName();
        reinitServerSocket(pipedname);
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetServerListener#serverInterfaceIsDown(java.lang.String)
     */
    public void serverInterfaceIsDown(String itf) {
        // We listen for io channel closing.
        if (this.ioHandler != null && this.ioHandler.getServerListener() == this) {
            String pipedname = this.ioHandler.getName();
            try {
                reinitServerSocket(pipedname);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetServerListener#serverInterfaceIsUp(java.lang.String, boolean)
     */
    public void serverInterfaceIsUp(String ipAddress, boolean stateChanged) {
        if (this.ioHandler != null && this.ioHandler.getServerListener() == this) {
            //notifyAll();
        }
    }
}

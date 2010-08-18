/**
 * 
 */
package wotlas.libs.net.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import wotlas.libs.net.IOChannel;
import wotlas.libs.net.IOServerChannel;
import wotlas.libs.net.NetServerListener;
import wotlas.libs.net.utils.NetInterface;
import wotlas.utils.Debug;

/**
 * @author SleepingOwl
 *
 */
public class SocketServerChannel implements IOServerChannel {

    /*------------------------------------------------------------------------------------*/

    /** Period between two bind() tries if the network interface is not ready.
     */
    public static final long INTERFACE_BIND_PERIOD = 1000 * 60 * 3; // 3 min

    /** To prevent servers from accessing to netwok info at the same time
     */
    private static final byte systemNetLock[] = new byte[0];

    /** Stop server ?
     */
    protected boolean stopServer;

    /** Server Socket
     */
    protected ServerSocket server;

    /** Maximum number of opened sockets for this server.
     */
    private int maxOpenedSockets;

    /*------------------------------------------------------------------------------------*/

    /**
     * 
     */
    public SocketServerChannel() {
        super();
        this.stopServer = false;
        this.maxOpenedSockets = 200; // default maximum number of opened sockets
    }

    /** To get a valid ServerSocket. If the interface is not ready we will display a warning
     *  message and wait INTERFACE_BIND_PERIOD milliseconds before retrying.
     * @param serverInterface 
     * @param serverPort 
     * @param listeners 
     *  @return a valid server socket
     */
    public ServerSocket initServerSocket(String serverInterface, int serverPort, NetServerListener listeners[]) {
        // We get the ip address of the specified host
        InetAddress hostIP = null;
        boolean updateServerSocket = false;

        // We check the format of the "host" field
        if (serverInterface.indexOf(',') < 0) {
            // ok this is an IP or DNS Name
            do {
                try {
                    hostIP = InetAddress.getByName(serverInterface);
                } catch (UnknownHostException ue) {
                    // Interface is not ready
                    for (int i = 0; i < listeners.length; i++)
                        listeners[i].serverInterfaceIsDown(serverInterface);

                    // we wait some time before retrying...
                    synchronized (this) {
                        try {
                            wait(SocketServerChannel.INTERFACE_BIND_PERIOD);
                        } catch (Exception e) {
                        }
                    }
                }

                if (mustStop())
                    return this.server;
            } while (hostIP == null);
        } else {
            // ok, we have an interface name
            int separatorIndex = serverInterface.indexOf(',');
            int ipIndex = -1;

            try {
                ipIndex = Integer.parseInt(serverInterface.substring(separatorIndex + 1, serverInterface.length()));
            } catch (Exception e) {
                Debug.signal(Debug.FAILURE, this, "Invalid network interface format : " + serverInterface + " should be <itf>,<ip-index> !");
                Debug.exit();
            }

            // We wait for the interface to be up
            do {
                String itfIP[] = null;

                synchronized (SocketServerChannel.systemNetLock) { // <-- to prevent servers from accessing net info at the same time
                    itfIP = NetInterface.getInterfaceAddresses(serverInterface.substring(0, separatorIndex));
                }

                if (itfIP == null || itfIP.length <= ipIndex) {
                    // Interface is not ready
                    if (this.server == null) {
                        Debug.signal(Debug.FAILURE, this, "Network Interface MUST be enabled at start-up so that we load appropriate libraries !");
                        Debug.exit();
                    }

                    for (int i = 0; i < listeners.length; i++)
                        listeners[i].serverInterfaceIsDown(serverInterface.substring(0, separatorIndex) + " - ip " + ipIndex);

                    // we wait some time before retrying...
                    synchronized (this) {
                        try {
                            wait(SocketServerChannel.INTERFACE_BIND_PERIOD);
                        } catch (Exception e) {
                        }
                    }
                } else {
                    // Interface is up !
                    try {
                        hostIP = InetAddress.getByName(itfIP[ipIndex]);
                    } catch (UnknownHostException uhe) {
                        Debug.signal(Debug.FAILURE, this, "Could not use IP given by NetworkInterface ! " + uhe); // FATAL !
                        Debug.exit();
                    }

                    if (this.server != null && !this.server.getInetAddress().equals(hostIP))
                        updateServerSocket = true;
                }

                if (mustStop())
                    return this.server;
            } while (hostIP == null);
        }

        // 2 - Has the state of the network interface changed ?
        if (!updateServerSocket && this.server != null) {
            for (int i = 0; i < listeners.length; i++)
                listeners[i].serverInterfaceIsUp(hostIP.getHostAddress(), false); // state not changed

            return this.server;
        }

        // 3 - ServerSocket creation is needed here.
        if (this.server != null)
            try {
                this.server.close();
            } catch (IOException ioe) {
                Debug.signal(Debug.ERROR, this, "Error while closing old server socket : " + ioe);
            }

        try {
            this.server = new ServerSocket(serverPort, 50, hostIP); // new server socket
            this.server.setSoTimeout(5000);
        } catch (Exception e) {
            Debug.signal(Debug.FAILURE, this, "Could not create server socket ! " + e); // FATAL !
            Debug.exit();
        }

        for (int i = 0; i < listeners.length; i++)
            listeners[i].serverInterfaceIsUp(hostIP.getHostAddress(), true); // state changed

        return this.server;
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
        this.maxOpenedSockets = maxOpenedSockets;
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetServerSocket#getDescription()
     */
    public String getDescription() {
        return this.server.getInetAddress() + ":" + this.server.getLocalPort();
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetServerSocket#accept()
     */
    public IOChannel accept() throws IOException {
        Socket socket = this.server.accept();
        IOChannel ioSocket = new SocketChannel(socket);
        return ioSocket;
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetServerSocket#close()
     */
    public void close() throws IOException {
        this.server.close();

    }
}

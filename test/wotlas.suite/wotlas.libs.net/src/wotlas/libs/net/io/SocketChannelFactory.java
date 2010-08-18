/**
 * 
 */
package wotlas.libs.net.io;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import wotlas.libs.net.IOChannel;
import wotlas.libs.net.IOChannelFactory;
import wotlas.libs.net.IOServerChannel;
import wotlas.libs.net.NetConfig;
import wotlas.libs.net.NetServerListener;
import wotlas.utils.Debug;

/**
 * @author SleepingOwl
 *
 */
public class SocketChannelFactory implements IOChannelFactory {

    /* (non-Javadoc)
     * @see wotlas.libs.net.WishNetSocketFactory#createNewClientSocket(java.lang.String, int)
     */
    public IOChannel createNewClientSocket(NetConfig netCfg) throws IOException {

        Socket socket = null;
        String serverInterface = netCfg.getServerInterface();
        int serverPort = netCfg.getServerPort();
        try {
            socket = new Socket(serverInterface, serverPort);
        } catch (UnknownHostException e) {
            Debug.signal(Debug.FAILURE, this, "Unknown Server - " + netCfg.getServerInterface() + ":" + netCfg.getServerPort());
            return null;
        }

        return new SocketChannel(socket);
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.WishNetSocketFactory#createNewServerSocket(java.lang.String, int, wotlas.libs.net.NetServerListener[])
     */
    public IOServerChannel createNewServerSocket(NetConfig netCfg, NetServerListener[] listeners) throws IOException {

        String serverInterface = netCfg.getServerInterface();
        int serverPort = netCfg.getServerPort();
        SocketServerChannel server = new SocketServerChannel();
        server.initServerSocket(serverInterface, serverPort, listeners);
        return server;
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.WishNetSocketFactory#isManagingType(int)
     */
    public boolean isManaging(NetConfig netCfg) {
        return (netCfg.getTypeOfMessageExchanged() == IOChannelFactory.NET_RAW_SOCKET);
    }

}

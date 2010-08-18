/**
 * 
 */
package wotlas.libs.net.io;

import java.io.IOException;
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
public class PipedChannelFactory implements IOChannelFactory {

    /* (non-Javadoc)
     * @see wotlas.libs.net.WishNetSocketFactory#createNewClientSocket(java.lang.String, int)
     */
    public IOChannel createNewClientSocket(NetConfig netCfg) throws IOException {

        PipedClientChannel ioClient = null;
        String serverInterface = netCfg.getServerInterface();
        int serverPort = netCfg.getServerPort();
        String pipedname = serverInterface + "#" + serverPort;
        PipedStreamsHandler streams;

        try {
            streams = PipedClientServerManager.getMgr().initStreams(pipedname);
            ioClient = new PipedClientChannel(streams, true);

            // Notify that the client is connecting.
            streams.openClient();

        } catch (IOException e) {
            Debug.signal(Debug.FAILURE, this, "Unknown Server - " + netCfg.getServerInterface() + ":" + netCfg.getServerPort());
            return null;
        }

        return ioClient;
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.WishNetSocketFactory#createNewServerSocket(java.lang.String, int, wotlas.libs.net.NetServerListener[])
     */
    public IOServerChannel createNewServerSocket(NetConfig netCfg, NetServerListener[] listeners) throws IOException {

        String serverInterface = netCfg.getServerInterface();
        int serverPort = netCfg.getServerPort();

        PipedServerChannel server = new PipedServerChannel();
        server.initServerSocket(serverInterface, serverPort, listeners);

        return server;
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.WishNetSocketFactory#isManagingType(int)
     */
    public boolean isManaging(NetConfig netCfg) {
        return (netCfg.getTypeOfMessageExchanged() == IOChannelFactory.NET_RAW_MEMORY);
    }

}

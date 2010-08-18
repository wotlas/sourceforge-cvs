/**
 * 
 */
package wotlas.libs.net;

import java.io.IOException;
import wotlas.utils.WishGameExtension;

/**
 * NetSocketFactory is a service used by NetClient and NetServer to handle the necessary socket layers needed to transfer io datas between client and server.
 * 
 * @author SleepingOwl
 *
 */
public interface IOChannelFactory extends WishGameExtension {

    /** Name used for the standalone server : the one used by a client that use memory buffers to exchange with a server instantiated in memory. */
    public static final String STANDALONE_SERVERNAME = "standalone";

    /** Type of NetSocketFactory that is creating a raw byte piped in and piped out using memory buffers in the same jre. */
    public static final int NET_RAW_MEMORY = 0;

    /** Type of NetSocketFactory that is creating a raw socket over tcp/ip. */
    public static final int NET_RAW_SOCKET = 1;

    /**
     * Only the first factory will be used.
     * @param netCfg the configuration of the server (name, port, ...);
     * @return true if this factory is able to create client or server side socket layers for this config;
     */
    public boolean isManaging(NetConfig netCfg);

    /** To get a valid ServerSocket. If the interface is not ready we will display a warning
     *  message and wait INTERFACE_BIND_PERIOD milliseconds before retrying.
     * @param netCfg the configuration of the server (name, port, ...);  
     * @param listeners used 
     *  @return a valid server socket
     * @throws IOException exception thrown by the io operations;
     */
    public IOServerChannel createNewServerSocket(NetConfig netCfg, NetServerListener listeners[]) throws IOException;

    /**
     * @param  netCfg the configuration of the server (name, port, ...);
     * @return netSocket used to exchange message with the server;
     * @throws IOException exception thrown by the io operations;
     */
    public IOChannel createNewClientSocket(NetConfig netCfg) throws IOException;

}

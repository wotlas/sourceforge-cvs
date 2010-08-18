/**
 * 
 */
package wotlas.libs.net;

/**
 * Object used to store all the technical information used to crate a connection between a client and a server.
 * @author SleepingOwl
 *
 */
public class NetConfig {

    private int typeOfMessageExchanged;

    /** ID of the server we want to join (-1 if not defined)*/
    private int serverId;

    private String serverName;

    /** Server Net Interface. We accept three format : an IP address, a DNS name or a network interface
     *  name followed by a ',' with an integer indicating the IP index for that network interface.
     *  If you are not sure just set the index to 0, it will point out the first available IP for
     *  the given interface.<br>
     *  Example : "wotlas.dynds.org", "192.168.0.2", "lan1,0".
     */
    private String serverInterface;

    /** Server Port.
     */
    private int serverPort;

    private String serverProtocole;

    /**
     * Base path used to load the informations needed by the standalone server only.
     */
    private String standaloneBasePath;

    /**
     * Default constructor (use the full constructor with serverInterface=serverName and typeOfMessageExchanged=WishNetSocketFactory.NET_RAW_SOCKET.
     * @param serverName
     * @param serverPort
     */
    public NetConfig(String serverName, int serverPort) {
        this(-1, serverName, serverPort, serverName, null, IOChannelFactory.NET_RAW_SOCKET);
    }

    /**
     * Full constructor.
     * <p>Server Host Name. We accept three format : an IP address, a DNS name or a network interface
     *  name followed by a ',' with an integer indicating the IP index for that network interface.
     *  If you are not sure just set the index to 0, it will point out the first available IP for
     *  the given interface.<br>
     *
     *  Example : "wotlas.dynds.org", "192.168.0.2", "lan1,0".</p>
     * @param serverId id of the server in a pool
     * @param serverInterface server host name
     * @param serverName use WishNetSocketFactory.STANDALONE_SERVERNAME to instantiate the standalone server.
     * @param serverPort
     * @param serverProtocole
     * @param typeOfMessageExchanged type of raw message layer used to exchange between client and server (socket, memory, ...);
     */
    public NetConfig(int serverId, String serverName, int serverPort, String serverInterface, String serverProtocole,
            int typeOfMessageExchanged) {
        super();
        this.serverId = serverId;
        this.serverInterface = serverInterface;
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.serverProtocole = serverProtocole;
        if (isStandaloneServer()) {
            this.typeOfMessageExchanged = IOChannelFactory.NET_RAW_MEMORY;
        } else {
            this.typeOfMessageExchanged = typeOfMessageExchanged;
        }
    }

    /**
     * @return the standaloneBasePath
     */
    public String getStandaloneBasePath() {
        return this.standaloneBasePath;
    }

    /**
     * @param standaloneBasePath the standaloneBasePath to set
     */
    public void setStandaloneBasePath(String standaloneBasePath) {
        this.standaloneBasePath = standaloneBasePath;
    }

    /**
     * @return true if it is the standalone server configuration.
     */
    public boolean isStandaloneServer() {
        return IOChannelFactory.STANDALONE_SERVERNAME.equals(this.serverName);
    }

    /**
     * @return the typeOfMessageExchanged
     */
    public int getTypeOfMessageExchanged() {
        return this.typeOfMessageExchanged;
    }

    /**
     * @param typeOfMessageExchanged the typeOfMessageExchanged to set
     */
    public void setTypeOfMessageExchanged(int typeOfMessageExchanged) {
        this.typeOfMessageExchanged = typeOfMessageExchanged;
    }

    /**
     * @return the serverId
     */
    public int getServerId() {
        return this.serverId;
    }

    /**
     * @param serverId the serverId to set
     */
    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return this.serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return the serverInterface
     */
    public String getServerInterface() {
        return this.serverInterface;
    }

    /**
     * @param serverInterface the serverInterface to set
     */
    public void setServerInterface(String serverInterface) {
        this.serverInterface = serverInterface;
    }

    /**
     * @return the serverPort
     */
    public int getServerPort() {
        return this.serverPort;
    }

    /**
     * @param serverPort the serverPort to set
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * @return the serverProtocole
     */
    public String getServerProtocole() {
        return this.serverProtocole;
    }

    /**
     * @param serverProtocole the serverProtocole to set
     */
    public void setServerProtocole(String serverProtocole) {
        this.serverProtocole = serverProtocole;
    }

}

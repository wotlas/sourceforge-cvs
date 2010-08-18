/**
 * 
 */
package wotlas.libs.net.io;

import java.io.IOException;
import java.util.HashMap;

/**
 * The PipedClientServerManager is used to store the different piped in and out used either by client or server.
 * 
 * 
 * @author SleepingOwl
 *
 */
public class PipedClientServerManager {

    /** */
    private static PipedClientServerManager PIPED_MANAGER = new PipedClientServerManager();

    public final static PipedClientServerManager getMgr() {
        return PipedClientServerManager.PIPED_MANAGER;
    }

    /** map used to store the io piped handlers */
    private HashMap<String, PipedStreamsHandler> map = new HashMap<String, PipedStreamsHandler>(5);

    /** default */
    public PipedClientServerManager() {
        super();
    }

    /**
     * @param name
     * @return the io streams handler
     * @throws IOException
     */
    public synchronized PipedStreamsHandler initStreams(String name) throws IOException {
        PipedStreamsHandler ioHandler = this.map.get(name);
        if (ioHandler == null) {
            ioHandler = new PipedStreamsHandler(name);
            this.map.put(name, ioHandler);
        }

        return ioHandler;
    }

    public synchronized PipedStreamsHandler closeStreams(String name) throws IOException {
        PipedStreamsHandler ioHandler = this.map.get(name);
        this.map.remove(name);
        return ioHandler;
    }

}

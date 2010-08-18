/**
 * 
 */
package wotlas.libs.net.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import wotlas.libs.net.IOChannel;

/**
 * @author SleepingOwl
 *
 */
public class PipedClientChannel implements IOChannel {

    private PipedStreamsHandler ioHandler;

    private boolean clientSide;

    /**
     * @param ioHandler object storing client and server in and out streams.
     * @param clientSide is true if this PipedClient is in the client side; false if it is the server side part;
     */
    public PipedClientChannel(PipedStreamsHandler ioHandler, boolean clientSide) {
        super();
        this.clientSide = clientSide;
        this.ioHandler = ioHandler;
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetSocket#close()
     */
    public void close() throws IOException {
        if (this.clientSide)
            this.ioHandler.closeClient();
        else
            this.ioHandler.closeServer();
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetSocket#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
        if (this.clientSide)
            return this.ioHandler.getClientIn();
        else
            return this.ioHandler.getServerIn();
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetSocket#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException {
        if (this.clientSide)
            return this.ioHandler.getClientOut();
        else
            return this.ioHandler.getServerOut();
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetSocket#setReceiveBufferSize(int)
     */
    public void setReceiveBufferSize(int i) throws SocketException {
        // TODO Handle it.
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetSocket#setSendBufferSize(int)
     */
    public void setSendBufferSize(int i) throws SocketException {
        // TODO Handle it.
    }

}

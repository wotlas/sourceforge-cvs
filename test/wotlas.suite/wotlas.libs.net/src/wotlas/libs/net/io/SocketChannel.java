/**
 * 
 */
package wotlas.libs.net.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import wotlas.libs.net.IOChannel;

/**
 * @author SleepingOwl
 *
 */
public class SocketChannel implements IOChannel {

    private Socket socket;

    /**
     * @param socket
     */
    public SocketChannel(Socket socket) {
        super();
        this.socket = socket;
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetSocket#close()
     */
    public void close() throws IOException {
        this.socket.close();
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetSocket#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
        // TODO Auto-generated method stub
        return this.socket.getInputStream();
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetSocket#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetSocket#setReceiveBufferSize(int)
     */
    public void setReceiveBufferSize(int i) throws SocketException {
        this.socket.setReceiveBufferSize(i);

    }

    /* (non-Javadoc)
     * @see wotlas.libs.net.NetSocket#setSendBufferSize(int)
     */
    public void setSendBufferSize(int i) throws SocketException {
        this.socket.setSendBufferSize(i);

    }

}

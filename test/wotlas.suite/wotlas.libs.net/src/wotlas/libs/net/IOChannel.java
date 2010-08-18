/**
 * 
 */
package wotlas.libs.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

/**
 * An IOChannel is an abstraction used by the wotlas.libs.net api in order to handle all
 * the input and output stream used to transfer data between client and server in the
 * architecture.
 * 
 * @author SleepingOwl
 * @see wotlas.libs.net.IOChannelFactory
 * @see wotlas.libs.net.NetClient
 * @see wotlas.libs.net.NetReceiver
 * @see wotlas.libs.net.NetSender
 * @see wotlas.libs.net.NetServer
 * @see wotlas.libs.net.NetThread
 * @see wotlas.libs.net.connection.AsynchronousNetConnection
 * @see wotlas.libs.net.connection.SynchronousNetConnection
 *
 */
public interface IOChannel {

    /**
     * @return non-buffered inputStream used for the transfer of datas.
     */
    public InputStream getInputStream() throws IOException;

    /**
     * @return non-buffered outputStream used for the transfer of datas.
     */
    public OutputStream getOutputStream() throws IOException;

    /**
     * After closing the 2 streams, ending the connection.
     * @throws IOException in case of io errors to release the used resources.
     */
    public void close() throws IOException;

    /**
     * @param i
     */
    public void setReceiveBufferSize(int i) throws SocketException;

    /**
     * @param i
     */
    public void setSendBufferSize(int i) throws SocketException;

}

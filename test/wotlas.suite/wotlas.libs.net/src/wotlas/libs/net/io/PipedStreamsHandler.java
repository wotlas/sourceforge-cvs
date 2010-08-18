/**
 * 
 */
package wotlas.libs.net.io;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import wotlas.libs.net.NetServerListener;

/**
 * Simple object to store each (clientIn, serverOut) and (clientOut, serverIn) streams used for the communications.
 * @author  SleepingOwl
 */
public class PipedStreamsHandler {

    /**
     * Store the synchonized state of a current client connecting to the server standalone.
     */
    protected class ClientConnectionState {
        /**
         * Says to server if a client is currently connecting to the standalone server.
         */
        private boolean clientConnecting;

        /**
         * @return true only once if a client is currently connecting to the standalone server.
         */
        protected synchronized boolean isClientConnecting() {
            if (this.clientConnecting) {
                this.clientConnecting = false;
                return true;
            }
            return false;
        }

        /**
         * @param clientConnecting the clientConnecting to set
         */
        protected synchronized void setClientConnecting(boolean clientConnecting) {
            this.clientConnecting = clientConnecting;
        }

    }

    /**
     * 
     */
    private String name;
    /**
     * 
     */
    private PipedInputStream clientIn;
    /**
     * 
     */
    private PipedInputStream serverIn;
    /**
     * 
     */
    private PipedOutputStream clientOut;
    /**
     * 
     */
    private PipedOutputStream serverOut;

    /**
     * 
     */
    private NetServerListener serverListener;

    private ClientConnectionState clientState;

    /**
     * @param name unique name of the handler
     * @throws IOException problems in creating and connecting pipes.
     */
    public PipedStreamsHandler(String name) throws IOException {
        super();
        this.clientState = new ClientConnectionState();
        this.name = name;
        initStreams();
    }

    /**
     * @throws IOException 
     * 
     */
    protected void initStreams() throws IOException {
        this.clientIn = new PipedInputStream();
        this.serverIn = new PipedInputStream();

        this.clientOut = new PipedOutputStream();
        this.serverOut = new PipedOutputStream();

        // connecting pipes.
        this.clientIn.connect(this.serverOut);
        this.serverIn.connect(this.clientOut);
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the clientIn
     */
    public PipedInputStream getClientIn() {
        return this.clientIn;
    }

    /**
     * @param clientIn the clientIn to set
     */
    public void setClientIn(PipedInputStream clientIn) {
        this.clientIn = clientIn;
    }

    /**
     * @return the serverIn
     */
    public PipedInputStream getServerIn() {
        return this.serverIn;
    }

    /**
     * @param serverIn the serverIn to set
     */
    public void setServerIn(PipedInputStream serverIn) {
        this.serverIn = serverIn;
    }

    /**
     * @return the clientOut
     */
    public PipedOutputStream getClientOut() {
        return this.clientOut;
    }

    /**
     * @param clientOut the clientOut to set
     */
    public void setClientOut(PipedOutputStream clientOut) {
        this.clientOut = clientOut;
    }

    /**
     * @return the serverOut
     */
    public PipedOutputStream getServerOut() {
        return this.serverOut;
    }

    /**
     * @param serverOut the serverOut to set
     */
    public void setServerOut(PipedOutputStream serverOut) {
        this.serverOut = serverOut;
    }

    /**
     * @return the serverListener
     */
    public NetServerListener getServerListener() {
        return this.serverListener;
    }

    /**
     * @param serverListener the serverListener to set
     */
    public void setServerListener(NetServerListener serverListener) {
        this.serverListener = serverListener;
    }

    /**
     * 
     */
    public void openClient() throws IOException {
        this.clientState.setClientConnecting(true);
        // Notify the server that the client is connecting.
        if (this.serverListener != null)
            this.serverListener.serverInterfaceIsUp(getName(), true);
    }

    /**
     * 
     */
    public void closeClient() throws IOException {
        this.clientState.setClientConnecting(false);
        if (this.serverListener != null)
            this.serverListener.serverInterfaceIsDown(getName());
    }

    /**
     * When closing we notify the server that this io channel is closed.
     */
    public void closeServer() throws IOException {
        this.clientState.setClientConnecting(false);
        if (this.serverListener != null)
            this.serverListener.serverInterfaceIsDown(getName());
    }

    /**
     * @return true only once if a client is currently connecting to the standalone server.
     */
    public boolean isClientConnecting() {

        return this.clientState.isClientConnecting();
    }

}
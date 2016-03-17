package ua.com.codefire.se.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author human
 */
public class DataSocketServer extends SocketServer {

    /**
     * Default thread pool size.
     */
    public static final int DEFAULT_POOL_SIZE = 5;
    /**
     * Default pool shutdown timeout in milliseconds.
     */
    public static final int DEFAULT_POOL_TIMEOUT = 500;

    private final List<DataSocketListener> listeners;
    private ExecutorService threadPool;

    /**
     * @see ua.com.codefire.se.net.SocketServer
     * @param port
     */
    public DataSocketServer(int port) {
        super(port);
        this.listeners = Collections.synchronizedList(new ArrayList<>());
    }

    public List<DataSocketListener> getListeners() {
        return listeners;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }
    
    public void addListener(DataSocketListener listener) {
        listeners.add(listener);
    }

    @Override
    protected void setState(ServerState state) {
        switch (state) {
            case RUNNING:
                threadPool = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
                break;
            case STOPPING:
                if (!threadPool.isShutdown() && !threadPool.isTerminated()) {
                    threadPool.shutdown();
                }
                break;
            case STOPPED:
                if (!threadPool.isTerminated()) {
                    try {
                        threadPool.awaitTermination(DEFAULT_POOL_TIMEOUT, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DataSocketServer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    threadPool.shutdownNow();
                }
                break;
        }

        super.setState(state);
    }

    @Override
    protected void incomingSocket(Socket socket) throws IOException {
        new Thread(new InputSocketWorker(socket))
                .start();
    }

    private class InputSocketWorker implements Runnable {

        private final Socket socket;
        private final DataInputStream inputStrem;
        private final DataOutputStream outputStream;

        public InputSocketWorker(Socket socket) throws IOException {
            this.socket = socket;
            this.inputStrem = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            listeners.stream().forEach((listener) -> {
                try {
                    listener.incomigData(socket, inputStrem, outputStream);
                } catch (IOException ex) {
                    Logger.getLogger(DataSocketServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }

    }
}

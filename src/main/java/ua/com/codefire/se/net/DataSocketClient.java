package ua.com.codefire.se.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author human
 */
public class DataSocketClient {

    private String address;
    private int port;

    public DataSocketClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void sendMessage(String message) {
        new Thread(() -> {
            try (Socket socket = new Socket(address, port)) {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStrem = new DataInputStream(socket.getInputStream());
                
                outputStream.writeUTF(message);
                outputStream.flush();
            } catch (IOException ex) {
                Logger.getLogger(DataSocketClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
    }

}

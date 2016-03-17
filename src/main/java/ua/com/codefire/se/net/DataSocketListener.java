package ua.com.codefire.se.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author human
 */
public interface DataSocketListener {
    
    public void incomigData(Socket socket, DataInputStream dis, DataOutputStream dos) throws IOException;
}

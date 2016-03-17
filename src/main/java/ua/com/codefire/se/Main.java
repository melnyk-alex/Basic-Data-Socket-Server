package ua.com.codefire.se;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import ua.com.codefire.se.net.DataSocketClient;
import ua.com.codefire.se.net.DataSocketServer;

/**
 *
 * @author human
 */
public class Main {
    
    private static final int GLOBAL_PORT = 6778;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        DataSocketServer dataSocketServer = new DataSocketServer(GLOBAL_PORT);
        dataSocketServer.addListener((Socket socket, DataInputStream dis, DataOutputStream dos) -> {
            System.out.printf("Message (%s):\n%s\n", socket.getInetAddress().getHostAddress(), dis.readUTF());
        });
        dataSocketServer.listen();
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter IP mask (192.168.1.): ");
        String ipmask = scanner.nextLine();
        
        if (ipmask.isEmpty()) {
            ipmask = "192.168.1.";
        }
        
        while (true) {
            System.out.println("MENU:");
            System.out.println("1. [S]end message");
            System.out.println("0. [E]xit");
            
            String input = scanner.nextLine();
            
            switch (input.toLowerCase()) {
                case "1":
                case "s":
                    System.out.printf("Enter address: %s", ipmask);
                    String address = ipmask.concat(scanner.nextLine());
                    System.out.print("Enter message: ");
                    String message = scanner.nextLine();
                    
                    new DataSocketClient(address, GLOBAL_PORT).sendMessage(message);
                    break;
                case "0":
                case "e":
                    System.out.println("GOOD BYE!");
                    dataSocketServer.stop();
                    return;
            }
        }
    }
    
}

package cs455.overlay.transport;

import cs455.overlay.node.*;

import java.io.IOException;
import java.net.*;

public class TCPServerThread implements Runnable {
    
    private ServerSocket serverSocket;
    private Node node;
    
    public TCPServerThread(Node node, ServerSocket serverSocket) {
        this.node = node;
        this.serverSocket = serverSocket;
    }
    
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
            
                // Accept a new connection to the server and create a socket
                Socket socket = serverSocket.accept();
            
                // Create a new TCPConnection and Call the onConnection method for the node
                TCPConnection con = new TCPConnection(node, socket);
                node.onConnection(con);
            
            } catch (IOException ioe) {
                // Do nothing
            }
        }
    }
    
    public void exit() {
        try {
            serverSocket.close();
        } catch (IOException ioe) {
            // Do nothing
        }
    }

}

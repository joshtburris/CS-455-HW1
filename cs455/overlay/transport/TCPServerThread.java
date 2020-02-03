package cs455.overlay.transport;

import cs455.overlay.node.*;

import java.io.IOException;
import java.net.*;

public class TCPServerThread implements Runnable {

    private Object threadExitLock;
    private boolean threadExit;
    public boolean hasThreadExited() { synchronized (threadExitLock) { return threadExit; } }
    public void exitThread() {
        try {
            serverSocket.close();
            serverSocket = null;
        } catch (IOException ioe) {
            // Do nothing
        }
    }
    
    private ServerSocket serverSocket;
    private Node node;
    
    public TCPServerThread(Node node, ServerSocket serverSocket) {
        this.node = node;
        this.serverSocket = serverSocket;
        threadExitLock = new Object();
        threadExit = false;
    }
    
    public void run() {
        while (serverSocket != null && !serverSocket.isClosed()) {
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
        
        synchronized (threadExitLock) {
            threadExit = true;
        }
    }

}

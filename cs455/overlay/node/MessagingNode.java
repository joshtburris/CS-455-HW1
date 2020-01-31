package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class MessagingNode implements Node {
    
    public TCPConnection registryConnection;
    private volatile boolean threadExit;
    
    public MessagingNode(String hostname, int portnum) {
        
        // Register this node with the registry
        try {
        
            Socket socket = new Socket(hostname, portnum);
            registryConnection = new TCPConnection(this, socket);
        
            OverlayNodeSendsRegistration regi = new OverlayNodeSendsRegistration(
                    registryConnection.getLocalIpAddress(), registryConnection.getLocalPortnum());
        
            registryConnection.sendData(regi.getBytes());
        
        } catch (UnknownHostException uhe) {
            System.out.println(uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    
    public void start() {
        // Create a server
        // Start a thread that accepts new messaging nodes into the serverSocket and adds them to the list of
        // messaging node connections, as well as the list of table entries to keep their information.
        threadExit = false;
        Thread thread = new Thread(() ->  {
            while (!threadExit) {
                try {
                
                    // Accept a new connection to the server and create a socket
                    Socket socket = serverSocket.accept();
                
                    TCPConnection con = new TCPConnection(this, socket);
                    messagingNodeConnections.put(con.getRemoteSocketAddress(), con);
                
                } catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                }
            }
        });
        thread.start();
        
    }
    
    public void onEvent(Event event) {
        switch (event.getType()) {
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                printRegistrationStatus((RegistryReportsRegistrationStatus)event);
                break;
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                printDeregistrationStatus((RegistryReportsDeregistrationStatus)event);
                break;
        }
    }
    
    private void printRegistrationStatus(RegistryReportsRegistrationStatus status) {
        System.out.println(status.getInformationString());
    }
    
    private void printDeregistrationStatus(RegistryReportsDeregistrationStatus status) {
        threadExit = true;
        System.out.println(status.getInformationString());
    }
    
    public static void main(String[] args) {
    
        // Take in the registry host and port number from the command line
        String hostname = args[0];
        int portnum = Integer.parseInt(args[1]);
        
        MessagingNode messagingNode = new MessagingNode(hostname, portnum);
        messagingNode.start();
    
        // Continuously check for console commands until none is given
        Scanner in = new Scanner(System.in);
        String line;
        System.out.print(">>> ");
        while (!(line = in.nextLine().trim()).isEmpty()) {
            // Process console command and break when an exit status is returned
            if (!processConsoleCommand(messagingNode, line))
                break;
            System.out.print(">>> ");
        }
    
        messagingNode.threadExit = true;
    }
    
    private static boolean processConsoleCommand(MessagingNode messagingNode, String line) {
        return false;
    }
    
}

package cs455.overlay.node;

import cs455.overlay.routing.*;
import cs455.overlay.transport.*;
import cs455.overlay.util.*;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.*;

public class MessagingNode implements Node {
    
    private TCPConnection registryConnection;
    private TCPServerThread serverThread;
    private RoutingTable routingTable;
    private TCPConnectionsCache connectionsCache;
    
    public MessagingNode(String hostname, int portnum) {
        connectionsCache = new TCPConnectionsCache();
        
        try {
            // Initialize the serverSocket to any open port
            ServerSocket serverSocket = new ServerSocket(0, 100);
    
            // Start the server thread to accept new connections
            serverThread = new TCPServerThread(this, serverSocket);
            Thread thread = new Thread(serverThread);
            thread.start();
    
            // Register this node with the registry
            Socket socket = new Socket(hostname, portnum);
            registryConnection = new TCPConnection(this, socket);
    
            OverlayNodeSendsRegistration reg = new OverlayNodeSendsRegistration(
                    registryConnection.getLocalIpAddress(), registryConnection.getLocalPortnum());
    
            registryConnection.sendData(reg.getBytes());
            
        } catch (UnknownHostException uhe) {
            System.out.println(uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        
    }
    
    public void onEvent(Event event) {
        switch (event.getType()) {
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                onRegistrationStatus((RegistryReportsRegistrationStatus)event);
                break;
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                onDeregistrationStatus((RegistryReportsDeregistrationStatus)event);
                break;
        }
    }
    
    public void onConnection(TCPConnection con) {
        connectionsCache.add(con.getRemoteSocketAddress(), con);
    }
    
    private void onRegistrationStatus(RegistryReportsRegistrationStatus status) {
        
        if (status.getNodeId() != -1) {
            routingTable = new RoutingTable(registryConnection.getLocalIpAddress(),
                    registryConnection.getLocalPortnum(), status.getNodeId());
        }
        
        System.out.println(status.getInfoString());
    }
    
    private void onDeregistrationStatus(RegistryReportsDeregistrationStatus status) {
        
        System.out.println(status.getInfoString());
    }
    
    public void start() {
        // Process console command and break when an exit status, AKA "exit", is returned
        InteractiveCommandParser parser = new InteractiveCommandParser();
        String command;
        while ((command = parser.getConsoleCommand()).compareTo("exit") != 0) {
            processConsoleCommand(command);
        }
    
        serverThread.exitThread();
    }
    
    private void processConsoleCommand(String command) {
        switch (command) {
            case "print-counters-and-diagnostics":
                printCountersDiagnostics();
                break;
            case "exit-overlay":
                exitOverlay();
                break;
        }
    }
    
    private void printCountersDiagnostics() {
    
    }
    
    private void exitOverlay() {
    
    }
    
    public static void main(String[] args) {
    
        // Take in the registry host and port number from the command line
        String hostname = args[0];
        int portnum = Integer.parseInt(args[1]);
        
        // Initiate the MessagingNode and call start().
        MessagingNode messagingNode = new MessagingNode(hostname, portnum);
        messagingNode.start();

    }
    
}

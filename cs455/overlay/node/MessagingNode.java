package cs455.overlay.node;

import cs455.overlay.routing.*;
import cs455.overlay.transport.*;
import cs455.overlay.util.*;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.*;

public class MessagingNode implements Node {
    
    private TCPConnection registryConnection;
    private TCPServerThread server;
    private Thread serverThread;
    private RoutingTable routingTable;
    private TCPConnectionsCache connectionsCache;
    private StatisticsCollectorAndDisplay stats;
    private int nodeId;
    private Integer[] allNodeIds;
    
    public MessagingNode(String hostname, int portnum) {
        connectionsCache = new TCPConnectionsCache();
        stats = new StatisticsCollectorAndDisplay();
        
        try {
            // Initialize the serverSocket to any open port
            ServerSocket serverSocket = new ServerSocket(0, 100);
    
            // Start the server thread to accept new connections
            server = new TCPServerThread(this, serverSocket);
            serverThread = new Thread(server);
            serverThread.start();
    
            // Register this node with the registry
            Socket socket = new Socket(hostname, portnum);
            registryConnection = new TCPConnection(this, socket);
    
            OverlayNodeSendsRegistration reg = new OverlayNodeSendsRegistration(
                    registryConnection.getLocalIpAddress(), serverSocket.getLocalPort());
            System.out.println(serverSocket.getLocalPort());
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
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                receiveNodeManifest((RegistrySendsNodeManifest)event);
                break;
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                onTaskInitiate((RegistryRequestsTaskInitiate)event);
                break;
            case Protocol.OVERLAY_NODE_SENDS_DATA:
                onDataReceived((OverlayNodeSendsData)event);
                break;
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                sendTrafficSummary();
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
            
            nodeId = status.getNodeId();
        }
        
        System.out.println(status.getInfoString());
    }
    
    private void onDeregistrationStatus(RegistryReportsDeregistrationStatus status) {
        
        try {
            registryConnection.close();
            connectionsCache.closeAll();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        
        System.out.println(status.getInfoString());
    }
    
    private void receiveNodeManifest(RegistrySendsNodeManifest manifest) {
        routingTable.clearEntries();
        routingTable.addAllEntries(manifest.getEntries());
        allNodeIds = manifest.getAllNodeIds();
    
        try {
            
            for (RoutingEntry entry : manifest.getEntries()) {
    
                Socket socket = new Socket(Inet4Address.getByAddress(entry.getIpAddress()), entry.getPortnum());
                TCPConnection con = new TCPConnection(this, socket);
                connectionsCache.add(entry.getIpAddress(), entry.getPortnum(), con);
            
            }
            
            Event status = new NodeReportsOverlaySetupStatus(nodeId, "Node manifest was successfully received and " +
                    "initiated");
        
            registryConnection.sendData(status.getBytes());
            
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void onTaskInitiate(RegistryRequestsTaskInitiate task) {
    
        // Reset your statistics
        stats.reset();
    
    }
    
    private void onDataReceived(OverlayNodeSendsData data) {
    
    }
    
    private void sendTrafficSummary() {
        Event summary = new OverlayNodeReportsTrafficSummary(stats);
        
        try {
            registryConnection.sendData(summary.getBytes());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    
    public void start() {
        // Process console command and break when an exit status, AKA "exit", is returned
        InteractiveCommandParser parser = new InteractiveCommandParser();
        String command;
        while ((command = parser.getConsoleCommand()) != null) {
            if (!processConsoleCommand(command))
                break;
        }
        
        try {
            server.exit();
            serverThread.join();
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }
    
    private boolean processConsoleCommand(String command) {
        switch (command) {
            case "print-counters-and-diagnostics":
                printCountersDiagnostics();
                return true;
            case "exit-overlay":
                exitOverlay();
                return false;
        }
        return true;
    }
    
    private void printCountersDiagnostics() {
        // This should print information (to the console using System.out) about the number of messages that have been
        // sent, received, and relayed along with the sums for the messages that have been sent from and received at
        // the node.
        System.out.println("Packets sent \t Packets Received \t Packets Relayed \t Sum Values Sent \t " +
                "Sum Values Received");
        System.out.println(stats.toString());
    }
    
    private void exitOverlay() {
        // This allows a messaging node to exit the overlay. The messaging node should first send a deregistration
        // message to the registry and await for a response before exiting and terminating the process.
        Event dereg = new OverlayNodeSendsDeregistration(registryConnection.getLocalIpAddress(),
                registryConnection.getLocalPortnum(), nodeId);
        
        try {
            registryConnection.sendData(dereg.getBytes());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
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

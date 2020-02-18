package cs455.overlay.node;

import cs455.overlay.routing.*;
import cs455.overlay.transport.*;
import cs455.overlay.util.*;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MessagingNode implements Node {
    
    private TCPConnection registryConnection;
    private TCPServerThread server;
    private Thread serverThread;
    private RoutingTable routingTable;
    private TCPConnectionsCache connectionsCache;
    private StatisticsCollectorAndDisplay stats;
    private volatile int nodeId;
    private ArrayList<Integer> allNodeIds;
    
    public MessagingNode(String hostname, int portnum) {
        connectionsCache = new TCPConnectionsCache();
        stats = new StatisticsCollectorAndDisplay();
        nodeId = -1;
        
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
            
            registryConnection.sendData(reg.getBytes());
            
        } catch (UnknownHostException uhe) {
            System.out.println(uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        
    }
    
    public void onEvent(Socket socket, Event event) {
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
                    registryConnection.getLocalPortnum(), server.getLocalPort(), status.getNodeId());
            
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
        allNodeIds = new ArrayList<>();
        Collections.addAll(allNodeIds, manifest.getAllNodeIds());
        allNodeIds.remove(allNodeIds.indexOf(nodeId));
    
        String errMessage = "An error occurred in receiving the node manifest. ";
        try {
            
            for (RoutingEntry entry : manifest.getEntries()) {
    
                Socket socket = new Socket(Inet4Address.getByAddress(entry.getIpAddress()), entry.getPortnum());
                TCPConnection con = new TCPConnection(this, socket);
                connectionsCache.add(entry.getIpAddress(), entry.getPortnum(), con);
            
            }
            
            Event status = new NodeReportsOverlaySetupStatus(nodeId, "Node manifest was successfully received and " +
                    "initiated.");
        
            registryConnection.sendData(status.getBytes());
            
            // We can return here because no errors have occurred.
            return;
            
        } catch (IOException ioe) {
            errMessage += "IOException: " +ioe.getMessage();
            System.out.println(ioe.getMessage());
        }
        
        
        // If we hit this point it means an error occurred and we need to inform the registry.
        Event status = new NodeReportsOverlaySetupStatus(nodeId, errMessage);
        try {
            registryConnection.sendData(status.getBytes());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    
    private void onTaskInitiate(RegistryRequestsTaskInitiate task) {
        
        // Reset your statistics
        stats.reset();
        
        Random rand = new Random();
        
        for (int round = 0; round < task.getNumPackets(); ++round) {
            
            int sinkId = allNodeIds.get(rand.nextInt(allNodeIds.size()));
            
            int packet = rand.nextInt();
            OverlayNodeSendsData event = new OverlayNodeSendsData(sinkId, nodeId, packet);
            
            relayToNextNode(event);
    
            stats.addDataSent(packet);
            stats.addTotalPacketsSent(1);
            
        }
        
        Event report = new OverlayNodeReportsTaskFinished(registryConnection.getLocalIpAddress(),
                registryConnection.getLocalPortnum(), nodeId);
        
        try {
            registryConnection.sendData(report.getBytes());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    
    }
    
    private void relayToNextNode(OverlayNodeSendsData event) {
    
        ArrayList<RoutingEntry> entries = routingTable.getEntries();
    
        TCPConnection messagingNode = null;
        
        // Check if the destination node is in our routing table.
        for (RoutingEntry e : entries) {
            
            if (e.getNodeId() == event.getDestId()) {
                messagingNode = connectionsCache.get(e.getIpAddress(), e.getPortnum());
                break;
            }
            
        }
        
        if (messagingNode == null) {
            for (int i = entries.size() - 1; i >= 0; --i) {
                RoutingEntry e = entries.get(i);
                if (e.getNodeId() < event.getDestId()) {
                    messagingNode = connectionsCache.get(e.getIpAddress(), e.getPortnum());
                    break;
                }
            }
        }
        
        if (messagingNode == null) {
            RoutingEntry e = entries.get(entries.size() - 1);
            messagingNode = connectionsCache.get(e.getIpAddress(), e.getPortnum());
        }
        
        //RoutingEntry e = entries.get(0);
        //messagingNode = connectionsCache.get(e.getIpAddress(), e.getPortnum());
    
        try {
            messagingNode.sendData(event.getBytes());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        
    }
    
    private void onDataReceived(OverlayNodeSendsData data) {
    
        synchronized (stats) {
            if (data.getDestId() == nodeId) {
        
                stats.addDataReceived(data.getPayload());
                stats.addTotalPacketsReceived(1);
        
            } else {
        
                stats.addTotalPacketsRelayed(1);
                data.addNodeToDisTrace(nodeId);
                relayToNextNode(data);
            }
        }
    }
    
    private void sendTrafficSummary() {
        Event summary = new OverlayNodeReportsTrafficSummary(nodeId, stats);
        
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
            default:
                System.out.println("\nUsage:\n");
                System.out.println("\tprint-counters-and-diagnostics\n\t\tPrints information about the number of " +
                        "messages that have been sent, received, and relayed along with the sums for the messages " +
                        "that have been sent from and received at the node.");
                System.out.println("\texit-overlay\n\t\tThis allows a messaging node to exit the overlay.\n");
                return true;
        }
    }
    
    private void printCountersDiagnostics() {
        // This should print information (to the console using System.out) about the number of messages that have been
        // sent, received, and relayed along with the sums for the messages that have been sent from and received at
        // the node.
        System.out.format("%15s%20s%20s%20s%25s\n", "Packets sent", "Packets Received", "Packets Relayed", "Sum Values Sent",
                "Sum Values Received");
        System.out.format("%15s%20s%20s%20s%25s\n", stats.toString().split("\t"));
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
        String hostname;
        int portnum;
        try {
            hostname = args[0];
            portnum = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("Error: Incorrect arguments. The first argument should be the host name of the " +
                    "registry, and the second should be an integer to represent the port number of the registry.");
            return;
        }
        
        // Initiate the MessagingNode and call start().
        MessagingNode messagingNode = new MessagingNode(hostname, portnum);
        messagingNode.start();

    }
    
}

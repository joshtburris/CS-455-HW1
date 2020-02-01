package cs455.overlay.node;

import cs455.overlay.transport.*;
import cs455.overlay.routing.*;
import cs455.overlay.util.*;
import cs455.overlay.wireformats.*;

import java.util.*;
import java.io.*;
import java.net.*;

public class Registry implements Node {
    
    private TCPServerThread serverThread;
    private TreeMap<Byte, RoutingTable> routingTables;
    private TCPConnectionsCache connectionsCache;
    
    private byte uniqueID = 0;
    private synchronized byte getUniqueID() {
        byte tmp = uniqueID;
        while (routingTables.containsKey(tmp)) {
            ++tmp;
            if (tmp < 0) tmp = 0;
        }
        uniqueID = (byte)(tmp + 1);
        if (uniqueID < 0) uniqueID = 0;
        return tmp;
    }
    
    public Registry(int portnum) {
        routingTables = new TreeMap<>();
        connectionsCache = new TCPConnectionsCache();
    
        // Initialize the serverSocket starting with port portNum, increasing the portNum until one doesn't give an
        // exception.
        ServerSocket serverSocket = null;
        while (serverSocket == null) {
            try {
                serverSocket = new ServerSocket(portnum, 100);
            } catch (IOException ioe) {
                ++portnum;
            }
        }
        System.out.println("Registry was assigned port number: "+ serverSocket.getLocalPort());
        
        serverThread = new TCPServerThread(this, serverSocket);
        Thread thread = new Thread(serverThread);
        thread.start();
    }
    
    public void onEvent(Event event) {
        switch (event.getType()) {
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                registerMessagingNode((OverlayNodeSendsRegistration)event);
                break;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                deregisterMessagingNode((OverlayNodeSendsDeregistration)event);
                break;
        }
    }
    
    public void onConnection(TCPConnection con) {
        connectionsCache.add(con.getRemoteSocketAddress(), con);
    }
    
    public void start() {
    
        // Process console command and break when an exit status (empty string) is returned
        InteractiveCommandParser parser = new InteractiveCommandParser();
        String command;
        while (!(command = parser.getConsoleCommand()).isEmpty()) {
            processConsoleCommand(command);
        }
        
        serverThread.exitThread();
    }
    
    private void processConsoleCommand(String command) {
        String[] data = command.split(" ");
        
        switch (data[0]) {
            case "list-messaging-nodes":
                // This should result in information about the messaging nodes (hostname, port-number, and node ID)
                // being listed. Information for each messaging node should be listed on a separate line.
    
                break;
            case "setup-overlay":
                // This should result in the registry setting up the overlay. It does so by sending every messaging
                // node the REGISTRY_SENDS_NODE_MANIFEST message that contains information about the routing table
                // specific to that node and also information about other nodes in the system.
                int numRoutingTableEntries = Integer.parseInt(data[1]);
    
                // NOTE: You are not required to deal with the case where a messaging node is added or removed
                // after the overlay has been set up. You must however deal with the case where a messaging node
                // registers and deregisters from the registry before the overlay is set up.
    
    
                System.out.println("Registry now ready to initiate tasks.");
                break;
            case "list-routing-tables":
                // This should list information about the computed routing tables for each node in the overlay.
                // Each messaging node’s information should be well separated (i.e., have 3-4 blank lines between
                // node listings) and should include the node’s IP address, portnum, and logical-ID. This is
                // useful for debugging.
    
                break;
            case "start":
                // The start command results in the registry sending the REGISTRY_REQUESTS_TASK_INITIATE to all
                // nodes within the overlay. A command of start 25000 results in each messaging node sending 25000
                // packets to nodes chosen at random (of course, a node should not send a packet to itself)
                int numMessages = Integer.parseInt(data[1]);
    
                break;
        }
    }
    
    private void registerMessagingNode(OverlayNodeSendsRegistration reg) {
    
        String nodeKey = IpAddressParser.parseByteArray(reg.getIpAddress()) +":"+ reg.getPortnum();
        TCPConnection messagingNode = connectionsCache.get(nodeKey);
    
        // Ensure the IP Address matches the address where the request originated.
        if (!Arrays.equals(reg.getIpAddress(), messagingNode.getRemoteIpAddress())) {
    
            Event report = new RegistryReportsRegistrationStatus(-1,
                    "Registration request unsuccessful. Sender's IP address did not match what was given.");
        
            try {
                messagingNode.sendData(report.getBytes());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            return;
        }
        
        // Check if node has been previously registered
        for (RoutingTable entry : routingTables.values()) {
            if (Arrays.equals(entry.getIpAddress(), reg.getIpAddress())
                    && entry.getPortNum() == reg.getPortnum()) {
    
                Event report = new RegistryReportsRegistrationStatus(-1,
                        "Registration request unsuccessful. You have already registered with the registry.");
    
                try {
                    messagingNode.sendData(report.getBytes());
                } catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                }
                return;
            }
        }
        
        // Register a new messaging node
        byte nodeId = getUniqueID();
    
        // Add a new entry to the list of routing tables
        RoutingTable newEntry = new RoutingTable(reg.getIpAddress(), reg.getPortnum(), nodeId);
        routingTables.put(nodeId, newEntry);
        
        // Send the successful registration status to the messaging node
        RegistryReportsRegistrationStatus report = new RegistryReportsRegistrationStatus(nodeId, "Registration " +
                "request successful. The number of messaging nodes currently constituting the overlay is ("+
                routingTables.size() +")");
    
        try {
            messagingNode.sendData(report.getBytes());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        
    }
    
    private void deregisterMessagingNode(OverlayNodeSendsDeregistration dereg) {
        
        String nodeKey = IpAddressParser.parseByteArray(dereg.getIpAddress()) +":"+ dereg.getPortnum();
        TCPConnection messagingNode = connectionsCache.get(nodeKey);
    
        // Ensure the IP Address matches the address where the request originated.
        if (!Arrays.equals(dereg.getIpAddress(), messagingNode.getRemoteIpAddress())) {
        
            Event report = new RegistryReportsDeregistrationStatus(-1,
                    "Deregistration request unsuccessful. Sender's IP address did not match what was given.");
        
            try {
                messagingNode.sendData(report.getBytes());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            return;
        }
        
        // Check if node has been registered, if so then deregister
        // Send error if node isn't currently registered
        if (!routingTables.containsKey(dereg.getNodeId())) {
    
            Event report = new RegistryReportsRegistrationStatus(-1, "Deregistration request unsuccessful. " +
                    "You were not registered with the registry.");
    
            try {
                messagingNode.sendData(report.getBytes());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            return;
        
        } else {
            
            // Deregister messaging node
            routingTables.remove(dereg.getNodeId());
    
            // Send the successful deregistration status to the messaging node
            Event report = new RegistryReportsDeregistrationStatus(dereg.getNodeId(), "Deregistration " +
                    "request successful. You are no longer in the overlay.");
    
            try {
                messagingNode.sendData(report.getBytes());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
        
    }
    
    public static void main(String[] args) {
        
        // Take in the port number from the command line
        int portnum = Integer.parseInt(args[0]);
        
        // Create a registry
        Registry registry = new Registry(portnum);
        registry.start();
        
    }
    
}

package cs455.overlay.node;

import cs455.overlay.transport.*;
import cs455.overlay.routing.*;
import cs455.overlay.wireformats.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.util.TreeMap;

public class Registry implements Node {
    
    private ServerSocket serverSocket;
    private ArrayList<RoutingTable> routingTables;
    private TreeMap<String, TCPConnection> messagingNodeConnections;
    
    private volatile boolean threadExit;
    
    private byte uniqueID = 0;
    private byte getUniqueID() { return uniqueID++; }
    
    public Registry(int portnum) {
        routingTables = new ArrayList<>();
        messagingNodeConnections = new TreeMap<>();
    
        // Initialize the serverSocket starting with port portNum, increasing the portNum until one doesn't give an
        // exception.
        while (serverSocket == null) {
            try {
                serverSocket = new ServerSocket(portnum, 100);
            } catch (IOException ioe) {
                ++portnum;
            }
        }
        System.out.println("Registry was assigned port number: "+ serverSocket.getLocalPort());
    
        // Start a thread that accepts new messaging nodes into the serverSocket and adds them to the list of
        // messaging node connections, as well as the list of table entries to keep their information.
        threadExit = false;
        Thread thread = new Thread(() ->  {
            while (!threadExit) {
                try {
                
                    // Accept a new connection to the server and create a socket
                    Socket socket = serverSocket.accept();
                    
                    messagingNodeConnections.put(socket.getRemoteSocketAddress().toString(),
                            new TCPConnection(this, socket));
                    System.out.println("New connection: "+ socket.getRemoteSocketAddress());
                    
                } catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                }
            }
        });
        thread.start();
    }
    
    public void onEvent(Event event) {
        
        if (event instanceof OverlayNodeSendsRegistration) {
            registerMessagingNode((OverlayNodeSendsRegistration)event);
        }
        
    }
    
    private void registerMessagingNode(OverlayNodeSendsRegistration registration) {
    
        String nodeKey = registration.getIpAddress() +"\t"+ registration.getPortNum();
        TCPConnection messagingNode = messagingNodeConnections.get(nodeKey);
        
        // Check if node has been previously registered
        for (RoutingTable entry : routingTables) {
            if (entry.isRegistered() && entry.getIpAddress() == registration.getIpAddress()
                    && entry.getPortNum() == registration.getPortNum()) {
    
                RegistryReportsRegistrationStatus report = new RegistryReportsRegistrationStatus(-1,
                        "Registration request unsuccessful. You have already registered with the registry.");
    
                try {
                    messagingNode.sendData(report.getBytes());
                } catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                }
                return;
            }
        }
        
        // Ensure the IP Address matches the address where the request originated.
        if (registration.getIpAddress() != messagingNode.getRemoteIpAddress()) {
            
            RegistryReportsRegistrationStatus report = new RegistryReportsRegistrationStatus(-1,
                    "Registration request unsuccessful. Sender's IP address did not match what was given.");
    
            try {
                messagingNode.sendData(report.getBytes());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            return;
        }
        
        // Register a new messaging node
        byte nodeId = getUniqueID();
        RegistryReportsRegistrationStatus report = new RegistryReportsRegistrationStatus(nodeId, "Registration " +
                "request successful. The number of messaging nodes currently constituting the overlay is ("+
                routingTables.size() +")");
    
        try {
            messagingNode.sendData(report.getBytes());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        
        // Add a new entry to the list of routing tables
        RoutingTable newEntry = new RoutingTable(registration.getIpAddress(), registration.getPortNum(),
                nodeId);
        routingTables.add(newEntry);
    }
    
    public static void main(String[] args) {
        
        // Take in the port number from the command line
        int portnum = Integer.parseInt(args[0]);
        
        // Create a registry
        Registry registry = new Registry(portnum);
        
        // Continuously check for console commands until none is given
        Scanner in = new Scanner(System.in);
        String line;
        System.out.print(">>> ");
        while (!(line = in.nextLine().trim()).isEmpty()) {
            // Process console command and break when an exit status is returned
            if (!processConsoleCommand(registry, line))
                break;
            System.out.print(">>> ");
        }
        
        registry.threadExit = true;
    }
    
    // Returns command status: 'false' for continue processing another command, 'true' for stop processing commands.
    private static boolean processConsoleCommand(Registry registry, String line) {
        String[] data = line.split(" ");
        
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
            case "exit": case "quit": case "stop":
                return false;
        }
        
        return true;
    }
    
}

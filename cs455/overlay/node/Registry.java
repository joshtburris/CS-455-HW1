package cs455.overlay.node;

import cs455.overlay.transport.*;
import cs455.overlay.routing.*;
import cs455.overlay.util.*;
import cs455.overlay.wireformats.*;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.net.*;

public class Registry implements Node {
    
    private TCPServerThread server;
    private Thread serverThread;
    private RoutingTablesCache tablesCache;
    private TCPConnectionsCache connectionsCache;
    
    private int uniqueID = 0;
    private synchronized int getUniqueID() {
        int tmp = uniqueID;
        while (tablesCache.containsKey(tmp)) {
            ++tmp;
            if (tmp < 0 || tmp > 127) tmp = 0;
        }
        uniqueID = tmp + 1;
        if (uniqueID < 0 || uniqueID > 127) uniqueID = 0;
        return tmp;
    }
    
    public Registry(int portnum) {
        tablesCache = new RoutingTablesCache();
        connectionsCache = new TCPConnectionsCache();
    
        // Initialize the serverSocket starting with port portnum, increasing the portnum until one doesn't give an
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
        
        server = new TCPServerThread(this, serverSocket);
        serverThread = new Thread(server);
        serverThread.start();
    }
    
    public void onEvent(Event event) {
        switch (event.getType()) {
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                registerMessagingNode((OverlayNodeSendsRegistration)event);
                break;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                deregisterMessagingNode((OverlayNodeSendsDeregistration)event);
                break;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                onOverlaySetupStatus((NodeReportsOverlaySetupStatus)event);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                onTaskFinished((OverlayNodeReportsTaskFinished)event);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                onTrafficSummary((OverlayNodeReportsTrafficSummary)event);
                break;
        }
    }
    
    public void onConnection(TCPConnection con) {
        connectionsCache.add(con.getRemoteSocketAddress(), con);
    }
    
    private void registerMessagingNode(OverlayNodeSendsRegistration reg) {
        
        TCPConnection messagingNode = connectionsCache.get(reg.getIpAddress(), reg.getPortnum());
        
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
        for (RoutingTable entry : tablesCache.getValues()) {
            if (Arrays.equals(entry.getIpAddress(), reg.getIpAddress())
                    && entry.getPortnum() == reg.getPortnum()) {
                
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
        int nodeId = getUniqueID();
        
        // Add a new entry to the list of routing tables
        RoutingTable newEntry = new RoutingTable(reg.getIpAddress(), reg.getPortnum(), nodeId);
        tablesCache.add(nodeId, newEntry);
        
        // Send the successful registration status to the messaging node
        RegistryReportsRegistrationStatus report = new RegistryReportsRegistrationStatus(nodeId, "Registration " +
                "request successful. The number of messaging nodes currently constituting the overlay is ("+
                tablesCache.size() +")");
        
        try {
            messagingNode.sendData(report.getBytes());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        
    }
    
    private void deregisterMessagingNode(OverlayNodeSendsDeregistration dereg) {
        
        TCPConnection messagingNode = connectionsCache.get(dereg.getIpAddress(), dereg.getPortnum());
        
        // Ensure the IP Address matches the address where the request originated.
        if (!Arrays.equals(dereg.getIpAddress(), messagingNode.getRemoteIpAddress())) {
            
            Event report = new RegistryReportsDeregistrationStatus(-1, "Deregistration request unsuccessful. " +
                    "Sender's IP address did not match what was given.");
            
            try {
                messagingNode.sendData(report.getBytes());
                System.out.println("Error sending deregistration status where IP address did not match.");
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            return;
        }
        
        // Check if node has been registered, if so then deregister
        // Send error if node isn't currently registered
        if (!tablesCache.containsKey(dereg.getNodeId())) {
            
            Event report = new RegistryReportsRegistrationStatus(-1, "Deregistration request unsuccessful. " +
                    "You were not registered with the registry.");
            
            try {
                messagingNode.sendData(report.getBytes());
            } catch (IOException ioe) {
                System.out.println("Error sending deregistration status where node was not previously registered.");
                System.out.println(ioe.getMessage());
            }
            
            return;
        }
            
        // Deregister messaging node
        tablesCache.remove(dereg.getNodeId());
        
        // Send the successful deregistration status to the messaging node
        Event report = new RegistryReportsDeregistrationStatus(dereg.getNodeId(), "Deregistration " +
                "request successful. You are no longer in the overlay.");
        
        try {
            messagingNode.sendData(report.getBytes());
            messagingNode.close();
        } catch (IOException ioe) {
            System.out.println("Error sending successful deregistration status, ioe.");
            System.out.println(ioe.getMessage());
        } catch (Exception e) {
            System.out.println("Error sending successful deregistration status, e.");
            System.out.println(e.getMessage());
        }
        
        connectionsCache.remove(dereg.getIpAddress(), dereg.getPortnum());
        
    }
    
    private void onOverlaySetupStatus(NodeReportsOverlaySetupStatus status) {
    
        // Increase the number of setup nodes if a successful setup status was returned
        if (status.getNodeId() != -1) {
            tablesCache.nodeIsSetup(status.getNodeId());
            
            // Inform the user of the status of the node in setting up connections to the nodes that are apart of its
            // routing table
            System.out.println("Node "+ status.getNodeId() +": "+ status.getInfoString());
            
            // If all of the nodes reported a successful status then notify the user that we are ready to initiate
            // tasks
            if (tablesCache.areAllNodesSetup())
                System.out.println("Registry now ready to initiate tasks.");
        }
        
    }
    
    private void onTaskFinished(OverlayNodeReportsTaskFinished task) {
    
        // Increase the number of finished nodes
        tablesCache.nodeIsFinished(task.getNodeId());
        
        // If all nodes have reported finished tasks then request a traffic summary
        if (tablesCache.areAllNodesFinished()) {
            Event request = new RegistryRequestsTrafficSummary();
            
            for (TCPConnection con : connectionsCache.getValues()) {
                try {
                    con.sendData(request.getBytes());
                } catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                }
            }
        }
    
    }
    
    private void onTrafficSummary(OverlayNodeReportsTrafficSummary summary) {
    
        tablesCache.nodeHasSummary(summary.getNodeId(), summary.getStats());
        
        if (tablesCache.allNodesHaveSummaries()) {
            
            StatisticsCollectorAndDisplay totalStats = new StatisticsCollectorAndDisplay();
            
            System.out.println(" \t Packets sent \t Packets Received \t Packets Relayed \t Sum Values Sent \t " +
                    "Sum Values Received");
            
            for (Entry<Integer, StatisticsCollectorAndDisplay> entries : tablesCache.getSummaries()) {
                StatisticsCollectorAndDisplay val = entries.getValue();
                totalStats.addTotalPacketsSent(val.getTotalPacketsSent());
                totalStats.addTotalPacketsReceived(val.getTotalPacketsReceived());
                totalStats.addTotalPacketsRelayed(val.getTotalPacketsRelayed());
                totalStats.addDataSent(val.getSumDataSent());
                totalStats.addDataReceived(val.getSumDataReceived());
                
                System.out.println("Node "+ entries.getKey() +" \t "+ val.toString());
            }
    
            System.out.println("Sum \t "+ totalStats.toString());
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
            connectionsCache.closeAll();
            serverThread.join();
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }
    
    private boolean processConsoleCommand(String command) {
        String[] data = command.split(" ");
        
        switch (data[0]) {
            case "list-messaging-nodes":
                listMessagingNodes();
                return true;
            case "setup-overlay":
                int numRoutingTableEntries = 3;
                try {
                    numRoutingTableEntries = Integer.parseInt(data[1]);
                } catch (ArrayIndexOutOfBoundsException ibe) {
                    System.out.println("The number of routing table entries was not specified. Defaulting to 3.");
                }
                setupOverlay(numRoutingTableEntries);
                return true;
            case "list-routing-tables":
                listRoutingTables();
                return true;
            case "start":
                int numMessages = 1;
                try {
                    numMessages = Integer.parseInt(data[1]);
                } catch (ArrayIndexOutOfBoundsException ibe) {
                    System.out.println("The number of messages was not specified. Defaulting to 1.");
                }
                startOverlay(numMessages);
                return true;
            case "exit-overlay":
                return false;
        }
        return true;
    }
    
    private void listMessagingNodes() {
        // This should result in information about the messaging nodes (hostname, port-number, and node ID)
        // being listed. Information for each messaging node should be listed on a separate line.
    
        for (Entry<Integer, RoutingTable> entry : tablesCache.getEntries()) {
            RoutingTable table = entry.getValue();
            System.out.println(table.getHostName() +"\t"+ table.getPortnum() +"\t"+ table.getNodeId());
        }
    }
    
    private void setupOverlay(int numRoutingTableEntries) {
        // This should result in the registry setting up the overlay. It does so by sending every messaging
        // node the REGISTRY_SENDS_NODE_MANIFEST message that contains information about the routing table
        // specific to that node and also information about other nodes in the system.
        // NOTE: You are not required to deal with the case where a messaging node is added or removed
        // after the overlay has been set up. You must however deal with the case where a messaging node
        // registers and deregisters from the registry before the overlay is set up.
        
        Integer[] keys = new Integer[tablesCache.size()];
        RoutingTable[] values = new RoutingTable[tablesCache.size()];
        tablesCache.getValues().toArray(values);
        tablesCache.getKeys().toArray(keys);
        
        int tableIndex = 0;
        for (RoutingTable table : tablesCache.getValues()) {
        
            // Clear any previous routing entries from the messaging node
            table.clearEntries();
            
            // Calculate routing entries
            for (int dist = 1; dist < Math.pow(2, numRoutingTableEntries); dist*=2) {
            
                int i = tableIndex+dist;
                while (i >= keys.length)
                    i -= keys.length;
                
                if (i != tableIndex) {
                    table.addEntry(new RoutingEntry(values[i].getIpAddress(), values[i].getPortnum(), keys[i]));
                }
            
            }
            
            ++tableIndex;
    
            // Send manifest to the node
            Event manifest = new RegistrySendsNodeManifest(table.getEntries(), keys);
            
            try {
                connectionsCache.get(table.getIpAddress(), table.getPortnum()).sendData(manifest.getBytes());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            
        }
        
    }
    
    private void listRoutingTables() {
        // This should list information about the computed routing tables for each node in the overlay.
        // Each messaging node’s information should be well separated (i.e., have 3-4 blank lines between
        // node listings) and should include the node’s IP address, portnum, and logical-ID. This is
        // useful for debugging.
        for (RoutingTable table : tablesCache.getValues()) {
            System.out.println("Host: " + table.getHostName());
            for (RoutingEntry entry : table.getEntries())
                System.out.println(IpAddressParser.parseByteArray(entry.getIpAddress()) + "\t" + entry.getPortnum() +
                        "\t" + entry.getNodeId());
            System.out.println();
        }
    }
    
    private void startOverlay(int numMessages) {
        // The start command results in the registry sending the REGISTRY_REQUESTS_TASK_INITIATE to all
        // nodes within the overlay. A command of start 25000 results in each messaging node sending 25000
        // packets to nodes chosen at random (of course, a node should not send a packet to itself)
        Event task = new RegistryRequestsTaskInitiate(numMessages);
        
        for (TCPConnection con : connectionsCache.getValues()) {
            try {
                con.sendData(task.getBytes());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        
        // Take in the port number from the command line
        int portnum = Integer.parseInt(args[0]);
        
        // Initiate the Registry and call start().
        Registry registry = new Registry(portnum);
        registry.start();
        
    }
    
}

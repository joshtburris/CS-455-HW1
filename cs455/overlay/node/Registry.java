package cs455.overlay.node;

import java.util.Scanner;
import java.io.*;
import java.net.*;

public class Registry {

    // Check if node has been previously registered
    // Ensure the IP Address matches the address where the request originated.
    
    // If no errors:
    //MessagingNode.sendByte(MessageType.REGISTRY_REPORTS_REGISTRATION_STATUS);
    //MessagingNode.sendInt(/*Length of IP Address*/);
    //MessagingNode.sendByteList(INetAddress.getAddress());
    //MessagingNode.sendInt(/*Port number*/);
    
    public static int portNum;
    private static ServerSocket serverSocket;
    
    private byte uniqueID = 0;
    private byte getUniqueID() { return uniqueID++; }
    
    public static void main(String[] args) {
        
        // Take in the port number from the command line
        portNum = Integer.parseInt(args[0]);
    
        while (serverSocket == null) {
            try {
                serverSocket = new ServerSocket(portNum, 100);
            } catch (IOException ioe) {
                ++portNum;
            }
        }
        System.out.println("We were given port number "+ portNum +".");
        
        
        
        // Continuously check for console commands until none is given
        Scanner in = new Scanner(System.in);
        String line;
        System.out.print(">>> ");
        while (!(line = in.nextLine().trim()).isEmpty()) {
            // Process console command and break when an exit status is returned
            if (processConsoleCommand(line) == 1)
                break;
            System.out.print(">>> ");
        }
        
    }
    
    // Returns command status: 0 for continue processing another command, 1 for stop processing commands.
    private static int processConsoleCommand(String line) {
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
            
            
                break;
            case "exit": case "quit": case "stop":
                return 1;
        }
        
        return 0;
    }

}

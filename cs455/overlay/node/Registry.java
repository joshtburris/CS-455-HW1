package cs455.overlay.node;

import java.util.Scanner;

public class Registry {

    // Check if node has been previously registered
    // Ensure the IP Address matches the address where the request originated.
    
    // If no errors:
    //MessagingNode.sendByte(MessageType.REGISTRY_REPORTS_REGISTRATION_STATUS);
    //MessagingNode.sendInt(/*Length of IP Address*/);
    //MessagingNode.sendByteList(INetAddress.getAddress());
    //MessagingNode.sendInt(/*Port number*/);
    
    public static int portNum;
    
    private byte uniqueID = 0;
    private byte getUniqueID() { return uniqueID++; }
    
    public static void main(String[] args) {
        
        // Take in the port number from the command line
        portNum = Integer.parseInt(args[0]);
    
        Scanner in = new Scanner(System.in);
        
        // Continuously check for console commands until none is given
        String[] line;
        while ((line = in.nextLine().split(" ")).length != 0) {
            
            // Process console command
            switch (line[0]) {
                case "list-messaging-nodes":
                    // This should result in information about the messaging nodes (hostname, port-number, and node ID)
                    // being listed. Information for each messaging node should be listed on a separate line.
                    
                    break;
                case "setup-overlay":
                    // This should result in the registry setting up the overlay. It does so by sending every messaging
                    // node the REGISTRY_SENDS_NODE_MANIFEST message that contains information about the routing table
                    // specific to that node and also information about other nodes in the system.
                    int numRoutingTableEntries = Integer.parseInt(line[1]);
                    
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
            }
            
        }
        
        Registry reg = new Registry();
        System.out.println(reg.getUniqueID());
        System.out.println(reg.getUniqueID());
        System.out.println(reg.getUniqueID());
        System.out.println("Hello from the Registry!");
    }

}

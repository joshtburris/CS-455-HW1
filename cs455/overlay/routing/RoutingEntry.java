package cs455.overlay.routing;

import cs455.overlay.transport.TCPConnection;

import java.net.*;

public class RoutingEntry {
    
    private String hostname;
    public String getHostname() { return hostname; }
    
    private byte portNum;
    public byte getPortNum() { return portNum; }
    
    private byte nodeId;
    public byte getNodeId() { return nodeId; }
    
    private boolean registered;
    public boolean isRegistered() { return registered; }
    public void deregister() { registered = false; }
    
    public RoutingEntry(Socket socket) {
        //TODO: Routing table is all wrong.
    }

}

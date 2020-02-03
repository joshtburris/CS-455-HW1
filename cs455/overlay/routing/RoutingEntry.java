package cs455.overlay.routing;

import cs455.overlay.transport.TCPConnection;

import java.net.*;

public class RoutingEntry {
    
    private String hostname;
    public String getHostname() { return hostname; }
    
    private byte[] ipAddress;
    public byte[] getIpAddress() { return ipAddress; }
    
    private byte portNum;
    public byte getPortNum() { return portNum; }
    
    private byte distance;
    public byte getDistance() { return distance; }
    
    private byte nodeId;
    public byte getNodeId() { return nodeId; }
    
    private boolean registered;
    public boolean isRegistered() { return registered; }
    public void deregister() { registered = false; }
    
    public RoutingEntry(String hostname, byte[] ipAddress, byte portNum, byte distance, byte nodeId) {
        //TODO: Routing table is all wrong.
        registered = true;
    }

}

package cs455.overlay.routing;

import java.util.*;

public class RoutingTable {
    
    private byte[] ipAddress;
    public byte[] getIpAddress() { return ipAddress; }
    
    private int portNum;
    public int getPortNum() { return portNum; }
    
    private byte nodeId;
    public byte getNodeId() { return nodeId; }
    
    private boolean registered;
    public boolean isRegistered() { return registered; }
    public void deregister() { registered = false; }
    
    public RoutingTable(byte[] ipAddress, int portNum, byte nodeId) {
        registered = true;
        this.ipAddress = ipAddress;
        this.portNum = portNum;
        this.nodeId = nodeId;
    }
    
    
    
    
    
    /*
    private Object lock;
    private TreeMap<Integer, RoutingEntry> entries;
    
    
    public RoutingTable() {
        lock = new Object();
        entries = new TreeMap<>();
    }
    
    public void addEntry(RoutingEntry entry, int id) {
        synchronized (lock) {
            entries.put(id, entry);
        }
    }
    
    public void listMessagingNodes() {
        synchronized (lock) {
        
            for (RoutingEntry entry : entries.values()) {
                System.out.println(entry.getHostname() +"\t"+ entry.getPortNum() +"\t"+ entry.getNodeId());
            }
        
        }
    }*/

}

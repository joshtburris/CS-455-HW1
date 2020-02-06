package cs455.overlay.routing;

import java.util.ArrayList;

public class RoutingTable {
    
    private byte[] ipAddress;
    public byte[] getIpAddress() { return ipAddress; }
    
    private int portnum;
    public int getPortnum() { return portnum; }
    
    private int nodeId;
    public int getNodeId() { return nodeId; }
    
    private ArrayList<RoutingEntry> entries;
    public ArrayList<RoutingEntry> getEntries() {
        return entries;
    }
    
    public void addEntry(RoutingEntry entry) {
        synchronized (entries) {
            entries.add(entry);
        }
    }
    
    public RoutingTable(byte[] ipAddress, int portnum, int nodeId) {
        this.ipAddress = ipAddress;
        this.portnum = portnum;
        this.nodeId = nodeId;
    
        entries = new ArrayList<>();
    }
    
    
    
    /*public void listMessagingNodes() {
        synchronized (entries) {
        
            for (RoutingEntry entry : entries) {
                System.out.println(entry.getHostname() +"\t"+ entry.getPortnum() +"\t"+ entry.getNodeId());
            }
        
        }
    }*/

}

package cs455.overlay.routing;

import java.net.*;
import java.util.ArrayList;

public class RoutingTable {
    
    private String hostName;
    public String getHostName() { return hostName; }
    
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
    
        String hostName = "UNKNOWN HOST";
        try {
            hostName = Inet4Address.getByAddress(ipAddress).getHostName().split("\\.")[0];
        } catch (UnknownHostException uhe) {
            System.out.println(uhe.getMessage());
        }
        this.hostName = hostName;
    }

}

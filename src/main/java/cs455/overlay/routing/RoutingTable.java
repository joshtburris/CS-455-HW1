package cs455.overlay.routing;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class RoutingTable {
    
    private String hostName;
    public String getHostName() { return hostName; }
    
    private byte[] ipAddress;
    public byte[] getIpAddress() { return ipAddress; }
    
    private int localPortnum;
    public int getLocalPortnum() { return localPortnum; }
    
    private int serverPortnum;
    public int getServerPortnum() { return serverPortnum; }
    
    private int nodeId;
    public int getNodeId() { return nodeId; }
    
    private ArrayList<RoutingEntry> entries;
    public ArrayList<RoutingEntry> getEntries() {
        ArrayList<RoutingEntry> arr;
        synchronized (entries) {
            arr = new ArrayList<>(entries);
        }
        return arr;
    }
    
    public void addEntry(RoutingEntry entry) {
        synchronized (entries) {
            entries.add(entry);
        }
    }
    
    public void addAllEntries(ArrayList<RoutingEntry> entries) {
        this.entries.addAll(entries);
    }
    
    public void clearEntries() {
        synchronized (entries) {
            entries.clear();
        }
    }
    
    public RoutingTable(byte[] ipAddress, int localPortnum, int serverPortnum, int nodeId) {
        this.ipAddress = ipAddress;
        this.localPortnum = localPortnum;
        this.serverPortnum = serverPortnum;
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

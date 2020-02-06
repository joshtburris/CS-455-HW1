package cs455.overlay.routing;

public class RoutingEntry {
    
    private byte[] ipAddress;
    public byte[] getIpAddress() { return ipAddress; }
    
    private int portnum;
    public int getPortnum() { return portnum; }
    
    private int nodeId;
    public int getNodeId() { return nodeId; }
    
    public RoutingEntry(byte[] ipAddress, int portnum, int nodeId) {
        this.ipAddress = ipAddress;
        this.portnum = portnum;
        this.nodeId = nodeId;
    }

}

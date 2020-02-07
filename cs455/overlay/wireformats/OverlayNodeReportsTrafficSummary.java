package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeReportsTrafficSummary extends Event {
    
    private int nodeId;
    public int getNodeId() {
        return nodeId;
    }
    
    private int totalPacketsSent;
    public int getTotalPacketsSent() {
        return totalPacketsSent;
    }
    
    private int totalPacketsRelayed;
    public int getTotalPacketsRelayed() {
        return totalPacketsRelayed;
    }
    
    private long sumDataSent;
    public long getSumDataSent() {
        return sumDataSent;
    }
    
    private int totalPacketsReceived;
    public int getTotalPacketsReceived() {
        return totalPacketsReceived;
    }
    
    private long sumDataReceived;
    public long getSumDataReceived() {
        return sumDataReceived;
    }
    
    public byte getType() {
        return Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
    }
    
    public byte[] getBytes() throws IOException {
        
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        
        dout.writeByte(getType());
        
        dout.writeInt(nodeId);
        dout.writeInt(totalPacketsSent);
        dout.writeInt(totalPacketsRelayed);
        dout.writeLong(sumDataSent);
        dout.writeInt(totalPacketsReceived);
        dout.writeLong(sumDataReceived);
        
        dout.flush();
        byte[] marshalledBytes = baOutputStream.toByteArray();
        
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }
    
    public OverlayNodeReportsTrafficSummary(int nodeId, int totalPacketsSent, int totalPacketsRelayed, long sumDataSent,
            int totalPacketsReceived, long sumDataReceived) {
        this.nodeId = nodeId;
        this.totalPacketsSent = totalPacketsSent;
        this.totalPacketsRelayed = totalPacketsRelayed;
        this.sumDataSent = sumDataSent;
        this.totalPacketsReceived = totalPacketsReceived;
        this.sumDataReceived = sumDataReceived;
    }
    
    public OverlayNodeReportsTrafficSummary(ByteArrayInputStream baInputStream, DataInputStream din)
            throws IOException {
        
        nodeId = din.readInt();
        totalPacketsSent = din.readInt();
        totalPacketsRelayed = din.readInt();
        sumDataSent = din.readLong();
        totalPacketsReceived = din.readInt();
        sumDataReceived = din.readLong();
        
        baInputStream.close();
        din.close();
    }
    
}

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
        
        byte[] infoStringBytes = infoString.getBytes();
        dout.writeByte(infoStringBytes.length);
        dout.write(infoStringBytes);
        
        dout.flush();
        byte[] marshalledBytes = baOutputStream.toByteArray();
        
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }
    
    public OverlayNodeReportsTrafficSummary(int nodeId, String infoString) {
        this.nodeId = nodeId;
        this.infoString = infoString;
    }
    
    public OverlayNodeReportsTrafficSummary(ByteArrayInputStream baInputStream, DataInputStream din)
            throws IOException {
        
        nodeId = din.readInt();
        
        byte len = din.readByte();
        byte[] infoStringBytes = new byte[len];
        din.readFully(infoStringBytes);
        infoString = new String(infoStringBytes);
        
        baInputStream.close();
        din.close();
    }
    
}

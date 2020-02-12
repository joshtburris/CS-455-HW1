package cs455.overlay.wireformats;

import java.io.*;

public class NodeReportsOverlaySetupStatus extends Event {
    
    private int nodeId;
    public int getNodeId() { return nodeId; }
    
    private String infoString;
    public String getInfoString() { return infoString; }
    
    public byte getType() { return Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS; }
    
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
    
    public NodeReportsOverlaySetupStatus(int nodeId, String infoString) {
        this.nodeId = nodeId;
        this.infoString = infoString;
    }
    
    public NodeReportsOverlaySetupStatus(ByteArrayInputStream baInputStream, DataInputStream din) throws IOException {
    
        nodeId = din.readInt();
    
        byte len = din.readByte();
        byte[] infoStringBytes = new byte[len];
        din.readFully(infoStringBytes);
        infoString = new String(infoStringBytes);
        
        baInputStream.close();
        din.close();
    }

}
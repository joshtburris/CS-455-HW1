package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsDeregistration extends Event {
    
    private byte[] ipAddress;
    private int portnum;
    private byte nodeId;
    
    public byte getType() { return Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION; }
    public byte[] getIpAddress() { return ipAddress; }
    public int getPortnum() { return portnum; }
    public byte getNodeId() { return nodeId; }
    
    public byte[] getBytes() throws IOException {
        
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        
        dout.writeByte(getType());
        
        dout.writeByte(ipAddress.length);
        dout.write(ipAddress);
        
        dout.writeInt(portnum);
        dout.writeByte(nodeId);
        
        dout.flush();
        byte[] marshalledBytes = baOutputStream.toByteArray();
        
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }
    
    public OverlayNodeSendsDeregistration(byte[] ipAddress, int portnum, byte nodeId) {
        this.ipAddress = ipAddress;
        this.portnum = portnum;
        this.nodeId = nodeId;
    }
    
    public OverlayNodeSendsDeregistration(ByteArrayInputStream baInputStream, DataInputStream din) throws IOException {
        
        byte len = din.readByte();
        ipAddress = new byte[len];
        din.readFully(ipAddress);
    
        portnum = din.readInt();
        nodeId = din.readByte();
        
        baInputStream.close();
        din.close();
    }
    
}

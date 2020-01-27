package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsRegistration extends Event {
    
    private byte type;
    private byte[] ipAddress;
    private int portNum;
    
    public byte getType() { return type; }
    public byte[] getIpAddress() { return ipAddress; }
    public int getPortNum() { return portNum; }
    
    public byte[] getBytes() throws IOException {
        
        byte[] marshalledBytes = null;
        
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        
        dout.writeByte(type);
        
        dout.writeByte(ipAddress.length);
        dout.write(ipAddress);
        
        dout.writeInt(portNum);
        
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }
    
    public OverlayNodeSendsRegistration(byte[] ipAddress, int portNum) {
        this.type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
        this.ipAddress = ipAddress;
        this.portNum = portNum;
    }
    
    public OverlayNodeSendsRegistration(ByteArrayInputStream baInputStream, DataInputStream din) throws IOException {
        
        /*ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        
        type = din.readByte();*/
        
        byte len = din.readByte();
        ipAddress = new byte[len];
        din.readFully(ipAddress);
        
        portNum = din.readInt();
        
        baInputStream.close();
        din.close();
    }
    
}

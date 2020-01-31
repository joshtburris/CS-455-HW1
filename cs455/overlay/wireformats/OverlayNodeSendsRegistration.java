package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsRegistration extends Event {
    
    private byte[] ipAddress;
    private int portnum;
    
    public byte getType() { return Protocol.OVERLAY_NODE_SENDS_REGISTRATION; }
    public byte[] getIpAddress() { return ipAddress; }
    public int getPortnum() { return portnum; }
    
    public byte[] getBytes() throws IOException {
        
        byte[] marshalledBytes = null;
        
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        
        dout.writeByte(getType());
        
        dout.writeByte(ipAddress.length);
        dout.write(ipAddress);
        
        dout.writeInt(portnum);
        
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }
    
    public OverlayNodeSendsRegistration(byte[] ipAddress, int portnum) {
        this.ipAddress = ipAddress;
        this.portnum = portnum;
    }
    
    public OverlayNodeSendsRegistration(ByteArrayInputStream baInputStream, DataInputStream din) throws IOException {
        
        /*ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        
        type = din.readByte();*/
        
        byte len = din.readByte();
        ipAddress = new byte[len];
        din.readFully(ipAddress);
    
        portnum = din.readInt();
        
        baInputStream.close();
        din.close();
    }
    
}

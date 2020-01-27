package cs455.overlay.wireformats;

import java.io.*;

public class RegistryReportsRegistrationStatus extends Event {

    private int nodeId;
    private byte[] ipAddress;
    
    public byte getType() { return Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS; }
    public int getNodeId() { return nodeId; }
    public byte[] getIpAddress() { return ipAddress; }
    
    public byte[] getBytes() throws IOException {
        
        byte[] marshalledBytes = null;
        
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        
        dout.writeByte(getType());
        
        dout.writeInt(nodeId);
        
        dout.writeByte(ipAddress.length);
        dout.write(ipAddress);
        
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }
    
    public RegistryReportsRegistrationStatus(int nodeId, byte[] ipAddress) {
        this.nodeId = nodeId;
        this.ipAddress = ipAddress;
    }
    
    public RegistryReportsRegistrationStatus(ByteArrayInputStream baInputStream, DataInputStream din)
            throws IOException {
        
        nodeId = din.readInt();
        
        byte len = din.readByte();
        ipAddress = new byte[len];
        din.readFully(ipAddress);
        
        baInputStream.close();
        din.close();
    }

}

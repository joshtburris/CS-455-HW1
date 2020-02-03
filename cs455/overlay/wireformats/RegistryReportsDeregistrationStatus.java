package cs455.overlay.wireformats;

import java.io.*;

public class RegistryReportsDeregistrationStatus extends Event {
    
    private int nodeId;
    private String informationString;
    
    public byte getType() { return Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS; }
    public int getNodeId() { return nodeId; }
    public String getInformationString() { return informationString; }
    
    public byte[] getBytes() throws IOException {
        
        byte[] marshalledBytes = null;
        
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        
        dout.writeByte(getType());
        
        dout.writeInt(nodeId);
        
        byte[] informationStringBytes = informationString.getBytes();
        dout.writeByte(informationStringBytes.length);
        dout.write(informationStringBytes);
        
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }
    
    public RegistryReportsDeregistrationStatus(int nodeId, String informationString) {
        this.nodeId = nodeId;
        this.informationString = informationString;
    }
    
    public RegistryReportsDeregistrationStatus(ByteArrayInputStream baInputStream, DataInputStream din)
            throws IOException {
        
        nodeId = din.readInt();
        
        byte len = din.readByte();
        byte[] informationStringBytes = new byte[len];
        din.readFully(informationStringBytes);
        informationString = new String(informationStringBytes);
        
        baInputStream.close();
        din.close();
    }
    
}

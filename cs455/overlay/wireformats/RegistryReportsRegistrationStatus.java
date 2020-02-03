package cs455.overlay.wireformats;

import java.io.*;

public class RegistryReportsRegistrationStatus extends Event {

    private byte nodeId;
    private String informationString;
    
    public byte getType() { return Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS; }
    public byte getNodeId() { return nodeId; }
    public String getInformationString() { return informationString; }
    
    public byte[] getBytes() throws IOException {
        
        byte[] marshalledBytes = null;
        
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        
        dout.writeByte(getType());
        
        dout.writeByte(nodeId);
    
        byte[] informationStringBytes = informationString.getBytes();
        dout.writeByte(informationStringBytes.length);
        dout.write(informationStringBytes);
        
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }
    
    public RegistryReportsRegistrationStatus(byte nodeId, String informationString) {
        this.nodeId = nodeId;
        this.informationString = informationString;
    }
    
    public RegistryReportsRegistrationStatus(ByteArrayInputStream baInputStream, DataInputStream din)
            throws IOException {
        
        nodeId = din.readByte();
        
        byte len = din.readByte();
        byte[] informationStringBytes = new byte[len];
        din.readFully(informationStringBytes);
        informationString = new String(informationStringBytes);
        
        baInputStream.close();
        din.close();
    }

}

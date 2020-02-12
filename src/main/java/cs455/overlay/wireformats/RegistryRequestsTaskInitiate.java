package cs455.overlay.wireformats;

import java.io.*;

public class RegistryRequestsTaskInitiate extends Event {
    
    private int numPackets;
    public int getNumPackets() { return numPackets; }
    
    public byte getType() { return Protocol.REGISTRY_REQUESTS_TASK_INITIATE; }
    
    public byte[] getBytes() throws IOException {
        
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        
        dout.writeByte(getType());
        
        dout.writeInt(numPackets);
        
        dout.flush();
        byte[] marshalledBytes = baOutputStream.toByteArray();
        
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }
    
    public RegistryRequestsTaskInitiate(int numPackets) {
        this.numPackets = numPackets;
    }
    
    public RegistryRequestsTaskInitiate(ByteArrayInputStream baInputStream, DataInputStream din)
            throws IOException {
        
        numPackets = din.readInt();
        
        baInputStream.close();
        din.close();
    }
    
}

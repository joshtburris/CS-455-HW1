package cs455.overlay.wireformats;

import java.io.*;

public class RegistryRequestsTrafficSummary extends Event {
    
    public byte getType() {
        return Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;
    }
    
    public byte[] getBytes() throws IOException {
        
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
    
        dout.writeByte(getType());
    
        dout.flush();
        byte[] marshalledBytes = baOutputStream.toByteArray();
    
        baOutputStream.close();
        dout.close();
    
        return marshalledBytes;
    }
}

package cs455.overlay.wireformats;

import java.io.*;

public class EventFactory {
    
    public static Event getEvent(byte[] marshalledBytes) {
    
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
    
        try {
            
            byte type = din.readByte();
            
            switch (type) {
                case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                    return new OverlayNodeSendsRegistration(baInputStream, din);
                case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                    return new RegistryReportsRegistrationStatus(baInputStream, din);
            }
            
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        
        return null;
    }
    
}

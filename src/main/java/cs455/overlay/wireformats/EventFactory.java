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
                case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                    return new OverlayNodeSendsDeregistration(baInputStream, din);
                case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                    return new RegistryReportsDeregistrationStatus(baInputStream, din);
                case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                    return new RegistrySendsNodeManifest(baInputStream, din);
                case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                    return new NodeReportsOverlaySetupStatus(baInputStream, din);
                case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                    return new RegistryRequestsTaskInitiate(baInputStream, din);
                case Protocol.OVERLAY_NODE_SENDS_DATA:
                    return new OverlayNodeSendsData(baInputStream, din);
                case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                    return new OverlayNodeReportsTaskFinished(baInputStream, din);
                case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                    return new RegistryRequestsTrafficSummary(baInputStream, din);
                case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                    return new OverlayNodeReportsTrafficSummary(baInputStream, din);
            }
            
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        
        return null;
    }
    
}

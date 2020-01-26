package cs455.overlay.wireformats;

public class Protocol {
    
    public static final byte
        OVERLAY_NODE_SENDS_REGISTRATION = 2,
        REGISTRY_REPORTS_REGISTRATION_STATUS = 3,
    
        OVERLAY_NODE_SENDS_DEREGISTRATION = 4,
        REGISTRY_REPORTS_DEREGISTRATION_STATUS = 5,
    
        REGISTRY_SENDS_NODE_MANIFEST = 6,
        NODE_REPORTS_OVERLAY_SETUP_STATUS = 7,
    
        REGISTRY_REQUESTS_TASK_INITIATE = 8,
        OVERLAY_NODE_SENDS_DATA = 9,
        OVERLAY_NODE_REPORTS_TASK_FINISHED = 10,
    
        REGISTRY_REQUESTS_TRAFFIC_SUMMARY = 11,
        OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY = 12;

}

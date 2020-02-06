package cs455.overlay.wireformats;

import cs455.overlay.routing.*;

import java.io.*;
import java.util.ArrayList;

public class RegistrySendsNodeManifest {

    private RoutingTable routingTable;
    public RoutingTable getRoutingTable() {
        return routingTable;
    }
    
    public byte getType() { return Protocol.REGISTRY_SENDS_NODE_MANIFEST; }
    
    public byte[] getBytes() throws IOException {
        
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
    
        dout.writeByte(getType());
    
        ArrayList<RoutingEntry> entries = routingTable.getRoutingEntries();
        dout.writeByte(entries.size());
        
        for (RoutingEntry entry : entries) {
            dout.writeInt(entry.getNodeId());
            
            byte[] ip = entry.getIpAddress();
            dout.writeByte(ip.length);
            dout.write(ip);
            
            dout.writeInt(entry.getPortnum());
        }
    
        dout.flush();
        byte[] marshalledBytes = baOutputStream.toByteArray();
    
        baOutputStream.close();
        dout.close();
    
        return marshalledBytes;
    }
    
    public RegistrySendsNodeManifest(RoutingTable routingTable) {
        this.routingTable = routingTable;
    }

}

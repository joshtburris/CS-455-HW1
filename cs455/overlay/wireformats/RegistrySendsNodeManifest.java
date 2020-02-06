package cs455.overlay.wireformats;

import cs455.overlay.routing.*;

import java.io.*;
import java.util.ArrayList;

public class RegistrySendsNodeManifest {
    
    private ArrayList<RoutingEntry> entries;
    public ArrayList<RoutingEntry> getEntries() { return entries; }
    
    private int[] allNodeIds;
    public int[] getAllNodeIds() { return allNodeIds; }
    
    public byte getType() { return Protocol.REGISTRY_SENDS_NODE_MANIFEST; }
    
    public byte[] getBytes() throws IOException {
        
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
    
        dout.writeByte(getType());
    
        dout.writeByte(entries.size());
        
        for (RoutingEntry entry : entries) {
            dout.writeInt(entry.getNodeId());
            
            byte[] ip = entry.getIpAddress();
            dout.writeByte(ip.length);
            dout.write(ip);
            
            dout.writeInt(entry.getPortnum());
        }
        
        dout.writeByte(allNodeIds.length);
        for (int i : allNodeIds)
            dout.writeInt(i);
    
        dout.flush();
        byte[] marshalledBytes = baOutputStream.toByteArray();
    
        baOutputStream.close();
        dout.close();
    
        return marshalledBytes;
    }
    
    public RegistrySendsNodeManifest(ArrayList<RoutingEntry> entries, int[] allNodeIds) {
        this.entries = entries;
        this.allNodeIds = allNodeIds;
    }
    
    public RegistrySendsNodeManifest(ByteArrayInputStream baInputStream, DataInputStream din) throws IOException {
        
        byte len = din.readByte();
        
        entries = new ArrayList<>(len);
        for (int i = 0; i < len; ++i) {
            int nodeId = din.readInt();
            
            byte[] ip = new byte[din.readByte()];
            din.readFully(ip);
            
            int portnum = din.readInt();
            
            RoutingEntry e = new RoutingEntry(ip, portnum, nodeId);
            entries.add(e);
        }
        
        len = din.readByte();
        allNodeIds = new int[len];
        for (int i = 0; i < len; ++i)
            allNodeIds[i] = din.readInt();
        
        baInputStream.close();
        din.close();
    }
    
}

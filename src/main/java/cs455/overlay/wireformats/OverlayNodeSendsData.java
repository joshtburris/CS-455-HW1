package cs455.overlay.wireformats;

import java.io.*;
import java.util.ArrayList;

public class OverlayNodeSendsData extends Event {
    
    private int destId;
    public int getDestId() {
        return destId;
    }
    
    private int srcId;
    public int getSrcId() {
        return srcId;
    }
    
    private int payload;
    public int getPayload() {
        return payload;
    }
    
    private ArrayList<Integer> disTrace;
    public ArrayList<Integer> getDisTrace() {
        return disTrace;
    }
    
    public void addNodeToDisTrace(int nodeId) {
        disTrace.add(nodeId);
    }
    
    public byte getType() { return Protocol.OVERLAY_NODE_SENDS_DATA; }
    
    public byte[] getBytes() throws IOException {
        
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        
        dout.writeByte(getType());
        
        dout.writeInt(destId);
        dout.writeInt(srcId);
        dout.writeInt(payload);
        
        dout.writeInt(disTrace.size());
        for (int i : disTrace)
            dout.writeInt(i);
        
        dout.flush();
        byte[] marshalledBytes = baOutputStream.toByteArray();
        
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }
    
    public OverlayNodeSendsData(int destId, int srcId, int payload) {
        this.destId = destId;
        this.srcId = srcId;
        this.payload = payload;
        disTrace = new ArrayList<>();
    }
    
    public OverlayNodeSendsData(ByteArrayInputStream baInputStream, DataInputStream din)
            throws IOException {
        
        destId = din.readInt();
        srcId = din.readInt();
        payload = din.readInt();
        
        int len = din.readInt();
        disTrace = new ArrayList<>(len);
        for (int i = 0; i < len; ++i) {
            disTrace.add(din.readInt());
        }
        
        baInputStream.close();
        din.close();
    }

}

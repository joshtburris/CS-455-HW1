package cs455.overlay.routing;

import java.util.*;

public class RoutingTable {

    private Object lock;
    private TreeMap<Integer, RoutingEntry> entries;
    
    
    public RoutingTable() {
        lock = new Object();
        entries = new TreeMap<>();
    }
    
    public void addEntry(RoutingEntry entry, int id) {
        synchronized (lock) {
            entries.put(id, entry);
        }
    }
    
    public void listMessagingNodes() {
        synchronized (lock) {
        
            for (RoutingEntry entry : entries.values()) {
                System.out.println(entry.getHostname() +"\t"+ entry.getPortNum() +"\t"+ entry.getNodeId());
            }
        
        }
    }

}

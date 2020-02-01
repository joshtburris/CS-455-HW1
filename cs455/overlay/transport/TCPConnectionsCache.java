package cs455.overlay.transport;

import java.util.TreeMap;

public class TCPConnectionsCache {
    
    private TreeMap<String, TCPConnection> map;
    
    public TCPConnectionsCache() {
        map = new TreeMap<>();
    }
    
    public void add(String key, TCPConnection val) {
    
    }
    
    public TCPConnection get(String key) {
        return map.get(key);
    }
    
}

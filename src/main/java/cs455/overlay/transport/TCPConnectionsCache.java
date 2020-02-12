package cs455.overlay.transport;

import cs455.overlay.util.IpAddressParser;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

public class TCPConnectionsCache {
    
    public ConcurrentHashMap<String, TCPConnection> map;
    
    public TCPConnectionsCache() {
        map = new ConcurrentHashMap<>();
    }
    
    public void add(String socketAddress, TCPConnection con) {
        map.put(socketAddress, con);
    }
    
    public void add(byte[] ipAddress, int portnum, TCPConnection con) {
        map.put(IpAddressParser.parseByteArray(ipAddress) + ":" + portnum, con);
    }
    
    public void remove(String socketAddress) {
        map.remove(socketAddress);
    }
    
    public void remove(byte[] ipAddress, int portnum) {
        map.remove(IpAddressParser.parseByteArray(ipAddress) + ":" + portnum);
    }
    
    public TCPConnection get(String socketAddress) {
        return map.get(socketAddress);
    }
    
    public TCPConnection get(byte[] ipAddress, int portnum) {
        return map.get(IpAddressParser.parseByteArray(ipAddress) +":"+ portnum);
    }
    
    public Set<Entry<String, TCPConnection>> getEntries() { return map.entrySet(); }
    
    public Collection<String> getKeys() { return Collections.list(map.keys()); }
    
    public Collection<TCPConnection> getValues() { return map.values(); }
    
    public void closeAll() {
        synchronized (map) {
            for (TCPConnection con : map.values()) {
                try {
                    con.close();
                } catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                }
            }
        }
    }
    
}

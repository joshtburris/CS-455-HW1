package cs455.overlay.transport;

import cs455.overlay.util.IpAddressParser;

import java.util.TreeMap;

public class TCPConnectionsCache {
    
    private TreeMap<String, TCPConnection> map;
    
    public TCPConnectionsCache() {
        map = new TreeMap<>();
    }
    
    public void add(String socketAddress, TCPConnection con) {
        synchronized (map) {
            map.put(socketAddress, con);
        }
    }
    
    public void add(byte[] ipAddress, int portnum, TCPConnection con) {
        synchronized (map) {
            map.put(IpAddressParser.parseByteArray(ipAddress) + ":" + portnum, con);
        }
    }
    
    public TCPConnection get(String socketAddress) {
        TCPConnection con;
        synchronized (map) {
            con = map.get(socketAddress);
        }
        return con;
    }
    
    public TCPConnection get(byte[] ipAddress, int portnum) {
        TCPConnection con;
        synchronized (map) {
            con = map.get(IpAddressParser.parseByteArray(ipAddress) +":"+ portnum);
        }
        return con;
    }
    
}

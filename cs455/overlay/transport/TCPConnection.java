package cs455.overlay.transport;

import cs455.overlay.util.IpAddressParser;
import cs455.overlay.wireformats.*;
import cs455.overlay.node.*;

import java.io.*;
import java.net.*;

public class TCPConnection {
    
    private Node node;
    private Socket socket;
    private TCPSender sender;
    private TCPReceiverThread receiver;
    private Thread receiverThread;
    
    public TCPConnection(Node node, Socket socket) throws IOException {
        this.node = node;
        this.socket = socket;
        localIpAddress = socket.getLocalAddress().getAddress();
        // We have to use the byte array to get the IP string or else they won't match. i.e. 129 = -127.
        localIpAddressString = IpAddressParser.parseByteArray(localIpAddress);
        localPortnum = socket.getLocalPort();
        
        String[] remoteSocketAddress = socket.getRemoteSocketAddress().toString().split(":");
        remotePortnum = Integer.parseInt(remoteSocketAddress[1]);

        try {
            remoteIpAddress = Inet4Address.getByName(remoteSocketAddress[0].split("/")[1]).getAddress();
            remoteIpAddressString = IpAddressParser.parseByteArray(remoteIpAddress);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        
        sender = new TCPSender(socket);
        receiver = new TCPReceiverThread(socket);
        receiverThread = new Thread(receiver);
        receiverThread.start();
    }
    
    public void sendData(byte[] data) throws IOException { sender.sendData(data); }
    
    private byte[] localIpAddress;
    private String localIpAddressString;
    private int localPortnum;
    private byte[] remoteIpAddress;
    private String remoteIpAddressString;
    private int remotePortnum;
    
    public byte[] getLocalIpAddress() { return localIpAddress; }
    public String getLocalIpAddressString() { return localIpAddressString; }
    public int getLocalPortnum() { return localPortnum; }
    public byte[] getRemoteIpAddress() { return remoteIpAddress; }
    public String getRemoteIpAddressString() { return remoteIpAddressString; }
    public int getRemotePortnum() { return remotePortnum; }
    public String getLocalSocketAddress() { return localIpAddressString +":"+ localPortnum; }
    public String getRemoteSocketAddress() { return remoteIpAddressString +":"+ remotePortnum; }
    
    public boolean isClosed() { return socket.isClosed(); }
    public void close() throws IOException { socket.close(); }
    
    private class TCPSender {
        
        private Socket socket;
        private DataOutputStream dout;
        
        public TCPSender(Socket socket) throws IOException {
            this.socket = socket;
            dout = new DataOutputStream(socket.getOutputStream());
        }
        
        public void sendData(byte[] data) throws IOException {
            int len = data.length;
            dout.writeInt(len);
            dout.write(data, 0, len);
            dout.flush();
        }
        
    }
    
    private class TCPReceiverThread implements Runnable {
        
        private Socket socket;
        private DataInputStream din;
        
        public TCPReceiverThread(Socket socket) throws IOException {
            this.socket = socket;
            din = new DataInputStream(socket.getInputStream());
        }
        
        public void run() {
            
            int len;
            while (socket != null && !socket.isClosed()) {
                try {
                    
                    // Read in all of the data from stream
                    len = din.readInt();
                    byte[] data = new byte[len];
                    din.readFully(data, 0, len);
                    
                    // Create an event from the EventFactory class using the data we just got from the stream
                    // and call the onEvent function for the node
                    Event event = EventFactory.getEvent(data);
                    node.onEvent(event);
                    
                } catch (SocketException se) {
                    System.out.println(se.getMessage());
                    break;
                } catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                    break;
                }
            }
        }
        
    }
    
}

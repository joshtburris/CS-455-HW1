package cs455.overlay.transport;

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
        sender = new TCPSender(socket);
        receiver = new TCPReceiverThread(socket);
        receiverThread = new Thread(receiver);
        receiverThread.start();
    }
    
    public void sendData(byte[] data) throws IOException { sender.sendData(data); }
    
    public byte[] getIpAddress() { return socket.getInetAddress().getAddress(); }
    
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
            while (socket != null) {
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

package cs455.overlay.node;

import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.*;

public class MessagingNode implements Node {
    
    public void onEvent(Event event) {
    
    }
    
    public static void main(String[] args) {
    
        // Take in the registry host and port number from the command line
        String hostname = args[0];
        int portnum = Integer.parseInt(args[1]);
        
        // Create a server
        
        // Register this node with the registry
        try {
            
            Socket socket = new Socket(hostname, portnum);
            
    
            OverlayNodeSendsRegistration regi = new OverlayNodeSendsRegistration(
                    Inet4Address.getLocalHost().getAddress(), socket.getLocalPort());
    
            System.out.println(Inet4Address.getLocalHost().getAddress());
            System.out.println(socket.getLocalAddress());
            System.out.println(socket.getLocalSocketAddress());
            
        } catch (UnknownHostException uhe) {
            System.out.println(uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    
    }
    
    //public void start() {
        //registry.sendByte(MessageType.OVERLAY_NODE_SENDS_REGISTRATION);
        //registry.sendByte(/*Length of IP Address*/);
        //registry.sendByteList(InetAddress.getAddress());
        //registry.sendInt(/*Port number*/);
    //}
    
}

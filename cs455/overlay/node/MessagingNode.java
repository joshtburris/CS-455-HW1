package cs455.overlay.node;

import cs455.overlay.wireformats.Event;

import java.net.InetAddress;

public class MessagingNode implements Node {
    
    public void onEvent(Event event) {
    
    }
    
    public static void main(String[] args) {
        
        
        System.out.println("Hello from the Messaging Node!");
        
    }
    
    //public void start() {
        //registry.sendByte(MessageType.OVERLAY_NODE_SENDS_REGISTRATION);
        //registry.sendByte(/*Length of IP Address*/);
        //registry.sendByteList(InetAddress.getAddress());
        //registry.sendInt(/*Port number*/);
    //}
    
}

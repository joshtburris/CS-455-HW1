package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Event;

import java.net.Socket;

public interface Node {

    void onEvent(Socket socket, Event event);
    
    void onConnection(TCPConnection con);

}

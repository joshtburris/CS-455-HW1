package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.*;

public interface Node {

    void onEvent(Event event);
    
    void onConnection(TCPConnection con);

}

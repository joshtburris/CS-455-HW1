package cs455.overlay.wireformats;

import java.io.IOException;

public abstract class Event {
    
    public abstract byte getType();
    
    public abstract byte[] getBytes() throws IOException;
    
}

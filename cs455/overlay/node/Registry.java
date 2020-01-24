package cs455.overlay.node;

public class Registry {

    // Check if node has been previously registered
    // Ensure the IP Address matches the address where the request originated.
    
    // If no errors:
    //MessagingNode.sendByte(MessageType.REGISTRY_REPORTS_REGISTRATION_STATUS);
    //MessagingNode.sendInt(/*Length of IP Address*/);
    //MessagingNode.sendByteList(INetAddress.getAddress());
    //MessagingNode.sendInt(/*Port number*/);
    
    private byte uniqueID = 0;
    private byte getUniqueID() { return uniqueID++; }
    
    public static void main(String[] args) {
        Registry reg = new Registry();
        System.out.println(reg.getUniqueID());
        System.out.println(reg.getUniqueID());
        System.out.println("Hello from the Registry!");
    }

}

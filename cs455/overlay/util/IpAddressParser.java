package cs455.overlay.util;

public class IpAddressParser {
    
    public static String parseByteArray(byte[] ip) {
        return ip[0] +"."+ ip[1] +"."+ ip[2] +"."+ ip[3];
    }
    
    public static byte[] parseString(String ip) {
        if (ip.charAt(0) == '/')
            ip = ip.substring(1);
        String[] stringBytes = ip.split("\\.");
        return new byte[] { Byte.parseByte(stringBytes[0]), Byte.parseByte(stringBytes[1]),
                Byte.parseByte(stringBytes[2]), Byte.parseByte(stringBytes[3])};
    }
    
}

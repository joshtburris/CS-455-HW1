package cs455.overlay.util;

public class IpAddressParser {
    
    public static String parseByteArray(byte[] ip) {
        return ip[0] +"."+ ip[1] +"."+ ip[2] +"."+ ip[3];
    }
    
    public static byte[] parseString(String ip) {
        if (ip.contains("/"))
            ip = ip.substring(ip.indexOf('/')+1);
        String[] stringBytes = ip.split("\\.");
        int[] barr = new int[] { Integer.parseInt(stringBytes[0]), Integer.parseInt(stringBytes[1]),
                Integer.parseInt(stringBytes[2]), Integer.parseInt(stringBytes[3])};
        return new byte[] { (byte)barr[0], (byte)barr[1], (byte)barr[2], (byte)barr[3]};
    }
    
}

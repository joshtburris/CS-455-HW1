package cs455.overlay.transport;

import java.io.*;
import java.net.*;

public class TCPConnection {
    
    public class TCPSender {
        
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
    
    public class TCPReceiverThread extends Thread {
        
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
                    
                    // TODO:This is where functionality goes
                    len = din.readInt();
                    byte[] data = new byte[len];
                    din.readFully(data, 0, len);
                    
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
package cs455.overlay.util;

public class StatisticsCollectorAndDisplay {
    
    private int totalPacketsSent;
    public synchronized int getTotalPacketsSent() {
        return totalPacketsSent;
    }
    public synchronized void addTotalPacketsSent(int packets) {
        totalPacketsSent += packets;
    }
    
    private int totalPacketsRelayed;
    public synchronized int getTotalPacketsRelayed() {
        return totalPacketsRelayed;
    }
    public synchronized void addTotalPacketsRelayed(int packets) { totalPacketsRelayed += packets; }
    
    private long sumDataSent;
    public synchronized long getSumDataSent() {
        return sumDataSent;
    }
    public synchronized void addDataSent(long data) { sumDataSent += data; }
    
    private int totalPacketsReceived;
    public synchronized int getTotalPacketsReceived() {
        return totalPacketsReceived;
    }
    public synchronized void addTotalPacketsReceived(int packets) { totalPacketsReceived += packets; }
    
    private long sumDataReceived;
    public synchronized long getSumDataReceived() {
        return sumDataReceived;
    }
    public synchronized void addDataReceived(long data) { sumDataReceived += data; }
    
    public StatisticsCollectorAndDisplay() {
        this(0, 0, 0, 0, 0);
    }
    
    public StatisticsCollectorAndDisplay(int totalPacketsSent, int totalPacketsRelayed, long sumDataSent,
            int totalPacketsReceived, long sumDataReceived) {
        this.totalPacketsSent = totalPacketsSent;
        this.totalPacketsRelayed = totalPacketsRelayed;
        this.sumDataSent = sumDataSent;
        this.totalPacketsReceived = totalPacketsReceived;
        this.sumDataReceived = sumDataReceived;
    }
    
    public synchronized void reset() {
        totalPacketsSent = 0;
        totalPacketsRelayed = 0;
        sumDataSent = 0;
        totalPacketsReceived = 0;
        sumDataReceived = 0;
    }
    
    public String toString() {
        return totalPacketsSent +"\t"+ totalPacketsReceived +"\t"+ totalPacketsRelayed +"\t"+ sumDataSent +"\t"
                + sumDataReceived;
    }

}

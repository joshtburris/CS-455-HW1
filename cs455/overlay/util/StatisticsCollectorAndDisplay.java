package cs455.overlay.util;

public class StatisticsCollectorAndDisplay {
    
    private volatile int totalPacketsSent;
    public int getTotalPacketsSent() {
        return totalPacketsSent;
    }
    public void addTotalPacketsSent(int packets) { totalPacketsSent += packets; }
    
    private volatile int totalPacketsRelayed;
    public int getTotalPacketsRelayed() {
        return totalPacketsRelayed;
    }
    public void addTotalPacketsRelayed(int packets) { totalPacketsRelayed += packets; }
    
    private volatile long sumDataSent;
    public long getSumDataSent() {
        return sumDataSent;
    }
    public void addDataSent(long data) { sumDataSent += data; }
    
    private volatile int totalPacketsReceived;
    public int getTotalPacketsReceived() {
        return totalPacketsReceived;
    }
    public void addTotalPacketsReceived(int packets) { totalPacketsReceived += packets; }
    
    private volatile long sumDataReceived;
    public long getSumDataReceived() {
        return sumDataReceived;
    }
    public void addDataReceived(long data) { sumDataReceived += data; }
    
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
    
    public void reset() {
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

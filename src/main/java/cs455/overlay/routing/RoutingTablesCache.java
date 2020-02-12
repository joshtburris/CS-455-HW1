package cs455.overlay.routing;

import cs455.overlay.util.StatisticsCollectorAndDisplay;

import java.util.*;
import java.util.Map.Entry;

/**
 * This class is designed to be a thread safe implementation of basic TreeMap functions for a collection of
 * RoutingTable's. It also contains additional functionality to keep track of node states such as which nodes have been
 * fully setup, which nodes have finished sending their rounds of packets, and which nodes have provided a summary of
 * their package statistics.
 */
public class RoutingTablesCache {
    
    private TreeMap<Integer, RoutingTable> map;
    private TreeMap<Integer, Boolean> setupNodes;
    private TreeMap<Integer, Boolean> finishedNodes;
    private TreeMap<Integer, StatisticsCollectorAndDisplay> summaryNodes;
    
    public RoutingTablesCache() {
        map = new TreeMap<>();
        setupNodes = new TreeMap<>();
        finishedNodes = new TreeMap<>();
        summaryNodes = new TreeMap<>();
    }
    
    public int add(Integer nodeId, RoutingTable table) {
        int size;
        synchronized (map) {
            map.put(nodeId, table);
            size = map.size();
        }
        return size;
    }
    
    public int remove(Integer nodeId) {
        int size;
        synchronized (map) {
            map.remove(nodeId);
            size = map.size();
        }
        return size;
    }
    
    public RoutingTable get(Integer nodeId) {
        RoutingTable table;
        synchronized (map) {
            table = map.get(nodeId);
        }
        return table;
    }
    
    public int size() {
        int size;
        synchronized (map) {
            size = map.size();
        }
        return size;
    }
    
    public boolean containsKey(Integer nodeId) {
        boolean contains;
        synchronized (map) {
            contains = map.containsKey(nodeId);
        }
        return contains;
    }
    
    public Set<Entry<Integer, RoutingTable>> getEntries() {
        Set<Entry<Integer, RoutingTable>> set;
        synchronized (map) {
            set = new TreeMap<>(map).entrySet();
        }
        return set;
    }
    
    public Set<Integer> getKeys() {
        Set<Integer> keys;
        synchronized (map) {
            keys = new TreeMap<>(map).keySet();
        }
        return keys;
    }
    
    public Collection<RoutingTable> getValues() {
        Collection<RoutingTable> values;
        synchronized (map) {
            values = new TreeMap<>(map).values();
        }
        return values;
    }
    
    public int nodeIsSetup(int nodeId) {
        int size;
        synchronized (map) {
            setupNodes.put(nodeId, true);
            size = setupNodes.size();
        }
        return size;
    }
    
    public int nodeIsFinished(int nodeId) {
        int size;
        synchronized (map) {
            finishedNodes.put(nodeId, true);
            size = finishedNodes.size();
        }
        return size;
    }
    
    public int nodeHasSummary(int nodeId, StatisticsCollectorAndDisplay stats) {
        int size;
        synchronized (map) {
            summaryNodes.put(nodeId, stats);
            size = summaryNodes.size();
        }
        return size;
    }
    
    public void resetSetupNodes() {
        synchronized (setupNodes) {
            setupNodes.clear();
        }
    }
    
    public void resetFinishedNodes() {
        synchronized (finishedNodes) {
            finishedNodes.clear();
        }
    }
    
    public void resetSummaryNodes() {
        synchronized (summaryNodes) {
            summaryNodes.clear();
        }
    }
    
    public Set<Entry<Integer, StatisticsCollectorAndDisplay>> getSummaries() {
        Set<Entry<Integer, StatisticsCollectorAndDisplay>> set;
        synchronized (map) {
            set = new TreeMap<>(summaryNodes).entrySet();
        }
        return set;
    }
    
}

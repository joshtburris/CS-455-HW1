package cs455.overlay.routing;

import cs455.overlay.util.StatisticsCollectorAndDisplay;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class RoutingTablesCache {
    
    private ConcurrentHashMap<Integer, RoutingTable> map;
    private ConcurrentHashMap<Integer, Boolean> setupNodes;
    private ConcurrentHashMap<Integer, Boolean> finishedNodes;
    private ConcurrentHashMap<Integer, StatisticsCollectorAndDisplay> summaryNodes;
    
    public RoutingTablesCache() {
        map = new ConcurrentHashMap<>();
        setupNodes = new ConcurrentHashMap<>();
        finishedNodes = new ConcurrentHashMap<>();
        summaryNodes = new ConcurrentHashMap<>();
    }
    
    public void add(Integer nodeId, RoutingTable table) {
        map.put(nodeId, table);
    }
    
    public void remove(Integer nodeId) {
        map.remove(nodeId);
    }
    
    public RoutingTable get(Integer nodeId) {
        return map.get(nodeId);
    }
    
    public int size() {
        return map.size();
    }
    
    public boolean containsKey(Integer nodeId) {
        return map.containsKey(nodeId);
    }
    
    public Set<Entry<Integer, RoutingTable>> getEntries() {
        return map.entrySet();
    }
    
    public Collection<Integer> getKeys() {
        return Collections.list(map.keys());
    }
    
    public Collection<RoutingTable> getValues() {
        return map.values();
    }
    
    public void nodeIsSetup(int nodeId) { setupNodes.put(nodeId, true); }
    
    public void nodeIsFinished(int nodeId) { finishedNodes.put(nodeId, true); }
    
    public void nodeHasSummary(int nodeId, StatisticsCollectorAndDisplay stats) { summaryNodes.put(nodeId, stats); }
    
    public boolean areAllNodesSetup() { return setupNodes.size() == map.size(); }
    
    public boolean areAllNodesFinished() { return finishedNodes.size() == map.size(); }
    
    public boolean allNodesHaveSummaries() { return summaryNodes.size() == map.size(); }
    
    public Set<Entry<Integer, StatisticsCollectorAndDisplay>> getSummaries() { return summaryNodes.entrySet(); }
    
}

import java.io.Serializable;
import java.util.*;

public class LRUCache<K, V> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    class DLinkedNode implements Serializable {
        private static final long serialVersionUID = 1L;
        K key;
        V value;
        DLinkedNode prev;
        DLinkedNode next;
        
        DLinkedNode() {}
        
        DLinkedNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    protected final int capacity;
    protected final Map<K, DLinkedNode> cache = new HashMap<>();
    protected DLinkedNode head;
    protected DLinkedNode tail;
    protected int size;
    private long totalAccesses;
    private long hits;
    private long misses;
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.head = new DLinkedNode();
        this.tail = new DLinkedNode();
        head.next = tail;
        tail.prev = head;
    }
    
    public V get(K key) {
        totalAccesses++;
        DLinkedNode node = cache.get(key);
        
        if (node == null) {
            misses++;
            return null;
        }
        
        hits++;
        moveToHead(node);
        return node.value;
    }
    
    public void put(K key, V value) {
        DLinkedNode node = cache.get(key);
        
        if (node == null) {
            DLinkedNode newNode = new DLinkedNode(key, value);
            cache.put(key, newNode);
            addToHead(newNode);
            size++;
            
            if (size > capacity) {
                DLinkedNode tail = removeTail();
                cache.remove(tail.key);
                size--;
            }
        } else {
            node.value = value;
            moveToHead(node);
        }
    }
    
    public boolean contains(K key) {
        return cache.containsKey(key);
    }
    
    public boolean remove(K key) {
        DLinkedNode node = cache.get(key);
        if (node == null) {
            return false;
        }
        
        removeNode(node);
        cache.remove(key);
        size--;
        return true;
    }
    
    public void clear() {
        cache.clear();
        head.next = tail;
        tail.prev = head;
        size = 0;
        totalAccesses = 0;
        hits = 0;
        misses = 0;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public Set<K> keySet() {
        return new HashSet<>(cache.keySet());
    }
    
    public List<K> getAccessOrder() {
        List<K> order = new ArrayList<>();
        DLinkedNode current = head.next;
        while (current != tail) {
            order.add(current.key);
            current = current.next;
        }
        return order;
    }
    
    public List<V> getValuesInOrder() {
        List<V> values = new ArrayList<>();
        DLinkedNode current = head.next;
        while (current != tail) {
            values.add(current.value);
            current = current.next;
        }
        return values;
    }
    
    private void addToHead(DLinkedNode node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    
    private void removeNode(DLinkedNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private void moveToHead(DLinkedNode node) {
        removeNode(node);
        addToHead(node);
    }
    
    protected DLinkedNode removeTail() {
        DLinkedNode res = tail.prev;
        removeNode(res);
        return res;
    }
    
    // Statistics and monitoring
    public double getHitRate() {
        return totalAccesses == 0 ? 0.0 : (double) hits / totalAccesses;
    }
    
    public long getTotalAccesses() {
        return totalAccesses;
    }
    
    public long getHits() {
        return hits;
    }
    
    public long getMisses() {
        return misses;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public CacheStatistics getStatistics() {
        return new CacheStatistics(size, capacity, totalAccesses, hits, misses, getHitRate());
    }
    
    public static class CacheStatistics implements Serializable {
        private static final long serialVersionUID = 1L;
        public final int currentSize;
        public final int capacity;
        public final long totalAccesses;
        public final long hits;
        public final long misses;
        public final double hitRate;
        
        public CacheStatistics(int currentSize, int capacity, long totalAccesses, long hits, long misses, double hitRate) {
            this.currentSize = currentSize;
            this.capacity = capacity;
            this.totalAccesses = totalAccesses;
            this.hits = hits;
            this.misses = misses;
            this.hitRate = hitRate;
        }
        
        @Override
        public String toString() {
            return String.format("CacheStats{size=%d, capacity=%d, accesses=%d, hits=%d, misses=%d, hitRate=%.2f%%}",
                    currentSize, capacity, totalAccesses, hits, misses, hitRate * 100);
        }
    }
    
    // Specialized cache types
    public static class CommitCache extends LRUCache<String, Commit> {
        private static final long serialVersionUID = 1L;
        
        public CommitCache(int capacity) {
            super(capacity);
        }
        
        public List<Commit> getRecentCommits(int limit) {
            List<Commit> recent = new ArrayList<>();
            DLinkedNode current = this.head.next;
            int count = 0;
            
            while (current != this.tail && count < limit) {
                recent.add((Commit) current.value);
                current = current.next;
                count++;
            }
            
            return recent;
        }
        
        public void cacheCommitChain(Commit headCommit) {
            Commit current = headCommit;
            int count = 0;
            
            while (current != null && count < this.capacity) {
                put(current.commitId, current);
                current = current.parent;
                count++;
            }
        }
    }
    
    public static class FileContentCache extends LRUCache<String, String> {
        private static final long serialVersionUID = 1L;
        private final long maxMemoryBytes;
        private long currentMemoryUsage;
        
        public FileContentCache(int capacity, long maxMemoryBytes) {
            super(capacity);
            this.maxMemoryBytes = maxMemoryBytes;
            this.currentMemoryUsage = 0;
        }
        
        @Override
        public void put(String key, String value) {
            long valueSize = value.getBytes().length;
            
            // Remove old value if exists
            String oldValue = get(key);
            if (oldValue != null) {
                currentMemoryUsage -= oldValue.getBytes().length;
            }
            
            // Check memory constraint
            while (currentMemoryUsage + valueSize > maxMemoryBytes && this.size > 0) {
                DLinkedNode tailNode = super.removeTail();
                String removedValue = tailNode.value;
                currentMemoryUsage -= removedValue.getBytes().length;
                this.cache.remove(tailNode.key);
                this.size--;
            }
            
            currentMemoryUsage += valueSize;
            super.put(key, value);
        }
        
        @Override
        public boolean remove(String key) {
            String value = get(key);
            if (value != null) {
                currentMemoryUsage -= value.getBytes().length;
            }
            return super.remove(key);
        }
        
        @Override
        public void clear() {
            super.clear();
            currentMemoryUsage = 0;
        }
        
        public long getMemoryUsage() {
            return currentMemoryUsage;
        }
        
        public long getMaxMemoryBytes() {
            return maxMemoryBytes;
        }
        
        public double getMemoryUtilization() {
            return (double) currentMemoryUsage / maxMemoryBytes;
        }
    }
}

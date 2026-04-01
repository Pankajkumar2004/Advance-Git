import java.io.Serializable;
import java.util.*;

public class FileContentHashTable implements Serializable {
    private static final long serialVersionUID = 1L;
    
    class HashNode<K, V> implements Serializable {
        private static final long serialVersionUID = 1L;
        K key;
        V value;
        HashNode<K, V> next;
        int hash;
        
        HashNode(K key, V value, int hash) {
            this.key = key;
            this.value = value;
            this.hash = hash;
        }
    }
    
    private HashNode<String, String>[] table;
    private int size;
    private int capacity;
    private final double loadFactor;
    private int threshold;
    
    @SuppressWarnings("unchecked")
    public FileContentHashTable(int initialCapacity, double loadFactor) {
        this.capacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.threshold = (int)(capacity * loadFactor);
        this.table = new HashNode[capacity];
    }
    
    public FileContentHashTable() {
        this(16, 0.75);
    }
    
    private int hash(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }
    
    public void put(String fileName, String content) {
        if (fileName == null || content == null) return;
        
        if (size >= threshold) {
            resize();
        }
        
        int hashValue = hash(fileName);
        int index = hashValue % capacity;
        
        HashNode<String, String> newNode = new HashNode<>(fileName, content, hashValue);
        
        if (table[index] == null) {
            table[index] = newNode;
        } else {
            HashNode<String, String> current = table[index];
            while (current.next != null) {
                if (current.key.equals(fileName)) {
                    current.value = content; // Update existing
                    return;
                }
                current = current.next;
            }
            
            if (current.key.equals(fileName)) {
                current.value = content; // Update existing
            } else {
                current.next = newNode; // Add to chain
            }
        }
        
        size++;
    }
    
    public String get(String fileName) {
        if (fileName == null) return null;
        
        int index = hash(fileName) % capacity;
        HashNode<String, String> current = table[index];
        
        while (current != null) {
            if (current.key.equals(fileName)) {
                return current.value;
            }
            current = current.next;
        }
        
        return null;
    }
    
    public boolean contains(String fileName) {
        return get(fileName) != null;
    }
    
    public boolean remove(String fileName) {
        if (fileName == null) return false;
        
        int index = hash(fileName) % capacity;
        HashNode<String, String> current = table[index];
        HashNode<String, String> prev = null;
        
        while (current != null) {
            if (current.key.equals(fileName)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        
        return false;
    }
    
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = capacity * 2;
        HashNode<String, String>[] newTable = new HashNode[newCapacity];
        
        for (HashNode<String, String> node : table) {
            while (node != null) {
                HashNode<String, String> next = node.next;
                int newIndex = Math.abs(node.hash) % newCapacity;
                
                node.next = newTable[newIndex];
                newTable[newIndex] = node;
                
                node = next;
            }
        }
        
        table = newTable;
        capacity = newCapacity;
        threshold = (int)(capacity * loadFactor);
    }
    
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        for (HashNode<String, String> node : table) {
            while (node != null) {
                keys.add(node.key);
                node = node.next;
            }
        }
        return keys;
    }
    
    public Collection<String> values() {
        List<String> values = new ArrayList<>();
        for (HashNode<String, String> node : table) {
            while (node != null) {
                values.add(node.value);
                node = node.next;
            }
        }
        return values;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }
    
    // Advanced methods for file content analysis
    public List<String> findFilesByContent(String contentSnippet) {
        List<String> matchingFiles = new ArrayList<>();
        String lowerSnippet = contentSnippet.toLowerCase();
        
        for (HashNode<String, String> node : table) {
            while (node != null) {
                if (node.value.toLowerCase().contains(lowerSnippet)) {
                    matchingFiles.add(node.key);
                }
                node = node.next;
            }
        }
        
        return matchingFiles;
    }
    
    public Map<String, Integer> getContentStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalFiles", size);
        stats.put("totalSize", getTotalContentSize());
        stats.put("averageSize", size > 0 ? getTotalContentSize() / size : 0);
        stats.put("maxChainLength", getMaxChainLength());
        return stats;
    }
    
    private int getTotalContentSize() {
        int totalSize = 0;
        for (HashNode<String, String> node : table) {
            while (node != null) {
                totalSize += node.value.length();
                node = node.next;
            }
        }
        return totalSize;
    }
    
    private int getMaxChainLength() {
        int maxLength = 0;
        for (HashNode<String, String> node : table) {
            int currentLength = 0;
            HashNode<String, String> current = node;
            while (current != null) {
                currentLength++;
                current = current.next;
            }
            maxLength = Math.max(maxLength, currentLength);
        }
        return maxLength;
    }
    
    public List<String> getDuplicateFiles() {
        Map<String, List<String>> contentToFiles = new HashMap<>();
        
        for (HashNode<String, String> node : table) {
            while (node != null) {
                contentToFiles.computeIfAbsent(node.value, k -> new ArrayList<>()).add(node.key);
                node = node.next;
            }
        }
        
        List<String> duplicates = new ArrayList<>();
        for (List<String> files : contentToFiles.values()) {
            if (files.size() > 1) {
                duplicates.addAll(files);
            }
        }
        
        return duplicates;
    }
    
    public double getLoadFactor() {
        return (double) size / capacity;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FileContentHashTable{size=").append(size);
        sb.append(", capacity=").append(capacity);
        sb.append(", loadFactor=").append(String.format("%.2f", getLoadFactor()));
        sb.append("}");
        return sb.toString();
    }
}

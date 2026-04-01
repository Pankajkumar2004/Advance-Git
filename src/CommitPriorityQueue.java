import java.io.Serializable;
import java.util.*;

public class CommitPriorityQueue implements Serializable {
    private static final long serialVersionUID = 1L;
    
    class PriorityCommit implements Serializable {
        private static final long serialVersionUID = 1L;
        Commit commit;
        long timestamp;
        int priority;
        
        PriorityCommit(Commit commit, long timestamp, int priority) {
            this.commit = commit;
            this.timestamp = timestamp;
            this.priority = priority;
        }
    }
    
    private List<PriorityCommit> heap = new ArrayList<>();
    private Map<String, Integer> commitIndex = new HashMap<>(); // For O(1) lookups
    private int maxSize;
    
    public CommitPriorityQueue(int maxSize) {
        this.maxSize = maxSize;
    }
    
    public void addCommit(Commit commit, int priority) {
        PriorityCommit pc = new PriorityCommit(commit, commit.timestamp, priority);
        
        if (commitIndex.containsKey(commit.commitId)) {
            // Update existing commit
            updateCommit(commit.commitId, pc);
        } else {
            // Add new commit
            if (heap.size() >= maxSize) {
                // Remove lowest priority if at max capacity
                removeLowestPriority();
            }
            addToHeap(pc);
        }
    }
    
    private void addToHeap(PriorityCommit pc) {
        heap.add(pc);
        commitIndex.put(pc.commit.commitId, heap.size() - 1);
        heapifyUp(heap.size() - 1);
    }
    
    private void updateCommit(String commitId, PriorityCommit newPc) {
        int index = commitIndex.get(commitId);
        heap.set(index, newPc);
        heapifyUp(index);
        heapifyDown(index);
    }
    
    private void removeLowestPriority() {
        if (heap.isEmpty()) return;
        
        PriorityCommit removed = heap.get(0);
        commitIndex.remove(removed.commit.commitId);
        
        PriorityCommit last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            commitIndex.put(last.commit.commitId, 0);
            heapifyDown(0);
        }
    }
    
    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (compare(heap.get(index), heap.get(parentIndex)) >= 0) break;
            
            swap(index, parentIndex);
            index = parentIndex;
        }
    }
    
    private void heapifyDown(int index) {
        int size = heap.size();
        while (true) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallest = index;
            
            if (leftChild < size && compare(heap.get(leftChild), heap.get(smallest)) < 0) {
                smallest = leftChild;
            }
            
            if (rightChild < size && compare(heap.get(rightChild), heap.get(smallest)) < 0) {
                smallest = rightChild;
            }
            
            if (smallest == index) break;
            
            swap(index, smallest);
            index = smallest;
        }
    }
    
    private void swap(int i, int j) {
        PriorityCommit temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
        
        commitIndex.put(heap.get(i).commit.commitId, i);
        commitIndex.put(heap.get(j).commit.commitId, j);
    }
    
    private int compare(PriorityCommit a, PriorityCommit b) {
        // Primary: Priority (lower number = higher priority)
        if (a.priority != b.priority) {
            return Integer.compare(a.priority, b.priority);
        }
        // Secondary: Timestamp (more recent = higher priority)
        return Long.compare(b.timestamp, a.timestamp);
    }
    
    public Commit peek() {
        return heap.isEmpty() ? null : heap.get(0).commit;
    }
    
    public Commit poll() {
        if (heap.isEmpty()) return null;
        
        PriorityCommit result = heap.get(0);
        commitIndex.remove(result.commit.commitId);
        
        PriorityCommit last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            commitIndex.put(last.commit.commitId, 0);
            heapifyDown(0);
        }
        
        return result.commit;
    }
    
    public List<Commit> getTopCommits(int n) {
        List<Commit> result = new ArrayList<>();
        List<PriorityCommit> copy = new ArrayList<>(heap);
        Collections.sort(copy, this::compare);
        
        for (int i = 0; i < Math.min(n, copy.size()); i++) {
            result.add(copy.get(i).commit);
        }
        
        return result;
    }
    
    public List<Commit> getRecentCommits(int n) {
        List<PriorityCommit> allCommits = new ArrayList<>(heap);
        allCommits.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
        
        List<Commit> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, allCommits.size()); i++) {
            result.add(allCommits.get(i).commit);
        }
        
        return result;
    }
    
    public boolean contains(String commitId) {
        return commitIndex.containsKey(commitId);
    }
    
    public void updatePriority(String commitId, int newPriority) {
        if (!commitIndex.containsKey(commitId)) return;
        
        int index = commitIndex.get(commitId);
        PriorityCommit pc = heap.get(index);
        PriorityCommit newPc = new PriorityCommit(pc.commit, pc.timestamp, newPriority);
        
        heap.set(index, newPc);
        heapifyUp(index);
        heapifyDown(index);
    }
    
    public int size() {
        return heap.size();
    }
    
    public boolean isEmpty() {
        return heap.isEmpty();
    }
    
    public void clear() {
        heap.clear();
        commitIndex.clear();
    }
    
    // Activity tracking methods
    public Map<String, Integer> getActivitySummary() {
        Map<String, Integer> activity = new HashMap<>();
        
        for (PriorityCommit pc : heap) {
            String date = new Date(pc.timestamp).toString().substring(0, 10);
            activity.put(date, activity.getOrDefault(date, 0) + 1);
        }
        
        return activity;
    }
    
    public List<Commit> getCommitsByPriorityRange(int minPriority, int maxPriority) {
        List<Commit> result = new ArrayList<>();
        
        for (PriorityCommit pc : heap) {
            if (pc.priority >= minPriority && pc.priority <= maxPriority) {
                result.add(pc.commit);
            }
        }
        
        result.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
        return result;
    }
}

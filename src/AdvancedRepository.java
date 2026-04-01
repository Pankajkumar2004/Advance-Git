import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AdvancedRepository implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String GIT_FOLDER = ".mygit";
    private static final String COMMITS_FOLDER = ".mygit/commits";
    private static final String INDEX_FILE = ".mygit/index";
    private static final String REPO_DATA = ".mygit/repo.ser";

    // Advanced DSA components
    private AVLCommitTree commitTree = new AVLCommitTree();
    private TrieSearchEngine searchEngine = new TrieSearchEngine();
    private CommitGraph commitGraph = new CommitGraph();
    private CommitPriorityQueue priorityQueue = new CommitPriorityQueue(100);
    private FileContentHashTable fileStorage = new FileContentHashTable(32, 0.75);
    private LRUCache<String, Commit> commitCache = new LRUCache.CommitCache(50);
    private LRUCache<String, String> fileCache = new LRUCache.FileContentCache(100, 1024 * 1024); // 1MB limit
    private DiffAlgorithm diffAlgorithm = new DiffAlgorithm();

    // Traditional repository data
    private HashMap<String, String> index = new HashMap<>();
    private HashMap<String, Commit> branches = new HashMap<>();
    private String currentBranch = "master";
    private Commit head = null;

    public void init() {
        try {
            Files.createDirectories(Paths.get(COMMITS_FOLDER));
            if (!Files.exists(Paths.get(INDEX_FILE))) {
                Files.createFile(Paths.get(INDEX_FILE));
            }
            loadIndex();
            branches.put(currentBranch, null);
            System.out.println("Initialized advanced MiniGit repository with DSA optimizations!");
            save();
        } catch (IOException e) {
            System.out.println("Repository already initialized.");
        }
    }

    public void addFile(String filename) {
        try {
            String content = FileSnapshot.readFileContent(filename);
            index.put(filename, content);
            
            // Store in advanced file storage
            fileStorage.put(filename, content);
            fileCache.put(filename, content);
            
            // Update search engine
            searchEngine.insertFile(filename);
            
            saveIndex();
            System.out.println("Added " + filename + " to staging area (optimized storage).");
        } catch (IOException e) {
            System.out.println("Error adding file: " + e.getMessage());
        }
    }

    public void commit(String message) {
        if (index.isEmpty()) {
            System.out.println("Nothing to commit.");
            return;
        }
        
        Commit newCommit = new Commit(message, head, index);
        head = newCommit;
        branches.put(currentBranch, head);
        
        // Update all advanced DSA structures
        commitTree.insert(newCommit);
        searchEngine.insertCommit(newCommit.commitId, message);
        commitGraph.addCommit(newCommit.commitId, newCommit, currentBranch);
        priorityQueue.addCommit(newCommit, calculatePriority(newCommit));
        commitCache.put(newCommit.commitId, newCommit);
        
        // Store files in hash table
        for (Map.Entry<String, String> entry : index.entrySet()) {
            fileStorage.put(entry.getKey(), entry.getValue());
        }
        
        System.out.println("Committed as " + newCommit.commitId + " on branch '" + currentBranch + "' (DSA optimized)");
        save();
    }

    private int calculatePriority(Commit commit) {
        // Priority based on message length, file count, and recency
        int priority = commit.message.length() / 10; // Shorter messages = higher priority
        priority += commit.snapshot.size(); // More files = lower priority
        priority += (System.currentTimeMillis() - commit.timestamp) / (1000 * 60 * 60); // Older commits = lower priority
        return priority;
    }

    public void advancedLog() {
        System.out.println("\n=== Advanced Commit Log (DSA Optimized) ===");
        
        // Show recent commits from priority queue
        System.out.println("\nRecent High-Priority Commits:");
        List<Commit> recentCommits = priorityQueue.getRecentCommits(5);
        for (Commit commit : recentCommits) {
            System.out.println("  " + commit.commitId + ": " + commit.message);
        }
        
        // Show commit tree structure
        System.out.println("\nCommit Tree Structure:");
        List<Commit> allCommits = commitTree.getCommitHistory();
        for (int i = 0; i < Math.min(10, allCommits.size()); i++) {
            Commit commit = allCommits.get(i);
            System.out.println("  " + commit.commitId + " [" + commit.timestamp + "]");
        }
        
        // Show graph statistics
        System.out.println("\nGraph Statistics:");
        System.out.println("  Total commits: " + commitTree.getSize());
        System.out.println("  Graph diameter: " + commitGraph.getGraphDiameter());
        System.out.println("  Has cycles: " + commitGraph.hasCycles());
        
        // Show cache statistics
        System.out.println("\nCache Statistics:");
        System.out.println("  " + commitCache.getStatistics());
        System.out.println("  File cache memory: " + ((LRUCache.FileContentCache) fileCache).getMemoryUtilization() * 100 + "%");
        
        // Show file storage statistics
        System.out.println("\nFile Storage Statistics:");
        Map<String, Integer> stats = fileStorage.getContentStatistics();
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    public void search(String query) {
        System.out.println("\n=== Search Results for: '" + query + "' ===");
        
        TrieSearchEngine.SearchResult result = searchEngine.search(query);
        
        if (result.hasResults()) {
            if (!result.exactCommits.isEmpty()) {
                System.out.println("\nExact Commit Matches:");
                for (String commitId : result.exactCommits) {
                    Commit commit = commitCache.get(commitId);
                    if (commit == null) commit = commitTree.find(commitId);
                    if (commit != null) {
                        System.out.println("  " + commitId + ": " + commit.message);
                    }
                }
            }
            
            if (!result.exactFiles.isEmpty()) {
                System.out.println("\nExact File Matches:");
                for (String fileName : result.exactFiles) {
                    System.out.println("  " + fileName);
                }
            }
            
            if (!result.partialCommits.isEmpty()) {
                System.out.println("\nPartial Commit Matches:");
                for (String commitId : result.partialCommits) {
                    Commit commit = commitCache.get(commitId);
                    if (commit == null) commit = commitTree.find(commitId);
                    if (commit != null) {
                        System.out.println("  " + commitId + ": " + commit.message);
                    }
                }
            }
            
            if (!result.partialFiles.isEmpty()) {
                System.out.println("\nPartial File Matches:");
                for (String fileName : result.partialFiles) {
                    System.out.println("  " + fileName);
                }
            }
        } else {
            System.out.println("No results found.");
        }
        
        // Show suggestions
        List<String> suggestions = searchEngine.getSuggestions(query);
        if (!suggestions.isEmpty()) {
            System.out.println("\nSuggestions:");
            for (String suggestion : suggestions.subList(0, Math.min(5, suggestions.size()))) {
                System.out.println("  " + suggestion);
            }
        }
    }

    public void showDiff(String commitId1, String commitId2) {
        System.out.println("\n=== Diff Analysis ===");
        
        Commit commit1 = commitCache.get(commitId1);
        if (commit1 == null) commit1 = commitTree.find(commitId1);
        
        Commit commit2 = commitCache.get(commitId2);
        if (commit2 == null) commit2 = commitTree.find(commitId2);
        
        if (commit1 == null || commit2 == null) {
            System.out.println("One or both commits not found.");
            return;
        }
        
        // Compare files between commits
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(commit1.snapshot.keySet());
        allFiles.addAll(commit2.snapshot.keySet());
        
        for (String fileName : allFiles) {
            String content1 = commit1.snapshot.getOrDefault(fileName, "");
            String content2 = commit2.snapshot.getOrDefault(fileName, "");
            
            if (!content1.equals(content2)) {
                System.out.println("\n--- File: " + fileName + " ---");
                
                DiffAlgorithm.DiffResult diff = diffAlgorithm.computeDiff(content1, content2);
                
                System.out.println("Similarity: " + String.format("%.1f%%", diff.getSimilarityPercentage()));
                System.out.println("Changes: +" + diff.getAddedCount() + " -" + diff.getDeletedCount() + " ~" + diff.getModifiedCount());
                
                // Show some diff lines
                if (diff.getTotalChanges() > 0) {
                    System.out.println("\nSample changes:");
                    int count = 0;
                    for (DiffAlgorithm.DiffLine line : diff.additions) {
                        if (count++ >= 3) break;
                        System.out.println("  " + line);
                    }
                    count = 0;
                    for (DiffAlgorithm.DiffLine line : diff.deletions) {
                        if (count++ >= 3) break;
                        System.out.println("  " + line);
                    }
                }
            }
        }
    }

    public void visualizeGraph() {
        System.out.println("\n=== Commit Graph Visualization ===");
        
        CommitGraph.GraphVisualizationData data = commitGraph.getVisualizationData();
        
        System.out.println("Nodes: " + data.nodes.size());
        System.out.println("Edges: " + data.edges.size());
        
        // Show branch information
        System.out.println("\nBranches:");
        for (String branchName : branches.keySet()) {
            List<String> branchCommits = commitGraph.getCommitsInBranch(branchName);
            System.out.println("  " + branchName + ": " + branchCommits.size() + " commits");
            if (!branchCommits.isEmpty()) {
                System.out.println("    Latest: " + branchCommits.get(0));
            }
        }
        
        // Show merge candidates
        if (branches.size() > 1) {
            System.out.println("\nMerge Analysis:");
            List<String> branchNames = new ArrayList<>(branches.keySet());
            for (int i = 0; i < branchNames.size(); i++) {
                for (int j = i + 1; j < branchNames.size(); j++) {
                    String branch1 = branchNames.get(i);
                    String branch2 = branchNames.get(j);
                    List<String> candidates = commitGraph.getMergeCandidates(branch1, branch2);
                    if (!candidates.isEmpty()) {
                        System.out.println("  " + branch1 + " -> " + branch2 + ": " + candidates.size() + " candidates");
                    }
                }
            }
        }
    }

    public void performanceReport() {
        System.out.println("\n=== Performance Report ===");
        
        // Tree performance
        System.out.println("AVL Tree:");
        System.out.println("  Size: " + commitTree.getSize());
        System.out.println("  Balance: O(log n) operations guaranteed");
        
        // Search performance
        System.out.println("\nTrie Search Engine:");
        System.out.println("  Prefix search: O(m) where m = query length");
        System.out.println("  Insert operations: O(k) where k = word length");
        
        // Graph performance
        System.out.println("\nCommit Graph:");
        System.out.println("  Traversal: O(V + E)");
        System.out.println("  Path finding: O(V + E) with BFS");
        
        // Cache performance
        System.out.println("\nCache Performance:");
        System.out.println("  " + commitCache.getStatistics());
        System.out.println("  File cache: " + String.format("%.1f%% memory used", 
            ((LRUCache.FileContentCache) fileCache).getMemoryUtilization() * 100));
        
        // Hash table performance
        System.out.println("\nHash Table:");
        System.out.println("  " + fileStorage);
        System.out.println("  Load factor: " + String.format("%.2f", fileStorage.getLoadFactor()));
        
        // Priority queue performance
        System.out.println("\nPriority Queue:");
        System.out.println("  Size: " + priorityQueue.size());
        System.out.println("  Operations: O(log n) guaranteed");
    }

    // Traditional methods (simplified)
    public void log() {
        if (head == null) {
            System.out.println("No commits yet.");
            return;
        }
        
        System.out.println("\n=== Basic Commit Log ===");
        Commit current = head;
        int count = 0;
        while (current != null && count < 10) {
            System.out.println("Commit: " + current.commitId);
            System.out.println("Message: " + current.message);
            System.out.println("Files: " + current.snapshot.size());
            System.out.println("-----------------------------");
            current = current.parent;
            count++;
        }
    }

    public void checkout(String nameOrId) {
        // Simplified checkout - would need full implementation
        System.out.println("Checkout feature not fully implemented in advanced version.");
        System.out.println("Use 'search' and 'diff' commands for advanced operations.");
    }

    public void createBranch(String branchName) {
        if (branches.containsKey(branchName)) {
            System.out.println("Branch already exists.");
            return;
        }
        branches.put(branchName, head);
        System.out.println("Created branch '" + branchName + "' at commit " + (head != null ? head.commitId : "null"));
        save();
    }

    private void saveIndex() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(INDEX_FILE));
        for (String filename : index.keySet()) {
            writer.write(filename);
            writer.newLine();
        }
        writer.close();
    }

    private void loadIndex() {
        Path indexPath = Paths.get(INDEX_FILE);
        if (!Files.exists(indexPath)) return;

        try {
            List<String> lines = Files.readAllLines(indexPath);
            for (String filename : lines) {
                try {
                    String content = FileSnapshot.readFileContent(filename);
                    index.put(filename, content);
                    fileStorage.put(filename, content);
                } catch (IOException e) {
                    System.out.println("Warning: Failed to read file from index: " + filename);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading index: " + e.getMessage());
        }
    }

    public void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(REPO_DATA))) {
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Error saving repository: " + e.getMessage());
        }
    }

    public void load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(REPO_DATA))) {
            AdvancedRepository loaded = (AdvancedRepository) in.readObject();
            this.index = loaded.index;
            this.branches = loaded.branches;
            this.currentBranch = loaded.currentBranch;
            this.head = loaded.head;
            this.commitTree = loaded.commitTree;
            this.searchEngine = loaded.searchEngine;
            this.commitGraph = loaded.commitGraph;
            this.priorityQueue = loaded.priorityQueue;
            this.fileStorage = loaded.fileStorage;
            this.commitCache = loaded.commitCache;
            this.fileCache = loaded.fileCache;
        } catch (Exception e) {
            // Ignore if file doesn't exist
        }
    }
}

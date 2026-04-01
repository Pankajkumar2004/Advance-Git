import java.io.Serializable;
import java.util.*;

public class CommitGraph implements Serializable {
    private static final long serialVersionUID = 1L;
    
    class GraphNode implements Serializable {
        private static final long serialVersionUID = 1L;
        String commitId;
        Commit commit;
        Set<String> parents = new HashSet<>();
        Set<String> children = new HashSet<>();
        String branchName;
        int depth;
        
        GraphNode(String commitId, Commit commit) {
            this.commitId = commitId;
            this.commit = commit;
            this.depth = 0;
        }
    }
    
    private Map<String, GraphNode> nodes = new HashMap<>();
    private Map<String, Set<String>> branches = new HashMap<>();
    
    public void addCommit(String commitId, Commit commit, String branchName) {
        GraphNode node = new GraphNode(commitId, commit);
        node.branchName = branchName;
        
        // Add parent relationship
        if (commit.parent != null) {
            node.parents.add(commit.parent.commitId);
            node.depth = getNodeDepth(commit.parent.commitId) + 1;
            
            // Add child relationship to parent
            if (nodes.containsKey(commit.parent.commitId)) {
                nodes.get(commit.parent.commitId).children.add(commitId);
            }
        }
        
        nodes.put(commitId, node);
        
        // Update branch mapping
        branches.computeIfAbsent(branchName, k -> new HashSet<>()).add(commitId);
    }
    
    private int getNodeDepth(String commitId) {
        if (!nodes.containsKey(commitId)) return 0;
        return nodes.get(commitId).depth;
    }
    
    public List<String> getCommitPath(String fromCommitId, String toCommitId) {
        if (!nodes.containsKey(fromCommitId) || !nodes.containsKey(toCommitId)) {
            return new ArrayList<>();
        }
        
        // BFS to find shortest path
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();
        
        queue.add(fromCommitId);
        visited.add(fromCommitId);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            if (current.equals(toCommitId)) {
                // Reconstruct path
                List<String> path = new ArrayList<>();
                String node = toCommitId;
                while (node != null) {
                    path.add(0, node);
                    node = parent.get(node);
                }
                return path;
            }
            
            GraphNode currentNode = nodes.get(current);
            for (String child : currentNode.children) {
                if (!visited.contains(child)) {
                    visited.add(child);
                    parent.put(child, current);
                    queue.add(child);
                }
            }
        }
        
        return new ArrayList<>(); // No path found
    }
    
    public List<String> getCommitsInBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            return new ArrayList<>();
        }
        
        List<String> commits = new ArrayList<>(branches.get(branchName));
        commits.sort((a, b) -> Integer.compare(nodes.get(b).depth, nodes.get(a).depth));
        return commits;
    }
    
    public List<String> getMergeCandidates(String branchName, String targetBranch) {
        List<String> branchCommits = getCommitsInBranch(branchName);
        List<String> targetCommits = getCommitsInBranch(targetBranch);
        
        Set<String> targetSet = new HashSet<>(targetCommits);
        List<String> candidates = new ArrayList<>();
        
        for (String commit : branchCommits) {
            if (!targetSet.contains(commit)) {
                candidates.add(commit);
            }
        }
        
        return candidates;
    }
    
    public GraphVisualizationData getVisualizationData() {
        GraphVisualizationData data = new GraphVisualizationData();
        
        for (GraphNode node : nodes.values()) {
            data.addNode(node.commitId, node.branchName, node.depth);
            
            for (String childId : node.children) {
                data.addEdge(node.commitId, childId);
            }
        }
        
        return data;
    }
    
    public boolean hasCycles() {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        
        for (String commitId : nodes.keySet()) {
            if (hasCycleDFS(commitId, visited, recursionStack)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean hasCycleDFS(String commitId, Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(commitId)) {
            return true;
        }
        
        if (visited.contains(commitId)) {
            return false;
        }
        
        visited.add(commitId);
        recursionStack.add(commitId);
        
        GraphNode node = nodes.get(commitId);
        for (String child : node.children) {
            if (hasCycleDFS(child, visited, recursionStack)) {
                return true;
            }
        }
        
        recursionStack.remove(commitId);
        return false;
    }
    
    public int getGraphDiameter() {
        int maxDistance = 0;
        
        for (String start : nodes.keySet()) {
            Map<String, Integer> distances = bfsDistances(start);
            for (int distance : distances.values()) {
                maxDistance = Math.max(maxDistance, distance);
            }
        }
        
        return maxDistance;
    }
    
    private Map<String, Integer> bfsDistances(String start) {
        Map<String, Integer> distances = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        
        distances.put(start, 0);
        queue.add(start);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDistance = distances.get(current);
            
            GraphNode node = nodes.get(current);
            for (String child : node.children) {
                if (!distances.containsKey(child)) {
                    distances.put(child, currentDistance + 1);
                    queue.add(child);
                }
            }
        }
        
        return distances;
    }
    
    public static class GraphVisualizationData implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<NodeData> nodes = new ArrayList<>();
        public List<EdgeData> edges = new ArrayList<>();
        
        public void addNode(String id, String branch, int depth) {
            nodes.add(new NodeData(id, branch, depth));
        }
        
        public void addEdge(String from, String to) {
            edges.add(new EdgeData(from, to));
        }
    }
    
    public static class NodeData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id;
        public String branch;
        public int depth;
        
        public NodeData(String id, String branch, int depth) {
            this.id = id;
            this.branch = branch;
            this.depth = depth;
        }
    }
    
    public static class EdgeData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String from;
        public String to;
        
        public EdgeData(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }
}

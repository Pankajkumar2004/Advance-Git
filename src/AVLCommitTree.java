import java.io.Serializable;
import java.util.*;

public class AVLCommitTree implements Serializable {
    private static final long serialVersionUID = 1L;
    
    class AVLNode implements Serializable {
        private static final long serialVersionUID = 1L;
        Commit commit;
        AVLNode left, right;
        int height;
        
        AVLNode(Commit commit) {
            this.commit = commit;
            this.height = 1;
        }
    }
    
    private AVLNode root;
    private Map<String, AVLNode> commitMap = new HashMap<>();
    
    public void insert(Commit commit) {
        root = insert(root, commit);
        commitMap.put(commit.commitId, findNode(root, commit.commitId));
    }
    
    private AVLNode insert(AVLNode node, Commit commit) {
        if (node == null) return new AVLNode(commit);
        
        int comparison = commit.commitId.compareTo(node.commit.commitId);
        if (comparison < 0) {
            node.left = insert(node.left, commit);
        } else if (comparison > 0) {
            node.right = insert(node.right, commit);
        } else {
            return node; // Duplicate commits not allowed
        }
        
        node.height = 1 + Math.max(height(node.left), height(node.right));
        
        int balance = getBalance(node);
        
        // Left Left Case
        if (balance > 1 && commit.commitId.compareTo(node.left.commit.commitId) < 0) {
            return rightRotate(node);
        }
        
        // Right Right Case
        if (balance < -1 && commit.commitId.compareTo(node.right.commit.commitId) > 0) {
            return leftRotate(node);
        }
        
        // Left Right Case
        if (balance > 1 && commit.commitId.compareTo(node.left.commit.commitId) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        
        // Right Left Case
        if (balance < -1 && commit.commitId.compareTo(node.right.commit.commitId) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        
        return node;
    }
    
    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;
        
        x.right = y;
        y.left = T2;
        
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        
        return x;
    }
    
    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;
        
        y.left = x;
        x.right = T2;
        
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        
        return y;
    }
    
    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }
    
    private int getBalance(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }
    
    private AVLNode findNode(AVLNode node, String commitId) {
        if (node == null) return null;
        
        int comparison = commitId.compareTo(node.commit.commitId);
        if (comparison == 0) return node;
        else if (comparison < 0) return findNode(node.left, commitId);
        else return findNode(node.right, commitId);
    }
    
    public Commit find(String commitId) {
        AVLNode node = commitMap.get(commitId);
        return node != null ? node.commit : null;
    }
    
    public List<Commit> getCommitHistory() {
        List<Commit> commits = new ArrayList<>();
        inOrderTraversal(root, commits);
        Collections.reverse(commits); // Reverse to get chronological order
        return commits;
    }
    
    private void inOrderTraversal(AVLNode node, List<Commit> commits) {
        if (node != null) {
            inOrderTraversal(node.left, commits);
            commits.add(node.commit);
            inOrderTraversal(node.right, commits);
        }
    }
    
    public List<Commit> getRecentCommits(int limit) {
        List<Commit> allCommits = getCommitHistory();
        return allCommits.subList(0, Math.min(limit, allCommits.size()));
    }
    
    public int getSize() {
        return commitMap.size();
    }
}

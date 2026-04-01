import java.io.Serializable;
import java.util.*;

public class TrieSearchEngine implements Serializable {
    private static final long serialVersionUID = 1L;
    
    class TrieNode implements Serializable {
        private static final long serialVersionUID = 1L;
        Map<Character, TrieNode> children = new HashMap<>();
        Set<String> commitIds = new HashSet<>();
        Set<String> fileNames = new HashSet<>();
        boolean isEndOfWord;
    }
    
    private TrieNode root;
    
    public TrieSearchEngine() {
        root = new TrieNode();
    }
    
    public void insertCommit(String commitId, String message) {
        String[] words = message.toLowerCase().split("\\s+");
        for (String word : words) {
            insertWord(word, commitId, null);
        }
        insertWord(commitId, commitId, null); // Also allow searching by commit ID
    }
    
    public void insertFile(String fileName) {
        String[] parts = fileName.toLowerCase().split("\\.");
        String nameWithoutExt = parts[0];
        String extension = parts.length > 1 ? parts[1] : "";
        
        insertWord(nameWithoutExt, null, fileName);
        insertWord(fileName, null, fileName);
        
        if (!extension.isEmpty()) {
            insertWord(extension, null, fileName);
        }
    }
    
    private void insertWord(String word, String commitId, String fileName) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                current.children.put(ch, new TrieNode());
            }
            current = current.children.get(ch);
        }
        
        if (commitId != null) {
            current.commitIds.add(commitId);
        }
        if (fileName != null) {
            current.fileNames.add(fileName);
        }
        current.isEndOfWord = true;
    }
    
    public SearchResult search(String query) {
        SearchResult result = new SearchResult();
        String lowerQuery = query.toLowerCase();
        
        // Exact match
        TrieNode exactNode = searchNode(lowerQuery);
        if (exactNode != null) {
            result.exactCommits.addAll(exactNode.commitIds);
            result.exactFiles.addAll(exactNode.fileNames);
        }
        
        // Prefix matches
        List<String> prefixMatches = new ArrayList<>();
        collectPrefixMatches(root, lowerQuery, "", prefixMatches);
        
        for (String match : prefixMatches) {
            TrieNode node = searchNode(match);
            if (node != null) {
                result.partialCommits.addAll(node.commitIds);
                result.partialFiles.addAll(node.fileNames);
            }
        }
        
        // Remove duplicates
        result.exactCommits.removeAll(result.partialCommits);
        result.exactFiles.removeAll(result.partialFiles);
        
        return result;
    }
    
    private TrieNode searchNode(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return null;
            }
            current = current.children.get(ch);
        }
        return current.isEndOfWord ? current : null;
    }
    
    private void collectPrefixMatches(TrieNode node, String query, String current, List<String> matches) {
        if (node == null) return;
        
        if (current.startsWith(query)) {
            matches.add(current);
        }
        
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            collectPrefixMatches(entry.getValue(), query, current + entry.getKey(), matches);
        }
    }
    
    public List<String> getSuggestions(String prefix) {
        List<String> suggestions = new ArrayList<>();
        collectSuggestions(root, prefix.toLowerCase(), "", suggestions);
        return suggestions;
    }
    
    private void collectSuggestions(TrieNode node, String prefix, String current, List<String> suggestions) {
        if (node == null) return;
        
        if (current.startsWith(prefix) && node.isEndOfWord) {
            suggestions.add(current);
        }
        
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            collectSuggestions(entry.getValue(), prefix, current + entry.getKey(), suggestions);
        }
    }
    
    public static class SearchResult implements Serializable {
        private static final long serialVersionUID = 1L;
        public Set<String> exactCommits = new HashSet<>();
        public Set<String> exactFiles = new HashSet<>();
        public Set<String> partialCommits = new HashSet<>();
        public Set<String> partialFiles = new HashSet<>();
        
        public boolean hasResults() {
            return !exactCommits.isEmpty() || !exactFiles.isEmpty() || 
                   !partialCommits.isEmpty() || !partialFiles.isEmpty();
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (!exactCommits.isEmpty()) {
                sb.append("Exact Commits: ").append(exactCommits).append("\n");
            }
            if (!exactFiles.isEmpty()) {
                sb.append("Exact Files: ").append(exactFiles).append("\n");
            }
            if (!partialCommits.isEmpty()) {
                sb.append("Partial Commits: ").append(partialCommits).append("\n");
            }
            if (!partialFiles.isEmpty()) {
                sb.append("Partial Files: ").append(partialFiles);
            }
            return sb.toString();
        }
    }
}

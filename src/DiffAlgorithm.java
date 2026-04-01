import java.io.Serializable;
import java.util.*;

public class DiffAlgorithm implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static class DiffResult implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<DiffLine> additions = new ArrayList<>();
        public List<DiffLine> deletions = new ArrayList<>();
        public List<DiffLine> modifications = new ArrayList<>();
        public List<DiffLine> unchanged = new ArrayList<>();
        
        public int getAddedCount() { return additions.size(); }
        public int getDeletedCount() { return deletions.size(); }
        public int getModifiedCount() { return modifications.size(); }
        public int getUnchangedCount() { return unchanged.size(); }
        
        public int getTotalChanges() {
            return getAddedCount() + getDeletedCount() + getModifiedCount();
        }
        
        public double getSimilarityPercentage() {
            int total = getAddedCount() + getDeletedCount() + getModifiedCount() + getUnchangedCount();
            return total == 0 ? 100.0 : (double) getUnchangedCount() / total * 100;
        }
    }
    
    public static class DiffLine implements Serializable {
        private static final long serialVersionUID = 1L;
        public String content;
        public int lineNumber;
        public DiffType type;
        
        public DiffLine(String content, int lineNumber, DiffType type) {
            this.content = content;
            this.lineNumber = lineNumber;
            this.type = type;
        }
        
        @Override
        public String toString() {
            return String.format("%s %d: %s", type.symbol, lineNumber, content);
        }
    }
    
    public enum DiffType {
        ADDED("+"), DELETED("-"), MODIFIED("~"), UNCHANGED(" ");
        
        public final String symbol;
        
        DiffType(String symbol) {
            this.symbol = symbol;
        }
    }
    
    public DiffResult computeDiff(String oldContent, String newContent) {
        String[] oldLines = oldContent.split("\n");
        String[] newLines = newContent.split("\n");
        
        // Compute LCS matrix
        int[][] lcsMatrix = computeLCSMatrix(oldLines, newLines);
        
        // Backtrack to find differences
        DiffResult result = new DiffResult();
        backtrackAndBuildDiff(lcsMatrix, oldLines, newLines, result);
        
        return result;
    }
    
    private int[][] computeLCSMatrix(String[] oldLines, String[] newLines) {
        int m = oldLines.length;
        int n = newLines.length;
        int[][] dp = new int[m + 1][n + 1];
        
        // Fill LCS matrix
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (oldLines[i - 1].equals(newLines[j - 1])) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        
        return dp;
    }
    
    private void backtrackAndBuildDiff(int[][] lcsMatrix, String[] oldLines, String[] newLines, DiffResult result) {
        int i = oldLines.length;
        int j = newLines.length;
        int oldLineNum = 1;
        int newLineNum = 1;
        
        Stack<DiffLine> diffStack = new Stack<>();
        
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && oldLines[i - 1].equals(newLines[j - 1])) {
                // Lines are the same
                diffStack.push(new DiffLine(oldLines[i - 1], oldLineNum, DiffType.UNCHANGED));
                i--;
                j--;
                oldLineNum--;
                newLineNum--;
            } else if (j > 0 && (i == 0 || lcsMatrix[i][j - 1] >= lcsMatrix[i - 1][j])) {
                // Line was added
                diffStack.push(new DiffLine(newLines[j - 1], newLineNum, DiffType.ADDED));
                j--;
                newLineNum--;
            } else if (i > 0 && (j == 0 || lcsMatrix[i - 1][j] > lcsMatrix[i][j - 1])) {
                // Line was deleted
                diffStack.push(new DiffLine(oldLines[i - 1], oldLineNum, DiffType.DELETED));
                i--;
                oldLineNum--;
            }
        }
        
        // Process the stack to build the final diff
        while (!diffStack.isEmpty()) {
            DiffLine line = diffStack.pop();
            
            switch (line.type) {
                case ADDED:
                    result.additions.add(line);
                    break;
                case DELETED:
                    result.deletions.add(line);
                    break;
                case UNCHANGED:
                    result.unchanged.add(line);
                    break;
            }
        }
        
        // Identify modifications (adjacent additions and deletions)
        identifyModifications(result);
    }
    
    private void identifyModifications(DiffResult result) {
        List<DiffLine> newAdditions = new ArrayList<>();
        List<DiffLine> newDeletions = new ArrayList<>();
        
        int i = 0, j = 0;
        while (i < result.deletions.size() && j < result.additions.size()) {
            DiffLine deleted = result.deletions.get(i);
            DiffLine added = result.additions.get(j);
            
            // Check if these lines could be a modification
            if (areSimilarLines(deleted.content, added.content)) {
                result.modifications.add(new DiffLine(
                    deleted.content + " -> " + added.content,
                    deleted.lineNumber,
                    DiffType.MODIFIED
                ));
                i++;
                j++;
            } else if (deleted.lineNumber < added.lineNumber) {
                newDeletions.add(deleted);
                i++;
            } else {
                newAdditions.add(added);
                j++;
            }
        }
        
        // Add remaining lines
        while (i < result.deletions.size()) {
            newDeletions.add(result.deletions.get(i++));
        }
        while (j < result.additions.size()) {
            newAdditions.add(result.additions.get(j++));
        }
        
        result.additions = newAdditions;
        result.deletions = newDeletions;
    }
    
    private boolean areSimilarLines(String line1, String line2) {
        // Simple similarity check - can be enhanced with more sophisticated algorithms
        double similarity = calculateStringSimilarity(line1, line2);
        return similarity > 0.5; // 50% similarity threshold
    }
    
    private double calculateStringSimilarity(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;
        
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) return 1.0;
        
        int lcsLength = computeLCSLength(s1, s2);
        return (double) lcsLength / maxLength;
    }
    
    private int computeLCSLength(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];
        
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        
        return dp[m][n];
    }
    
    // Advanced diff features
    public String generateUnifiedDiff(String oldFileName, String newFileName, DiffResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ").append(oldFileName).append("\n");
        sb.append("+++ ").append(newFileName).append("\n");
        
        // Group changes by hunks
        List<DiffHunk> hunks = groupChangesIntoHunks(result);
        
        for (DiffHunk hunk : hunks) {
            sb.append("@@ -").append(hunk.oldStart).append(",").append(hunk.oldCount)
              .append(" +").append(hunk.newStart).append(",").append(hunk.newCount)
              .append(" @@\n");
            
            for (DiffLine line : hunk.lines) {
                sb.append(line.type.symbol).append(line.content).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    private List<DiffHunk> groupChangesIntoHunks(DiffResult result) {
        List<DiffHunk> hunks = new ArrayList<>();
        List<DiffLine> allLines = new ArrayList<>();
        
        // Combine all lines in order
        allLines.addAll(result.deletions);
        allLines.addAll(result.additions);
        allLines.addAll(result.modifications);
        allLines.addAll(result.unchanged);
        
        // Sort by line number
        allLines.sort(Comparator.comparingInt(l -> l.lineNumber));
        
        // Group into hunks (simplified grouping)
        if (allLines.isEmpty()) return hunks;
        
        DiffHunk currentHunk = new DiffHunk();
        currentHunk.oldStart = allLines.get(0).lineNumber;
        currentHunk.newStart = allLines.get(0).lineNumber;
        
        int lastLine = allLines.get(0).lineNumber;
        
        for (DiffLine line : allLines) {
            if (line.lineNumber - lastLine > 3) { // Context gap
                hunks.add(currentHunk);
                currentHunk = new DiffHunk();
                currentHunk.oldStart = line.lineNumber;
                currentHunk.newStart = line.lineNumber;
            }
            
            currentHunk.lines.add(line);
            
            switch (line.type) {
                case DELETED:
                    currentHunk.oldCount++;
                    break;
                case ADDED:
                    currentHunk.newCount++;
                    break;
                case MODIFIED:
                    currentHunk.oldCount++;
                    currentHunk.newCount++;
                    break;
                case UNCHANGED:
                    currentHunk.oldCount++;
                    currentHunk.newCount++;
                    break;
            }
            
            lastLine = line.lineNumber;
        }
        
        hunks.add(currentHunk);
        return hunks;
    }
    
    private static class DiffHunk implements Serializable {
        private static final long serialVersionUID = 1L;
        int oldStart, newStart, oldCount, newCount;
        List<DiffLine> lines = new ArrayList<>();
    }
    
    // Performance-optimized diff for large files
    public DiffResult computeOptimizedDiff(String oldContent, String newContent, int chunkSize) {
        if (oldContent.length() < chunkSize && newContent.length() < chunkSize) {
            return computeDiff(oldContent, newContent);
        }
        
        // Split into chunks and compute diff for each
        String[] oldChunks = splitIntoChunks(oldContent, chunkSize);
        String[] newChunks = splitIntoChunks(newContent, chunkSize);
        
        DiffResult combinedResult = new DiffResult();
        
        for (int i = 0; i < Math.max(oldChunks.length, newChunks.length); i++) {
            String oldChunk = i < oldChunks.length ? oldChunks[i] : "";
            String newChunk = i < newChunks.length ? newChunks[i] : "";
            
            DiffResult chunkResult = computeDiff(oldChunk, newChunk);
            
            // Adjust line numbers and combine results
            adjustLineNumbers(chunkResult, i * chunkSize);
            combineResults(combinedResult, chunkResult);
        }
        
        return combinedResult;
    }
    
    private String[] splitIntoChunks(String content, int chunkSize) {
        int numChunks = (content.length() + chunkSize - 1) / chunkSize;
        String[] chunks = new String[numChunks];
        
        for (int i = 0; i < numChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, content.length());
            chunks[i] = content.substring(start, end);
        }
        
        return chunks;
    }
    
    private void adjustLineNumbers(DiffResult result, int offset) {
        adjustLineNumbers(result.additions, offset);
        adjustLineNumbers(result.deletions, offset);
        adjustLineNumbers(result.modifications, offset);
        adjustLineNumbers(result.unchanged, offset);
    }
    
    private void adjustLineNumbers(List<DiffLine> lines, int offset) {
        for (DiffLine line : lines) {
            line.lineNumber += offset;
        }
    }
    
    private void combineResults(DiffResult target, DiffResult source) {
        target.additions.addAll(source.additions);
        target.deletions.addAll(source.deletions);
        target.modifications.addAll(source.modifications);
        target.unchanged.addAll(source.unchanged);
    }
}

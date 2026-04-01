import { clsx } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs) {
  return twMerge(clsx(inputs));
}

export function formatBytes(bytes, decimals = 2) {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

export function formatTimeAgo(timestamp) {
  const seconds = Math.floor((Date.now() - timestamp) / 1000);
  let interval = seconds / 31536000;
  if (interval > 1) return Math.floor(interval) + " years ago";
  interval = seconds / 2592000;
  if (interval > 1) return Math.floor(interval) + " months ago";
  interval = seconds / 86400;
  if (interval > 1) return Math.floor(interval) + " days ago";
  interval = seconds / 3600;
  if (interval > 1) return Math.floor(interval) + " hours ago";
  interval = seconds / 60;
  if (interval > 1) return Math.floor(interval) + " minutes ago";
  return Math.floor(seconds) + " seconds ago";
}

export function generateMockCommits() {
  return [
    {
      id: 'a1b2c3d',
      message: 'Initial commit with advanced DSA features',
      author: 'Advanced Developer',
      timestamp: Date.now() - 86400000,
      files: ['AVLCommitTree.java', 'TrieSearchEngine.java'],
      branch: 'master',
      priority: 1
    },
    {
      id: 'e4f5g6h',
      message: 'Add graph visualization and branch management',
      author: 'Graph Expert',
      timestamp: Date.now() - 43200000,
      files: ['CommitGraph.java', 'AdvancedRepository.java'],
      branch: 'feature/graph-viz',
      priority: 2
    },
    {
      id: 'i7j8k9l',
      message: 'Implement LRU cache for performance optimization',
      author: 'Cache Master',
      timestamp: Date.now() - 21600000,
      files: ['LRUCache.java', 'PerformanceReport.java'],
      branch: 'master',
      priority: 1
    },
    {
      id: 'm0n1o2p',
      message: 'Add diff algorithm with LCS implementation',
      author: 'Diff Specialist',
      timestamp: Date.now() - 10800000,
      files: ['DiffAlgorithm.java', 'DiffViewer.java'],
      branch: 'feature/diff-enhancement',
      priority: 3
    }
  ];
}

export function generateMockPerformanceData() {
  return {
    cache: {
      hitRate: 87.5,
      totalAccesses: 15420,
      hits: 13492,
      misses: 1928,
      currentSize: 45,
      capacity: 100
    },
    tree: {
      size: 156,
      height: 8,
      balanceFactor: 0.95,
      operations: 3420
    },
    hashTable: {
      loadFactor: 0.72,
      totalSize: 89,
      capacity: 128,
      maxChainLength: 3,
      collisions: 23
    },
    graph: {
      nodes: 156,
      edges: 142,
      diameter: 12,
      hasCycles: false,
      branches: 4
    },
    search: {
      queries: 892,
      avgResponseTime: 23,
      suggestions: 156,
      exactMatches: 34
    }
  };
}

export function generateMockFiles() {
  return [
    {
      name: 'AVLCommitTree.java',
      path: 'src/AVLCommitTree.java',
      size: 4521,
      type: 'java',
      lastModified: Date.now() - 3600000,
      content: `public class AVLCommitTree {
  // Advanced balanced tree implementation
  // O(log n) guaranteed operations
  // Self-balancing with rotations
}`
    },
    {
      name: 'TrieSearchEngine.java',
      path: 'src/TrieSearchEngine.java',
      size: 3892,
      type: 'java',
      lastModified: Date.now() - 7200000,
      content: `public class TrieSearchEngine {
  // Fast prefix search implementation
  // O(m) complexity where m = query length
  // Auto-suggestions and fuzzy matching
}`
    },
    {
      name: 'CommitGraph.java',
      path: 'src/CommitGraph.java',
      size: 5234,
      type: 'java',
      lastModified: Date.now() - 1800000,
      content: `public class CommitGraph {
  // Graph-based branch visualization
  // BFS/DFS traversal algorithms
  // Cycle detection and path finding
}`
    },
    {
      name: 'LRUCache.java',
      path: 'src/LRUCache.java',
      size: 4156,
      type: 'java',
      lastModified: Date.now() - 5400000,
      content: `public class LRUCache {
  // Least Recently Used cache
  // O(1) get and put operations
  // Memory-efficient storage
}`
    }
  ];
}

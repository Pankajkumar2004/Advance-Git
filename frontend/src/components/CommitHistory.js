import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { 
  GitBranch, 
  User, 
  Clock, 
  FileText, 
  ChevronDown,
  ChevronRight,
  Search,
  Filter,
  Zap,
  Database,
  ArrowUpDown
} from 'lucide-react';
import { generateMockCommits, formatTimeAgo } from '../lib/utils';

export function CommitHistory() {
  const [expandedCommits, setExpandedCommits] = useState(new Set());
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('time');
  const [filterBranch, setFilterBranch] = useState('all');
  
  const commits = generateMockCommits();
  const branches = ['all', 'master', 'feature/graph-viz', 'feature/diff-enhancement', 'develop'];
  
  const filteredCommits = commits.filter(commit => {
    const matchesSearch = commit.message.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         commit.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         commit.author.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesBranch = filterBranch === 'all' || commit.branch === filterBranch;
    
    return matchesSearch && matchesBranch;
  });
  
  const sortedCommits = [...filteredCommits].sort((a, b) => {
    switch (sortBy) {
      case 'time':
        return b.timestamp - a.timestamp;
      case 'priority':
        return a.priority - b.priority;
      case 'files':
        return b.files.length - a.files.length;
      default:
        return 0;
    }
  });

  const toggleCommitExpansion = (commitId) => {
    const newExpanded = new Set(expandedCommits);
    if (newExpanded.has(commitId)) {
      newExpanded.delete(commitId);
    } else {
      newExpanded.add(commitId);
    }
    setExpandedCommits(newExpanded);
  };

  const getPriorityColor = (priority) => {
    switch (priority) {
      case 1: return 'text-green-600 bg-green-50';
      case 2: return 'text-yellow-600 bg-yellow-50';
      case 3: return 'text-red-600 bg-red-50';
      default: return 'text-gray-600 bg-gray-50';
    }
  };

  const getPriorityLabel = (priority) => {
    switch (priority) {
      case 1: return 'High';
      case 2: return 'Medium';
      case 3: return 'Low';
      default: return 'Normal';
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Commit History</h1>
          <p className="text-muted-foreground">AVL Tree-Optimized Commit Management</p>
        </div>
        
        <div className="flex items-center space-x-4">
          <div className="flex items-center space-x-2 text-sm text-muted-foreground">
            <Database className="h-4 w-4" />
            <span>O(log n) traversal</span>
          </div>
          
          <div className="flex items-center space-x-2 text-sm text-muted-foreground">
            <Zap className="h-4 w-4" />
            <span>Priority Queue</span>
          </div>
        </div>
      </div>

      {/* Filters and Search */}
      <div className="border rounded-lg p-4">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <input
              type="text"
              placeholder="Search commits..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border rounded-md bg-background"
            />
          </div>
          
          <select
            value={filterBranch}
            onChange={(e) => setFilterBranch(e.target.value)}
            className="px-4 py-2 border rounded-md bg-background"
          >
            {branches.map(branch => (
              <option key={branch} value={branch}>
                {branch === 'all' ? 'All Branches' : branch}
              </option>
            ))}
          </select>
          
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            className="px-4 py-2 border rounded-md bg-background"
          >
            <option value="time">Sort by Time</option>
            <option value="priority">Sort by Priority</option>
            <option value="files">Sort by Files</option>
          </select>
        </div>
        
        <div className="flex items-center justify-between mt-4">
          <p className="text-sm text-muted-foreground">
            Showing {sortedCommits.length} of {commits.length} commits
          </p>
          
          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2">
              <div className="w-3 h-3 bg-green-500 rounded-full"></div>
              <span className="text-xs">High Priority</span>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-3 h-3 bg-yellow-500 rounded-full"></div>
              <span className="text-xs">Medium Priority</span>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-3 h-3 bg-red-500 rounded-full"></div>
              <span className="text-xs">Low Priority</span>
            </div>
          </div>
        </div>
      </div>

      {/* Commit Tree Visualization */}
      <div className="border rounded-lg">
        <div className="p-4 border-b bg-muted/50">
          <h2 className="text-lg font-semibold flex items-center">
            <GitBranch className="h-5 w-5 mr-2" />
            AVL Tree Structure
          </h2>
        </div>
        
        <div className="p-4">
          <div className="relative">
            {/* Tree visualization would go here - simplified for demo */}
            <div className="flex items-center justify-center py-8">
              <div className="text-center">
                <div className="w-16 h-16 bg-primary rounded-full flex items-center justify-center mb-4">
                  <GitBranch className="h-8 w-8 text-primary-foreground" />
                </div>
                <p className="font-medium">Root Commit</p>
                <p className="text-sm text-muted-foreground">Balanced AVL Tree</p>
              </div>
            </div>
            
            <div className="flex justify-center space-x-8 mt-8">
              {sortedCommits.slice(0, 3).map((commit, index) => (
                <div key={commit.id} className="text-center">
                  <div className={`w-12 h-12 rounded-full flex items-center justify-center mb-2 ${
                    getPriorityColor(commit.priority).split(' ')[1]
                  }`}>
                    <GitBranch className={`h-6 w-6 ${getPriorityColor(commit.priority).split(' ')[0]}`} />
                  </div>
                  <p className="text-xs font-medium">{commit.id}</p>
                  <p className="text-xs text-muted-foreground">{getPriorityLabel(commit.priority)}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Commit List */}
      <div className="space-y-4">
        {sortedCommits.map((commit) => (
          <div key={commit.id} className="border rounded-lg">
            <div 
              className="p-4 hover:bg-accent/50 transition-colors cursor-pointer"
              onClick={() => toggleCommitExpansion(commit.id)}
            >
              <div className="flex items-start justify-between">
                <div className="flex items-start space-x-4">
                  <div className="flex items-center space-x-2">
                    {expandedCommits.has(commit.id) ? (
                      <ChevronDown className="h-4 w-4 text-muted-foreground" />
                    ) : (
                      <ChevronRight className="h-4 w-4 text-muted-foreground" />
                    )}
                    
                    <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                      getPriorityColor(commit.priority).split(' ')[1]
                    }`}>
                      <GitBranch className={`h-4 w-4 ${getPriorityColor(commit.priority).split(' ')[0]}`} />
                    </div>
                  </div>
                  
                  <div className="flex-1">
                    <div className="flex items-center space-x-3 mb-2">
                      <h3 className="font-semibold">{commit.message}</h3>
                      <span className="text-xs px-2 py-1 bg-secondary rounded-md">
                        {commit.id}
                      </span>
                      <span className={`text-xs px-2 py-1 rounded-md ${getPriorityColor(commit.priority)}`}>
                        {getPriorityLabel(commit.priority)}
                      </span>
                    </div>
                    
                    <div className="flex items-center space-x-4 text-sm text-muted-foreground">
                      <div className="flex items-center space-x-1">
                        <User className="h-3 w-3" />
                        <span>{commit.author}</span>
                      </div>
                      <div className="flex items-center space-x-1">
                        <GitBranch className="h-3 w-3" />
                        <span>{commit.branch}</span>
                      </div>
                      <div className="flex items-center space-x-1">
                        <Clock className="h-3 w-3" />
                        <span>{formatTimeAgo(commit.timestamp)}</span>
                      </div>
                      <div className="flex items-center space-x-1">
                        <FileText className="h-3 w-3" />
                        <span>{commit.files.length} files</span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div className="flex items-center space-x-2">
                  <Link 
                    to={`/diff/${commit.id}/HEAD`}
                    className="text-sm text-primary hover:underline"
                    onClick={(e) => e.stopPropagation()}
                  >
                    View Diff
                  </Link>
                </div>
              </div>
            </div>
            
            {expandedCommits.has(commit.id) && (
              <div className="border-t p-4 bg-muted/30">
                <h4 className="font-medium mb-3">Changed Files</h4>
                <div className="space-y-2">
                  {commit.files.map((file, index) => (
                    <div key={index} className="flex items-center justify-between p-2 bg-background rounded">
                      <div className="flex items-center space-x-2">
                        <FileText className="h-4 w-4 text-muted-foreground" />
                        <span className="text-sm font-mono">{file}</span>
                      </div>
                      <div className="flex items-center space-x-2">
                        <span className="text-xs text-green-600">+42</span>
                        <span className="text-xs text-red-600">-12</span>
                      </div>
                    </div>
                  ))}
                </div>
                
                <div className="mt-4 pt-4 border-t">
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                    <div>
                      <span className="text-muted-foreground">Tree Depth:</span>
                      <span className="ml-2 font-medium">O(log n)</span>
                    </div>
                    <div>
                      <span className="text-muted-foreground">Balance Factor:</span>
                      <span className="ml-2 font-medium">0.95</span>
                    </div>
                    <div>
                      <span className="text-muted-foreground">Rotations:</span>
                      <span className="ml-2 font-medium">2</span>
                    </div>
                    <div>
                      <span className="text-muted-foreground">Cache Hits:</span>
                      <span className="ml-2 font-medium">87%</span>
                    </div>
                  </div>
                </div>
              </div>
            )}
          </div>
        ))}
      </div>
      
      {sortedCommits.length === 0 && (
        <div className="text-center py-12">
          <GitBranch className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
          <h3 className="text-lg font-medium mb-2">No commits found</h3>
          <p className="text-muted-foreground">Try adjusting your search or filter criteria</p>
        </div>
      )}
    </div>
  );
}

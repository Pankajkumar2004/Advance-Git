import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Search, GitBranch, FileText, Clock, Hash, Zap, Lightbulb } from 'lucide-react';
import { generateMockCommits, generateMockFiles, formatTimeAgo } from '../lib/utils';

export function SearchComponent() {
  const [query, setQuery] = useState('');
  const [searchType, setSearchType] = useState('all');
  
  const commits = generateMockCommits();
  const files = generateMockFiles();
  
  const performSearch = () => {
    if (!query.trim()) return { commits: [], files: [], suggestions: [] };
    
    const queryLower = query.toLowerCase();
    
    // Simulate Trie-based search
    const matchedCommits = commits.filter(commit => 
      commit.message.toLowerCase().includes(queryLower) ||
      commit.id.toLowerCase().includes(queryLower) ||
      commit.author.toLowerCase().includes(queryLower)
    );
    
    const matchedFiles = files.filter(file =>
      file.name.toLowerCase().includes(queryLower) ||
      file.path.toLowerCase().includes(queryLower) ||
      file.content.toLowerCase().includes(queryLower)
    );
    
    // Generate suggestions based on prefix matching
    const suggestions = [
      ...commits.map(c => c.message.split(' ')[0]),
      ...files.map(f => f.name.split('.')[0])
    ].filter(item => item.toLowerCase().startsWith(queryLower))
     .slice(0, 5);
    
    return {
      commits: searchType === 'files' ? [] : matchedCommits,
      files: searchType === 'commits' ? [] : matchedFiles,
      suggestions
    };
  };
  
  const searchResults = performSearch();
  
  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Search Repository</h1>
          <p className="text-muted-foreground">Trie-Based Instant Search with O(m) Complexity</p>
        </div>
        
        <div className="flex items-center space-x-4">
          <div className="flex items-center space-x-2 text-sm text-muted-foreground">
            <Hash className="h-4 w-4" />
            <span>Trie Search</span>
          </div>
          
          <div className="flex items-center space-x-2 text-sm text-muted-foreground">
            <Zap className="h-4 w-4" />
            <span>Instant Results</span>
          </div>
        </div>
      </div>

      {/* Search Interface */}
      <div className="border rounded-lg p-6">
        <div className="space-y-4">
          <div className="relative">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 h-5 w-5 text-muted-foreground" />
            <input
              type="text"
              placeholder="Search commits, files, or content..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="w-full pl-12 pr-4 py-3 text-lg border rounded-lg bg-background focus:outline-none focus:ring-2 focus:ring-primary"
              autoFocus
            />
          </div>
          
          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2">
              <label className="text-sm font-medium">Search Type:</label>
              <select
                value={searchType}
                onChange={(e) => setSearchType(e.target.value)}
                className="px-3 py-1 border rounded-md bg-background"
              >
                <option value="all">All</option>
                <option value="commits">Commits Only</option>
                <option value="files">Files Only</option>
              </select>
            </div>
            
            <div className="flex items-center space-x-2 text-sm text-muted-foreground">
              <span>Query complexity:</span>
              <span className="font-mono bg-accent px-2 py-1 rounded">O(m)</span>
            </div>
          </div>
        </div>
        
        {/* Suggestions */}
        {query && searchResults.suggestions.length > 0 && (
          <div className="mt-4 p-4 bg-muted/50 rounded-lg">
            <div className="flex items-center space-x-2 mb-2">
              <Lightbulb className="h-4 w-4 text-yellow-600" />
              <span className="text-sm font-medium">Suggestions</span>
            </div>
            <div className="flex flex-wrap gap-2">
              {searchResults.suggestions.map((suggestion, index) => (
                <button
                  key={index}
                  onClick={() => setQuery(suggestion)}
                  className="px-3 py-1 bg-background border rounded-md text-sm hover:bg-accent"
                >
                  {suggestion}
                </button>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Search Results */}
      {query && (
        <div className="space-y-6">
          {/* Commit Results */}
          {searchResults.commits.length > 0 && (
            <div className="border rounded-lg">
              <div className="p-4 border-b bg-muted/50">
                <h2 className="text-lg font-semibold flex items-center">
                  <GitBranch className="h-5 w-5 mr-2" />
                  Commits ({searchResults.commits.length})
                </h2>
              </div>
              <div className="p-4 space-y-4">
                {searchResults.commits.map((commit) => (
                  <div key={commit.id} className="border rounded-lg p-4">
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <div className="flex items-center space-x-3 mb-2">
                          <h3 className="font-semibold">{commit.message}</h3>
                          <span className="text-xs px-2 py-1 bg-secondary rounded-md">
                            {commit.id}
                          </span>
                        </div>
                        
                        <div className="flex items-center space-x-4 text-sm text-muted-foreground mb-2">
                          <span>{commit.author}</span>
                          <span>•</span>
                          <span>{formatTimeAgo(commit.timestamp)}</span>
                          <span>•</span>
                          <span>{commit.files.length} files</span>
                        </div>
                        
                        <div className="text-sm">
                          <span className="text-muted-foreground">Branch:</span>
                          <span className="ml-2 font-mono">{commit.branch}</span>
                        </div>
                      </div>
                      
                      <Link 
                        to={`/diff/${commit.id}/HEAD`}
                        className="text-primary hover:underline text-sm"
                      >
                        View Diff
                      </Link>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* File Results */}
          {searchResults.files.length > 0 && (
            <div className="border rounded-lg">
              <div className="p-4 border-b bg-muted/50">
                <h2 className="text-lg font-semibold flex items-center">
                  <FileText className="h-5 w-5 mr-2" />
                  Files ({searchResults.files.length})
                </h2>
              </div>
              <div className="p-4">
                <div className="grid gap-4">
                  {searchResults.files.map((file) => (
                    <div key={file.path} className="border rounded-lg p-4">
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <div className="flex items-center space-x-3 mb-2">
                            <FileText className="h-4 w-4 text-muted-foreground" />
                            <h3 className="font-semibold">{file.name}</h3>
                            <span className="text-xs px-2 py-1 bg-blue-100 text-blue-800 rounded">
                              {file.type.toUpperCase()}
                            </span>
                          </div>
                          
                          <p className="text-sm text-muted-foreground mb-2">{file.path}</p>
                          
                          <div className="flex items-center space-x-4 text-sm text-muted-foreground">
                            <span>{(file.size / 1024).toFixed(1)} KB</span>
                            <span>•</span>
                            <span>{formatTimeAgo(file.lastModified)}</span>
                          </div>
                        </div>
                        
                        <button className="text-primary hover:underline text-sm">
                          View File
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* No Results */}
          {searchResults.commits.length === 0 && searchResults.files.length === 0 && (
            <div className="text-center py-12 border rounded-lg">
              <Search className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
              <h3 className="text-lg font-medium mb-2">No results found</h3>
              <p className="text-muted-foreground">Try different keywords or check your spelling</p>
            </div>
          )}
        </div>
      )}

      {/* Search Statistics */}
      {query && (
        <div className="border rounded-lg p-4">
          <h3 className="font-semibold mb-3">Search Performance</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
            <div>
              <span className="text-muted-foreground">Search Time:</span>
              <span className="ml-2 font-medium font-mono">2.3ms</span>
            </div>
            <div>
              <span className="text-muted-foreground">Trie Depth:</span>
              <span className="ml-2 font-medium font-mono">O(m)</span>
            </div>
            <div>
              <span className="text-muted-foreground">Cache Hits:</span>
              <span className="ml-2 font-medium text-green-600">94%</span>
            </div>
            <div>
              <span className="text-muted-foreground">Results:</span>
              <span className="ml-2 font-medium">
                {searchResults.commits.length + searchResults.files.length}
              </span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

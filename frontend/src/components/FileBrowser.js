import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { 
  FileText, 
  Folder, 
  Search, 
  Download,
  Eye,
  GitBranch,
  Clock,
  Hash,
  Database,
  Zap
} from 'lucide-react';
import { generateMockFiles, formatBytes, formatTimeAgo } from '../lib/utils';

export function FileBrowser() {
  const [selectedFile, setSelectedFile] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [viewMode, setViewMode] = useState('list');
  
  const files = generateMockFiles();
  
  const filteredFiles = files.filter(file =>
    file.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    file.path.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getFileIcon = (type) => {
    return <FileText className="h-4 w-4" />;
  };

  const getLanguageColor = (type) => {
    const colors = {
      java: 'bg-blue-100 text-blue-800',
      js: 'bg-yellow-100 text-yellow-800',
      ts: 'bg-blue-100 text-blue-800',
      css: 'bg-purple-100 text-purple-800',
      html: 'bg-orange-100 text-orange-800',
      json: 'bg-green-100 text-green-800'
    };
    return colors[type] || 'bg-gray-100 text-gray-800';
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">File Browser</h1>
          <p className="text-muted-foreground">Hash Table Optimized File Storage</p>
        </div>
        
        <div className="flex items-center space-x-4">
          <div className="flex items-center space-x-2 text-sm text-muted-foreground">
            <Database className="h-4 w-4" />
            <span>O(1) access</span>
          </div>
          
          <div className="flex items-center space-x-2 text-sm text-muted-foreground">
            <Zap className="h-4 w-4" />
            <span>LRU Cached</span>
          </div>
        </div>
      </div>

      {/* Search and Controls */}
      <div className="border rounded-lg p-4">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <input
              type="text"
              placeholder="Search files..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border rounded-md bg-background"
            />
          </div>
          
          <div className="flex items-center space-x-2">
            <button
              onClick={() => setViewMode('list')}
              className={`px-3 py-2 rounded-md text-sm ${
                viewMode === 'list' 
                  ? 'bg-primary text-primary-foreground' 
                  : 'bg-secondary text-secondary-foreground'
              }`}
            >
              List View
            </button>
            <button
              onClick={() => setViewMode('grid')}
              className={`px-3 py-2 rounded-md text-sm ${
                viewMode === 'grid' 
                  ? 'bg-primary text-primary-foreground' 
                  : 'bg-secondary text-secondary-foreground'
              }`}
            >
              Grid View
            </button>
          </div>
        </div>
        
        <div className="flex items-center justify-between mt-4">
          <p className="text-sm text-muted-foreground">
            Showing {filteredFiles.length} of {files.length} files
          </p>
          
          <div className="flex items-center space-x-4 text-sm text-muted-foreground">
            <span>Total Size: {formatBytes(files.reduce((acc, f) => acc + f.size, 0))}</span>
            <span>Hash Table Load: 72%</span>
          </div>
        </div>
      </div>

      {/* File Storage Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="p-4 border rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">Total Files</p>
              <p className="text-2xl font-bold">{files.length}</p>
            </div>
            <FileText className="h-8 w-8 text-muted-foreground" />
          </div>
        </div>
        
        <div className="p-4 border rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">Cache Hit Rate</p>
              <p className="text-2xl font-bold">87%</p>
            </div>
            <Zap className="h-8 w-8 text-green-600" />
          </div>
        </div>
        
        <div className="p-4 border rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">Hash Collisions</p>
              <p className="text-2xl font-bold">3</p>
            </div>
            <Hash className="h-8 w-8 text-orange-600" />
          </div>
        </div>
        
        <div className="p-4 border rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">Max Chain</p>
              <p className="text-2xl font-bold">3</p>
            </div>
            <Database className="h-8 w-8 text-blue-600" />
          </div>
        </div>
      </div>

      {/* File List/Grid */}
      <div className="border rounded-lg">
        <div className="p-4 border-b bg-muted/50">
          <h2 className="text-lg font-semibold">Repository Files</h2>
        </div>
        
        {viewMode === 'list' ? (
          <div className="divide-y">
            {filteredFiles.map((file) => (
              <div 
                key={file.path}
                className="p-4 hover:bg-accent/50 transition-colors cursor-pointer"
                onClick={() => setSelectedFile(file)}
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-4">
                    <div className="flex items-center space-x-2">
                      {getFileIcon(file.type)}
                      <div>
                        <h3 className="font-medium">{file.name}</h3>
                        <p className="text-sm text-muted-foreground">{file.path}</p>
                      </div>
                    </div>
                    
                    <span className={`text-xs px-2 py-1 rounded ${getLanguageColor(file.type)}`}>
                      {file.type.toUpperCase()}
                    </span>
                  </div>
                  
                  <div className="flex items-center space-x-6 text-sm text-muted-foreground">
                    <div className="flex items-center space-x-1">
                      <span>{formatBytes(file.size)}</span>
                    </div>
                    
                    <div className="flex items-center space-x-1">
                      <Clock className="h-3 w-3" />
                      <span>{formatTimeAgo(file.lastModified)}</span>
                    </div>
                    
                    <div className="flex items-center space-x-2">
                      <button className="p-1 hover:bg-accent rounded">
                        <Eye className="h-4 w-4" />
                      </button>
                      <button className="p-1 hover:bg-accent rounded">
                        <Download className="h-4 w-4" />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="p-4">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {filteredFiles.map((file) => (
                <div 
                  key={file.path}
                  className="border rounded-lg p-4 hover:bg-accent/50 transition-colors cursor-pointer"
                  onClick={() => setSelectedFile(file)}
                >
                  <div className="flex flex-col items-center text-center">
                    <div className="w-12 h-12 bg-muted rounded-lg flex items-center justify-center mb-3">
                      {getFileIcon(file.type)}
                    </div>
                    
                    <h3 className="font-medium text-sm mb-1 truncate w-full">{file.name}</h3>
                    <p className="text-xs text-muted-foreground mb-2">{file.type.toUpperCase()}</p>
                    
                    <div className="flex items-center justify-between w-full text-xs text-muted-foreground">
                      <span>{formatBytes(file.size)}</span>
                      <span>{formatTimeAgo(file.lastModified)}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* File Preview Modal */}
      {selectedFile && (
        <div className="border rounded-lg">
          <div className="p-4 border-b bg-muted/50 flex items-center justify-between">
            <h2 className="text-lg font-semibold">File Preview: {selectedFile.name}</h2>
            <button
              onClick={() => setSelectedFile(null)}
              className="text-muted-foreground hover:text-foreground"
            >
              ×
            </button>
          </div>
          
          <div className="p-4">
            <div className="mb-4 flex items-center space-x-4 text-sm text-muted-foreground">
              <span>Path: {selectedFile.path}</span>
              <span>•</span>
              <span>Size: {formatBytes(selectedFile.size)}</span>
              <span>•</span>
              <span>Type: {selectedFile.type.toUpperCase()}</span>
              <span>•</span>
              <span>Modified: {formatTimeAgo(selectedFile.lastModified)}</span>
            </div>
            
            <div className="bg-muted rounded-lg p-4">
              <pre className="text-sm font-mono overflow-x-auto">
                <code>{selectedFile.content}</code>
              </pre>
            </div>
            
            <div className="mt-4 pt-4 border-t">
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                <div>
                  <span className="text-muted-foreground">Hash Index:</span>
                  <span className="ml-2 font-medium">O(1)</span>
                </div>
                <div>
                  <span className="text-muted-foreground">Cache Status:</span>
                  <span className="ml-2 font-medium text-green-600">Cached</span>
                </div>
                <div>
                  <span className="text-muted-foreground">Chain Length:</span>
                  <span className="ml-2 font-medium">1</span>
                </div>
                <div>
                  <span className="text-muted-foreground">Access Count:</span>
                  <span className="ml-2 font-medium">42</span>
                </div>
              </div>
            </div>
            
            <div className="mt-4 flex items-center space-x-2">
              <button className="px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90">
                <GitBranch className="h-4 w-4 mr-2 inline" />
                View History
              </button>
              <button className="px-4 py-2 bg-secondary text-secondary-foreground rounded-md hover:bg-secondary/90">
                <Download className="h-4 w-4 mr-2 inline" />
                Download
              </button>
            </div>
          </div>
        </div>
      )}
      
      {filteredFiles.length === 0 && (
        <div className="text-center py-12">
          <FileText className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
          <h3 className="text-lg font-medium mb-2">No files found</h3>
          <p className="text-muted-foreground">Try adjusting your search criteria</p>
        </div>
      )}
    </div>
  );
}

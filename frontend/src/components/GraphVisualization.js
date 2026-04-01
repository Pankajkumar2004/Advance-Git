import React, { useState } from 'react';
import { Network, GitBranch, ArrowRight, Circle, Square, Triangle } from 'lucide-react';
import { generateMockCommits } from '../lib/utils';

export function GraphVisualization() {
  const [selectedNode, setSelectedNode] = useState(null);
  const commits = generateMockCommits();
  
  const branches = {
    master: commits.filter(c => c.branch === 'master'),
    'feature/graph-viz': commits.filter(c => c.branch === 'feature/graph-viz'),
    'feature/diff-enhancement': commits.filter(c => c.branch === 'feature/diff-enhancement')
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Commit Graph</h1>
          <p className="text-muted-foreground">Graph-Based Branch Visualization</p>
        </div>
      </div>

      <div className="border rounded-lg p-6">
        <h2 className="text-lg font-semibold mb-4">Branch Relationships</h2>
        
        {/* Simplified graph visualization */}
        <div className="space-y-8">
          {Object.entries(branches).map(([branchName, branchCommits]) => (
            <div key={branchName} className="border rounded-lg p-4">
              <h3 className="font-medium mb-4 flex items-center">
                <GitBranch className="h-4 w-4 mr-2" />
                {branchName}
              </h3>
              
              <div className="flex items-center space-x-4">
                {branchCommits.map((commit, index) => (
                  <div key={commit.id} className="flex items-center">
                    <div className="text-center">
                      <div className="w-12 h-12 bg-primary rounded-full flex items-center justify-center mb-2">
                        <Network className="h-6 w-6 text-primary-foreground" />
                      </div>
                      <p className="text-xs font-mono">{commit.id}</p>
                      <p className="text-xs text-muted-foreground">{commit.message.substring(0, 20)}...</p>
                    </div>
                    {index < branchCommits.length - 1 && (
                      <ArrowRight className="h-4 w-4 text-muted-foreground mx-2" />
                    )}
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="border rounded-lg p-4">
        <h3 className="font-semibold mb-3">Graph Statistics</h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
          <div>
            <span className="text-muted-foreground">Total Nodes:</span>
            <span className="ml-2 font-medium">{commits.length}</span>
          </div>
          <div>
            <span className="text-muted-foreground">Total Edges:</span>
            <span className="ml-2 font-medium">{commits.length - 1}</span>
          </div>
          <div>
            <span className="text-muted-foreground">Graph Diameter:</span>
            <span className="ml-2 font-medium">12</span>
          </div>
          <div>
            <span className="text-muted-foreground">Cycles:</span>
            <span className="ml-2 font-medium text-green-600">None</span>
          </div>
        </div>
      </div>
    </div>
  );
}

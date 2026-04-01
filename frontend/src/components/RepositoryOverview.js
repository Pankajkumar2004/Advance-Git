import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { 
  GitBranch, 
  FileText, 
  Clock, 
  Users, 
  Activity,
  TrendingUp,
  Zap,
  Database,
  Search,
  Network,
  BarChart3
} from 'lucide-react';
import { generateMockCommits, generateMockPerformanceData, formatTimeAgo } from '../lib/utils';

export function RepositoryOverview() {
  const [selectedBranch, setSelectedBranch] = useState('master');
  const commits = generateMockCommits();
  const performanceData = generateMockPerformanceData();
  
  const recentCommits = commits.slice(0, 3);
  const branches = ['master', 'feature/graph-viz', 'feature/diff-enhancement', 'develop'];
  
  const dsaFeatures = [
    {
      icon: Database,
      title: 'AVL Tree',
      description: 'Balanced commit history with O(log n) operations',
      color: 'text-blue-600',
      bgColor: 'bg-blue-50'
    },
    {
      icon: Search,
      title: 'Trie Search',
      description: 'Instant prefix search with O(m) complexity',
      color: 'text-green-600',
      bgColor: 'bg-green-50'
    },
    {
      icon: Network,
      title: 'Graph Structure',
      description: 'Branch visualization and dependency analysis',
      color: 'text-purple-600',
      bgColor: 'bg-purple-50'
    },
    {
      icon: Zap,
      title: 'LRU Cache',
      description: '80%+ performance boost with intelligent caching',
      color: 'text-orange-600',
      bgColor: 'bg-orange-50'
    }
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Repository Overview</h1>
          <p className="text-muted-foreground">Advanced MiniGit with DSA Optimizations</p>
        </div>
        
        <div className="flex items-center space-x-4">
          <select 
            value={selectedBranch}
            onChange={(e) => setSelectedBranch(e.target.value)}
            className="px-4 py-2 border rounded-md bg-background"
          >
            {branches.map(branch => (
              <option key={branch} value={branch}>{branch}</option>
            ))}
          </select>
          
          <Link 
            to="/commits"
            className="px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90 transition-colors"
          >
            View All Commits
          </Link>
        </div>
      </div>

      {/* DSA Features Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {dsaFeatures.map((feature, index) => {
          const Icon = feature.icon;
          return (
            <div key={index} className="p-6 border rounded-lg">
              <div className={`w-12 h-12 ${feature.bgColor} rounded-lg flex items-center justify-center mb-4`}>
                <Icon className={`h-6 w-6 ${feature.color}`} />
              </div>
              <h3 className="font-semibold mb-2">{feature.title}</h3>
              <p className="text-sm text-muted-foreground">{feature.description}</p>
            </div>
          );
        })}
      </div>

      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-6 border rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">Total Commits</p>
              <p className="text-2xl font-bold">{commits.length}</p>
            </div>
            <GitBranch className="h-8 w-8 text-muted-foreground" />
          </div>
        </div>
        
        <div className="p-6 border rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">Active Branches</p>
              <p className="text-2xl font-bold">{branches.length}</p>
            </div>
            <Activity className="h-8 w-8 text-muted-foreground" />
          </div>
        </div>
        
        <div className="p-6 border rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">Cache Hit Rate</p>
              <p className="text-2xl font-bold">{performanceData.cache.hitRate}%</p>
            </div>
            <TrendingUp className="h-8 w-8 text-green-600" />
          </div>
        </div>
        
        <div className="p-6 border rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">Tree Operations</p>
              <p className="text-2xl font-bold">{performanceData.tree.operations}</p>
            </div>
            <Database className="h-8 w-8 text-blue-600" />
          </div>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Commits */}
        <div className="border rounded-lg">
          <div className="p-6 border-b">
            <h2 className="text-xl font-semibold flex items-center">
              <Clock className="h-5 w-5 mr-2" />
              Recent Commits
            </h2>
          </div>
          <div className="p-6 space-y-4">
            {recentCommits.map((commit) => (
              <div key={commit.id} className="flex items-start space-x-4">
                <div className="w-10 h-10 bg-primary/10 rounded-full flex items-center justify-center">
                  <GitBranch className="h-5 w-5 text-primary" />
                </div>
                <div className="flex-1">
                  <div className="flex items-center space-x-2">
                    <h3 className="font-medium">{commit.message}</h3>
                    <span className="text-xs px-2 py-1 bg-secondary rounded-md">
                      {commit.id}
                    </span>
                  </div>
                  <div className="flex items-center space-x-4 text-sm text-muted-foreground mt-1">
                    <span>{commit.author}</span>
                    <span>•</span>
                    <span>{formatTimeAgo(commit.timestamp)}</span>
                    <span>•</span>
                    <span>{commit.files.length} files</span>
                  </div>
                </div>
              </div>
            ))}
            
            <Link 
              to="/commits"
              className="text-primary hover:underline text-sm font-medium"
            >
              View all commits →
            </Link>
          </div>
        </div>

        {/* Performance Metrics */}
        <div className="border rounded-lg">
          <div className="p-6 border-b">
            <h2 className="text-xl font-semibold flex items-center">
              <BarChart3 className="h-5 w-5 mr-2" />
              Performance Metrics
            </h2>
          </div>
          <div className="p-6 space-y-4">
            <div className="flex justify-between items-center">
              <span className="text-sm font-medium">Cache Hit Rate</span>
              <div className="flex items-center space-x-2">
                <div className="w-32 bg-gray-200 rounded-full h-2">
                  <div 
                    className="bg-green-600 h-2 rounded-full" 
                    style={{ width: `${performanceData.cache.hitRate}%` }}
                  ></div>
                </div>
                <span className="text-sm font-medium">{performanceData.cache.hitRate}%</span>
              </div>
            </div>
            
            <div className="flex justify-between items-center">
              <span className="text-sm font-medium">Tree Balance</span>
              <div className="flex items-center space-x-2">
                <div className="w-32 bg-gray-200 rounded-full h-2">
                  <div 
                    className="bg-blue-600 h-2 rounded-full" 
                    style={{ width: `${performanceData.tree.balanceFactor * 100}%` }}
                  ></div>
                </div>
                <span className="text-sm font-medium">{(performanceData.tree.balanceFactor * 100).toFixed(0)}%</span>
              </div>
            </div>
            
            <div className="flex justify-between items-center">
              <span className="text-sm font-medium">Hash Table Load</span>
              <div className="flex items-center space-x-2">
                <div className="w-32 bg-gray-200 rounded-full h-2">
                  <div 
                    className="bg-purple-600 h-2 rounded-full" 
                    style={{ width: `${performanceData.hashTable.loadFactor * 100}%` }}
                  ></div>
                </div>
                <span className="text-sm font-medium">{(performanceData.hashTable.loadFactor * 100).toFixed(0)}%</span>
              </div>
            </div>
            
            <div className="flex justify-between items-center">
              <span className="text-sm font-medium">Search Response</span>
              <span className="text-sm font-medium text-green-600">
                {performanceData.search.avgResponseTime}ms
              </span>
            </div>
            
            <Link 
              to="/performance"
              className="text-primary hover:underline text-sm font-medium"
            >
              Detailed performance analysis →
            </Link>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="border rounded-lg p-6">
        <h2 className="text-xl font-semibold mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Link 
            to="/search"
            className="p-4 border rounded-lg hover:bg-accent transition-colors"
          >
            <Search className="h-6 w-6 mb-2 text-primary" />
            <h3 className="font-medium">Search Repository</h3>
            <p className="text-sm text-muted-foreground">Find commits and files instantly</p>
          </Link>
          
          <Link 
            to="/graph"
            className="p-4 border rounded-lg hover:bg-accent transition-colors"
          >
            <Network className="h-6 w-6 mb-2 text-primary" />
            <h3 className="font-medium">View Graph</h3>
            <p className="text-sm text-muted-foreground">Visualize branch relationships</p>
          </Link>
          
          <Link 
            to="/files"
            className="p-4 border rounded-lg hover:bg-accent transition-colors"
          >
            <FileText className="h-6 w-6 mb-2 text-primary" />
            <h3 className="font-medium">Browse Files</h3>
            <p className="text-sm text-muted-foreground">Explore repository structure</p>
          </Link>
        </div>
      </div>
    </div>
  );
}

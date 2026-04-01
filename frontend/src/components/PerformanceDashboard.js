import React from 'react';
import { BarChart3, TrendingUp, Zap, Database, Activity } from 'lucide-react';
import { generateMockPerformanceData } from '../lib/utils';

export function PerformanceDashboard() {
  const performanceData = generateMockPerformanceData();
  
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Performance Dashboard</h1>
          <p className="text-muted-foreground">Real-time DSA Performance Metrics</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-6 border rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">Cache Hit Rate</p>
              <p className="text-2xl font-bold text-green-600">{performanceData.cache.hitRate}%</p>
            </div>
            <Zap className="h-8 w-8 text-green-600" />
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
        
        <div className="p-6 border rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">Search Response</p>
              <p className="text-2xl font-bold">{performanceData.search.avgResponseTime}ms</p>
            </div>
            <TrendingUp className="h-8 w-8 text-purple-600" />
          </div>
        </div>
        
        <div className="p-6 border rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">Graph Nodes</p>
              <p className="text-2xl font-bold">{performanceData.graph.nodes}</p>
            </div>
            <Activity className="h-8 w-8 text-orange-600" />
          </div>
        </div>
      </div>

      <div className="border rounded-lg p-6">
        <h2 className="text-lg font-semibold mb-4 flex items-center">
          <BarChart3 className="h-5 w-5 mr-2" />
          Detailed Performance Analysis
        </h2>
        
        <div className="space-y-6">
          <div>
            <h3 className="font-medium mb-3">Cache Performance</h3>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
              <div>
                <span className="text-muted-foreground">Hit Rate:</span>
                <span className="ml-2 font-medium">{performanceData.cache.hitRate}%</span>
              </div>
              <div>
                <span className="text-muted-foreground">Total Accesses:</span>
                <span className="ml-2 font-medium">{performanceData.cache.totalAccesses}</span>
              </div>
              <div>
                <span className="text-muted-foreground">Hits:</span>
                <span className="ml-2 font-medium text-green-600">{performanceData.cache.hits}</span>
              </div>
              <div>
                <span className="text-muted-foreground">Misses:</span>
                <span className="ml-2 font-medium text-red-600">{performanceData.cache.misses}</span>
              </div>
            </div>
          </div>
          
          <div>
            <h3 className="font-medium mb-3">Tree Structure</h3>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
              <div>
                <span className="text-muted-foreground">Size:</span>
                <span className="ml-2 font-medium">{performanceData.tree.size}</span>
              </div>
              <div>
                <span className="text-muted-foreground">Height:</span>
                <span className="ml-2 font-medium">{performanceData.tree.height}</span>
              </div>
              <div>
                <span className="text-muted-foreground">Balance Factor:</span>
                <span className="ml-2 font-medium">{performanceData.tree.balanceFactor}</span>
              </div>
              <div>
                <span className="text-muted-foreground">Operations:</span>
                <span className="ml-2 font-medium">{performanceData.tree.operations}</span>
              </div>
            </div>
          </div>
          
          <div>
            <h3 className="font-medium mb-3">Hash Table</h3>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
              <div>
                <span className="text-muted-foreground">Load Factor:</span>
                <span className="ml-2 font-medium">{(performanceData.hashTable.loadFactor * 100).toFixed(0)}%</span>
              </div>
              <div>
                <span className="text-muted-foreground">Total Size:</span>
                <span className="ml-2 font-medium">{performanceData.hashTable.totalSize}</span>
              </div>
              <div>
                <span className="text-muted-foreground">Capacity:</span>
                <span className="ml-2 font-medium">{performanceData.hashTable.capacity}</span>
              </div>
              <div>
                <span className="text-muted-foreground">Max Chain:</span>
                <span className="ml-2 font-medium">{performanceData.hashTable.maxChainLength}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

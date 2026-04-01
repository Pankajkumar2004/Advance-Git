import React from 'react';
import { useParams } from 'react-router-dom';
import { GitBranch, ArrowRight, Plus, Minus, Edit } from 'lucide-react';

export function DiffViewer() {
  const { commit1, commit2 } = useParams();
  
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Diff Viewer</h1>
          <p className="text-muted-foreground">LCS-Based Advanced Diff Algorithm</p>
        </div>
      </div>

      <div className="border rounded-lg p-4">
        <div className="flex items-center space-x-4 mb-4">
          <div className="flex items-center space-x-2">
            <GitBranch className="h-4 w-4" />
            <span className="font-mono">{commit1}</span>
          </div>
          <ArrowRight className="h-4 w-4" />
          <div className="flex items-center space-x-2">
            <GitBranch className="h-4 w-4" />
            <span className="font-mono">{commit2}</span>
          </div>
        </div>
        
        <div className="space-y-4">
          <div className="border rounded-lg">
            <div className="p-3 bg-green-50 border-b flex items-center space-x-2">
              <Plus className="h-4 w-4 text-green-600" />
              <span className="font-medium text-green-800">Added Lines</span>
            </div>
            <div className="p-4 space-y-2">
              <div className="flex">
                <span className="w-8 text-right text-muted-foreground mr-4">42</span>
                <span className="text-green-600">+</span>
                <span className="ml-2">{"public class AVLCommitTree {"}</span>
              </div>
              <div className="flex">
                <span className="w-8 text-right text-muted-foreground mr-4">43</span>
                <span className="text-green-600">+</span>
                <span className="ml-2">{"// Advanced balanced tree implementation"}</span>
              </div>
              <div className="flex">
                <span className="w-8 text-right text-muted-foreground mr-4">44</span>
                <span className="text-green-600">+</span>
                <span className="ml-2">{"// O(log n) guaranteed operations"}</span>
              </div>
            </div>
          </div>
          
          <div className="border rounded-lg">
            <div className="p-3 bg-red-50 border-b flex items-center space-x-2">
              <Minus className="h-4 w-4 text-red-600" />
              <span className="font-medium text-red-800">Removed Lines</span>
            </div>
            <div className="p-4 space-y-2">
              <div className="flex">
                <span className="w-8 text-right text-muted-foreground mr-4">12</span>
                <span className="text-red-600">-</span>
                <span className="ml-2">{"public class SimpleTree {"}</span>
              </div>
              <div className="flex">
                <span className="w-8 text-right text-muted-foreground mr-4">13</span>
                <span className="text-red-600">-</span>
                <span className="ml-2">{"// Basic tree structure"}</span>
              </div>
            </div>
          </div>
          
          <div className="border rounded-lg">
            <div className="p-3 bg-yellow-50 border-b flex items-center space-x-2">
              <Edit className="h-4 w-4 text-yellow-600" />
              <span className="font-medium text-yellow-800">Modified Lines</span>
            </div>
            <div className="p-4 space-y-2">
              <div className="flex">
                <span className="w-8 text-right text-muted-foreground mr-4">25</span>
                <span className="text-red-600">-</span>
                <span className="ml-2 line-through">{"private Node root;"}</span>
              </div>
              <div className="flex">
                <span className="w-8 text-right text-muted-foreground mr-4">25</span>
                <span className="text-green-600">+</span>
                <span className="ml-2">private AVLNode root;</span>
              </div>
            </div>
          </div>
        </div>
        
        <div className="mt-6 pt-4 border-t">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
            <div>
              <span className="text-muted-foreground">Similarity:</span>
              <span className="ml-2 font-medium text-green-600">87.3%</span>
            </div>
            <div>
              <span className="text-muted-foreground">LCS Length:</span>
              <span className="ml-2 font-medium">156</span>
            </div>
            <div>
              <span className="text-muted-foreground">Time Complexity:</span>
              <span className="ml-2 font-medium font-mono">O(m×n)</span>
            </div>
            <div>
              <span className="text-muted-foreground">Processing Time:</span>
              <span className="ml-2 font-medium">12.4ms</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

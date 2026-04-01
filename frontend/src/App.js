import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Navbar } from './components/Navbar';
import { RepositoryOverview } from './components/RepositoryOverview';
import { CommitHistory } from './components/CommitHistory';
import { FileBrowser } from './components/FileBrowser';
import { SearchComponent } from './components/Search';
import { GraphVisualization } from './components/GraphVisualization';
import { PerformanceDashboard } from './components/PerformanceDashboard';
import { DiffViewer } from './components/DiffViewer';

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-background">
        <Navbar />
        <main className="container mx-auto px-4 py-8">
          <Routes>
            <Route path="/" element={<Navigate to="/repository" replace />} />
            <Route path="/repository" element={<RepositoryOverview />} />
            <Route path="/commits" element={<CommitHistory />} />
            <Route path="/files" element={<FileBrowser />} />
            <Route path="/search" element={<SearchComponent />} />
            <Route path="/graph" element={<GraphVisualization />} />
            <Route path="/performance" element={<PerformanceDashboard />} />
            <Route path="/diff/:commit1/:commit2" element={<DiffViewer />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;

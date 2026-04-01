import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { GitBranch, Search, FileText, BarChart3, Network, Zap, Home } from 'lucide-react';
import { cn } from '../lib/utils';

const navigation = [
  { name: 'Repository', href: '/repository', icon: Home },
  { name: 'Commits', href: '/commits', icon: GitBranch },
  { name: 'Files', href: '/files', icon: FileText },
  { name: 'Search', href: '/search', icon: Search },
  { name: 'Graph', href: '/graph', icon: Network },
  { name: 'Performance', href: '/performance', icon: Zap },
];

export function Navbar() {
  const location = useLocation();

  return (
    <nav className="border-b bg-card">
      <div className="container mx-auto px-4">
        <div className="flex h-16 items-center justify-between">
          <div className="flex items-center space-x-8">
            <Link to="/repository" className="flex items-center space-x-2">
              <GitBranch className="h-8 w-8 text-primary" />
              <div>
                <h1 className="text-xl font-bold">Advanced MiniGit</h1>
                <p className="text-xs text-muted-foreground">DSA Optimized</p>
              </div>
            </Link>
            
            <div className="hidden md:flex items-center space-x-1">
              {navigation.map((item) => {
                const Icon = item.icon;
                const isActive = location.pathname === item.href;
                
                return (
                  <Link
                    key={item.name}
                    to={item.href}
                    className={cn(
                      "flex items-center space-x-2 px-3 py-2 rounded-md text-sm font-medium transition-colors",
                      isActive
                        ? "bg-primary text-primary-foreground"
                        : "text-muted-foreground hover:text-foreground hover:bg-accent"
                    )}
                  >
                    <Icon className="h-4 w-4" />
                    <span>{item.name}</span>
                  </Link>
                );
              })}
            </div>
          </div>
          
          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2 text-sm text-muted-foreground">
              <div className="h-2 w-2 bg-green-500 rounded-full"></div>
              <span>Repository Active</span>
            </div>
            
            <div className="flex items-center space-x-2 px-3 py-1 bg-accent rounded-md">
              <BarChart3 className="h-4 w-4 text-primary" />
              <span className="text-sm font-medium">Live</span>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
}

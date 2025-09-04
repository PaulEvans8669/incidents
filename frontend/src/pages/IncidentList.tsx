import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Search, Filter, AlertTriangle } from 'lucide-react';
import { incidentApi } from '../lib/api';
import { IncidentCard } from '../components/IncidentCard';
import { IncidentSummary } from '../types/incident';
import { cn } from '../lib/utils';

export function IncidentList() {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [severityFilter, setSeverityFilter] = useState<string>('all');

  const { data: incidents = [], isLoading, error } = useQuery({
    queryKey: ['incidents'],
    queryFn: incidentApi.getIncidentSummaries,
    refetchInterval: 30000, // Refresh every 30 seconds
  });

  const filteredIncidents = incidents.filter((incident: IncidentSummary) => {
    const matchesSearch = incident.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         incident.summary.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         incident.tags.some(tag => tag.toLowerCase().includes(searchTerm.toLowerCase()));
    
    const matchesStatus = statusFilter === 'all' || incident.status === statusFilter;
    const matchesSeverity = severityFilter === 'all' || incident.severity.toLowerCase() === severityFilter;
    
    return matchesSearch && matchesStatus && matchesSeverity;
  });

  const statusCounts = incidents.reduce((acc, incident) => {
    acc[incident.status] = (acc[incident.status] || 0) + 1;
    return acc;
  }, {} as Record<string, number>);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <AlertTriangle className="h-12 w-12 text-red-500 mx-auto mb-4" />
        <h3 className="text-lg font-medium text-gray-900 mb-2">Failed to load incidents</h3>
        <p className="text-gray-600">Please check if the backend server is running on port 8080</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Incidents</h1>
          <p className="text-gray-600">
            {incidents.length} total incidents
            {statusCounts.OPEN && ` • ${statusCounts.OPEN} open`}
            {statusCounts.IN_PROGRESS && ` • ${statusCounts.IN_PROGRESS} in progress`}
          </p>
        </div>
      </div>

      {/* Filters */}
      <div className="card p-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search incidents..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="input pl-10"
            />
          </div>

          {/* Status Filter */}
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="input"
          >
            <option value="all">All Statuses</option>
            <option value="OPEN">Open ({statusCounts.OPEN || 0})</option>
            <option value="IN_PROGRESS">In Progress ({statusCounts.IN_PROGRESS || 0})</option>
            <option value="RESOLVED">Resolved ({statusCounts.RESOLVED || 0})</option>
            <option value="CLOSED">Closed ({statusCounts.CLOSED || 0})</option>
          </select>

          {/* Severity Filter */}
          <select
            value={severityFilter}
            onChange={(e) => setSeverityFilter(e.target.value)}
            className="input"
          >
            <option value="all">All Severities</option>
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
            <option value="critical">Critical</option>
          </select>
        </div>
      </div>

      {/* Results */}
      <div className="space-y-4">
        {filteredIncidents.length === 0 ? (
          <div className="text-center py-12">
            <AlertTriangle className="h-12 w-12 text-gray-300 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">No incidents found</h3>
            <p className="text-gray-600">
              {searchTerm || statusFilter !== 'all' || severityFilter !== 'all'
                ? 'Try adjusting your filters'
                : 'Create your first incident to get started'
              }
            </p>
          </div>
        ) : (
          <div className="grid gap-4">
            {filteredIncidents.map((incident) => (
              <IncidentCard key={incident.id} incident={incident} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
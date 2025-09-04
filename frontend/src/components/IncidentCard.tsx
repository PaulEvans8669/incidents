import { Link } from 'react-router-dom';
import { Clock, User, Tag } from 'lucide-react';
import { IncidentSummary } from '../types/incident';
import { formatRelativeTime, getSeverityColor, getStatusColor, cn } from '../lib/utils';

interface IncidentCardProps {
  incident: IncidentSummary;
}

export function IncidentCard({ incident }: IncidentCardProps) {
  return (
    <Link
      to={`/incident/${incident.id}`}
      className="block card p-6 hover:shadow-md transition-shadow duration-200 animate-fade-in"
    >
      <div className="flex items-start justify-between mb-3">
        <h3 className="text-lg font-semibold text-gray-900 line-clamp-2">
          {incident.title}
        </h3>
        <div className="flex items-center space-x-2 ml-4">
          <span className={cn('badge border', getSeverityColor(incident.severity))}>
            {incident.severity}
          </span>
          <span className={cn('badge border', getStatusColor(incident.status))}>
            {incident.status.replace('_', ' ')}
          </span>
        </div>
      </div>
      
      <p className="text-gray-600 mb-4 line-clamp-2">
        {incident.summary}
      </p>
      
      <div className="flex items-center justify-between text-sm text-gray-500">
        <div className="flex items-center space-x-4">
          <div className="flex items-center">
            <User className="h-4 w-4 mr-1" />
            {incident.createdBy}
          </div>
          <div className="flex items-center">
            <Clock className="h-4 w-4 mr-1" />
            {formatRelativeTime(incident.createdAt)}
          </div>
        </div>
        
        {incident.tags.length > 0 && (
          <div className="flex items-center">
            <Tag className="h-4 w-4 mr-1" />
            <span className="text-xs">
              {incident.tags.slice(0, 2).join(', ')}
              {incident.tags.length > 2 && ` +${incident.tags.length - 2}`}
            </span>
          </div>
        )}
      </div>
      
      {incident.resolvedAt && (
        <div className="mt-3 pt-3 border-t border-gray-100">
          <p className="text-sm text-green-600">
            Resolved {formatRelativeTime(incident.resolvedAt)}
            {incident.resolutionNote && `: ${incident.resolutionNote}`}
          </p>
        </div>
      )}
    </Link>
  );
}
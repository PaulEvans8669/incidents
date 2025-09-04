import { useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ArrowLeft, Edit2, Save, X, Trash2, CheckCircle } from 'lucide-react';
import { incidentApi } from '../lib/api';
import { Timeline } from '../components/Timeline';
import { StatusSelector } from '../components/StatusSelector';
import { SeveritySelector } from '../components/SeveritySelector';
import { TagInput } from '../components/TagInput';
import { Incident, IncidentStatus, TimelineEvent, Note } from '../types/incident';
import { formatDate, getSeverityColor, getStatusColor, cn, generateId } from '../lib/utils';

export function IncidentDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  
  const [isEditing, setIsEditing] = useState(false);
  const [editedIncident, setEditedIncident] = useState<Incident | null>(null);
  const [resolutionNote, setResolutionNote] = useState('');

  const { data: incident, isLoading, error } = useQuery({
    queryKey: ['incident', id],
    queryFn: () => incidentApi.getIncident(id!),
    enabled: !!id,
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, updates }: { id: string; updates: Partial<Incident> }) =>
      incidentApi.updateIncident(id, updates),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['incident', id] });
      queryClient.invalidateQueries({ queryKey: ['incidents'] });
      setIsEditing(false);
      setEditedIncident(null);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: incidentApi.deleteIncident,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['incidents'] });
      navigate('/');
    },
  });

  const handleEdit = () => {
    setEditedIncident({ ...incident! });
    setIsEditing(true);
  };

  const handleSave = () => {
    if (editedIncident) {
      updateMutation.mutate({
        id: editedIncident.id,
        updates: {
          title: editedIncident.title,
          summary: editedIncident.summary,
          severity: editedIncident.severity,
          status: editedIncident.status,
          tags: editedIncident.tags,
          resolutionNote: editedIncident.resolutionNote,
        },
      });
    }
  };

  const handleCancel = () => {
    setEditedIncident(null);
    setIsEditing(false);
  };

  const handleStatusChange = (status: IncidentStatus) => {
    if (editedIncident) {
      const updates = { ...editedIncident, status };
      
      // If resolving, set resolvedAt timestamp
      if (status === 'RESOLVED' && editedIncident.status !== 'RESOLVED') {
        updates.resolvedAt = new Date().toISOString();
      }
      
      setEditedIncident(updates);
    }
  };

  const handleResolve = () => {
    if (incident && resolutionNote.trim()) {
      updateMutation.mutate({
        id: incident.id,
        updates: {
          status: 'RESOLVED',
          resolutionNote: resolutionNote.trim(),
          resolvedAt: new Date().toISOString(),
        },
      });
      setResolutionNote('');
    }
  };

  const handleAddEvent = (event: Omit<TimelineEvent, 'id'>) => {
    if (incident) {
      const newEvent = { ...event, id: generateId() };
      updateMutation.mutate({
        id: incident.id,
        updates: {
          timeline: [...incident.timeline, newEvent],
        },
      });
    }
  };

  const handleAddNote = (note: Omit<Note, 'id'>) => {
    if (incident) {
      const newNote = { ...note, id: generateId() };
      updateMutation.mutate({
        id: incident.id,
        updates: {
          notes: [...incident.notes, newNote],
        },
      });
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (error || !incident) {
    return (
      <div className="text-center py-12">
        <h3 className="text-lg font-medium text-gray-900 mb-2">Incident not found</h3>
        <Link to="/" className="text-primary-600 hover:text-primary-700">
          ← Back to incidents
        </Link>
      </div>
    );
  }

  const currentIncident = editedIncident || incident;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Link
            to="/"
            className="inline-flex items-center text-gray-600 hover:text-gray-900 transition-colors"
          >
            <ArrowLeft className="h-4 w-4 mr-1" />
            Back to incidents
          </Link>
        </div>
        <div className="flex items-center space-x-2">
          {!isEditing ? (
            <>
              <button onClick={handleEdit} className="btn-secondary btn-sm">
                <Edit2 className="h-4 w-4 mr-1" />
                Edit
              </button>
              <button
                onClick={() => deleteMutation.mutate(incident.id)}
                className="btn-sm bg-red-600 text-white hover:bg-red-700 px-3 py-1.5"
                disabled={deleteMutation.isPending}
              >
                <Trash2 className="h-4 w-4 mr-1" />
                Delete
              </button>
            </>
          ) : (
            <>
              <button
                onClick={handleSave}
                disabled={updateMutation.isPending}
                className="btn-primary btn-sm"
              >
                <Save className="h-4 w-4 mr-1" />
                Save
              </button>
              <button onClick={handleCancel} className="btn-secondary btn-sm">
                <X className="h-4 w-4 mr-1" />
                Cancel
              </button>
            </>
          )}
        </div>
      </div>

      {/* Incident Details */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          {/* Main Info */}
          <div className="card p-6">
            <div className="space-y-4">
              {isEditing ? (
                <>
                  <div>
                    <label className="text-sm font-medium text-gray-700">Title</label>
                    <input
                      type="text"
                      value={currentIncident.title}
                      onChange={(e) => setEditedIncident({ ...currentIncident, title: e.target.value })}
                      className="input mt-1"
                    />
                  </div>
                  <div>
                    <label className="text-sm font-medium text-gray-700">Summary</label>
                    <textarea
                      value={currentIncident.summary}
                      onChange={(e) => setEditedIncident({ ...currentIncident, summary: e.target.value })}
                      className="textarea mt-1"
                      rows={3}
                    />
                  </div>
                </>
              ) : (
                <>
                  <div>
                    <h1 className="text-2xl font-bold text-gray-900">{currentIncident.title}</h1>
                    <p className="text-gray-600 mt-2">{currentIncident.summary}</p>
                  </div>
                </>
              )}
            </div>
          </div>

          {/* Timeline */}
          <div className="card p-6">
            <Timeline
              events={currentIncident.timeline}
              notes={currentIncident.notes}
              onAddEvent={handleAddEvent}
              onAddNote={handleAddNote}
              isEditable={!isEditing}
            />
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Status & Severity */}
          <div className="card p-6 space-y-4">
            {isEditing ? (
              <>
                <StatusSelector
                  value={currentIncident.status}
                  onChange={handleStatusChange}
                />
                <SeveritySelector
                  value={currentIncident.severity}
                  onChange={(severity) => setEditedIncident({ ...currentIncident, severity })}
                />
              </>
            ) : (
              <>
                <div>
                  <label className="text-sm font-medium text-gray-700">Status</label>
                  <div className="mt-1">
                    <span className={cn('badge border', getStatusColor(currentIncident.status))}>
                      {currentIncident.status.replace('_', ' ')}
                    </span>
                  </div>
                </div>
                <div>
                  <label className="text-sm font-medium text-gray-700">Severity</label>
                  <div className="mt-1">
                    <span className={cn('badge border', getSeverityColor(currentIncident.severity))}>
                      {currentIncident.severity}
                    </span>
                  </div>
                </div>
              </>
            )}
          </div>

          {/* Quick Resolve */}
          {!isEditing && currentIncident.status !== 'RESOLVED' && currentIncident.status !== 'CLOSED' && (
            <div className="card p-6">
              <h3 className="font-medium text-gray-900 mb-3">Quick Resolve</h3>
              <textarea
                value={resolutionNote}
                onChange={(e) => setResolutionNote(e.target.value)}
                placeholder="Add resolution notes..."
                className="textarea mb-3"
                rows={3}
              />
              <button
                onClick={handleResolve}
                disabled={!resolutionNote.trim() || updateMutation.isPending}
                className="btn-primary w-full"
              >
                <CheckCircle className="h-4 w-4 mr-2" />
                Mark as Resolved
              </button>
            </div>
          )}

          {/* Tags */}
          <div className="card p-6">
            <TagInput
              tags={currentIncident.tags}
              onChange={(tags) => isEditing && setEditedIncident({ ...currentIncident, tags })}
              disabled={!isEditing}
            />
          </div>

          {/* Metadata */}
          <div className="card p-6 space-y-3">
            <h3 className="font-medium text-gray-900">Details</h3>
            <div className="space-y-2 text-sm">
              <div>
                <span className="text-gray-500">Created by:</span>
                <span className="ml-2 text-gray-900">{currentIncident.createdBy}</span>
              </div>
              <div>
                <span className="text-gray-500">Created:</span>
                <span className="ml-2 text-gray-900">{formatDate(currentIncident.createdAt)}</span>
              </div>
              {currentIncident.updatedAt && (
                <div>
                  <span className="text-gray-500">Updated:</span>
                  <span className="ml-2 text-gray-900">{formatDate(currentIncident.updatedAt)}</span>
                </div>
              )}
              {currentIncident.resolvedAt && (
                <div>
                  <span className="text-gray-500">Resolved:</span>
                  <span className="ml-2 text-gray-900">{formatDate(currentIncident.resolvedAt)}</span>
                </div>
              )}
            </div>
            
            {currentIncident.resolutionNote && (
              <div className="mt-4 pt-4 border-t border-gray-200">
                <span className="text-sm font-medium text-gray-700">Resolution Note:</span>
                <p className="mt-1 text-sm text-gray-900">{currentIncident.resolutionNote}</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
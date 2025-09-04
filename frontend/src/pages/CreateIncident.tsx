import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ArrowLeft, Save, AlertTriangle } from 'lucide-react';
import { incidentApi } from '../lib/api';
import { StatusSelector } from '../components/StatusSelector';
import { SeveritySelector } from '../components/SeveritySelector';
import { TagInput } from '../components/TagInput';
import { CreateIncidentRequest, IncidentStatus } from '../types/incident';

export function CreateIncident() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  
  const [formData, setFormData] = useState({
    title: '',
    summary: '',
    severity: 'Medium',
    status: 'OPEN' as IncidentStatus,
    tags: [] as string[],
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const createMutation = useMutation({
    mutationFn: incidentApi.createIncident,
    onSuccess: (newIncident) => {
      queryClient.invalidateQueries({ queryKey: ['incidents'] });
      navigate(`/incident/${newIncident.id}`);
    },
    onError: (error: any) => {
      if (error.response?.data?.details) {
        const fieldErrors: Record<string, string> = {};
        error.response.data.details.forEach((detail: string) => {
          const [field, message] = detail.split(': ');
          fieldErrors[field] = message;
        });
        setErrors(fieldErrors);
      }
    },
  });

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    
    if (!formData.title.trim()) {
      newErrors.title = 'Title is required';
    }
    if (!formData.summary.trim()) {
      newErrors.summary = 'Summary is required';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    const incidentData: CreateIncidentRequest = {
      ...formData,
      createdBy: 'system',
      createdAt: new Date().toISOString(),
      timeline: [{
        timestamp: new Date().toISOString(),
        description: 'Incident created',
        actor: 'system',
      }],
      notes: [],
    };

    createMutation.mutate(incidentData);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <button
            onClick={() => navigate('/')}
            className="inline-flex items-center text-gray-600 hover:text-gray-900 transition-colors"
          >
            <ArrowLeft className="h-4 w-4 mr-1" />
            Back to incidents
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <form onSubmit={handleSubmit} className="card p-6 space-y-6">
            <div className="flex items-center space-x-3 mb-6">
              <AlertTriangle className="h-6 w-6 text-primary-600" />
              <h1 className="text-2xl font-bold text-gray-900">Create New Incident</h1>
            </div>

            {/* Title */}
            <div>
              <label className="text-sm font-medium text-gray-700">Title *</label>
              <input
                type="text"
                value={formData.title}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                className={cn('input mt-1', errors.title && 'border-red-500')}
                placeholder="Brief description of the incident"
              />
              {errors.title && (
                <p className="mt-1 text-sm text-red-600">{errors.title}</p>
              )}
            </div>

            {/* Summary */}
            <div>
              <label className="text-sm font-medium text-gray-700">Summary *</label>
              <textarea
                value={formData.summary}
                onChange={(e) => setFormData({ ...formData, summary: e.target.value })}
                className={cn('textarea mt-1', errors.summary && 'border-red-500')}
                rows={4}
                placeholder="Detailed description of what happened, impact, and any initial observations"
              />
              {errors.summary && (
                <p className="mt-1 text-sm text-red-600">{errors.summary}</p>
              )}
            </div>

            {/* Tags */}
            <TagInput
              tags={formData.tags}
              onChange={(tags) => setFormData({ ...formData, tags })}
              placeholder="Add relevant tags (e.g., database, api, frontend)"
            />

            {/* Submit */}
            <div className="flex justify-end space-x-3 pt-4 border-t border-gray-200">
              <button
                type="button"
                onClick={() => navigate('/')}
                className="btn-secondary"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={createMutation.isPending}
                className="btn-primary"
              >
                <Save className="h-4 w-4 mr-2" />
                {createMutation.isPending ? 'Creating...' : 'Create Incident'}
              </button>
            </div>
          </form>
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          <div className="card p-6">
            <SeveritySelector
              value={formData.severity}
              onChange={(severity) => setFormData({ ...formData, severity })}
            />
          </div>

          <div className="card p-6">
            <StatusSelector
              value={formData.status}
              onChange={(status) => setFormData({ ...formData, status })}
            />
          </div>

          {/* Preview */}
          <div className="card p-6">
            <h3 className="font-medium text-gray-900 mb-3">Preview</h3>
            <div className="space-y-2 text-sm">
              <div className="flex items-center justify-between">
                <span className="text-gray-500">Severity:</span>
                <span className={cn('badge border', getSeverityColor(formData.severity))}>
                  {formData.severity}
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-500">Status:</span>
                <span className={cn('badge border', getStatusColor(formData.status))}>
                  {formData.status.replace('_', ' ')}
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-500">Created by:</span>
                <span className="text-gray-900">system</span>
              </div>
              {formData.tags.length > 0 && (
                <div>
                  <span className="text-gray-500">Tags:</span>
                  <div className="flex flex-wrap gap-1 mt-1">
                    {formData.tags.map((tag, index) => (
                      <span key={index} className="badge bg-gray-100 text-gray-800">
                        {tag}
                      </span>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
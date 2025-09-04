export interface Incident {
  id: string;
  title: string;
  summary: string;
  severity: string;
  status: IncidentStatus;
  createdBy: string;
  createdAt: string;
  updatedAt?: string;
  resolutionNote?: string;
  resolvedAt?: string;
  timeline: TimelineEvent[];
  notes: Note[];
  tags: string[];
}

export interface IncidentSummary {
  id: string;
  title: string;
  summary: string;
  severity: string;
  status: IncidentStatus;
  createdBy: string;
  createdAt: string;
  resolutionNote?: string;
  resolvedAt?: string;
  tags: string[];
}

export interface TimelineEvent {
  id: string;
  timestamp: string;
  description: string;
  actor: string;
}

export interface Note {
  id: string;
  author: string;
  note: string;
  timestamp: string;
}

export type IncidentStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';

export interface CreateIncidentRequest {
  title: string;
  summary: string;
  severity: string;
  status: IncidentStatus;
  createdBy: string;
  createdAt: string;
  tags: string[];
  timeline: Omit<TimelineEvent, 'id'>[];
  notes: Omit<Note, 'id'>[];
}
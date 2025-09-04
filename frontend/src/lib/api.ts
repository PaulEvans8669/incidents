import axios from 'axios';
import { Incident, IncidentSummary, CreateIncidentRequest } from '../types/incident';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const incidentApi = {
  // Get all incidents (summaries)
  getIncidentSummaries: async (): Promise<IncidentSummary[]> => {
    const response = await api.get('/incidents/summaries');
    return response.data;
  },

  // Get full incident details
  getIncident: async (id: string): Promise<Incident> => {
    const response = await api.get(`/incidents/${id}`);
    return response.data;
  },

  // Create new incident
  createIncident: async (incident: CreateIncidentRequest): Promise<Incident> => {
    const response = await api.post('/incidents', incident);
    return response.data;
  },

  // Update incident (patch)
  updateIncident: async (id: string, updates: Partial<Incident>): Promise<Incident> => {
    const response = await api.patch(`/incidents/${id}`, updates);
    return response.data;
  },

  // Delete incident
  deleteIncident: async (id: string): Promise<void> => {
    await api.delete(`/incidents/${id}`);
  },
};
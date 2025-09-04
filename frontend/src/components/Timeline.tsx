import { useState } from 'react';
import { Plus, Clock, MessageSquare, AlertCircle, CheckCircle } from 'lucide-react';
import { TimelineEvent, Note } from '../types/incident';
import { formatDate, formatRelativeTime, cn, generateId } from '../lib/utils';

interface TimelineProps {
  events: TimelineEvent[];
  notes: Note[];
  onAddEvent: (event: Omit<TimelineEvent, 'id'>) => void;
  onAddNote: (note: Omit<Note, 'id'>) => void;
  isEditable?: boolean;
}

interface TimelineItem {
  id: string;
  type: 'event' | 'note';
  timestamp: string;
  content: string;
  actor: string;
  isEvent?: boolean;
}

export function Timeline({ events, notes, onAddEvent, onAddNote, isEditable = true }: TimelineProps) {
  const [showEventForm, setShowEventForm] = useState(false);
  const [showNoteForm, setShowNoteForm] = useState(false);
  const [eventDescription, setEventDescription] = useState('');
  const [noteContent, setNoteContent] = useState('');

  // Combine and sort timeline items
  const timelineItems: TimelineItem[] = [
    ...events.map(event => ({
      id: event.id,
      type: 'event' as const,
      timestamp: event.timestamp,
      content: event.description,
      actor: event.actor,
      isEvent: true,
    })),
    ...notes.map(note => ({
      id: note.id,
      type: 'note' as const,
      timestamp: note.timestamp,
      content: note.note,
      actor: note.author,
      isEvent: false,
    })),
  ].sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());

  const handleAddEvent = () => {
    if (eventDescription.trim()) {
      onAddEvent({
        timestamp: new Date().toISOString(),
        description: eventDescription.trim(),
        actor: 'system',
      });
      setEventDescription('');
      setShowEventForm(false);
    }
  };

  const handleAddNote = () => {
    if (noteContent.trim()) {
      onAddNote({
        author: 'system',
        note: noteContent.trim(),
        timestamp: new Date().toISOString(),
      });
      setNoteContent('');
      setShowNoteForm(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold text-gray-900">Timeline</h3>
        {isEditable && (
          <div className="flex space-x-2">
            <button
              onClick={() => setShowEventForm(true)}
              className="btn-secondary btn-sm"
            >
              <AlertCircle className="h-4 w-4 mr-1" />
              Add Event
            </button>
            <button
              onClick={() => setShowNoteForm(true)}
              className="btn-secondary btn-sm"
            >
              <MessageSquare className="h-4 w-4 mr-1" />
              Add Note
            </button>
          </div>
        )}
      </div>

      {/* Add Event Form */}
      {showEventForm && (
        <div className="card p-4 border-l-4 border-l-blue-500 animate-slide-up">
          <h4 className="font-medium text-gray-900 mb-3">Add Timeline Event</h4>
          <textarea
            value={eventDescription}
            onChange={(e) => setEventDescription(e.target.value)}
            placeholder="Describe what happened (e.g., 'New issue discovered related to database connection')"
            className="textarea mb-3"
            rows={3}
          />
          <div className="flex space-x-2">
            <button onClick={handleAddEvent} className="btn-primary btn-sm">
              Add Event
            </button>
            <button
              onClick={() => {
                setShowEventForm(false);
                setEventDescription('');
              }}
              className="btn-secondary btn-sm"
            >
              Cancel
            </button>
          </div>
        </div>
      )}

      {/* Add Note Form */}
      {showNoteForm && (
        <div className="card p-4 border-l-4 border-l-green-500 animate-slide-up">
          <h4 className="font-medium text-gray-900 mb-3">Add Side Note</h4>
          <textarea
            value={noteContent}
            onChange={(e) => setNoteContent(e.target.value)}
            placeholder="Add a note or suggestion (e.g., 'User X suggested we check the cache configuration')"
            className="textarea mb-3"
            rows={3}
          />
          <div className="flex space-x-2">
            <button onClick={handleAddNote} className="btn-primary btn-sm">
              Add Note
            </button>
            <button
              onClick={() => {
                setShowNoteForm(false);
                setNoteContent('');
              }}
              className="btn-secondary btn-sm"
            >
              Cancel
            </button>
          </div>
        </div>
      )}

      {/* Timeline */}
      <div className="relative">
        {/* Timeline line */}
        <div className="absolute left-8 top-0 bottom-0 w-0.5 bg-gray-200"></div>
        
        <div className="space-y-6">
          {timelineItems.map((item, index) => (
            <div key={item.id} className="relative flex items-start space-x-4">
              {/* Timeline dot */}
              <div className={cn(
                'relative z-10 flex items-center justify-center w-8 h-8 rounded-full border-2 bg-white',
                item.isEvent 
                  ? 'border-blue-500 text-blue-600' 
                  : 'border-green-500 text-green-600'
              )}>
                {item.isEvent ? (
                  <AlertCircle className="h-4 w-4" />
                ) : (
                  <MessageSquare className="h-4 w-4" />
                )}
              </div>
              
              {/* Content */}
              <div className="flex-1 min-w-0">
                <div className={cn(
                  'card p-4 border-l-4',
                  item.isEvent 
                    ? 'border-l-blue-500 bg-blue-50/50' 
                    : 'border-l-green-500 bg-green-50/50'
                )}>
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center space-x-2">
                      <span className={cn(
                        'badge',
                        item.isEvent 
                          ? 'bg-blue-100 text-blue-800' 
                          : 'bg-green-100 text-green-800'
                      )}>
                        {item.isEvent ? 'Event' : 'Note'}
                      </span>
                      <span className="text-sm text-gray-600">by {item.actor}</span>
                    </div>
                    <div className="text-xs text-gray-500">
                      {formatRelativeTime(item.timestamp)}
                    </div>
                  </div>
                  <p className="text-gray-900 leading-relaxed">{item.content}</p>
                  <div className="mt-2 text-xs text-gray-500">
                    {formatDate(item.timestamp)}
                  </div>
                </div>
              </div>
            </div>
          ))}
          
          {timelineItems.length === 0 && (
            <div className="text-center py-8 text-gray-500">
              <Clock className="h-12 w-12 mx-auto mb-3 text-gray-300" />
              <p>No timeline events yet</p>
              {isEditable && (
                <p className="text-sm">Add events and notes to track the incident progress</p>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
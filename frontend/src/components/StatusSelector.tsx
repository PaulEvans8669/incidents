import { IncidentStatus } from '../types/incident';
import { getStatusColor, cn } from '../lib/utils';

interface StatusSelectorProps {
  value: IncidentStatus;
  onChange: (status: IncidentStatus) => void;
  disabled?: boolean;
}

const statusOptions: { value: IncidentStatus; label: string }[] = [
  { value: 'OPEN', label: 'Open' },
  { value: 'IN_PROGRESS', label: 'In Progress' },
  { value: 'RESOLVED', label: 'Resolved' },
  { value: 'CLOSED', label: 'Closed' },
];

export function StatusSelector({ value, onChange, disabled }: StatusSelectorProps) {
  return (
    <div className="space-y-2">
      <label className="text-sm font-medium text-gray-700">Status</label>
      <div className="grid grid-cols-2 gap-2">
        {statusOptions.map((option) => (
          <button
            key={option.value}
            type="button"
            disabled={disabled}
            onClick={() => onChange(option.value)}
            className={cn(
              'p-3 rounded-lg border-2 text-sm font-medium transition-all',
              value === option.value
                ? 'border-primary-500 bg-primary-50 text-primary-700'
                : 'border-gray-200 bg-white text-gray-700 hover:border-gray-300',
              disabled && 'opacity-50 cursor-not-allowed'
            )}
          >
            <div className="flex items-center justify-center space-x-2">
              <div className={cn('w-2 h-2 rounded-full', getStatusColor(option.value).split(' ')[0])} />
              <span>{option.label}</span>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}
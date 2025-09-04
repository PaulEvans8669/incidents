import { getSeverityColor, cn } from '../lib/utils';

interface SeveritySelectorProps {
  value: string;
  onChange: (severity: string) => void;
  disabled?: boolean;
}

const severityOptions = [
  { value: 'Low', label: 'Low', description: 'Minor issue' },
  { value: 'Medium', label: 'Medium', description: 'Moderate impact' },
  { value: 'High', label: 'High', description: 'Significant impact' },
  { value: 'Critical', label: 'Critical', description: 'Service down' },
];

export function SeveritySelector({ value, onChange, disabled }: SeveritySelectorProps) {
  return (
    <div className="space-y-2">
      <label className="text-sm font-medium text-gray-700">Severity</label>
      <div className="grid grid-cols-2 gap-2">
        {severityOptions.map((option) => (
          <button
            key={option.value}
            type="button"
            disabled={disabled}
            onClick={() => onChange(option.value)}
            className={cn(
              'p-3 rounded-lg border-2 text-sm font-medium transition-all text-left',
              value === option.value
                ? 'border-primary-500 bg-primary-50 text-primary-700'
                : 'border-gray-200 bg-white text-gray-700 hover:border-gray-300',
              disabled && 'opacity-50 cursor-not-allowed'
            )}
          >
            <div className="flex items-center space-x-2 mb-1">
              <div className={cn('w-2 h-2 rounded-full', getSeverityColor(option.value).split(' ')[0])} />
              <span className="font-medium">{option.label}</span>
            </div>
            <div className="text-xs text-gray-500">{option.description}</div>
          </button>
        ))}
      </div>
    </div>
  );
}
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';
import { format, formatDistanceToNow } from 'date-fns';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatDate(date: string | Date) {
  return format(new Date(date), 'MMM dd, yyyy HH:mm');
}

export function formatRelativeTime(date: string | Date) {
  return formatDistanceToNow(new Date(date), { addSuffix: true });
}

export function getSeverityColor(severity: string) {
  const colors = {
    low: 'bg-green-100 text-green-800 border-green-200',
    medium: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    high: 'bg-red-100 text-red-800 border-red-200',
    critical: 'bg-red-200 text-red-900 border-red-300',
  };
  return colors[severity.toLowerCase() as keyof typeof colors] || 'bg-gray-100 text-gray-800 border-gray-200';
}

export function getStatusColor(status: string) {
  const colors = {
    open: 'bg-red-100 text-red-800 border-red-200',
    'in_progress': 'bg-yellow-100 text-yellow-800 border-yellow-200',
    resolved: 'bg-green-100 text-green-800 border-green-200',
    closed: 'bg-gray-100 text-gray-800 border-gray-200',
  };
  return colors[status.toLowerCase() as keyof typeof colors] || 'bg-gray-100 text-gray-800 border-gray-200';
}

export function generateId() {
  return Math.random().toString(36).substr(2, 9);
}
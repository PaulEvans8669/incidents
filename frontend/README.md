# Incident Management Frontend

A React-based frontend for the Incident Management System built with:

- **React 18** with TypeScript
- **Vite** for fast development
- **TailwindCSS** for styling
- **React Query** for data fetching
- **React Router** for navigation
- **Lucide React** for icons

## Features

- **Incident List**: View all incidents with filtering and search
- **Incident Details**: Full incident view with timeline
- **Timeline Management**: Add events and notes to track incident progress
- **Status Management**: Update incident status and resolution
- **Responsive Design**: Works on desktop and mobile

## Getting Started

1. Start the backend server (Spring Boot on port 8080)
2. Install dependencies: `npm install`
3. Start the development server: `npm run dev`
4. Open http://localhost:3000

## API Integration

The frontend connects to the Spring Boot backend running on `http://localhost:8080/api`. Make sure the backend is running before starting the frontend.

## Timeline Features

- **Events**: Major milestones like "Issue discovered" or "Fix deployed"
- **Notes**: Side notes and suggestions from team members
- **Real-time updates**: Timeline updates automatically when changes are made
- **Visual distinction**: Events and notes are visually differentiated

## Status Workflow

1. **Open**: New incident created
2. **In Progress**: Someone is actively working on it
3. **Resolved**: Issue has been fixed
4. **Closed**: Incident is completely closed

## User Experience

- Clean, modern interface inspired by industry-leading tools
- Smooth animations and transitions
- Intuitive navigation and clear visual hierarchy
- Responsive design for all screen sizes
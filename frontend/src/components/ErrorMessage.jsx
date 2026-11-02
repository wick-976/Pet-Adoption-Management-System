import React from 'react';
import { AlertTriangle, RefreshCw } from 'lucide-react';

export const ErrorMessage = ({ message = 'An unexpected error occurred.', onRetry }) => {
  return (
    <div className="alert alert-error" role="alert" style={{ maxWidth: '600px', margin: '1.5rem auto' }}>
      <AlertTriangle size={22} style={{ flexShrink: 0, marginTop: '2px' }} />
      <div style={{ flex: 1 }}>
        <h4 style={{ fontSize: '0.9375rem', fontWeight: 600, color: 'var(--rose-900)', marginBottom: '0.25rem' }}>
          Action Failed
        </h4>
        <p style={{ color: 'var(--rose-700)', fontSize: '0.875rem' }}>{message}</p>
        {onRetry && (
          <button
            onClick={onRetry}
            className="btn btn-sm"
            style={{
              marginTop: '0.75rem',
              backgroundColor: 'var(--rose-600)',
              color: '#ffffff',
              border: 'none',
            }}
          >
            <RefreshCw size={14} />
            Try Again
          </button>
        )}
      </div>
    </div>
  );
};

export default ErrorMessage;

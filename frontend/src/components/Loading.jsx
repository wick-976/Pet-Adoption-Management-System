import React from 'react';

export const Loading = ({ text = 'Loading...', fullScreen = false }) => {
  if (fullScreen) {
    return (
      <div style={{ minHeight: '60vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: '1rem' }}>
        <div className="spinner"></div>
        <p style={{ color: 'var(--slate-500)', fontWeight: 500 }}>{text}</p>
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.75rem', padding: '2rem 1rem' }}>
      <div className="spinner spinner-sm"></div>
      <span style={{ color: 'var(--slate-600)', fontSize: '0.9375rem' }}>{text}</span>
    </div>
  );
};

export const SkeletonCard = () => (
  <div className="card" style={{ height: '380px', display: 'flex', flexDirection: 'column' }}>
    <div className="skeleton" style={{ height: '200px', width: '100%' }}></div>
    <div style={{ padding: '1.25rem', display: 'flex', flexDirection: 'column', gap: '0.75rem', flex: 1 }}>
      <div className="skeleton" style={{ height: '1.5rem', width: '60%' }}></div>
      <div className="skeleton" style={{ height: '1rem', width: '40%' }}></div>
      <div className="skeleton" style={{ height: '3rem', width: '100%', marginTop: 'auto' }}></div>
    </div>
  </div>
);

export default Loading;

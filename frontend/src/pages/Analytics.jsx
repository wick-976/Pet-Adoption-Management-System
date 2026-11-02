import React, { useEffect, useState } from 'react';
import analyticsService from '../services/analyticsService';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';

const Analytics = () => {
  const [overall, setOverall] = useState(null);
  const [petAnalytics, setPetAnalytics] = useState(null);
  const [adoptionAnalytics, setAdoptionAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadAnalytics = async () => {
    try {
      setLoading(true);
      setError('');
      const [overallData, petsData, adoptionData] = await Promise.all([
        analyticsService.getOverallAnalytics(),
        analyticsService.getPetAnalytics(),
        analyticsService.getAdoptionAnalytics(),
      ]);
      setOverall(overallData);
      setPetAnalytics(petsData);
      setAdoptionAnalytics(adoptionData);
    } catch (err) {
      setError(err.message || 'Unable to load analytics.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAnalytics();
  }, []);

  if (loading) return <Loading text="Loading analytics..." fullScreen />;
  if (error) return <ErrorMessage message={error} onRetry={loadAnalytics} />;

  const cardData = [
    { label: 'Available pets', value: overall?.totalAvailablePets ?? 0 },
    { label: 'Total requests', value: overall?.totalAdoptionRequests ?? 0 },
    { label: 'Completed adoptions', value: overall?.completedAdoptions ?? 0 },
    { label: 'Approval rate', value: adoptionAnalytics?.approvalRatePercentage ? `${adoptionAnalytics.approvalRatePercentage.toFixed(1)}%` : '0%' },
  ];

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div style={{ marginBottom: '1.5rem' }}>
        <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Insights</p>
        <h1 style={{ marginTop: '0.35rem', fontSize: '2.2rem' }}>Analytics & reports</h1>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1.25rem', marginBottom: '2rem' }}>
        {cardData.map((card) => (
          <div key={card.label} className="card" style={{ padding: '1.5rem' }}>
            <div style={{ color: 'var(--slate-500)', fontWeight: 600, marginBottom: '0.5rem' }}>{card.label}</div>
            <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--slate-900)' }}>{card.value}</div>
          </div>
        ))}
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1.5rem' }}>
        <div className="card" style={{ padding: '1.5rem' }}>
          <h3 style={{ marginBottom: '1rem' }}>Pet breakdown</h3>
          {petAnalytics?.speciesBreakdown && Object.entries(petAnalytics.speciesBreakdown).length > 0 ? (
            <div style={{ display: 'grid', gap: '0.75rem' }}>
              {Object.entries(petAnalytics.speciesBreakdown).map(([key, value]) => (
                <div key={key} style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--slate-200)', paddingBottom: '0.5rem' }}>
                  <span>{key}</span>
                  <strong>{value}</strong>
                </div>
              ))}
            </div>
          ) : (
            <p style={{ margin: 0, color: 'var(--slate-500)' }}>No pet species data available.</p>
          )}
        </div>

        <div className="card" style={{ padding: '1.5rem' }}>
          <h3 style={{ marginBottom: '1rem' }}>Request status</h3>
          {overall?.adoptionStatusBreakdown && Object.entries(overall.adoptionStatusBreakdown).length > 0 ? (
            <div style={{ display: 'grid', gap: '0.75rem' }}>
              {Object.entries(overall.adoptionStatusBreakdown).map(([key, value]) => (
                <div key={key} style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--slate-200)', paddingBottom: '0.5rem' }}>
                  <span>{key}</span>
                  <strong>{value}</strong>
                </div>
              ))}
            </div>
          ) : (
            <p style={{ margin: 0, color: 'var(--slate-500)' }}>No adoption status data available.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default Analytics;

import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Gauge, PawPrint, FileText, Users, ArrowRight } from 'lucide-react';
import analyticsService from '../services/analyticsService';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';

const AdminDashboard = () => {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadAnalytics = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await analyticsService.getOverallAnalytics();
      setAnalytics(data);
    } catch (err) {
      setError(err.message || 'Unable to load dashboard metrics.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAnalytics();
  }, []);

  if (loading) return <Loading text="Loading dashboard..." fullScreen />;
  if (error) return <ErrorMessage message={error} onRetry={loadAnalytics} />;

  const cards = [
    { label: 'Available pets', value: analytics?.totalAvailablePets ?? 0, icon: PawPrint },
    { label: 'Adoption requests', value: analytics?.totalAdoptionRequests ?? 0, icon: FileText },
    { label: 'Completed adoptions', value: analytics?.completedAdoptions ?? 0, icon: Gauge },
    { label: 'Species tracked', value: Object.keys(analytics?.petsBySpecies || {}).length, icon: Users },
  ];

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '1rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
        <div>
          <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Operations</p>
          <h1 style={{ marginTop: '0.35rem', fontSize: '2.2rem' }}>Staff dashboard</h1>
        </div>
        <Link to="/admin/pets" className="btn btn-primary">
          Manage pets <ArrowRight size={16} />
        </Link>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1.25rem', marginBottom: '2rem' }}>
        {cards.map(({ label, value, icon: Icon }) => (
          <div key={label} className="card" style={{ padding: '1.5rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
              <span style={{ color: 'var(--slate-500)', fontWeight: 600 }}>{label}</span>
              <div style={{ width: '2.5rem', height: '2.5rem', borderRadius: '50%', backgroundColor: 'var(--primary-100)', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Icon size={18} />
              </div>
            </div>
            <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--slate-900)' }}>{value}</div>
          </div>
        ))}
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '1.25rem' }}>
        <div className="card" style={{ padding: '1.5rem' }}>
          <h3 style={{ marginBottom: '1rem' }}>Quick actions</h3>
          <div style={{ display: 'grid', gap: '0.75rem' }}>
            <Link to="/admin/pets" className="btn btn-secondary" style={{ justifyContent: 'space-between' }}>View pet catalog <ArrowRight size={16} /></Link>
            <Link to="/admin/adoption-requests" className="btn btn-secondary" style={{ justifyContent: 'space-between' }}>Review adoption requests <ArrowRight size={16} /></Link>
            <Link to="/admin/shelter" className="btn btn-secondary" style={{ justifyContent: 'space-between' }}>Update shelter profile <ArrowRight size={16} /></Link>
            <Link to="/admin/analytics" className="btn btn-secondary" style={{ justifyContent: 'space-between' }}>Open analytics <ArrowRight size={16} /></Link>
          </div>
        </div>

        <div className="card" style={{ padding: '1.5rem' }}>
          <h3 style={{ marginBottom: '1rem' }}>Species summary</h3>
          <div style={{ display: 'grid', gap: '0.75rem' }}>
            {analytics?.petsBySpecies && Object.entries(analytics.petsBySpecies).length > 0 ? (
              Object.entries(analytics.petsBySpecies).map(([species, count]) => (
                <div key={species} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderBottom: '1px solid var(--slate-200)', paddingBottom: '0.5rem' }}>
                  <span>{species}</span>
                  <strong>{count}</strong>
                </div>
              ))
            ) : (
              <p style={{ color: 'var(--slate-500)', margin: 0 }}>No species data available yet.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;

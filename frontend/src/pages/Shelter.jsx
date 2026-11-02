import React, { useEffect, useState } from 'react';
import { Building2, Mail, Phone, Clock3, Globe } from 'lucide-react';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import shelterService from '../services/shelterService';

const Shelter = () => {
  const [shelter, setShelter] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadShelter = async () => {
      try {
        setLoading(true);
        const data = await shelterService.getShelterInfo();
        setShelter(data);
      } catch (err) {
        setError(err.message || 'Unable to load shelter details.');
      } finally {
        setLoading(false);
      }
    };

    loadShelter();
  }, []);

  if (loading) return <Loading text="Loading shelter information..." fullScreen />;
  if (error) return <ErrorMessage message={error} onRetry={() => window.location.reload()} />;

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div className="card" style={{ padding: '2rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.8rem', marginBottom: '1.5rem' }}>
          <div style={{ width: '3rem', height: '3rem', borderRadius: '50%', backgroundColor: 'var(--primary-100)', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Building2 size={22} />
          </div>
          <div>
            <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Shelter profile</p>
            <h1 style={{ margin: '0.2rem 0 0' }}>{shelter?.name || 'Our shelter'}</h1>
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1.25rem' }}>
          <div className="card" style={{ padding: '1.25rem', backgroundColor: 'var(--slate-50)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.75rem', color: 'var(--primary-700)', fontWeight: 700 }}><Building2 size={16} /> Address</div>
            <p style={{ margin: 0 }}>{shelter?.address || 'Address not available.'}</p>
          </div>

          <div className="card" style={{ padding: '1.25rem', backgroundColor: 'var(--slate-50)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.75rem', color: 'var(--primary-700)', fontWeight: 700 }}><Phone size={16} /> Phone</div>
            <p style={{ margin: 0 }}>{shelter?.phone || 'Phone not available.'}</p>
          </div>

          <div className="card" style={{ padding: '1.25rem', backgroundColor: 'var(--slate-50)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.75rem', color: 'var(--primary-700)', fontWeight: 700 }}><Mail size={16} /> Email</div>
            <p style={{ margin: 0 }}>{shelter?.email || 'Email not available.'}</p>
          </div>

          <div className="card" style={{ padding: '1.25rem', backgroundColor: 'var(--slate-50)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.75rem', color: 'var(--primary-700)', fontWeight: 700 }}><Clock3 size={16} /> Operating hours</div>
            <p style={{ margin: 0 }}>{shelter?.operatingHours || 'Hours not available.'}</p>
          </div>

          {shelter?.website && (
            <div className="card" style={{ padding: '1.25rem', backgroundColor: 'var(--slate-50)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.75rem', color: 'var(--primary-700)', fontWeight: 700 }}><Globe size={16} /> Website</div>
              <p style={{ margin: 0 }}><a href={shelter.website} target="_blank" rel="noreferrer">{shelter.website}</a></p>
            </div>
          )}
        </div>

        {shelter?.description && (
          <div style={{ marginTop: '1.5rem' }}>
            <h3>About the shelter</h3>
            <p>{shelter.description}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Shelter;

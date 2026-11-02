import React, { useEffect, useState } from 'react';
import shelterService from '../services/shelterService';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';

const emptyForm = {
  name: '',
  address: '',
  phone: '',
  email: '',
  operatingHours: '',
  description: '',
  website: '',
};

const ManageShelter = () => {
  const [form, setForm] = useState(emptyForm);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const loadShelter = async () => {
    try {
      setLoading(true);
      const data = await shelterService.getShelterInfo();
      setForm({
        name: data?.name || '',
        address: data?.address || '',
        phone: data?.phone || '',
        email: data?.email || '',
        operatingHours: data?.operatingHours || '',
        description: data?.description || '',
        website: data?.website || '',
      });
    } catch (err) {
      setError(err.message || 'Unable to load shelter details.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadShelter();
  }, []);

  const handleChange = (field, value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');

    try {
      await shelterService.updateShelterInfo(form);
      await loadShelter();
    } catch (err) {
      setError(err.message || 'Unable to save shelter profile.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Loading text="Loading shelter profile..." fullScreen />;

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div style={{ marginBottom: '1.5rem' }}>
        <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Shelter operations</p>
        <h1 style={{ marginTop: '0.35rem', fontSize: '2.2rem' }}>Update shelter details</h1>
      </div>

      {error && <ErrorMessage message={error} />}

      <div className="card" style={{ padding: '1.5rem' }}>
        <form onSubmit={handleSubmit}>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1rem' }}>
            <div className="form-group"><label className="form-label">Shelter name</label><input className="form-control" value={form.name} onChange={(e) => handleChange('name', e.target.value)} required /></div>
            <div className="form-group"><label className="form-label">Phone</label><input className="form-control" value={form.phone} onChange={(e) => handleChange('phone', e.target.value)} required /></div>
            <div className="form-group"><label className="form-label">Email</label><input type="email" className="form-control" value={form.email} onChange={(e) => handleChange('email', e.target.value)} required /></div>
            <div className="form-group"><label className="form-label">Website</label><input className="form-control" value={form.website} onChange={(e) => handleChange('website', e.target.value)} /></div>
            <div className="form-group" style={{ gridColumn: '1 / -1' }}><label className="form-label">Address</label><textarea className="form-textarea" rows="2" value={form.address} onChange={(e) => handleChange('address', e.target.value)} required /></div>
            <div className="form-group" style={{ gridColumn: '1 / -1' }}><label className="form-label">Operating hours</label><textarea className="form-textarea" rows="2" value={form.operatingHours} onChange={(e) => handleChange('operatingHours', e.target.value)} /></div>
            <div className="form-group" style={{ gridColumn: '1 / -1' }}><label className="form-label">Description</label><textarea className="form-textarea" rows="4" value={form.description} onChange={(e) => handleChange('description', e.target.value)} /></div>
          </div>
          <div style={{ marginTop: '1rem' }}>
            <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? 'Saving...' : 'Save changes'}</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ManageShelter;

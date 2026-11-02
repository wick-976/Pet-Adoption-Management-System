import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserCog } from 'lucide-react';
import ErrorMessage from '../components/ErrorMessage';
import authService from '../services/authService';

const StaffRegister = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: '', email: '', password: '', role: 'ROLE_STAFF' });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    setSuccess('');

    try {
      await authService.registerStaff(form);
      setSuccess('Staff account created successfully.');
      setTimeout(() => navigate('/admin'), 800);
    } catch (err) {
      setError(err.message || 'Unable to create staff account.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div style={{ maxWidth: '560px', margin: '0 auto' }}>
        <div className="card" style={{ padding: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.75rem', marginBottom: '1.25rem' }}>
            <div style={{ width: '3rem', height: '3rem', borderRadius: '50%', backgroundColor: 'var(--indigo-100)', color: 'var(--indigo-600)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <UserCog size={20} />
            </div>
            <div>
              <p style={{ margin: 0, color: 'var(--indigo-600)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.68rem' }}>Access control</p>
              <h1 style={{ margin: '0.15rem 0 0' }}>Register staff</h1>
            </div>
          </div>

          {error && <ErrorMessage message={error} />}
          {success && <div className="alert alert-success" role="alert">{success}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label" htmlFor="username">Username</label>
              <input id="username" className="form-control" value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} required />
            </div>
            <div className="form-group">
              <label className="form-label" htmlFor="email">Email</label>
              <input id="email" type="email" className="form-control" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
            </div>
            <div className="form-group">
              <label className="form-label" htmlFor="password">Password</label>
              <input id="password" type="password" className="form-control" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required />
            </div>
            <div className="form-group">
              <label className="form-label" htmlFor="role">Role</label>
              <select id="role" className="form-select" value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
                <option value="ROLE_STAFF">ROLE_STAFF</option>
                <option value="ROLE_ADMIN">ROLE_ADMIN</option>
              </select>
            </div>

            <button type="submit" className="btn btn-primary" style={{ width: '100%' }} disabled={submitting}>
              {submitting ? 'Creating account...' : 'Create staff account'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default StaffRegister;

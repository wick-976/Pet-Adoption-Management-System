import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { UserPlus } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import ErrorMessage from '../components/ErrorMessage';

const Register = () => {
  const navigate = useNavigate();
  const { register } = useAuth();
  const [form, setForm] = useState({ username: '', email: '', password: '' });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (field, value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSubmitting(true);

    try {
      await register(form);
      setSuccess('Your account was created successfully. Please log in.');
      setTimeout(() => navigate('/login'), 900);
    } catch (err) {
      setError(err.message || 'Unable to create your account. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container" style={{ paddingTop: '2.5rem', paddingBottom: '3rem' }}>
      <div style={{ maxWidth: '520px', margin: '0 auto' }}>
        <div className="card" style={{ padding: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.75rem', marginBottom: '1.25rem' }}>
            <div style={{ width: '3rem', height: '3rem', borderRadius: '50%', backgroundColor: 'var(--primary-100)', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <UserPlus size={20} />
            </div>
            <div>
              <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.68rem' }}>New user</p>
              <h1 style={{ margin: '0.15rem 0 0' }}>Create account</h1>
            </div>
          </div>

          {error && <ErrorMessage message={error} />}
          {success && <div className="alert alert-success" role="alert">{success}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label" htmlFor="username">Username</label>
              <input id="username" className="form-control" value={form.username} onChange={(e) => handleChange('username', e.target.value)} />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="email">Email</label>
              <input id="email" type="email" className="form-control" value={form.email} onChange={(e) => handleChange('email', e.target.value)} />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="password">Password</label>
              <input id="password" type="password" className="form-control" value={form.password} onChange={(e) => handleChange('password', e.target.value)} />
            </div>

            <button type="submit" className="btn btn-primary" style={{ width: '100%' }} disabled={submitting}>
              {submitting ? 'Creating account...' : 'Register'}
            </button>
          </form>

          <div style={{ marginTop: '1rem', textAlign: 'center', color: 'var(--slate-600)' }}>
            Already have an account? <Link to="/login" style={{ fontWeight: 600 }}>Sign in</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;

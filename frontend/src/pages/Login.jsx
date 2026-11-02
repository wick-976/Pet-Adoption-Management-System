import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { KeyRound, UserRound } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import ErrorMessage from '../components/ErrorMessage';

const Login = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, isAuthenticated } = useAuth();
  const [form, setForm] = useState({ username: '', password: '' });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const redirectPath = location.state?.from || '/';

  if (isAuthenticated) {
    navigate(redirectPath, { replace: true });
    return null;
  }

  const handleChange = (field, value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);

    try {
      await login(form);
      navigate(redirectPath, { replace: true });
    } catch (err) {
      setError(err.message || 'Unable to sign in. Please check your credentials and try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container" style={{ paddingTop: '2.5rem', paddingBottom: '3rem' }}>
      <div style={{ maxWidth: '500px', margin: '0 auto' }}>
        <div className="card" style={{ padding: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.75rem', marginBottom: '1.25rem' }}>
            <div style={{ width: '3rem', height: '3rem', borderRadius: '50%', backgroundColor: 'var(--primary-100)', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <UserRound size={22} />
            </div>
            <div>
              <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.68rem' }}>Welcome back</p>
              <h1 style={{ margin: '0.15rem 0 0' }}>Sign in</h1>
            </div>
          </div>

          {error && <ErrorMessage message={error} />}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label" htmlFor="username">Username</label>
              <input id="username" className="form-control" value={form.username} onChange={(e) => handleChange('username', e.target.value)} autoComplete="username" />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="password">Password</label>
              <input id="password" type="password" className="form-control" value={form.password} onChange={(e) => handleChange('password', e.target.value)} autoComplete="current-password" />
            </div>

            <button type="submit" className="btn btn-primary" style={{ width: '100%' }} disabled={submitting}>
              <KeyRound size={16} />
              {submitting ? 'Signing in...' : 'Login'}
            </button>
          </form>

          <div style={{ marginTop: '1rem', textAlign: 'center', color: 'var(--slate-600)' }}>
            Need an account? <Link to="/register" style={{ fontWeight: 600 }}>Create one</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;

import React from 'react';
import { Navigate, useLocation, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ShieldAlert, ArrowLeft } from 'lucide-react';

export const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user, isAuthenticated } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (allowedRoles && (!user || !allowedRoles.includes(user.role))) {
    return (
      <div className="container" style={{ padding: '4rem 1rem', textAlign: 'center' }}>
        <div className="card" style={{ maxWidth: '500px', margin: '0 auto', padding: '2.5rem' }}>
          <div style={{ display: 'inline-flex', padding: '1rem', background: 'var(--rose-50)', borderRadius: '50%', color: 'var(--rose-600)', marginBottom: '1rem' }}>
            <ShieldAlert size={48} />
          </div>
          <h2 style={{ fontSize: '1.5rem', marginBottom: '0.75rem' }}>Access Restricted</h2>
          <p style={{ color: 'var(--slate-600)', marginBottom: '1.5rem' }}>
            You do not have the required permissions to access this administrative section. Please sign in with an authorized staff or admin account.
          </p>
          <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'center' }}>
            <Link to="/" className="btn btn-secondary">
              <ArrowLeft size={18} />
              Return Home
            </Link>
            <Link to="/login" className="btn btn-primary">
              Switch Account
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return children;
};

export default ProtectedRoute;

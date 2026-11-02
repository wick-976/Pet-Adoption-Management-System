import React, { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  Heart,
  PawPrint,
  Menu,
  X,
  LogOut,
  User,
  LayoutDashboard,
  FileText,
  Building2,
  BarChart3,
  Users,
  Gift,
  Search,
} from 'lucide-react';

export const Navbar = () => {
  const { user, isAuthenticated, isStaff, isAdmin, logout } = useAuth();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
    setMobileMenuOpen(false);
  };

  const closeMenu = () => setMobileMenuOpen(false);

  return (
    <header
      style={{
        position: 'sticky',
        top: 0,
        zIndex: 100,
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        backdropFilter: 'blur(10px)',
        borderBottom: '1px solid var(--slate-200)',
        boxShadow: 'var(--shadow-sm)',
      }}
    >
      <div className="container" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', height: 'var(--header-height)' }}>
        {/* Brand Logo */}
        <Link
          to="/"
          onClick={closeMenu}
          style={{ display: 'flex', alignItems: 'center', gap: '0.625rem', textDecoration: 'none' }}
        >
          <div
            style={{
              width: '2.5rem',
              height: '2.5rem',
              borderRadius: 'var(--radius-lg)',
              background: 'linear-gradient(135deg, var(--primary-600), var(--primary-400))',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#ffffff',
              boxShadow: '0 4px 10px rgba(16, 185, 129, 0.3)',
            }}
          >
            <PawPrint size={22} />
          </div>
          <div>
            <span style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--slate-900)', letterSpacing: '-0.02em', display: 'block', lineHeight: 1.1 }}>
              Pet<span style={{ color: 'var(--primary-600)' }}>Haven</span>
            </span>
            <span style={{ fontSize: '0.6875rem', fontWeight: 600, color: 'var(--slate-500)', letterSpacing: '0.05em', textTransform: 'uppercase' }}>
              Adoption & Rescue
            </span>
          </div>
        </Link>

        {/* Desktop Navigation */}
        <nav style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }} className="desktop-nav">
          <NavLink
            to="/"
            className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
            style={navLinkStyle}
          >
            Home
          </NavLink>
          <NavLink
            to="/pets"
            className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
            style={navLinkStyle}
          >
            <Search size={16} />
            Browse Pets
          </NavLink>
          <NavLink
            to="/shelter"
            className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
            style={navLinkStyle}
          >
            <Building2 size={16} />
            Shelter
          </NavLink>
          <NavLink
            to="/donate"
            className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
            style={navLinkStyle}
          >
            <Gift size={16} />
            Donate
          </NavLink>

          {/* User Links */}
          {isAuthenticated && !isStaff && (
            <>
              <NavLink
                to="/favorites"
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
                style={navLinkStyle}
              >
                <Heart size={16} />
                Favorites
              </NavLink>
              <NavLink
                to="/my-requests"
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
                style={navLinkStyle}
              >
                <FileText size={16} />
                My Requests
              </NavLink>
            </>
          )}

          {/* Staff/Admin Links */}
          {isAuthenticated && isStaff && (
            <>
              <div style={{ width: '1px', height: '1.5rem', backgroundColor: 'var(--slate-300)', margin: '0 0.25rem' }} />
              <NavLink
                to="/admin"
                end
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
                style={navLinkStyle}
              >
                <LayoutDashboard size={16} />
                Dashboard
              </NavLink>
              <NavLink
                to="/admin/pets"
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
                style={navLinkStyle}
              >
                <PawPrint size={16} />
                Pets
              </NavLink>
              <NavLink
                to="/admin/adoption-requests"
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
                style={navLinkStyle}
              >
                <FileText size={16} />
                Requests
              </NavLink>
              <NavLink
                to="/admin/shelter"
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
                style={navLinkStyle}
              >
                <Building2 size={16} />
                Shelter Info
              </NavLink>
              <NavLink
                to="/admin/analytics"
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
                style={navLinkStyle}
              >
                <BarChart3 size={16} />
                Analytics
              </NavLink>
              {isAdmin && (
                <NavLink
                  to="/admin/staff-register"
                  className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
                  style={navLinkStyle}
                >
                  <Users size={16} />
                  Staff
                </NavLink>
              )}
            </>
          )}
        </nav>

        {/* Right Auth Section */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }} className="desktop-nav">
          {isAuthenticated ? (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                  padding: '0.375rem 0.75rem',
                  backgroundColor: 'var(--slate-100)',
                  borderRadius: 'var(--radius-full)',
                  border: '1px solid var(--slate-200)',
                }}
              >
                <div
                  style={{
                    width: '1.75rem',
                    height: '1.75rem',
                    borderRadius: '50%',
                    backgroundColor: isStaff ? 'var(--indigo-100)' : 'var(--primary-100)',
                    color: isStaff ? 'var(--indigo-600)' : 'var(--primary-700)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontWeight: 700,
                    fontSize: '0.75rem',
                  }}
                >
                  <User size={14} />
                </div>
                <span style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--slate-800)' }}>
                  {user?.username}
                </span>
                <span
                  style={{
                    fontSize: '0.6875rem',
                    fontWeight: 700,
                    padding: '0.125rem 0.375rem',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: isStaff ? 'var(--indigo-600)' : 'var(--slate-200)',
                    color: isStaff ? '#ffffff' : 'var(--slate-700)',
                    textTransform: 'uppercase',
                  }}
                >
                  {user?.role?.replace('ROLE_', '')}
                </span>
              </div>
              <button
                onClick={handleLogout}
                className="btn btn-secondary btn-sm"
                title="Logout"
                style={{ display: 'flex', alignItems: 'center', gap: '0.375rem' }}
              >
                <LogOut size={16} />
                Logout
              </button>
            </div>
          ) : (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Link to="/login" className="btn btn-secondary btn-sm">
                Login
              </Link>
              <Link to="/register" className="btn btn-primary btn-sm">
                Register
              </Link>
            </div>
          )}
        </div>

        {/* Mobile menu toggle */}
        <button
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          className="btn btn-secondary btn-icon mobile-menu-btn"
          aria-label="Toggle menu"
          style={{ display: 'none' }}
        >
          {mobileMenuOpen ? <X size={22} /> : <Menu size={22} />}
        </button>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div
          style={{
            borderTop: '1px solid var(--slate-200)',
            backgroundColor: '#ffffff',
            padding: '1.25rem',
            display: 'flex',
            flexDirection: 'column',
            gap: '0.875rem',
          }}
        >
          <NavLink to="/" onClick={closeMenu} style={mobileNavLinkStyle}>Home</NavLink>
          <NavLink to="/pets" onClick={closeMenu} style={mobileNavLinkStyle}>Browse Pets</NavLink>
          <NavLink to="/shelter" onClick={closeMenu} style={mobileNavLinkStyle}>Shelter Information</NavLink>
          <NavLink to="/donate" onClick={closeMenu} style={mobileNavLinkStyle}>Donate</NavLink>

          {isAuthenticated && !isStaff && (
            <>
              <NavLink to="/favorites" onClick={closeMenu} style={mobileNavLinkStyle}>My Favorites</NavLink>
              <NavLink to="/my-requests" onClick={closeMenu} style={mobileNavLinkStyle}>My Adoption Requests</NavLink>
            </>
          )}

          {isAuthenticated && isStaff && (
            <div style={{ borderTop: '1px solid var(--slate-200)', paddingTop: '0.75rem', display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              <span style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--slate-400)', textTransform: 'uppercase' }}>
                Staff Management
              </span>
              <NavLink to="/admin" onClick={closeMenu} style={mobileNavLinkStyle}>Dashboard</NavLink>
              <NavLink to="/admin/pets" onClick={closeMenu} style={mobileNavLinkStyle}>Manage Pets</NavLink>
              <NavLink to="/admin/adoption-requests" onClick={closeMenu} style={mobileNavLinkStyle}>Adoption Requests</NavLink>
              <NavLink to="/admin/shelter" onClick={closeMenu} style={mobileNavLinkStyle}>Shelter Settings</NavLink>
              <NavLink to="/admin/analytics" onClick={closeMenu} style={mobileNavLinkStyle}>Analytics & Reports</NavLink>
              {isAdmin && (
                <NavLink to="/admin/staff-register" onClick={closeMenu} style={mobileNavLinkStyle}>Staff Accounts</NavLink>
              )}
            </div>
          )}

          <div style={{ borderTop: '1px solid var(--slate-200)', paddingTop: '1rem', marginTop: '0.5rem' }}>
            {isAuthenticated ? (
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontWeight: 600 }}>{user?.username}</div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--slate-500)' }}>{user?.role}</div>
                </div>
                <button onClick={handleLogout} className="btn btn-secondary btn-sm">
                  <LogOut size={16} /> Logout
                </button>
              </div>
            ) : (
              <div style={{ display: 'flex', gap: '0.75rem' }}>
                <Link to="/login" onClick={closeMenu} className="btn btn-secondary" style={{ flex: 1 }}>Login</Link>
                <Link to="/register" onClick={closeMenu} className="btn btn-primary" style={{ flex: 1 }}>Register</Link>
              </div>
            )}
          </div>
        </div>
      )}

      <style>{`
        @media (max-width: 900px) {
          .desktop-nav { display: none !important; }
          .mobile-menu-btn { display: inline-flex !important; }
        }
        .nav-link {
          transition: all 0.15s ease;
        }
        .nav-link:hover {
          color: var(--primary-600) !important;
        }
        .nav-link.active {
          color: var(--primary-600) !important;
          font-weight: 700 !important;
        }
      `}</style>
    </header>
  );
};

const navLinkStyle = {
  display: 'flex',
  alignItems: 'center',
  gap: '0.375rem',
  fontSize: '0.9375rem',
  fontWeight: 500,
  color: 'var(--slate-700)',
  textDecoration: 'none',
  padding: '0.375rem 0.25rem',
};

const mobileNavLinkStyle = {
  fontSize: '1rem',
  fontWeight: 500,
  color: 'var(--slate-800)',
  textDecoration: 'none',
  padding: '0.5rem 0',
};

export default Navbar;

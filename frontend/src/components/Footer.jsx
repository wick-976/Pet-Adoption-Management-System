import React from 'react';
import { Link } from 'react-router-dom';
import { PawPrint, Heart, Phone, Mail, Clock, Globe, Facebook, Twitter, Instagram } from 'lucide-react';

export const Footer = () => {
  return (
    <footer
      style={{
        backgroundColor: 'var(--slate-900)',
        color: 'var(--slate-300)',
        marginTop: 'auto',
        paddingTop: '3rem',
        paddingBottom: '1.5rem',
      }}
    >
      <div className="container">
        {/* Top Grid */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
            gap: '2.5rem',
            marginBottom: '2.5rem',
          }}
        >
          {/* Brand */}
          <div style={{ gridColumn: 'span 1' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.625rem', marginBottom: '1rem' }}>
              <div
                style={{
                  width: '2.25rem',
                  height: '2.25rem',
                  borderRadius: 'var(--radius-lg)',
                  background: 'linear-gradient(135deg, var(--primary-600), var(--primary-400))',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  color: '#ffffff',
                }}
              >
                <PawPrint size={20} />
              </div>
              <span style={{ fontSize: '1.25rem', fontWeight: 800, color: '#ffffff', letterSpacing: '-0.02em' }}>
                Pet<span style={{ color: 'var(--primary-400)' }}>Haven</span>
              </span>
            </div>
            <p style={{ fontSize: '0.875rem', lineHeight: 1.7, color: 'var(--slate-400)', marginBottom: '1.25rem' }}>
              Dedicated to rescuing, rehabilitating, and finding forever homes for abandoned and homeless pets since 2010.
            </p>
            <div style={{ display: 'flex', gap: '0.625rem' }}>
              {[Facebook, Twitter, Instagram].map((Icon, i) => (
                <button
                  key={i}
                  style={{
                    width: '2.25rem',
                    height: '2.25rem',
                    borderRadius: 'var(--radius-md)',
                    backgroundColor: 'var(--slate-800)',
                    border: '1px solid var(--slate-700)',
                    color: 'var(--slate-400)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    cursor: 'pointer',
                    transition: 'all 0.2s ease',
                  }}
                  onMouseEnter={e => { e.target.style.backgroundColor = 'var(--primary-600)'; e.target.style.color = '#fff'; }}
                  onMouseLeave={e => { e.target.style.backgroundColor = 'var(--slate-800)'; e.target.style.color = 'var(--slate-400)'; }}
                >
                  <Icon size={16} />
                </button>
              ))}
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 style={{ fontSize: '0.9375rem', fontWeight: 700, color: '#ffffff', marginBottom: '1rem' }}>Quick Links</h4>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.625rem' }}>
              {[
                { to: '/', label: 'Home' },
                { to: '/pets', label: 'Browse Pets' },
                { to: '/shelter', label: 'Our Shelter' },
                { to: '/donate', label: 'Support Us' },
                { to: '/login', label: 'Staff Login' },
              ].map(({ to, label }) => (
                <li key={to}>
                  <Link
                    to={to}
                    style={{
                      fontSize: '0.875rem',
                      color: 'var(--slate-400)',
                      textDecoration: 'none',
                      transition: 'color 0.15s ease',
                    }}
                    onMouseEnter={e => (e.target.style.color = 'var(--primary-400)')}
                    onMouseLeave={e => (e.target.style.color = 'var(--slate-400)')}
                  >
                    {label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Adoption Process */}
          <div>
            <h4 style={{ fontSize: '0.9375rem', fontWeight: 700, color: '#ffffff', marginBottom: '1rem' }}>Adopt a Pet</h4>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.625rem' }}>
              {[
                'Browse available pets',
                'View their story',
                'Submit an application',
                'Get approved & adopt',
              ].map((step, i) => (
                <li key={i} style={{ display: 'flex', alignItems: 'flex-start', gap: '0.625rem', fontSize: '0.875rem', color: 'var(--slate-400)' }}>
                  <span
                    style={{
                      width: '1.25rem',
                      height: '1.25rem',
                      borderRadius: '50%',
                      backgroundColor: 'var(--primary-800)',
                      color: 'var(--primary-300)',
                      fontSize: '0.6875rem',
                      fontWeight: 700,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      flexShrink: 0,
                      marginTop: '1px',
                    }}
                  >
                    {i + 1}
                  </span>
                  {step}
                </li>
              ))}
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h4 style={{ fontSize: '0.9375rem', fontWeight: 700, color: '#ffffff', marginBottom: '1rem' }}>Contact Us</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {[
                { Icon: Phone, text: '+1 (555) 123-4567' },
                { Icon: Mail, text: 'contact@happypaws.org' },
                { Icon: Clock, text: 'Mon-Sat: 9AM-6PM\nSun: 10AM-4PM' },
                { Icon: Globe, text: 'www.happypawsshelter.org' },
              ].map(({ Icon, text }, i) => (
                <div key={i} style={{ display: 'flex', alignItems: 'flex-start', gap: '0.625rem', fontSize: '0.875rem', color: 'var(--slate-400)' }}>
                  <Icon size={16} style={{ color: 'var(--primary-500)', marginTop: '2px', flexShrink: 0 }} />
                  <span style={{ whiteSpace: 'pre-line' }}>{text}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Divider */}
        <div style={{ borderTop: '1px solid var(--slate-800)', paddingTop: '1.5rem', display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'center', gap: '0.75rem' }}>
          <p style={{ fontSize: '0.8125rem', color: 'var(--slate-500)' }}>
            © {new Date().getFullYear()} PetHaven Adoption & Rescue. All rights reserved.
          </p>
          <p style={{ fontSize: '0.8125rem', color: 'var(--slate-500)', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
            Made with <Heart size={13} style={{ color: 'var(--rose-500)' }} /> for pets everywhere
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;

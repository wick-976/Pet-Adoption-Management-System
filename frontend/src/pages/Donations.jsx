import React, { useState } from 'react';
import { Gift, CheckCircle2 } from 'lucide-react';
import donationService from '../services/donationService';
import ErrorMessage from '../components/ErrorMessage';

const Donations = () => {
  const [form, setForm] = useState({ donorName: '', donorEmail: '', amount: '25', currency: 'USD', message: '' });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    setSuccess(null);

    try {
      const payload = {
        donorName: form.donorName.trim(),
        donorEmail: form.donorEmail.trim(),
        amount: Number(form.amount),
        currency: form.currency || 'USD',
        message: form.message.trim(),
      };

      const result = await donationService.initiateDonation(payload);
      if (result?.checkoutUrl) {
        window.location.href = result.checkoutUrl;
        return;
      }
      setSuccess(result || { message: 'Donation submitted successfully.' });
    } catch (err) {
      setError(err.message || 'Unable to process your donation. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', alignItems: 'stretch' }}>
        <div className="card" style={{ padding: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
            <div style={{ width: '3rem', height: '3rem', borderRadius: '50%', backgroundColor: 'var(--primary-100)', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <Gift size={20} />
            </div>
            <div>
              <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Support us</p>
              <h1 style={{ margin: '0.2rem 0 0' }}>Make a donation</h1>
            </div>
          </div>

          <p style={{ marginBottom: '1.25rem' }}>Your generosity helps provide food, shelter, medical care, and enrichment for pets awaiting adoption.</p>

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label" htmlFor="donorName">Donor name</label>
              <input id="donorName" className="form-control" value={form.donorName} onChange={(e) => setForm({ ...form, donorName: e.target.value })} />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="donorEmail">Email</label>
              <input id="donorEmail" type="email" className="form-control" value={form.donorEmail} onChange={(e) => setForm({ ...form, donorEmail: e.target.value })} />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 140px', gap: '1rem' }}>
              <div className="form-group">
                <label className="form-label" htmlFor="amount">Amount</label>
                <input id="amount" type="number" min="1" step="0.01" className="form-control" value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="currency">Currency</label>
                <select id="currency" className="form-select" value={form.currency} onChange={(e) => setForm({ ...form, currency: e.target.value })}>
                  <option value="USD">USD</option>
                </select>
              </div>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="message">Message</label>
              <textarea id="message" className="form-textarea" rows="4" value={form.message} onChange={(e) => setForm({ ...form, message: e.target.value })} />
            </div>

            {error && <ErrorMessage message={error} />}
            {success && (
              <div className="alert alert-success" role="alert">
                <CheckCircle2 size={18} />
                <span>{success.message || 'Donation submitted successfully.'}</span>
              </div>
            )}

            <button type="submit" className="btn btn-primary" style={{ width: '100%' }} disabled={submitting}>
              {submitting ? 'Processing...' : 'Donate now'}
            </button>
          </form>
        </div>

        <div className="card" style={{ padding: '2rem', background: 'linear-gradient(135deg, rgba(16,185,129,0.08), rgba(59,130,246,0.05))' }}>
          <h2 style={{ marginBottom: '1rem' }}>Why donate?</h2>
          <div style={{ display: 'grid', gap: '1rem' }}>
            {[
              'Emergency medical care for rescued animals',
              'Nutritious food and safe shelter',
              'Behavioral rehabilitation and enrichment',
              'Adoption support and family matching',
            ].map((item) => (
              <div key={item} style={{ display: 'flex', alignItems: 'flex-start', gap: '0.75rem' }}>
                <div style={{ minWidth: '1.5rem', minHeight: '1.5rem', borderRadius: '50%', backgroundColor: 'var(--primary-600)', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700 }}>✓</div>
                <p style={{ margin: 0 }}>{item}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Donations;

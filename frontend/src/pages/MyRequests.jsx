import React, { useEffect, useState } from 'react';
import { FileText, Clock3 } from 'lucide-react';
import adoptionService from '../services/adoptionService';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import StatusBadge from '../components/StatusBadge';

const MyRequests = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadRequests = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await adoptionService.getMyRequests();
      setRequests(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message || 'Unable to load your adoption requests.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRequests();
  }, []);

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div style={{ marginBottom: '1.5rem' }}>
        <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Adoption tracking</p>
        <h1 style={{ marginTop: '0.35rem', fontSize: '2.2rem' }}>My adoption requests</h1>
      </div>

      {error ? <ErrorMessage message={error} onRetry={loadRequests} /> : loading ? <Loading text="Loading your requests..." /> : (
        <div className="card" style={{ overflow: 'hidden' }}>
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Pet</th>
                  <th>Applicant</th>
                  <th>Status</th>
                  <th>Submitted</th>
                </tr>
              </thead>
              <tbody>
                {requests.length === 0 ? (
                  <tr>
                    <td colSpan="4" style={{ textAlign: 'center', padding: '2rem', color: 'var(--slate-500)' }}>
                      No adoption requests yet.
                    </td>
                  </tr>
                ) : requests.map((request) => (
                  <tr key={request.requestId}>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <div style={{ width: '2rem', height: '2rem', borderRadius: '50%', backgroundColor: 'var(--primary-100)', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                          <FileText size={14} />
                        </div>
                        <div>
                          <div style={{ fontWeight: 700 }}>{request.petName || 'Pet'}</div>
                          <div style={{ color: 'var(--slate-500)', fontSize: '0.78rem' }}>{request.petSpecies || 'Unknown species'}</div>
                        </div>
                      </div>
                    </td>
                    <td>
                      <div style={{ fontWeight: 600 }}>{request.applicantName}</div>
                      <div style={{ color: 'var(--slate-500)', fontSize: '0.78rem' }}>{request.applicantEmail}</div>
                    </td>
                    <td><StatusBadge status={request.status} /></td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: 'var(--slate-600)' }}>
                        <Clock3 size={14} />
                        {request.submissionDate ? new Date(request.submissionDate).toLocaleDateString() : 'N/A'}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default MyRequests;

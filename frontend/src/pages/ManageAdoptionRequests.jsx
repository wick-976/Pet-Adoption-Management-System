import React, { useEffect, useState } from 'react';
import { CheckCircle2, XCircle } from 'lucide-react';
import adoptionService from '../services/adoptionService';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import StatusBadge from '../components/StatusBadge';

const ManageAdoptionRequests = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadRequests = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await adoptionService.getAdoptionRequests({ page: 0, size: 50 });
      setRequests(response?.content || []);
    } catch (err) {
      setError(err.message || 'Unable to load adoption requests.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRequests();
  }, []);

  const handleStatusUpdate = async (id, status, staffNotes) => {
    try {
      await adoptionService.updateAdoptionRequestStatus(id, { status, staffNotes });
      await loadRequests();
    } catch (err) {
      setError(err.message || 'Unable to update request status.');
    }
  };

  if (loading) return <Loading text="Loading adoption requests..." fullScreen />;

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div style={{ marginBottom: '1.5rem' }}>
        <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Applications</p>
        <h1 style={{ marginTop: '0.35rem', fontSize: '2.2rem' }}>Adoption requests</h1>
      </div>

      {error && <ErrorMessage message={error} />}

      <div className="card" style={{ overflow: 'hidden' }}>
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Applicant</th>
                <th>Pet</th>
                <th>Status</th>
                <th>Notes</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {requests.length === 0 ? (
                <tr><td colSpan="5" style={{ textAlign: 'center', padding: '2rem', color: 'var(--slate-500)' }}>No requests found.</td></tr>
              ) : requests.map((request) => (
                <tr key={request.requestId}>
                  <td>
                    <div style={{ fontWeight: 700 }}>{request.applicantName}</div>
                    <div style={{ color: 'var(--slate-500)', fontSize: '0.78rem' }}>{request.applicantEmail}</div>
                  </td>
                  <td>
                    <div style={{ fontWeight: 600 }}>{request.petName}</div>
                    <div style={{ color: 'var(--slate-500)', fontSize: '0.78rem' }}>{request.petSpecies}</div>
                  </td>
                  <td><StatusBadge status={request.status} /></td>
                  <td>
                    <textarea
                      className="form-textarea"
                      rows="2"
                      defaultValue={request.staffNotes || ''}
                      onBlur={(e) => handleStatusUpdate(request.requestId, request.status || 'PENDING', e.target.value)}
                    />
                  </td>
                  <td>
                    <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                      <select
                        className="form-select"
                        value={request.status || 'PENDING'}
                        onChange={(e) => handleStatusUpdate(request.requestId, e.target.value, request.staffNotes || '')}
                      >
                        <option value="PENDING">PENDING</option>
                        <option value="RECEIVED">RECEIVED</option>
                        <option value="APPROVED">APPROVED</option>
                        <option value="REJECTED">REJECTED</option>
                      </select>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default ManageAdoptionRequests;

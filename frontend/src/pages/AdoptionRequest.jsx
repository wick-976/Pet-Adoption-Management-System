import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft, HeartHandshake } from 'lucide-react';
import petService from '../services/petService';
import adoptionService from '../services/adoptionService';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import { useAuth } from '../context/AuthContext';

const initialForm = {
  petId: '',
  applicantName: '',
  applicantEmail: '',
  applicantPhone: '',
  applicantAddress: '',
  housingType: '',
  hasOtherPets: false,
  reasonForAdoption: '',
};

const AdoptionRequest = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [pet, setPet] = useState(null);
  const [form, setForm] = useState(initialForm);
  const [loadingPet, setLoadingPet] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (!id) return;
    const loadPet = async () => {
      try {
        setLoadingPet(true);
        const data = await petService.getPetById(id);
        setPet(data);
        setForm((prev) => ({ ...prev, petId: Number(id) }));
      } catch (err) {
        setError(err.message || 'Unable to load that pet.');
      } finally {
        setLoadingPet(false);
      }
    };

    loadPet();
  }, [id]);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login', { replace: true, state: { from: `/pets/${id}/adopt` } });
    }
  }, [isAuthenticated, navigate, id]);

  const handleChange = (field, value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const validate = () => {
    if (!form.petId) return 'Pet selection is required.';
    if (!form.applicantName.trim()) return 'Applicant name is required.';
    if (!form.applicantEmail.trim()) return 'Applicant email is required.';
    if (!form.applicantPhone.trim()) return 'Applicant phone is required.';

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(form.applicantEmail)) return 'Please enter a valid email address.';

    return '';
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationMessage = validate();
    if (validationMessage) {
      setError(validationMessage);
      setSuccess('');
      return;
    }

    try {
      setSubmitting(true);
      setError('');
      const payload = {
        petId: Number(form.petId),
        applicantName: form.applicantName.trim(),
        applicantEmail: form.applicantEmail.trim(),
        applicantPhone: form.applicantPhone.trim(),
        applicantAddress: form.applicantAddress?.trim() || '',
        housingType: form.housingType?.trim() || '',
        hasOtherPets: Boolean(form.hasOtherPets),
        reasonForAdoption: form.reasonForAdoption?.trim() || '',
      };

      const result = await adoptionService.submitAdoptionRequest(payload);
      setSuccess('Your adoption request was submitted successfully. The shelter team will review it shortly.');
      setForm(initialForm);
      setTimeout(() => navigate('/my-requests'), 1200);
      if (result?.petName) {
        setPet((prev) => ({ ...prev, adoptionStatus: 'PENDING' }));
      }
    } catch (err) {
      setSuccess('');
      setError(err.message || 'Unable to submit the adoption request. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  if (loadingPet) return <Loading text="Preparing adoption form..." fullScreen />;

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div style={{ marginBottom: '1rem' }}>
        <Link to={pet ? `/pets/${pet.petId}` : '/pets'} className="btn btn-secondary btn-sm">
          <ArrowLeft size={16} />
          Back to pet
        </Link>
      </div>

      <div style={{ maxWidth: '780px', margin: '0 auto' }}>
        <div className="card" style={{ padding: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.25rem' }}>
            <div style={{ width: '2.75rem', height: '2.75rem', borderRadius: '50%', backgroundColor: 'var(--primary-100)', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <HeartHandshake size={18} />
            </div>
            <div>
              <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Adoption request</p>
              <h1 style={{ margin: '0.1rem 0 0' }}>Apply to adopt {pet?.name || 'this pet'}</h1>
            </div>
          </div>

          {error && <ErrorMessage message={error} />}
          {success && <div className="alert alert-success" role="alert">{success}</div>}

          <form onSubmit={handleSubmit}>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1rem' }}>
              <div className="form-group">
                <label className="form-label" htmlFor="applicantName">Applicant name <span className="required">*</span></label>
                <input id="applicantName" className="form-control" value={form.applicantName} onChange={(e) => handleChange('applicantName', e.target.value)} />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="applicantEmail">Applicant email <span className="required">*</span></label>
                <input id="applicantEmail" type="email" className="form-control" value={form.applicantEmail} onChange={(e) => handleChange('applicantEmail', e.target.value)} />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="applicantPhone">Phone number <span className="required">*</span></label>
                <input id="applicantPhone" className="form-control" value={form.applicantPhone} onChange={(e) => handleChange('applicantPhone', e.target.value)} />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="housingType">Housing type</label>
                <input id="housingType" className="form-control" value={form.housingType} onChange={(e) => handleChange('housingType', e.target.value)} placeholder="Apartment, house, etc." />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="applicantAddress">Address</label>
              <textarea id="applicantAddress" className="form-textarea" rows="3" value={form.applicantAddress} onChange={(e) => handleChange('applicantAddress', e.target.value)} />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="reasonForAdoption">Why do you want to adopt?</label>
              <textarea id="reasonForAdoption" className="form-textarea" rows="4" value={form.reasonForAdoption} onChange={(e) => handleChange('reasonForAdoption', e.target.value)} />
            </div>

            <div className="form-group" style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <input id="hasOtherPets" type="checkbox" checked={form.hasOtherPets} onChange={(e) => handleChange('hasOtherPets', e.target.checked)} />
              <label htmlFor="hasOtherPets" style={{ margin: 0, fontWeight: 500, color: 'var(--slate-700)' }}>I currently have other pets at home</label>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem', flexWrap: 'wrap' }}>
              <button type="button" className="btn btn-secondary" onClick={() => setForm(initialForm)} disabled={submitting}>
                Reset
              </button>
              <button type="submit" className="btn btn-primary" disabled={submitting}>
                {submitting ? 'Submitting...' : 'Submit Adoption Request'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AdoptionRequest;

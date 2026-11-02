import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { ArrowLeft, CheckCircle2, Info, UserRound } from 'lucide-react';
import PetImageGallery from '../components/PetImageGallery';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import StatusBadge from '../components/StatusBadge';
import petService from '../services/petService';

const PetDetails = () => {
  const { id } = useParams();
  const [pet, setPet] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchPet = async () => {
      try {
        setLoading(true);
        setError('');
        const data = await petService.getPetById(id);
        setPet(data);
      } catch (err) {
        setError(err.message || 'Unable to load this pet.');
      } finally {
        setLoading(false);
      }
    };

    fetchPet();
  }, [id]);

  if (loading) return <Loading text="Loading pet details..." fullScreen />;
  if (error) return <ErrorMessage message={error} onRetry={() => window.location.reload()} />;
  if (!pet) return null;

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div style={{ marginBottom: '1rem' }}>
        <Link to="/pets" className="btn btn-secondary btn-sm">
          <ArrowLeft size={16} />
          Back to pets
        </Link>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 0.8fr', gap: '2rem' }}>
        <PetImageGallery images={pet.images || []} primaryImageUrl={pet.primaryImageUrl} petName={pet.name} />

        <div className="card" style={{ padding: '1.75rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem', flexWrap: 'wrap' }}>
            <h1 style={{ margin: 0, fontSize: '2.2rem' }}>{pet.name}</h1>
            <StatusBadge status={pet.adoptionStatus} />
          </div>

          <div style={{ display: 'grid', gap: '0.75rem', marginBottom: '1.25rem' }}>
            <div><strong>Species:</strong> {pet.species}</div>
            <div><strong>Breed:</strong> {pet.breed || 'Not specified'}</div>
            <div><strong>Age:</strong> {pet.age} {pet.age === 1 ? 'year' : 'years'}</div>
            <div><strong>Views:</strong> {pet.viewCount ?? 0}</div>
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <h3 style={{ marginBottom: '0.5rem' }}>About {pet.name}</h3>
            <p style={{ margin: 0 }}>{pet.description || 'No description provided yet.'}</p>
          </div>

          {pet.adoptionStatus === 'AVAILABLE' ? (
            <Link to={`/pets/${pet.petId}/adopt`} className="btn btn-primary btn-lg" style={{ width: '100%' }}>
              Submit Adoption Request
            </Link>
          ) : (
            <div className="alert alert-info" role="alert" style={{ marginBottom: 0 }}>
              <Info size={18} />
              <span>This pet is currently {pet.adoptionStatus.toLowerCase()} and is not accepting new adoption requests at this time.</span>
            </div>
          )}
        </div>
      </div>

      <div className="card" style={{ marginTop: '2rem', padding: '1.5rem' }}>
        <h3 style={{ marginBottom: '0.75rem' }}>Adoption information</h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1rem' }}>
          <div style={{ backgroundColor: 'var(--slate-50)', borderRadius: '1rem', padding: '1rem', border: '1px solid var(--slate-200)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem', color: 'var(--primary-700)' }}><CheckCircle2 size={16} /> Availability</div>
            <strong>{pet.adoptionStatus}</strong>
          </div>
          <div style={{ backgroundColor: 'var(--slate-50)', borderRadius: '1rem', padding: '1rem', border: '1px solid var(--slate-200)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem', color: 'var(--primary-700)' }}><UserRound size={16} /> Match support</div>
            <span>Our team will guide the adoption process.</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PetDetails;

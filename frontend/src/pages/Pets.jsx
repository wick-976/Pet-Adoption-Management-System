import React, { useEffect, useMemo, useState } from 'react';
import { Search, Filter, RotateCcw } from 'lucide-react';
import PetGrid from '../components/PetGrid';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import petService from '../services/petService';

const initialFilters = {
  name: '',
  breed: '',
  species: '',
  age: '',
  adoptionStatus: '',
};

const Pets = () => {
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState(initialFilters);

  const queryParams = useMemo(() => {
    const params = {};
    if (filters.name) params.name = filters.name;
    if (filters.breed) params.breed = filters.breed;
    if (filters.species) params.species = filters.species;
    if (filters.age) params.age = Number(filters.age);
    if (filters.adoptionStatus) params.adoptionStatus = filters.adoptionStatus;
    return params;
  }, [filters]);

  const loadPets = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await petService.searchPets({ ...queryParams, page: 0, size: 20, sortBy: 'creationDate', sortDir: 'desc' });
      setPets(response?.content || []);
    } catch (err) {
      setError(err.message || 'Unable to load pets right now.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPets();
  }, [queryParams.name, queryParams.breed, queryParams.species, queryParams.age, queryParams.adoptionStatus]);

  const handleFieldChange = (field, value) => {
    setFilters((prev) => ({ ...prev, [field]: value }));
  };

  const clearFilters = () => setFilters(initialFilters);

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '1rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
        <div>
          <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Pet catalog</p>
          <h1 style={{ marginTop: '0.35rem', fontSize: '2.4rem' }}>Browse available pets</h1>
        </div>
      </div>

      <div className="card" style={{ padding: '1.5rem', marginBottom: '1.5rem' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1rem', alignItems: 'end' }}>
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label" htmlFor="pet-name">Pet name</label>
            <div style={{ position: 'relative' }}>
              <Search size={16} style={{ position: 'absolute', top: '50%', left: '0.75rem', transform: 'translateY(-50%)', color: 'var(--slate-400)' }} />
              <input
                id="pet-name"
                className="form-control"
                value={filters.name}
                onChange={(e) => handleFieldChange('name', e.target.value)}
                placeholder="Search by name"
                style={{ paddingLeft: '2.3rem' }}
              />
            </div>
          </div>

          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label" htmlFor="pet-breed">Breed</label>
            <input id="pet-breed" className="form-control" value={filters.breed} onChange={(e) => handleFieldChange('breed', e.target.value)} placeholder="e.g. Labrador" />
          </div>

          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label" htmlFor="pet-species">Species</label>
            <input id="pet-species" className="form-control" value={filters.species} onChange={(e) => handleFieldChange('species', e.target.value)} placeholder="e.g. Dog" />
          </div>

          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label" htmlFor="pet-age">Age</label>
            <input id="pet-age" type="number" min="0" className="form-control" value={filters.age} onChange={(e) => handleFieldChange('age', e.target.value)} placeholder="Age" />
          </div>

          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label" htmlFor="pet-status">Adoption status</label>
            <select id="pet-status" className="form-select" value={filters.adoptionStatus} onChange={(e) => handleFieldChange('adoptionStatus', e.target.value)}>
              <option value="">All statuses</option>
              <option value="AVAILABLE">AVAILABLE</option>
              <option value="PENDING">PENDING</option>
              <option value="ADOPTED">ADOPTED</option>
            </select>
          </div>

          <button type="button" className="btn btn-secondary" onClick={clearFilters}>
            <RotateCcw size={16} />
            Clear filters
          </button>
        </div>
      </div>

      {error ? (
        <ErrorMessage message={error} onRetry={loadPets} />
      ) : loading ? (
        <Loading text="Loading pets..." />
      ) : (
        <PetGrid pets={pets} loading={false} error={''} />
      )}
    </div>
  );
};

export default Pets;

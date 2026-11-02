import React, { useEffect, useState } from 'react';
import PetGrid from '../components/PetGrid';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import favoriteService from '../services/favoriteService';

const Favorites = () => {
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadFavorites = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await favoriteService.getFavorites();
      const favoritePets = Array.isArray(response) ? response.map((fav) => fav.pet).filter(Boolean) : [];
      setPets(favoritePets);
    } catch (err) {
      setError(err.message || 'Unable to load your favorite pets.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadFavorites();
  }, []);

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '1rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
        <div>
          <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Saved pets</p>
          <h1 style={{ marginTop: '0.35rem', fontSize: '2.2rem' }}>My favorites</h1>
        </div>
      </div>

      {error ? <ErrorMessage message={error} onRetry={loadFavorites} /> : loading ? <Loading text="Loading favorites..." /> : <PetGrid pets={pets} loading={false} error={''} />}
    </div>
  );
};

export default Favorites;

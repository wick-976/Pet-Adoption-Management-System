import React from 'react';
import PetCard from './PetCard';
import { SkeletonCard } from './Loading';
import { PawPrint, Search } from 'lucide-react';

export const PetGrid = ({ pets, loading, error, skeletonCount = 6, onFavoriteToggle }) => {
  const gridStyle = {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
    gap: '1.5rem',
  };

  if (loading) {
    return (
      <div style={gridStyle}>
        {Array.from({ length: skeletonCount }).map((_, i) => (
          <SkeletonCard key={i} />
        ))}
      </div>
    );
  }

  if (error) return null;

  if (!pets || pets.length === 0) {
    return (
      <div
        style={{
          textAlign: 'center',
          padding: '4rem 1rem',
          color: 'var(--slate-500)',
        }}
      >
        <div
          style={{
            width: '5rem',
            height: '5rem',
            borderRadius: '50%',
            backgroundColor: 'var(--slate-100)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 1.25rem',
          }}
        >
          <Search size={40} style={{ color: 'var(--slate-400)' }} />
        </div>
        <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--slate-700)', marginBottom: '0.5rem' }}>
          No Pets Found
        </h3>
        <p style={{ fontSize: '0.9375rem', color: 'var(--slate-500)', marginBottom: '0.5rem' }}>
          Try adjusting your search filters or browse all available pets.
        </p>
      </div>
    );
  }

  return (
    <div style={gridStyle}>
      {pets.map((pet) => (
        <PetCard key={pet.petId} pet={pet} onFavoriteToggle={onFavoriteToggle} />
      ))}
    </div>
  );
};

export default PetGrid;

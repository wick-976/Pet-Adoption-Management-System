import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Heart, MapPin, Calendar, Eye, ChevronRight, PawPrint } from 'lucide-react';
import StatusBadge from './StatusBadge';
import { useAuth } from '../context/AuthContext';
import { favoriteService } from '../services/favoriteService';

const PLACEHOLDER_IMG = 'https://placehold.co/400x300/e2e8f0/94a3b8?text=No+Image';

export const PetCard = ({ pet, onFavoriteToggle, isFavorited = false, showFavorite = true }) => {
  const { isAuthenticated } = useAuth();
  const [favorited, setFavorited] = useState(isFavorited);
  const [favLoading, setFavLoading] = useState(false);
  const [imgError, setImgError] = useState(false);

  const handleFavoriteClick = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (!isAuthenticated || favLoading) return;

    setFavLoading(true);
    try {
      if (favorited) {
        await favoriteService.removeFavorite(pet.petId);
        setFavorited(false);
      } else {
        await favoriteService.addFavorite(pet.petId);
        setFavorited(true);
      }
      onFavoriteToggle && onFavoriteToggle(pet.petId, !favorited);
    } catch {
      // silently fail
    } finally {
      setFavLoading(false);
    }
  };

  const imageUrl = (!imgError && pet.primaryImageUrl) ? pet.primaryImageUrl : PLACEHOLDER_IMG;

  return (
    <div className="card card-hover" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      {/* Image */}
      <div style={{ position: 'relative', overflow: 'hidden', height: '210px', flexShrink: 0 }}>
        <img
          src={imageUrl}
          alt={`${pet.name} - ${pet.breed || pet.species}`}
          onError={() => setImgError(true)}
          style={{
            width: '100%',
            height: '100%',
            objectFit: 'cover',
            transition: 'transform 0.4s ease',
          }}
          className="pet-card-img"
        />
        {/* Favorite Button */}
        {showFavorite && (
          <button
            onClick={handleFavoriteClick}
            disabled={!isAuthenticated || favLoading}
            aria-label={favorited ? 'Remove from favorites' : 'Add to favorites'}
            style={{
              position: 'absolute',
              top: '0.75rem',
              right: '0.75rem',
              width: '2.25rem',
              height: '2.25rem',
              borderRadius: '50%',
              border: 'none',
              cursor: isAuthenticated ? 'pointer' : 'default',
              backgroundColor: 'rgba(255,255,255,0.9)',
              backdropFilter: 'blur(4px)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              transition: 'all 0.2s ease',
              boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
            }}
          >
            <Heart
              size={17}
              style={{
                color: favorited ? 'var(--rose-500)' : 'var(--slate-400)',
                fill: favorited ? 'var(--rose-500)' : 'transparent',
                transition: 'all 0.2s ease',
              }}
            />
          </button>
        )}
        {/* Status Badge Overlay */}
        <div style={{ position: 'absolute', bottom: '0.625rem', left: '0.625rem' }}>
          <StatusBadge status={pet.adoptionStatus} />
        </div>
        {/* View Count */}
        {pet.viewCount != null && (
          <div
            style={{
              position: 'absolute',
              bottom: '0.625rem',
              right: '0.625rem',
              display: 'flex',
              alignItems: 'center',
              gap: '0.25rem',
              backgroundColor: 'rgba(0,0,0,0.5)',
              color: '#ffffff',
              padding: '0.2rem 0.5rem',
              borderRadius: 'var(--radius-full)',
              fontSize: '0.75rem',
              fontWeight: 600,
            }}
          >
            <Eye size={12} />
            {pet.viewCount}
          </div>
        )}
      </div>

      {/* Body */}
      <div style={{ padding: '1.25rem', display: 'flex', flexDirection: 'column', flex: 1, gap: '0.625rem' }}>
        <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '0.5rem' }}>
          <h3 style={{ fontSize: '1.125rem', fontWeight: 700, color: 'var(--slate-900)', margin: 0 }}>
            {pet.name}
          </h3>
        </div>

        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.625rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', fontSize: '0.8125rem', color: 'var(--slate-500)' }}>
            <PawPrint size={13} />
            <span>{pet.species}</span>
          </div>
          {pet.breed && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', fontSize: '0.8125rem', color: 'var(--slate-500)' }}>
              <MapPin size={13} />
              <span>{pet.breed}</span>
            </div>
          )}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', fontSize: '0.8125rem', color: 'var(--slate-500)' }}>
            <Calendar size={13} />
            <span>{pet.age} {pet.age === 1 ? 'yr' : 'yrs'}</span>
          </div>
        </div>

        {pet.description && (
          <p
            style={{
              fontSize: '0.875rem',
              color: 'var(--slate-600)',
              lineHeight: 1.6,
              display: '-webkit-box',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
              overflow: 'hidden',
              margin: 0,
            }}
          >
            {pet.description}
          </p>
        )}

        {/* Actions */}
        <div style={{ marginTop: 'auto', paddingTop: '0.875rem', display: 'flex', gap: '0.625rem' }}>
          <Link
            to={`/pets/${pet.petId}`}
            className="btn btn-primary"
            style={{ flex: 1, fontSize: '0.875rem' }}
          >
            View Details
            <ChevronRight size={16} />
          </Link>
          {pet.adoptionStatus === 'AVAILABLE' && (
            <Link
              to={`/pets/${pet.petId}/adopt`}
              className="btn btn-outline-primary btn-sm"
              style={{ fontSize: '0.8125rem', whiteSpace: 'nowrap' }}
              title="Apply to Adopt"
            >
              Adopt
            </Link>
          )}
        </div>
      </div>

      <style>{`
        .pet-card-img:hover {
          transform: scale(1.05);
        }
      `}</style>
    </div>
  );
};

export default PetCard;

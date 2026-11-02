import React, { useState } from 'react';
import { ChevronLeft, ChevronRight, ZoomIn, X } from 'lucide-react';

const PLACEHOLDER_IMG = 'https://placehold.co/800x500/e2e8f0/94a3b8?text=No+Image+Available';

export const PetImageGallery = ({ images = [], primaryImageUrl, petName = 'Pet' }) => {
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [lightboxOpen, setLightboxOpen] = useState(false);
  const [imgError, setImgError] = useState({});

  // Build deduplicated image list
  const imageList = (() => {
    if (images && images.length > 0) return images;
    if (primaryImageUrl) return [{ imageId: 0, imageUrl: primaryImageUrl, isPrimary: true }];
    return [];
  })();

  const currentImage = imageList[selectedIndex];
  const displayUrl = (!imgError[selectedIndex] && currentImage?.imageUrl) ? currentImage.imageUrl : PLACEHOLDER_IMG;

  const goTo = (idx) => setSelectedIndex((idx + imageList.length) % imageList.length);

  if (imageList.length === 0) {
    return (
      <div
        style={{
          borderRadius: 'var(--radius-xl)',
          overflow: 'hidden',
          backgroundColor: 'var(--slate-100)',
          height: '360px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: 'var(--slate-400)',
          fontSize: '0.9375rem',
          fontWeight: 500,
        }}
      >
        No images available
      </div>
    );
  }

  return (
    <div>
      {/* Main Image */}
      <div
        style={{ position: 'relative', borderRadius: 'var(--radius-xl)', overflow: 'hidden', height: '380px', cursor: 'zoom-in', backgroundColor: 'var(--slate-100)' }}
        onClick={() => setLightboxOpen(true)}
      >
        <img
          src={displayUrl}
          alt={`${petName} - image ${selectedIndex + 1}`}
          onError={() => setImgError((p) => ({ ...p, [selectedIndex]: true }))}
          style={{ width: '100%', height: '100%', objectFit: 'cover', transition: 'transform 0.3s ease' }}
        />
        <div
          style={{
            position: 'absolute',
            inset: 0,
            background: 'linear-gradient(to top, rgba(0,0,0,0.3) 0%, transparent 50%)',
            pointerEvents: 'none',
          }}
        />
        <button
          aria-label="View full size"
          style={{
            position: 'absolute',
            top: '0.75rem',
            right: '0.75rem',
            backgroundColor: 'rgba(255,255,255,0.85)',
            border: 'none',
            borderRadius: 'var(--radius-md)',
            width: '2.25rem',
            height: '2.25rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            color: 'var(--slate-700)',
          }}
          onClick={(e) => { e.stopPropagation(); setLightboxOpen(true); }}
        >
          <ZoomIn size={18} />
        </button>

        {imageList.length > 1 && (
          <>
            <button
              onClick={(e) => { e.stopPropagation(); goTo(selectedIndex - 1); }}
              aria-label="Previous image"
              style={navBtnStyle('left')}
            >
              <ChevronLeft size={22} />
            </button>
            <button
              onClick={(e) => { e.stopPropagation(); goTo(selectedIndex + 1); }}
              aria-label="Next image"
              style={navBtnStyle('right')}
            >
              <ChevronRight size={22} />
            </button>
          </>
        )}

        {imageList.length > 1 && (
          <div style={{ position: 'absolute', bottom: '0.75rem', left: '50%', transform: 'translateX(-50%)', display: 'flex', gap: '0.375rem' }}>
            {imageList.map((_, i) => (
              <span
                key={i}
                style={{
                  width: i === selectedIndex ? '1.5rem' : '0.5rem',
                  height: '0.5rem',
                  borderRadius: 'var(--radius-full)',
                  backgroundColor: i === selectedIndex ? '#ffffff' : 'rgba(255,255,255,0.5)',
                  transition: 'all 0.2s ease',
                  cursor: 'pointer',
                }}
                onClick={(e) => { e.stopPropagation(); setSelectedIndex(i); }}
              />
            ))}
          </div>
        )}
      </div>

      {/* Thumbnails */}
      {imageList.length > 1 && (
        <div style={{ display: 'flex', gap: '0.625rem', marginTop: '0.75rem', flexWrap: 'wrap' }}>
          {imageList.map((img, i) => (
            <button
              key={img.imageId ?? i}
              onClick={() => setSelectedIndex(i)}
              aria-label={`View image ${i + 1}`}
              style={{
                width: '68px',
                height: '52px',
                borderRadius: 'var(--radius-md)',
                overflow: 'hidden',
                border: `2px solid ${i === selectedIndex ? 'var(--primary-500)' : 'var(--slate-200)'}`,
                padding: 0,
                cursor: 'pointer',
                transition: 'border-color 0.15s ease',
                backgroundColor: 'var(--slate-100)',
              }}
            >
              <img
                src={imgError[i] ? PLACEHOLDER_IMG : img.imageUrl}
                alt={`Thumbnail ${i + 1}`}
                onError={() => setImgError((p) => ({ ...p, [i]: true }))}
                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
              />
            </button>
          ))}
        </div>
      )}

      {/* Lightbox */}
      {lightboxOpen && (
        <div
          onClick={() => setLightboxOpen(false)}
          style={{
            position: 'fixed',
            inset: 0,
            backgroundColor: 'rgba(0,0,0,0.92)',
            zIndex: 2000,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '1rem',
          }}
        >
          <button
            onClick={() => setLightboxOpen(false)}
            style={{
              position: 'absolute',
              top: '1rem',
              right: '1rem',
              backgroundColor: 'rgba(255,255,255,0.15)',
              border: 'none',
              borderRadius: '50%',
              width: '2.5rem',
              height: '2.5rem',
              color: '#ffffff',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
            aria-label="Close lightbox"
          >
            <X size={22} />
          </button>
          <img
            src={displayUrl}
            alt={`${petName} full size`}
            style={{ maxWidth: '90vw', maxHeight: '90vh', objectFit: 'contain', borderRadius: 'var(--radius-lg)' }}
            onClick={(e) => e.stopPropagation()}
          />
          {imageList.length > 1 && (
            <>
              <button onClick={(e) => { e.stopPropagation(); goTo(selectedIndex - 1); }} style={{ ...navBtnStyle('left'), top: '50%', transform: 'translateY(-50%)' }} aria-label="Previous">
                <ChevronLeft size={28} />
              </button>
              <button onClick={(e) => { e.stopPropagation(); goTo(selectedIndex + 1); }} style={{ ...navBtnStyle('right'), top: '50%', transform: 'translateY(-50%)' }} aria-label="Next">
                <ChevronRight size={28} />
              </button>
            </>
          )}
        </div>
      )}
    </div>
  );
};

const navBtnStyle = (side) => ({
  position: 'absolute',
  top: '50%',
  [side === 'left' ? 'left' : 'right']: '0.75rem',
  transform: 'translateY(-50%)',
  backgroundColor: 'rgba(255,255,255,0.85)',
  border: 'none',
  borderRadius: '50%',
  width: '2.5rem',
  height: '2.5rem',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  cursor: 'pointer',
  color: 'var(--slate-700)',
  boxShadow: 'var(--shadow-md)',
  transition: 'background-color 0.15s ease',
});

export default PetImageGallery;

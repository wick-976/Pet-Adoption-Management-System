import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, HeartHandshake, ShieldCheck, Sparkles, PawPrint, Building2, Gift, Stethoscope } from 'lucide-react';
import PetCard from '../components/PetCard';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import petService from '../services/petService';
import shelterService from '../services/shelterService';

const Home = () => {
  const [featuredPets, setFeaturedPets] = useState([]);
  const [shelter, setShelter] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let isMounted = true;

    const loadHomeData = async () => {
      try {
        setLoading(true);
        const [petsResponse, shelterResponse] = await Promise.all([
          petService.getMostViewedPets(3),
          shelterService.getShelterInfo(),
        ]);

        if (!isMounted) return;
        setFeaturedPets(Array.isArray(petsResponse) ? petsResponse : []);
        setShelter(shelterResponse || null);
      } catch (err) {
        if (!isMounted) return;
        setError(err.message || 'Unable to load the homepage right now.');
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    loadHomeData();
    return () => { isMounted = false; };
  }, []);

  if (loading) return <Loading text="Loading Pet Haven..." fullScreen />;
  if (error) return <ErrorMessage message={error} onRetry={() => window.location.reload()} />;

  return (
    <div className="container" style={{ paddingTop: '2rem', paddingBottom: '3rem' }}>
      <section style={{
        background: 'linear-gradient(135deg, rgba(16,185,129,0.12), rgba(59,130,246,0.06), rgba(255,255,255,1))',
        borderRadius: '2rem',
        border: '1px solid var(--slate-200)',
        overflow: 'hidden',
        padding: '2rem',
        boxShadow: 'var(--shadow-md)',
      }}>
        <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 0.8fr', gap: '2rem', alignItems: 'center' }}>
          <div>
            <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', padding: '0.5rem 0.85rem', borderRadius: '999px', backgroundColor: 'rgba(16,185,129,0.1)', color: 'var(--primary-700)', fontWeight: 700, fontSize: '0.8rem', marginBottom: '1rem' }}>
              <Sparkles size={16} />
              Compassion in action
            </div>
            <h1 style={{ fontSize: 'clamp(2.3rem, 5vw, 4.2rem)', lineHeight: 1.05, marginBottom: '1rem' }}>
              Find a loving home for every pet.
            </h1>
            <p style={{ fontSize: '1.05rem', color: 'var(--slate-600)', maxWidth: '620px', marginBottom: '1.5rem' }}>
              Browse rescue pets, connect with our shelter team, and help create happier stories one adoption at a time.
            </p>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem' }}>
              <Link to="/pets" className="btn btn-primary btn-lg">
                Browse Pets
                <ArrowRight size={18} />
              </Link>
              <Link to="/donate" className="btn btn-secondary btn-lg">
                <Gift size={18} />
                Support the Shelter
              </Link>
            </div>
            <div style={{ display: 'flex', gap: '1.25rem', flexWrap: 'wrap', marginTop: '1.75rem' }}>
              <div>
                <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--slate-900)' }}>200+</div>
                <div style={{ color: 'var(--slate-500)', fontSize: '0.87rem' }}>Happy adoptions</div>
              </div>
              <div>
                <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--slate-900)' }}>24/7</div>
                <div style={{ color: 'var(--slate-500)', fontSize: '0.87rem' }}>Care support</div>
              </div>
              <div>
                <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--slate-900)' }}>96%</div>
                <div style={{ color: 'var(--slate-500)', fontSize: '0.87rem' }}>Placement success</div>
              </div>
            </div>
          </div>

          <div className="card" style={{ padding: '1.5rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.25rem' }}>
              <div>
                <p style={{ margin: 0, color: 'var(--slate-500)', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 700, fontSize: '0.7rem' }}>Featured rescue</p>
                <h3 style={{ margin: '0.25rem 0 0', fontSize: '1.6rem' }}>{featuredPets[0]?.name || 'Meet our pets'}</h3>
              </div>
              <div style={{ width: '3rem', height: '3rem', borderRadius: '50%', backgroundColor: 'var(--primary-100)', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <PawPrint size={20} />
              </div>
            </div>
            <img
              src={featuredPets[0]?.primaryImageUrl || 'https://placehold.co/800x600/e2e8f0/94a3b8?text=Pet+Haven'}
              alt={featuredPets[0]?.name ? `${featuredPets[0].name} photo` : 'Featured pet'}
              style={{ width: '100%', height: '280px', objectFit: 'cover', borderRadius: '1rem', marginBottom: '1rem' }}
            />
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
              <span style={{ color: 'var(--slate-500)', fontSize: '0.8rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.06em' }}>{featuredPets[0]?.species || 'Pet'}</span>
              <span style={{ color: 'var(--primary-700)', fontWeight: 700 }}>{featuredPets[0]?.breed || 'Ready for a home'}</span>
            </div>
            <p style={{ color: 'var(--slate-600)', marginBottom: '1rem' }}>
              {featuredPets[0]?.description || 'Find a companion who is eager to make your house feel like home.'}
            </p>
            <Link to={featuredPets[0] ? `/pets/${featuredPets[0].petId}` : '/pets'} className="btn btn-secondary" style={{ width: '100%' }}>
              Meet {featuredPets[0]?.name || 'Them'}
            </Link>
          </div>
        </div>
      </section>

      <section style={{ marginTop: '3.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '1rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
          <div>
            <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Featured pets</p>
            <h2 style={{ marginTop: '0.35rem', fontSize: '2rem' }}>Available to adopt</h2>
          </div>
          <Link to="/pets" className="btn btn-outline-primary">
            View all pets
            <ArrowRight size={16} />
          </Link>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1.5rem' }}>
          {featuredPets.map((pet) => (
            <PetCard key={pet.petId} pet={pet} showFavorite={false} />
          ))}
        </div>
      </section>

      <section style={{ marginTop: '3.5rem' }}>
        <div className="card" style={{ padding: '2rem' }}>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1.25rem' }}>
            <div style={{ display: 'flex', gap: '0.9rem', alignItems: 'flex-start' }}>
              <div style={{ width: '2.75rem', height: '2.75rem', borderRadius: '50%', backgroundColor: 'var(--primary-100)', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <ShieldCheck size={18} />
              </div>
              <div>
                <h3 style={{ marginBottom: '0.35rem' }}>Safe placements</h3>
                <p>Every pet is reviewed with care, health checks, and adoption guidance to support long-term matches.</p>
              </div>
            </div>
            <div style={{ display: 'flex', gap: '0.9rem', alignItems: 'flex-start' }}>
              <div style={{ width: '2.75rem', height: '2.75rem', borderRadius: '50%', backgroundColor: 'var(--amber-100)', color: 'var(--amber-700)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <HeartHandshake size={18} />
              </div>
              <div>
                <h3 style={{ marginBottom: '0.35rem' }}>Supportive adoption process</h3>
                <p>We help families and pets connect with the right match, at the right pace, and with the right support.</p>
              </div>
            </div>
            <div style={{ display: 'flex', gap: '0.9rem', alignItems: 'flex-start' }}>
              <div style={{ width: '2.75rem', height: '2.75rem', borderRadius: '50%', backgroundColor: 'var(--indigo-100)', color: 'var(--indigo-600)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Stethoscope size={18} />
              </div>
              <div>
                <h3 style={{ marginBottom: '0.35rem' }}>Health-aware care</h3>
                <p>Our staff tracks pet health details, updates, and readiness so adopters can make informed choices.</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section style={{ marginTop: '3.5rem' }}>
        <div style={{ display: 'grid', gridTemplateColumns: '0.95fr 1.05fr', gap: '1.75rem' }}>
          <div className="card" style={{ padding: '2rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
              <div style={{ width: '2.5rem', height: '2.5rem', borderRadius: '50%', backgroundColor: 'var(--primary-100)', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Building2 size={18} />
              </div>
              <h2 style={{ margin: 0 }}>Our shelter</h2>
            </div>
            <h3 style={{ marginBottom: '0.75rem' }}>{shelter?.name || 'Pet Haven Shelter'}</h3>
            <p style={{ marginBottom: '0.75rem' }}>{shelter?.description || 'A place where pets receive love, medical care, and a second chance at a forever family.'}</p>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.5rem', color: 'var(--slate-600)' }}>
              {shelter?.address && <li>📍 {shelter.address}</li>}
              {shelter?.phone && <li>📞 {shelter.phone}</li>}
              {shelter?.email && <li>✉️ {shelter.email}</li>}
              {shelter?.operatingHours && <li>🕒 {shelter.operatingHours}</li>}
            </ul>
            <Link to="/shelter" className="btn btn-secondary" style={{ marginTop: '1.25rem' }}>
              Visit shelter info
            </Link>
          </div>

          <div className="card" style={{ padding: '2rem' }}>
            <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Adoption process</p>
            <h2 style={{ marginTop: '0.35rem', marginBottom: '1.25rem' }}>How it works</h2>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1rem' }}>
              {[
                { step: '1', title: 'Browse pets', desc: 'Explore available companions and check their profiles.' },
                { step: '2', title: 'Apply', desc: 'Submit a simple adoption request with your details.' },
                { step: '3', title: 'Review', desc: 'Our team reviews the request and updates the status.' },
                { step: '4', title: 'Welcome home', desc: 'Approved adopters meet their new friend and begin a new chapter.' },
              ].map((item) => (
                <div key={item.step} style={{ padding: '1rem', borderRadius: '1rem', backgroundColor: 'var(--slate-50)', border: '1px solid var(--slate-200)' }}>
                  <div style={{ width: '2rem', height: '2rem', borderRadius: '50%', backgroundColor: 'var(--primary-600)', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700, marginBottom: '0.8rem' }}>{item.step}</div>
                  <h3 style={{ marginBottom: '0.35rem', fontSize: '1.05rem' }}>{item.title}</h3>
                  <p style={{ margin: 0, fontSize: '0.88rem' }}>{item.desc}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      <section style={{ marginTop: '3.5rem' }}>
        <div className="card" style={{ background: 'linear-gradient(135deg, rgba(16,185,129,0.08), rgba(59,130,246,0.06))', padding: '2rem', textAlign: 'center' }}>
          <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Support our mission</p>
          <h2 style={{ marginTop: '0.35rem', marginBottom: '0.8rem' }}>Every donation helps fund rescue, care, and adoption support.</h2>
          <p style={{ maxWidth: '720px', margin: '0 auto 1.25rem' }}>
            Your generosity helps cover food, shelter, medical care, and enrichment for the pets waiting for their forever homes.
          </p>
          <Link to="/donate" className="btn btn-primary btn-lg">
            <Gift size={18} />
            Make a donation
          </Link>
        </div>
      </section>
    </div>
  );
};

export default Home;

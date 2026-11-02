import React, { useEffect, useState } from 'react';
import { PlusCircle, Pencil, Trash2 } from 'lucide-react';
import petService from '../services/petService';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import StatusBadge from '../components/StatusBadge';

const emptyForm = {
  name: '',
  species: '',
  breed: '',
  age: '',
  description: '',
  vaccinationStatus: '',
  spayNeuterStatus: '',
  specialMedicalNeeds: '',
  initialImageUrl: '',
};

const ManagePets = () => {
  const [pets, setPets] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const loadPets = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await petService.searchPetsStaff({ page: 0, size: 50 });
      setPets(response?.content || []);
    } catch (err) {
      setError(err.message || 'Unable to load pets.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPets();
  }, []);

  const handleChange = (field, value) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const resetForm = () => {
    setForm(emptyForm);
    setEditingId(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');

    try {
      const payload = {
        ...form,
        age: Number(form.age),
      };

      if (editingId) {
        await petService.updatePet(editingId, payload);
      } else {
        await petService.createPet(payload);
      }
      resetForm();
      await loadPets();
    } catch (err) {
      setError(err.message || 'Unable to save pet details.');
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (pet) => {
    setEditingId(pet.petId);
    setForm({
      name: pet.name || '',
      species: pet.species || '',
      breed: pet.breed || '',
      age: pet.age ?? '',
      description: pet.description || '',
      vaccinationStatus: pet.vaccinationStatus || '',
      spayNeuterStatus: pet.spayNeuterStatus || '',
      specialMedicalNeeds: pet.specialMedicalNeeds || '',
      initialImageUrl: pet.primaryImageUrl || '',
    });
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this pet listing?')) return;
    try {
      await petService.deletePet(id);
      await loadPets();
    } catch (err) {
      setError(err.message || 'Unable to delete pet.');
    }
  };

  const handleStatusChange = async (id, status) => {
    try {
      await petService.updatePetStatus(id, status);
      await loadPets();
    } catch (err) {
      setError(err.message || 'Unable to update pet status.');
    }
  };

  if (loading) return <Loading text="Loading pet management..." fullScreen />;

  return (
    <div className="container" style={{ paddingTop: '1.5rem', paddingBottom: '3rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '1rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
        <div>
          <p style={{ margin: 0, color: 'var(--primary-700)', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', fontSize: '0.7rem' }}>Pet management</p>
          <h1 style={{ marginTop: '0.35rem', fontSize: '2.2rem' }}>Manage pets</h1>
        </div>
      </div>

      {error && <ErrorMessage message={error} />}

      <div className="card" style={{ padding: '1.5rem', marginBottom: '1.5rem' }}>
        <h3 style={{ marginBottom: '1rem' }}>{editingId ? 'Edit pet' : 'Add a new pet'}</h3>
        <form onSubmit={handleSubmit}>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1rem' }}>
            <div className="form-group"><label className="form-label">Name</label><input className="form-control" value={form.name} onChange={(e) => handleChange('name', e.target.value)} required /></div>
            <div className="form-group"><label className="form-label">Species</label><input className="form-control" value={form.species} onChange={(e) => handleChange('species', e.target.value)} required /></div>
            <div className="form-group"><label className="form-label">Breed</label><input className="form-control" value={form.breed} onChange={(e) => handleChange('breed', e.target.value)} /></div>
            <div className="form-group"><label className="form-label">Age</label><input type="number" min="0" className="form-control" value={form.age} onChange={(e) => handleChange('age', e.target.value)} required /></div>
            <div className="form-group"><label className="form-label">Vaccination status</label><input className="form-control" value={form.vaccinationStatus} onChange={(e) => handleChange('vaccinationStatus', e.target.value)} /></div>
            <div className="form-group"><label className="form-label">Spay/neuter</label><input className="form-control" value={form.spayNeuterStatus} onChange={(e) => handleChange('spayNeuterStatus', e.target.value)} /></div>
            <div className="form-group" style={{ gridColumn: '1 / -1' }}><label className="form-label">Description</label><textarea className="form-textarea" rows="3" value={form.description} onChange={(e) => handleChange('description', e.target.value)} /></div>
            <div className="form-group" style={{ gridColumn: '1 / -1' }}><label className="form-label">Initial image URL</label><input className="form-control" value={form.initialImageUrl} onChange={(e) => handleChange('initialImageUrl', e.target.value)} /></div>
            <div className="form-group" style={{ gridColumn: '1 / -1' }}><label className="form-label">Special medical needs</label><textarea className="form-textarea" rows="2" value={form.specialMedicalNeeds} onChange={(e) => handleChange('specialMedicalNeeds', e.target.value)} /></div>
          </div>
          <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1rem' }}>
            <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? 'Saving...' : editingId ? 'Update pet' : 'Add pet'}</button>
            {editingId && <button type="button" className="btn btn-secondary" onClick={resetForm}>Cancel</button>}
          </div>
        </form>
      </div>

      <div className="card" style={{ overflow: 'hidden' }}>
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Species</th>
                <th>Status</th>
                <th>Age</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {pets.length === 0 ? (
                <tr><td colSpan="5" style={{ textAlign: 'center', padding: '2rem', color: 'var(--slate-500)' }}>No pets found.</td></tr>
              ) : pets.map((pet) => (
                <tr key={pet.petId}>
                  <td>{pet.name}</td>
                  <td>{pet.species}</td>
                  <td>
                    <select className="form-select" value={pet.adoptionStatus || 'AVAILABLE'} onChange={(e) => handleStatusChange(pet.petId, e.target.value)}>
                      <option value="AVAILABLE">AVAILABLE</option>
                      <option value="PENDING">PENDING</option>
                      <option value="ADOPTED">ADOPTED</option>
                    </select>
                  </td>
                  <td>{pet.age}</td>
                  <td>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <button className="btn btn-sm btn-secondary" onClick={() => handleEdit(pet)}><Pencil size={14} /></button>
                      <button className="btn btn-sm btn-danger" onClick={() => handleDelete(pet.petId)}><Trash2 size={14} /></button>
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

export default ManagePets;

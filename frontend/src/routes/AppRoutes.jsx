import React from 'react';
import { Routes, Route } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';

import Home from '../pages/Home';
import Pets from '../pages/Pets';
import PetDetails from '../pages/PetDetails';
import AdoptionRequest from '../pages/AdoptionRequest';
import Shelter from '../pages/Shelter';
import Donations from '../pages/Donations';
import Login from '../pages/Login';
import Register from '../pages/Register';
import Favorites from '../pages/Favorites';
import MyRequests from '../pages/MyRequests';
import AdminDashboard from '../pages/AdminDashboard';
import ManagePets from '../pages/ManagePets';
import ManageAdoptionRequests from '../pages/ManageAdoptionRequests';
import ManageShelter from '../pages/ManageShelter';
import Analytics from '../pages/Analytics';
import StaffRegister from '../pages/StaffRegister';

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/pets" element={<Pets />} />
      <Route path="/pets/:id" element={<PetDetails />} />
      <Route path="/pets/:id/adopt" element={<AdoptionRequest />} />
      <Route path="/shelter" element={<Shelter />} />
      <Route path="/donate" element={<Donations />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      <Route
        path="/favorites"
        element={
          <ProtectedRoute allowedRoles={['ROLE_USER', 'ROLE_STAFF', 'ROLE_ADMIN']}>
            <Favorites />
          </ProtectedRoute>
        }
      />

      <Route
        path="/my-requests"
        element={
          <ProtectedRoute allowedRoles={['ROLE_USER', 'ROLE_STAFF', 'ROLE_ADMIN']}>
            <MyRequests />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']}>
            <AdminDashboard />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/pets"
        element={
          <ProtectedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']}>
            <ManagePets />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/adoption-requests"
        element={
          <ProtectedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']}>
            <ManageAdoptionRequests />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/shelter"
        element={
          <ProtectedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']}>
            <ManageShelter />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/analytics"
        element={
          <ProtectedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ADMIN']}>
            <Analytics />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/staff-register"
        element={
          <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
            <StaffRegister />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
};

export default AppRoutes;

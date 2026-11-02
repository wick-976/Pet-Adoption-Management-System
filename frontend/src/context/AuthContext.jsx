import React, { createContext, useContext, useState, useEffect } from 'react';
import authService from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    try {
      const savedUser = localStorage.getItem('pet_user');
      return savedUser ? JSON.parse(savedUser) : null;
    } catch {
      return null;
    }
  });

  const [token, setToken] = useState(() => localStorage.getItem('pet_token') || null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const handleUnauthorized = () => {
      logout();
    };

    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
  }, []);

  const login = async (credentials) => {
    setLoading(true);
    try {
      const authData = await authService.login(credentials);
      // authData: { token, tokenType, id, username, role, expiresIn }
      const currentUser = {
        id: authData.id,
        username: authData.username,
        role: authData.role,
      };

      setToken(authData.token);
      setUser(currentUser);

      localStorage.setItem('pet_token', authData.token);
      localStorage.setItem('pet_user', JSON.stringify(currentUser));

      return currentUser;
    } finally {
      setLoading(false);
    }
  };

  const register = async (userData) => {
    setLoading(true);
    try {
      return await authService.registerUser(userData);
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('pet_token');
    localStorage.removeItem('pet_user');
  };

  const isAuthenticated = !!token && !!user;
  const isStaff = user?.role === 'ROLE_STAFF' || user?.role === 'ROLE_ADMIN';
  const isAdmin = user?.role === 'ROLE_ADMIN';
  const isUser = user?.role === 'ROLE_USER';

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loading,
        isAuthenticated,
        isStaff,
        isAdmin,
        isUser,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;

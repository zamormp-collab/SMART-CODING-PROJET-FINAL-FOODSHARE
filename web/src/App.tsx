import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './Context/AuthContext';
import AuthContainer from './components/AuthContainer';
import DashboardPage from './pages/DashboardPage';
import OffreDetailPage from './pages/OffreDetailPage';
import CreerOffrePage from './pages/CreerOffrePage';
import ModifierOffrePage from './pages/ModifierOffrePage';
import ReservationsPage from './pages/ReservationsPage';


const PrivateRoute: React.FC<{ children: React.JSX.Element }> = ({ children }) => {
  const { token } = useAuth();
  return token ? children : <Navigate to="/login" replace />;
};

const App: React.FC = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>

          {/*  Route publique  */}
          <Route path="/login" element={<AuthContainer />} />

          {/*  Routes protégées (token JWT requis) */}

          {/* Tableau de bord — liste des offres de l'offreur */}
          <Route path="/dashboard" element={
            <PrivateRoute><DashboardPage /></PrivateRoute>
          } />

          {/* Créer une nouvelle offre */}
          <Route path="/offres/creer" element={
            <PrivateRoute><CreerOffrePage /></PrivateRoute>
          } />

          {/* Modifier une offre existante (ACTIVE uniquement) */}
          <Route path="/offres/:id/modifier" element={
            <PrivateRoute><ModifierOffrePage /></PrivateRoute>
          } />

          {/* Détail d'une offre */}
          <Route path="/offres/:id" element={
            <PrivateRoute><OffreDetailPage /></PrivateRoute>
          } />

          {/* Réservations reçues pour une offre */}
          <Route path="/offres/:id/reservations" element={
            <PrivateRoute><ReservationsPage /></PrivateRoute>
          } />

          {/* Réservations reçues pour une offre */}
          <Route path="/offres/:id/reservations" element={
            <PrivateRoute><ReservationsPage /></PrivateRoute>
          } />

          {/* AJOUT — Toutes les réservations \\*/}
          <Route path="/reservations" element={
            <PrivateRoute><ReservationsPage /></PrivateRoute>
          } />

          {/* Fallback — toute URL inconnue → login */}
          <Route path="*" element={<Navigate to="/login" replace />} />


          {/* Fallback — toute URL inconnue → login */}
          <Route path="*" element={<Navigate to="/login" replace />} />

        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
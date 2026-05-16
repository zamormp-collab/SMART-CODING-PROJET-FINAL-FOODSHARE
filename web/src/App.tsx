import React from 'react';
import AuthContainer from './components/AuthContainer';

const App: React.FC = () => {
  return (
    <>
      {/* On appelle ici les conteneurs de connexion et d'inscription */}
      <AuthContainer />
    </>
  );
};

export default App;
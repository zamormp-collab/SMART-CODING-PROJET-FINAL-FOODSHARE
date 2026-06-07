# FoodShare — Back-office React (Web Lead)

## Présentation du projet> Interface d'administration pour les **offreurs** de la plateforme FoodShare,  
une application anti-gaspillage alimentaire connectant fournisseurs de nourriture et étudiants.
FoodShare est un projet académique Agile (SCRUM / JIRA / Git Flow) développé en équipe de 5 :

 Rôle :
| **Web Lead** |


Ce Readme couvre uniquement la **partie React** dédiée aux offreurs (restaurants, cantines, particuliers)

## Stack technique

| Outil |    Version          Rôle 

 React         19           Framework UI |
 TypeScript     6           Typage statique |
 Vite          8            Bundler / serveur de dev |
 Tailwind CSS  v4           Styles utilitaires |
 React Router  v7           Navigation SPA |
 ngrok         free tier    Tunnel local → Spring Boot |


## Architecture des fichiers
src/
├── App.tsx                        # Routeur principal + PrivateRoute
├── Context/
│   └── AuthContext.tsx            # Contexte JWT (login, logout, user)
├── components/
│   ├── AuthContainer.tsx          # Enveloppe Login / Inscription
│   ├── LoginForm.tsx              # Formulaire de connexion
│   ├── RegisterForm.tsx           # Formulaire d'inscription
│   └── Offres/
│       ├── OffreCard.tsx          # Carte d'une offre (image, statut, stock)
│       └── OffreList.tsx          # Grille des cartes + états vide / erreur
├── hooks/
│   └── useOffres.ts               # Hook custom CRUD offres
├── pages/
│   ├── DashboardPage.tsx          # Tableau de bord principal (liste + stats)
│   ├── CreerOffrePage.tsx         # Formulaire création d'offre
│   ├── ModifierOffrePage.tsx      # Formulaire modification d'offre
│   ├── OffreDetailPage.tsx        # Page détail d'une offre
│   └── ReservationsPage.tsx       # Liste des réservations reçues
├── services/
│   ├── api.ts                     # apiFetch — couche HTTP centralisée
│   ├── authService.ts             # Appels /auth/connexion et /auth/inscription
│   ├── OffreService.ts            # CRUD /offres
│   └── ReservationServices.tsx    # GET + PATCH réservations
└── types/
    └── index.ts                   # Interfaces TypeScript (Offre, Reservation, User…)
```


## Routes de l'application

 Chemin               Page               Protection 

| `/login`            AuthContainer       Publique  
| `/dashboard`        DashboardPage       JWT requis 
| `/offres/creer`     CreerOffrePage      JWT requis 
| `/offres/:id`       OffreDetailPage     JWT requis 
| `/offres/:id/modifier`  ModifierOffrePage  JWT requis 
| `/offres/:id/reservations`  ReservationsPage (offre unique)  JWT requis 
| `/reservations`  ReservationsPage (toutes) JWT requis 
| `*`Redirect → `/login` 

---

## 1. Installation et démarrage

### Prérequis
- Node.js ≥ 18
- Spring Boot API démarrée localement sur le port 8080
- ngrok lanceé

## 0. Cloner et installer

dans le terminal de Vscode taper les commandes :
git clone <url-du-repo>
cd web
npm install

## 2. Lancer le tunnel ngrok
Copier l'URL de forwarding (ex. `https://xxxx-xxxx.ngrok-free.app`) **sans slash final**.

## 3. Configurer le proxy Vite
Dans `vite.config.ts`, remplacer la cible du proxy :
proxy: {
  '/api': {
    target: 'https://VOTRE-URL-NGROK.ngrok-free.app',
    changeOrigin: true,
    secure: false,
  }
}
```

> ⚠️ L'URL ngrok change à chaque redémarrage du tunnel (version gratuite).  
> Penser à la mettre à jour dans `vite.config.ts` et redémarrer Vite.
## 4. Démarrer l'application
npm run dev

L'application est accessible sur http://localhost:5173.

## Données de démonstration (mode mock)

Lorsque le backend n'est pas disponible ou ne retourne aucune offre, le Dashboard affiche automatiquement **8 offres fictives** codées en dur dans `DashboardPage.tsx` :
- Croissants du matin, Soupe de légumes, Pizza margherita, Salade César
- Brownies chocolat, Sandwich jambon, Quiche lorraine, Tarte aux pommes

La logique de bascule est simple 
Quand Backend disponible les vraies données s'affiche Sinon les mocks visuels.
Ces mocks sont **temporaires**, prévus pour les démos visuelles en attente du merge backend.

## Fonctionnalités implémentées (MVP)

# 1- Authentification
- Connexion offreur avec email / mot de passe → JWT stocké dans `localStorage`
- Inscription : nom, prénom, email, téléphone, adresse, mot de passe (+ confirmation)
- Rôle fixé à `OFFREUR` côté inscription (champ lecture seule)
- Déconnexion propre (vide token + user en mémoire et localStorage)
- Restauration de session au rechargement (via `AuthContext`)
- Protection de toutes les routes privées via `PrivateRoute`
- Vérification d'e-mail disponible (`/auth/verification-email`)

# 2- Gestion des offres
- **Lister** ses offres (`GET /offres/mes-offres`) avec filtrage par statut (ACTIVE / EXPIREE / ANNULEE) et recherche textuelle
- **Créer** une offre (`POST /offres`) : titre, description, quantité, prix, lieu, créneau de retrait, image URL optionnelle avec aperçu temps réel
- **Modifier** une offre active (`PUT /offres/:id`) avec pré-remplissage du formulaire
- **Annuler** une offre (`DELETE /offres/:id`) — passe le statut à `ANNULEE` sans effacer la carte
- **Voir le détail** d'une offre : image, statut, barre de stock colorée, créneau de retrait formaté
- Statistiques cliquables sur le Dashboard (Total / Actives / Expirées / Portions disponibles)

# 3- Gestion des réservations
- Voir les réservations d'une offre spécifique (`GET /reservations/offres/:id`)
- Voir toutes les réservations de ses offres (agrégation parallèle)
- Filtre par offre en mode "toutes réservations"
- Marquer une réservation **Retirée** (`PATCH /reservations/:id/retiree`)
- Marquer une réservation **Non retirée** (`PATCH /reservations/:id/non-retiree`)
- Badges colorés par statut (EN_ATTENTE / RETIREE / NON_RETIREE)

### UI / UX
- Design sombre uniforme (palette brun foncé `#1e130c` + ambre `#d98236`)
- Sidebar de navigation avec état actif mis en surbrillance
- Navbar sticky avec avatar initiales + déconnexion
- États de chargement (spinner), d'erreur et d'état vide sur toutes les pages
- Bouton retour flottant orange circulaire (CreerOffre, ReservationsPage)
- Grille responsive des offres (1 → 2 → 3 → 4 colonnes selon l'écran)


# Commandes utiles

# Démarrer l'application
npm run dev

# Build de production
npm run build

# Vérification TypeScript + lint
npm run lint

# Libérer le port 5173 si bloqué (Windows)
taskkill /F /PID $(netstat -ano | findstr :5173 | awk '{print $5}')

# Libérer le port 5173 (cross-platform)
npx kill-port 5173


*Projet — Équipe 5 · Sprint React back-office · Web Lead*
// Les deux rôles possibles dans FoodShare
export type Role = 'OFFREUR' | 'ETUDIANT';

// Les 3 états d'une réservation
export type StatutReservation = 'EN_ATTENTE' | 'RETIREE' | 'NON_RETIREE';

// Les 3 états d'une offre (retournés par le backend)
export type StatutOffre = 'ACTIVE' | 'EXPIREE' | 'ANNULEE';

//  Interface User
export interface User {
    id: number;
    email: string;
    nom: string;
    prenom: string;
    role: Role;
    adresse: string;
    telephone: string;
    password: string;
    dateInscription?: string;
}

// Interface Offre
export interface Offre {
    id: number;
    titre: string;
    description: string;
    quantiteInitiale: number;
    quantiteRestante: number;
    prix: number;
    debutRetrait: string;
    finRetrait: string;
    lieu: string;
    offreurId: number;
    offreurNom?: string;
    offreurPrenom?: string;
    statutOffre: StatutOffre;
    dateCreation?: string;
    dateModification?: string;
    imageUrl?: string;
}

export type CreateOffrePayload = {
    titre: string;
    description: string;
    quantite: number;
    prix: number;
    debutRetrait: string;
    finRetrait: string;
    lieu: string;
    imageUrl?: string;
}

// Interface Reservation — 
export interface Reservation {
    id: number;
    offreId: number;
    offreTitre: string;
    offreLieu: string;
    offrePrix: number;
    debutRetrait: string;
    finRetrait: string;
    etudiantId: number;
    etudiantNom: string;
    etudiantPrenom: string;
    offreurId: number;
    offreurNom: string;
    offreurPrenom: string;
    dateReservation: string;
    statutReservation: StatutReservation;
    dateCreation?: string;
    dateModification?: string;
}

// --- AUTHENTIFICATION ---
export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    nom: string;
    prenom: string;
    email: string;
    motDePasse: string;
    role: Role;
    telephone: string;
    adresse: string;
}

export interface AuthResponse {
    token: string;
    nom: string;
    prenom: string;
    email: string;
    role: Role;
    message: string;
    userId?: number;
}
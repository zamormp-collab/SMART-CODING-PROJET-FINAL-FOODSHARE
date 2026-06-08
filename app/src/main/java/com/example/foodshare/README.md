# Documentation Technique - FoodShare Android

Ce document décrit le fonctionnement technique de l'application Android **FoodShare**, développée pour permettre aux étudiants de réserver des surplus alimentaires.

## 🏗️ Architecture Logicielle

L'application suit le pattern architectural **MVVM (Model-View-ViewModel)** recommandé par Google, favorisant une séparation nette entre l'interface utilisateur et la logique métier.

### Couches de l'application :

1.  **View (UI)** : Construite entièrement avec **Jetpack Compose**. Les composants sont sans état (stateless) autant que possible, réagissant aux changements d'état émis par les ViewModels.
2.  **ViewModel** : Gère l'état de l'interface utilisateur et la communication avec la couche de données. Utilise `mutableStateOf` ou `StateFlow` pour exposer les données à la vue.
3.  **Data (Repository)** : Sert de point d'entrée unique pour les données. Le Repository décide s'il doit récupérer les données via le réseau ou le cache local.

---

## 🚀 Fonctionnement Technique

### 1. Gestion des Sessions (Authentification)
- L'authentification utilise des jetons **JWT**.
- Le `SessionManager` stocke le jeton et les informations de l'utilisateur de manière persistante via `SharedPreferences`.
- Un `Interceptor` OkHttp injecte automatiquement le header `Authorization: Bearer <token>` dans chaque requête sortante via `RetrofitClient`.

### 2. Flux de Données et Réactivité
- **Auto-Refresh** : L'écran d'accueil utilise une coroutine dans le `HomeViewModel` qui effectue un "polling" (interrogation périodique) toutes les secondes pour garantir que la liste des offres est toujours à jour.
- **Gestion d'État** : Chaque écran possède une `sealed class` (ex: `HomeState`, `OffreDetailState`) représentant les différents états possibles : `Loading`, `Success`, `Error`, `Idle`.

### 3. Communication Réseau
- **Retrofit** est utilisé pour définir les endpoints REST.
- Les requêtes sont asynchrones et exécutées dans des `coroutines` (`viewModelScope`) pour ne pas bloquer le thread principal (UI thread).

---

## 🛠️ Stack Technique & Bibliothèques

- **Kotlin Coroutines** : Gestion de l'asynchronisme.
- **Retrofit / OkHttp** : Client HTTP et sérialisation JSON (Gson).
- **Coil** : Chargement et mise en cache des images de manière asynchrone.
- **Jetpack Navigation** : Gestion de la navigation entre les différents écrans via un `NavHost`.
- **Material Design 3** : Système de design pour une interface moderne et accessible.

---

## 📁 Organisation du Code (`com.example.foodshare`)

- `data/` : Contient les `api` (interfaces Retrofit), `dto` (modèles de données), `repository` (logique de données) et `local` (gestion session).
- `ui/` : 
    - `screens/` : Écrans principaux (Auth, Home, Detail, Profile, Reservation).
    - `components/` : Composants UI réutilisables (Boutons, Cartes, etc.).
    - `theme/` : Configuration du thème Material (Couleurs, Typographie).
- `viewmodel/` : Logique de présentation et gestion d'état pour chaque écran.

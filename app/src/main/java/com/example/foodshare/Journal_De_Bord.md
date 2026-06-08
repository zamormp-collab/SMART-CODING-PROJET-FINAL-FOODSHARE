# JOURNAUX DE BORD DU PROJET FOODSHARE POUR MOBILE

## INFORMATIONS GENERALES
- **Nom du Projet** : FOODSHARE
- **Type de Projet** : Projet de Synthèse BTS SIO/SLAM
- **Partie Travaillée** : Application Mobile avec Jetpack Compose
- **Technologies Utilisées** : 
    - Kotlin 
    - Jetpack Compose 
    - Android Studio 
    - Architecture MVVM 
    - Retrofit  
    - API REST 
    - Git 
    - GitHub 
- **Environnement de Développement** :
    - Windows 11 
    - Android Studio 
    - Émulateur Android 
    - Git Bash 
    - GitHub
- **Développeur** : DELVA Patrick

---

## PRESENTATION DU PROJET
FoodShare est une plateforme permettant de partager et de consulter des offres alimentaires. Dans le cadre de ce projet, j'étais responsable du développement de la partie mobile Android. Mon travail consistait principalement à concevoir l'architecture de l'application, développer les interfaces utilisateur, intégrer les services API, participer aux activités de conception et assurer l'intégration finale des différentes fonctionnalités développées.
Le projet a été réalisé en collaboration avec plusieurs membres de l'équipe travaillant sur les parties Backend et Frontend Web (React).

---

### Semaine 1 : Analyse, conception et organisation du projet

#### Ce que j'ai construit cette semaine
Cette première semaine a été consacrée à la préparation du projet et à la définition de ses fondations techniques.
J'ai participé à :
- La création du projet Android dans Android Studio.
- La mise en place de l'architecture MVVM.
- L'analyse des besoins fonctionnels.
- La conception du Modèle Conceptuel de Données (MCD).
- La conception du Modèle Logique de Données (MLD).
- La compréhension des interactions entre les différentes couches du système.
- La préparation de la structure des dossiers du projet.
- La configuration du dépôt GitHub.
- La création des premières branches de développement.
- L'organisation du projet dans Jira.
- La planification des tâches sous forme de sprints hebdomadaires.

Durant cette même semaine, j'ai également participé à la conception de l'identité visuelle du projet :
- La réflexion autour du logo FoodShare.
- Le choix des couleurs principales.
- L'harmonisation du design entre l'application Mobile Android et l'application Web React.
- La définition d'une première charte graphique.

#### Utilisation de l'intelligence artificielle
J'ai utilisé l'IA pour :
- Comprendre plus rapidement certains concepts de l'architecture MVVM.
- Obtenir des exemples de structures de projets Android modernes.
- Vérifier certains modèles de données.
- Générer des idées pour l'identité visuelle.
- Comparer différentes approches de conception logicielle.

#### Ce que j'ai appris
Cette semaine m'a permis de mieux comprendre :
- L'architecture MVVM.
- L'organisation d'un projet Android professionnel.
- Les principes de modélisation des données.
- L'utilisation de Jira dans un contexte Agile.
- L'importance de la planification avant le développement.

#### Difficultés rencontrées
Les principales difficultés étaient :
- Comprendre les communications entre View, ViewModel et Repository.
- Concevoir correctement les modèles de données.
- Organiser efficacement les tâches du projet.
*Ces difficultés ont été surmontées grâce aux échanges avec l'équipe, à la documentation technique et à l'assistance de l'IA.*

---

### Semaine 2 : Développement du module d'authentification

#### Ce que j'ai construit cette semaine
Cette semaine était consacrée à la mise en place du système d'authentification.
J'ai créé la branche `feature/MobileAuth` pour les fonctionnalités suivantes :
- Écran de connexion (Login).
- Écran d'inscription (Register).
- Validation des champs utilisateur.
- Gestion des erreurs de saisie.
- Mise en place des ViewModels.
- Création des modèles de données nécessaires.
- Préparation des services API.

En parallèle, j'ai créé la branche `feature/HomeProfileUI` pour développer :
- **Home Screen** : Interface d'accueil, navigation principale et personnalisation selon l'utilisateur connecté.
- **Profile Screen** : Affichage des informations utilisateur et préparation des futures modifications du profil.

À ce stade, l'API backend n'était pas encore disponible publiquement. J'ai donc préparé l'ensemble de l'architecture MVVM afin que l'intégration puisse être réalisée rapidement dès sa disponibilité. En fin de semaine, grâce à Ngrok, j'ai réalisé les premiers tests d'intégration : connexion, inscription et validation des réponses API.

#### Utilisation de l'intelligence artificielle
J'ai utilisé l'IA pour :
- Générer certains composants Jetpack Compose.
- Comprendre Retrofit.
- Résoudre des erreurs de communication API.
- Structurer correctement les ViewModels.
- Vérifier certaines implémentations MVVM.

#### Ce que j'ai appris
J'ai développé mes compétences en :
- Jetpack Compose.
- Architecture MVVM.
- Intégration d'API REST.
- Gestion des états utilisateur.
- Communication client-serveur.

#### Difficultés rencontrées
La principale difficulté concernait les erreurs HTTP 403 Forbidden. J'ai également rencontré des problèmes liés aux permissions API, aux configurations Ngrok et à la récupération des données utilisateur. Ces problèmes ont été résolus grâce aux tests et à l'analyse des réponses du serveur.

---

### Semaine 3 : Développement des modules Offres et Réservations

#### Ce que j'ai construit cette semaine
Une fois le système d'authentification opérationnel, j'ai commencé le développement des principales fonctionnalités métier.
J'ai créé la branche `feature/MobileOffres` :
- Écran de consultation des offres alimentaires.
- Cartes d'affichage des offres.
- Listes dynamiques et navigation vers les détails.

J'ai également créé la branche `feature/MobileReservations` :
- Écrans de réservation et formulaires.
- Préparation des appels API et navigation.

J'ai utilisé des données fictives (mock) pour tester les interfaces et vérifier le comportement de l'application avant la disponibilité totale de l'API.

#### Utilisation de l'intelligence artificielle
J'ai utilisé l'IA pour :
- Améliorer certains designs.
- Générer des idées d'interfaces.
- Optimiser certains composants Compose.
- Vérifier l'organisation du code.

#### Ce que j'ai appris
Amélioration des connaissances sur les listes dynamiques, les composants réutilisables, les interfaces modernes avec Jetpack Compose et l'expérience utilisateur mobile.

#### Difficultés rencontrées
Les principales difficultés étaient la synchronisation avec l'API, l'adaptation des interfaces aux futures données réelles et la gestion de certains états complexes.

---

### Semaine 4 : Intégration finale et validation du projet

#### Ce que j'ai construit cette semaine
Phase finale de regroupement et de validation. L'API backend étant pleinement fonctionnelle via Ngrok, j'ai finalisé :
- **Module Offres** : Connexion avec l'API, préparation des données dynamiques et vérification des affichages.
- **Module Réservations** : Finalisation des interfaces, appels backend et validation du fonctionnement général.
- **Améliorations** : Home Screen, Profile Screen et expérience utilisateur globale.

J'ai créé la branche `integration/android-restore` pour regrouper toutes les fonctionnalités, effectué les Pull Requests, réalisé les rebases, corrigé les conflits Git et fusionné le tout dans la branche `develop`.

#### Utilisation de l'intelligence artificielle
L'IA m'a aidé à :
- Comprendre le fonctionnement des rebases.
- Résoudre certains conflits Git.
- Vérifier les bonnes pratiques GitHub.
- Optimiser certaines portions du code.
- Corriger certaines erreurs de logique.

#### Ce que j'ai appris
Maîtrise du Git Flow (Pull Requests, Rebase, résolution de conflits), intégration continue et travail collaboratif.

#### Difficultés rencontrées
Difficultés avec des branches non basées sur `develop`, conflits lors des fusions et problèmes de récupération des données après inscription. Résolus par rebase, correction manuelle et tests approfondis.

---

## BILAN PERSONNEL
Le projet FoodShare Mobile m'a permis d'acquérir une expérience pratique dans le développement d'applications Android modernes utilisant Kotlin et Jetpack Compose. J'ai pu mettre en œuvre l'architecture MVVM, intégrer des services API REST et participer activement à l'organisation d'un projet collaboratif.
L'utilisation de l'intelligence artificielle a constitué un soutien important pour obtenir des explications techniques, des exemples de code et résoudre des problèmes complexes, tout en gardant un esprit critique sur les solutions intégrées.
Cette expérience a renforcé mes compétences techniques, ma capacité à résoudre des problèmes complexes et ma compréhension du cycle complet de développement d'une application mobile professionnelle.

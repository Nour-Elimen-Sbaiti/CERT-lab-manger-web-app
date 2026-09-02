# RAPPORT DE STAGE

## Application Web de Gestion et de Traçabilité des Appareils de Mesure

### CertBench (Calibration Hub) — Laboratoire des Essais et de Mesure, Département d'Homologation, CERT

---

**Réalisé par** : Nour Elimen Sbaiti  
**Formation** : Manouba School of Engineering (MSE) — Génie Géomatique  
**Encadrant professionnel** : M. Karim Miladi, Chef du Laboratoire des Essais et de Mesure  
**Organisme d'accueil** : Centre d'Études et de Recherches des Télécommunications (CERT) — Parc Technologique El Ghazala, Route de Raoued Km 3,5, 2088 Ariana  
**Durée du stage** : Un mois

---

## 1. Introduction générale

La fiabilité des essais réalisés par un laboratoire d'homologation dépend directement du bon fonctionnement, de la maintenance et de l'étalonnage périodique de ses appareils de mesure. Dans un contexte où les exigences de qualité, de conformité et de traçabilité se renforcent — notamment en vue d'une démarche d'accréditation — le suivi de ce parc d'équipements ne peut plus reposer sur des méthodes manuelles ni sur des fichiers dispersés.

Ce rapport présente la conception et le développement d'**CertBench (Calibration Hub)**, une application web dédiée à la gestion et à la traçabilité des appareils de mesure du **Laboratoire des Essais et de Mesure (Département d'Homologation)** du CERT. Réalisé dans le cadre d'un stage d'un mois, le projet a suivi une démarche pragmatique : une phase d'analyse de terrain resserrée, suivie d'un développement itératif nourri par les retours directs du chef de laboratoire.

L'application permet de centraliser les informations des équipements, d'assurer la traçabilité des opérations d'étalonnage et de maintenance, d'automatiser le suivi des échéances avec des alertes visuelles, de gérer la documentation associée, de générer une fiche de vie exportable en PDF et de sécuriser l'accès par une gestion des rôles.

---

## 2. Contexte général du projet

### 2.1 Présentation de l'organisme d'accueil

**Centre d'Études et de Recherches des Télécommunications (CERT)**

| Élément | Détail |
|--------|-------|
| **Adresse** | Parc Technologique El Ghazala, Route de Raoued Km 3,5, 2088 Ariana |
| **Forme juridique** | Entreprise publique à caractère non administratif |
| **Texte de création** | Loi n° 1988-145 du 31 décembre 1988 (articles 110, 111, 112) |
| **Organisation** | Décret n° 1989-1981 du 23 décembre 1989, modifié par le décret n° 1995-2033 du 16 octobre 1995 |

**Missions principales du CERT :**

- Études pour le développement des réseaux de télécommunications et de télédiffusion : ingénierie et planification des réseaux et services, cahiers des charges, études technico-économiques, schémas directeurs ;
- Études et pilotage de projets stratégiques ;
- Expertise, audit, assistance et conseil technique : optimisation des réseaux, analyse des performances, qualité de service, sécurisation des réseaux informatiques ;
- **Homologation des terminaux de télécommunications destinés au marché tunisien** — mission directement servie par le projet ;
- Contrôle et acceptance des équipements et nouvelles installations réseau.

**Le Laboratoire des Essais et de Mesure (Département d'Homologation)** est la structure technique du CERT chargée d'évaluer la conformité des équipements de télécommunications à l'aide d'appareils de mesure de haute précision : analyseurs de spectre, générateurs de signaux, oscilloscopes, wattmètres, générateurs RF, atténuateurs, etc.

### 2.2 Contexte et problématique

La fiabilité des résultats d'essais dépend directement du bon fonctionnement, de la maintenance et de l'étalonnage périodique des appareils utilisés. Or, la gestion actuelle du parc reposait principalement sur des **registres papier et des fichiers bureautiques dispersés** (tableurs Excel, photos de tableaux manuscrits scannés).

**Limites identifiées concrètement sur le terrain :**

1. **Absence d'une base de données centralisée** — les informations d'un même appareil (identification, marque, numéro de série, dates, laboratoire d'étalonnage, garantie, contrat de maintenance) étaient réparties entre plusieurs supports non reliés entre eux.
2. **Difficulté de consultation rapide de l'historique** d'un appareil : retrouver quand un appareil a été étalonné, par qui, ou s'il a eu une panne, nécessitait de croiser plusieurs registres.
3. **Risque d'oubli des dates d'étalonnage** — constat direct : plusieurs appareils du parc réel avaient déjà un étalonnage expiré au moment de l'analyse, sans qu'aucune alerte n'ait été déclenchée.
4. **Recherche manuelle et lente des certificats** et rapports d'étalonnage.
5. **Manque de visibilité globale et instantanée** sur l'état du parc (combien d'appareils actifs, combien en retard, combien critiques).
6. **Difficulté à produire rapidement des preuves de conformité** lors d'audits qualité — un point sensible pour la démarche d'accréditation du laboratoire.

### 2.3 État de l'existant (AS-IS)

L'analyse de terrain a permis de reconstituer la nature exacte des données suivies manuellement. Les registres existants (partiellement scannés lors de l'analyse) contenaient, pour chaque appareil : désignation, marque, modèle, numéro de série, code inventaire/immatriculation, date d'acquisition, valeur d'acquisition, laboratoire d'étalonnage prestataire (par exemple DEFNAT, TTM, PRISMA), périodicité d'étalonnage, état de la garantie, état et validité du contrat de maintenance, et date de validité du dernier étalonnage (avec mention explicite « expirée » pour plusieurs lignes).

**Processus métier AS-IS reconstitué :**

| Étape | Description du processus actuel |
|-------|-------------------------------|
| 1. Réception d'un nouvel appareil | Le chef de labo enregistre manuellement dans un registre papier/Excel. Aucune génération automatique d'identifiant. |
| 2. Suivi de l'étalonnage | Le personnel consulte le registre papier pour vérifier la date d'échéance. Si oubli de consultation → risque d'utiliser un appareil non conforme. Envoi de l'appareil chez un prestataire externe (DEFNAT / TTM / PRISMA) → suivi manuel du retour. |
| 3. Signalement de panne | Communication orale ou note manuscrite au chef de labo. Pas de traçabilité centralisée de la résolution. |
| 4. Recherche d'un certificat | Recherche physique dans des classeurs ou fichiers dispersés. Délai important, surtout en cas d'audit. |
| 5. Reporting pour l'accréditation | Compilation manuelle, chronophage, sujette à erreur. |

Ce schéma AS-IS a été construit à partir des entretiens et de l'observation directe, et a servi de base de comparaison pour justifier chaque fonctionnalité du cahier des charges (le TO-BE étant directement l'inverse : centralisation, alerte automatique, traçabilité systématique, export en un clic).

### 2.4 Objectifs du projet

**Objectif général** : concevoir et développer une application web centralisée assurant le suivi complet du cycle de vie des appareils de mesure, de leur acquisition à leur réforme, avec une traçabilité intégrale des opérations.

**Objectifs spécifiques :**

- Centraliser les informations des équipements dans une base de données unique.
- Assurer la traçabilité des opérations d'étalonnage, de maintenance et des changements d'état.
- Automatiser le suivi des échéances (étalonnage, garantie, contrat de maintenance) et générer des alertes intelligentes selon le délai restant.
- Faciliter la gestion documentaire (archivage, consultation, téléchargement des certificats et contrats en PDF).
- Générer automatiquement la fiche de vie officielle de chaque appareil, exportable en PDF.
- Fournir un tableau de bord et des indicateurs de performance en temps réel (état du parc, répartition, échéances à venir).
- Sécuriser l'accès via une gestion des utilisateurs, des rôles et des permissions.

### 2.5 Périmètre du projet

**Inclus dans le périmètre (version 1) :**

- Gestion des utilisateurs, des équipements, des accessoires et logiciels installés.
- Suivi des étalonnages, maintenances, pannes et interventions.
- Gestion documentaire, historique, fiche de vie, tableau de bord et alertes.
- Gestion des laboratoires d'étalonnage / fournisseurs externes.
- Export CSV et PDF.

**Hors périmètre (version 1) :**

- Pilotage direct ou connexion automatique aux instruments de mesure.
- Intégration avec un ERP ou un logiciel de métrologie externe.
- Application mobile dédiée.
- Notifications email automatiques (identifiées comme amélioration future).

Le déploiement initial est limité au Laboratoire des Essais et de Mesure du CERT ; l'architecture modulaire retenue permet une extension future à d'autres structures du centre.

---

## 3. Méthodologie de travail

Le stage a duré un mois. Compte tenu de cette durée courte, aucune démarche Agile/Scrum formelle n'a été mise en place ; le travail a été organisé en **deux phases pragmatiques**.

### Phase 1 — Analyse des besoins (≈ 10 jours)

- Entretiens avec le personnel du laboratoire, en particulier le chef de labo M. Karim Miladi (et le personnel présent en son absence), sous forme de questionnaire d'entretien et de prise de notes.
- Découverte physique du laboratoire et de son parc d'appareils : identification concrète des instruments (analyseurs de spectre, générateurs RF, wattmètres, etc.), lecture des registres existants.
- Réalisation d'un **prototype HTML statique simple**, sans logique fonctionnelle réelle, uniquement pour simuler l'interface et susciter la discussion avec le personnel — un levier efficace pour faire émerger des besoins que l'entretien seul n'aurait pas révélés (par exemple la distinction entre « accessoires manquants » et « kit incomplet », ou le besoin d'un historique de type « fiche de vie » unifié).
- Rédaction d'une première version du cahier des charges, présentée au chef de labo pour recueillir ses retours et ajuster la portée du projet.

### Phase 2 — Développement itératif avec feedback (reste du mois)

- Développement de l'application web à partir du cahier des charges validé.
- Présentations intermédiaires au chef de labo pour recueillir des retours et ajuster le produit en cours de route.

Cette approche — prototypage léger avant développement, feedback continu plutôt que sprints formalisés — a permis de couvrir l'ensemble du cycle (analyse → conception → développement → retours) dans le temps imparti.

### 3.1 Entretiens et questionnaire d'entretien

Les entretiens avec le chef de labo et le personnel ont couvert quatre axes, servant directement de base à la structuration du cahier des charges :

1. **Identification des données suivies** — quelles informations existent déjà, sous quelle forme, où sont-elles stockées.
2. **Processus actuels** — comment se passe concrètement un étalonnage, une panne, une réforme d'appareil.
3. **Douleurs ressenties** — quels sont les oublis ou pertes de temps les plus fréquents (réponse principale : oubli des dates d'étalonnage et recherche de certificats).
4. **Attentes vis-à-vis de l'outil** — validées et affinées via le prototype HTML, notamment sur la nécessité d'un historique unifié type « fiche de vie » plutôt que des écrans séparés par type d'événement.

---

## 4. Acteurs du système

| Acteur | Rôle principal |
|-------|---------------|
| **Administrateur** | Gestion des utilisateurs, des rôles, des permissions et des paramètres du système. |
| **Chef du laboratoire** | Supervision du parc d'équipements, validation des opérations (étalonnage, maintenance), consultation des tableaux de bord et des indicateurs. |
| **Personnel autorisé** | Gestion quotidienne : enregistrement d'interventions, mise à jour des données autorisées, déclaration de pannes. |

**Évolution par rapport au cahier des charges initial :** un quatrième rôle, « Utilisateur en consultation » (accès en lecture seule), figurait dans la première version du cahier des charges. Il a été **retiré après retours du terrain** : dans la pratique quotidienne du laboratoire, toute personne ayant accès au système est amenée à agir (saisir une intervention, signaler une panne, mettre à jour un statut) — un accès strictement passif n'avait pas d'usage réel identifié par le chef de labo.

---

## 5. Expression des besoins

### 5.1 Besoins fonctionnels (par module, avec priorisation)

#### Gestion des équipements — Must

- Ajouter, modifier, consulter et supprimer (logiquement) un appareil de mesure.
- Rechercher un appareil par code inventaire, numéro de série, marque ou catégorie.
- Générer automatiquement le code inventaire à la création (Should).
- Attribuer un statut à l'appareil : Actif, En réparation, En étalonnage externe, Retiré/Réformé.
- **Aucune suppression physique :** un équipement retiré est marqué « Réformé », son historique est conservé intégralement.
- **Recherche multicritère** : la recherche porte sur la désignation, la marque, le modèle, le code identifiant, le numéro de série et le code d'emplacement, et non plus seulement sur la désignation.
- **QR code** généré pour chaque équipement et affiché dans l'interface.

#### Fiche de vie — Must

- Créer automatiquement une fiche de vie à la création de l'appareil.
- Enregistrer chronologiquement tous les événements : mise en service, maintenance, étalonnage, panne, réparation, mise hors service.
- Afficher cet historique dans une vue unique et chronologique par appareil (onglet dédié dans la fiche appareil).
- Exporter la fiche de vie en PDF.

#### Étalonnage — Must

- Enregistrer les opérations d'étalonnage (date, laboratoire prestataire, coût, certificat).
- Calculer et afficher automatiquement la date du prochain étalonnage.
- Générer des **alertes visuelles** selon le délai restant : à jour / bientôt dû / en retard / critique.
- Historiser les certificats successifs pour un même appareil (Should).

#### Maintenance et réparation — Must

- Créer un ticket/enregistrement de maintenance (titre, description, priorité, coût estimé).
- Suivre le statut de l'intervention (Ouvert / En cours / Résolu).
- Signaler une panne ou une observation, avec suivi de résolution (résolu ou non).
- En cas de besoin de maintenance ou d'étalonnage externe, contacter directement le laboratoire/expert prestataire, avec précision de l'intervention réalisée si applicable.

#### Contrats de maintenance et garantie — Must

- Suivre l'état de la garantie (oui/non).
- Suivre l'état du contrat de maintenance (valide/non valide), avec date, période et alerte associée.

#### Gestion des accessoires et logiciels installés — Should

- Associer une liste d'accessoires et de logiciels à un équipement (nom, référence, quantité, état, version).
- Vérifier le kit au retour d'un prêt (case « accessoires vérifiés »).
- Bloquer le passage en statut « Actif » si le kit est incomplet (Could).

#### Gestion des fournisseurs / laboratoires d'étalonnage — Should

- Créer une fiche fournisseur/laboratoire externe (nom, spécialité, contact, téléphone, délai moyen).
- Lier chaque opération externe (étalonnage ou réparation) à un fournisseur.

#### Gestion documentaire — Must

- Attacher plusieurs documents à un appareil (certificat d'étalonnage, contrat de maintenance, fiche technique, rapport) au format PDF.
- Classer les documents par type et par date.
- Consulter et télécharger les documents archivés.

#### Alertes et tableau de bord — Must

- Indicateurs de synthèse : total du parc, appareils actifs, bientôt dus, en retard, critiques.
- Bandeau d'alerte visible en cas d'appareils en retard ou critiques.
- Liste des prochains étalonnages, cliquable vers la fiche appareil.

#### Rapports — Must

- Générer un rapport basé sur l'état des appareils (en panne/réparation, étalonnage en retard/critique, bientôt dû).
- Exporter les rapports et le registre complet en **CSV**.
- Envoyer un rapport ciblé à un expert/laboratoire pour un ou plusieurs appareils (Should).

#### Demande de prestation — Must

- Créer, modifier et supprimer une demande de prestation liée à un étalonnage.
- Paramètres métrologiques : état à la livraison, lieu (Laboratoire/Site), comparaison inter-laboratoire, déclaration de conformité, règle de décision (binaire simple, binaire avec bande de garde, non binaire sans bande de garde), points à étalonner, niveau d'incertitude (supérieur ou inférieur à 1×10⁻⁴).
- Télécharger la demande en PDF.
- **Accessible à tous les rôles authentifiés** pour modification.

#### Gestion des utilisateurs et droits d'accès — Must

- Créer, modifier, désactiver un compte utilisateur.
- **Supprimer un compte utilisateur** (fonctionnalité ajoutée pour l'administrateur).
- Attribuer un rôle (Administrateur, Chef de laboratoire, Personnel autorisé).
- Restreindre les actions selon le rôle.
- Journaliser les actions sensibles (Should).
- **Mot de passe oublié** : demande par email, lien de réinitialisation temporaire, formulaire de réinitialisation sécurisé.

### 5.2 Besoins non fonctionnels

| Catégorie | Exigence |
|-----------|----------|
| **Performance** | Temps de réponse rapide, usage par plusieurs utilisateurs sans dégradation notable. |
| **Sécurité** | Authentification par JWT, gestion fine des rôles, accès restreint selon le profil. |
| **Fiabilité** | Aucune perte de données, historique jamais supprimé physiquement. |
| **Ergonomie** | Interface simple et directe, pensée pour un usage quotidien par du personnel technique non-développeur. Icônes professionnelles (SVG) plutôt que symboles informels. |
| **Compatibilité** | Navigateurs modernes, usage sur poste de travail desktop du laboratoire. |
| **Maintenabilité** | Architecture modulaire (migrations versionnées) facilitant les évolutions futures. |
| **Marque** | Branding clair et cohérent : titre de la plateforme **CERT CALIBRATION HUB**, logo dimensionné de manière significative. |

### 5.3 Environnement matériel et logiciel

| Composant | Choix retenu |
|-----------|-------------|
| Frontend | React.js (Vite) |
| Backend | Spring Boot, Apache Tomcat (port 8081) |
| Base de données | PostgreSQL, migrations versionnées avec Flyway |
| Authentification | JWT (JSON Web Token) |
| Génération de documents | iText (export PDF) |
| Suivi de version | Git / GitHub |

---

## 6. Conception et architecture

### 6.1 Architecture générale

Architecture **3-tiers** classique :

```
Utilisateur → Interface Web (React.js)
              → API REST (Spring Boot / Apache Tomcat, port 8081)
              → Base de données PostgreSQL + stockage documentaire (dossier uploads)
```

- Le frontend (React + Vite) communique avec le backend via des appels REST.
- En développement, Vite assure le proxy des requêtes `/api` vers le backend sur le port 8081.
- Le backend expose une API REST sécurisée par JWT, avec une gestion fine des permissions par rôle.
- La base de données PostgreSQL est migrée de manière versionnée via Flyway.

### 6.2 Modèle de données (entités principales)

| Entité | Rôle |
|--------|------|
| **Equipement** | Identification, caractéristiques techniques, marque, modèle, numéro de série, date d'acquisition, localisation, statut (NOUVEAU / EN_SERVICES / EN_REPARATION / EN_ETALONNAGE_EXTERNE / REFORME), responsable, suppleant, contact technique, laboratoire d'étalonnage. |
| **Intervention** | Tableau unifié pour tous les événements liés à un équipement : type (ÉTALONNAGE, MAINTENANCE_PREVENTIVE, MAINTENANCE_CORRECTIVE, PANNE, VÉRIFICATION), date, nature, coût, intervenant, laboratoire d'étalonnage, statut (OUVERT / EN_COURS / RESOLU). |
| **Etalonnage** | Entité séparée liée à une intervention d'étalonnage : date de validité, EMT (exprès maximal de tolérance), bande de garde, résultat (EN_ATTENTE / CONFORME / NON_CONFORME / SANS_SUITE). |
| **ContratMaintenance** | Contrat de maintenance associé à un équipement : date de début, date de fin, période, statut de validité, alerte associée. |
| **Document** | Fichier PDF (certificat, contrat, rapport, fiche technique), type, date, lien vers l'équipement. Types autorisés : CERTIFICAT_ETALONNAGE, CONTRAT_MAINTENANCE, RAPPORT, MANUEL, GARANTIE, AUTRE. |
| **Personnel** | Utilisateur du système avec coordonnées individuelles (nom, prénom, email, téléphone). Coordonnées séparées de celles du laboratoire. |
| **LaboEtalonnage** | Fiche fournisseur/laboratoire externe (nom, spécialité, contact, téléphone, délai moyen). Coordonnées globales du laboratoire distinctes du personnel. |
| **Accessoire** | Nom, référence, quantité, état, lié à un équipement. |
| **DemandePrestation** | Demande liée à un étalonnage : état à la livraison, lieu, comparaison inter-laboratoire, déclaration de conformité, règle de décision, points à étalonner, niveau d'incertitude. |
| **Alerte / AlerteResolue** | Gestion des alertes et de leur résolution. |
| **User (compte)** | Identifiant, mot de passe haché, nom, prénom, email, rôle, état (activé/désactivé), vérification email, photo de profil, historique de connexion, tokens de vérification et de réinitialisation. |

### 6.3 Décision de conception notable

Plutôt que de créer des tables séparées pour l'étalonnage, la maintenance et les pannes (comme envisagé initialement dans l'analyse), ces événements ont été **fusionnés dans une table Intervention unique typée par un enum** (`TypeIntervention`).

Ce choix, **validé après retours d'expérience sur la première maquette de base de données**, simplifie l'affichage de l'historique chronologique par appareil (une seule requête triée par date plutôt que plusieurs unions) et évite de dupliquer la logique de suivi de statut (Ouvert/En cours/Résolu) entre plusieurs modules.

L'étalonnage conserve néanmoins une entité dédiée (`Etalonnage`) pour les informations métrologiques spécifiques (date de validité, EMT, bande de garde, résultat), liée à l'intervention d'étalonnage.

### 6.4 Cas d'utilisation (UML — récapitulatif)

| Cas d'utilisation | Administrateur | Chef de labo | Personnel autorisé |
|-------------------|---------------|-------------|-------------------|
| Gérer les comptes utilisateurs | ✓ | | |
| Ajouter / modifier un équipement | | ✓ | ✓ |
| Consulter le tableau de bord | ✓ | ✓ | ✓ |
| Enregistrer un étalonnage | | ✓ | ✓ |
| Déclarer une panne | | ✓ | ✓ |
| Valider une opération de maintenance | | ✓ | |
| Consulter la fiche de vie | ✓ | ✓ | ✓ |
| Exporter la fiche de vie (PDF) | ✓ | ✓ | ✓ |
| Télécharger un certificat / contrat | ✓ | ✓ | ✓ |
| Gérer les laboratoires d'étalonnage externes | | ✓ | ✓ |
| Recevoir / consulter une alerte d'échéance | | ✓ | ✓ |
| Modifier une demande de prestation | ✓ | ✓ | ✓ |
| Supprimer un compte utilisateur | ✓ | | |

**Relations notables :**

- « Enregistrer un étalonnage » inclut « Calculer la prochaine échéance ».
- « Consulter la fiche de vie » étend vers « Exporter en PDF ».
- « Ajouter un équipement » inclut « Générer le code inventaire ».

---

## 7. Réalisation

### 7.1 Stack technique

**Backend** : Spring Boot 4.1.0 exposant une API REST sécurisée par JWT, avec sécurité par rôles (ADMINISTRATEUR, CHEF_LABORATOIRE, PERSONNEL_AUTORISE), base PostgreSQL versionnée par migrations Flyway, génération de PDF via iText pour la fiche de vie. Package : `com.cert.calibration_hub`.

**Frontend** : React.js avec Vite, communication via un client HTTP centralisé (`api.js`), proxy de développement vers le backend.

**Base de données** : PostgreSQL avec migrations Flyway (V1 à V12), couvrant les utilisateurs, l'historique de connexion, les tokens de vérification/réinitialisation, les équipements, interventions, étalonnages, contrats, documents, personnel, accessoires, laboratoires d'étalonnage, demandes de prestation et alertes résolues.

**Dépôt** : `Nour-Elimen-Sbaiti/CERT-lab-manger-web-app`

### 7.2 Modules livrés côté backend

| Module | Responsabilité |
|--------|---------------|
| `AuthController` | Authentification, inscription, vérification, connexion, profil, mise à jour de la photo, mot de passe oublié et réinitialisation. |
| `UserController` / `UserService` | Gestion des comptes : liste, suppression, activation/désactivation, changement de rôle, mise à jour de la photo. |
| `EquipementController` / `EquipementService` / `EquipementRepository` | CRUD des équipements, recherche multicritère (designation, marque, modèle, codeIdentifiant, numeroSerie, codeEmplacement), génération du QR code. |
| `EtalonnageController` / `EtalonnageService` / `EtalonnageRepository` | Gestion des étalonnages métrologiques. |
| `InterventionController` / `InterventionService` / `InterventionRepository` | Gestion des interventions unifiées (étalonnage, maintenance, panne, vérification). |
| `ContratMaintenanceController` / `ContratMaintenanceService` | Gestion des contrats de maintenance. |
| `DocumentController` / `DocumentService` / `DocumentRepository` | Upload, liste, suppression des documents par équipement. |
| `PersonnelController` / `PersonnelService` / `PersonnelRepository` | Gestion du personnel (CRUD). |
| `LaboEtalonnageController` / `LaboEtalonnageService` / `LaboEtalonnageRepository` | Gestion des laboratoires/fournisseurs externes (CRUD). |
| `AccessoireController` / `AccessoireService` / `AccessoireRepository` | Gestion des accessoires. |
| `AlerteController` / `AlerteService` / `AlerteResolue` | Gestion des alertes et de leur résolution. |
| `DashboardController` / `DashboardService` | Indicateurs de synthèse du parc. |
| `DemandePrestationController` / `DemandePrestationService` / `DemandePrestationPdfService` | CRUD des demandes de prestation, génération PDF. |
| `GlobalExceptionHandler` | Gestion centralisée des exceptions. |
| `FicheDeVieService` | Assemblage des informations pour la fiche de vie. |

### 7.3 Modules livrés côté frontend

- **Login** : authentification sécurisée.
- **Dashboard** : indicateurs de synthèse du parc, alertes visibles.
- **Liste des Équipements** : recherche multicritère, filtres, export CSV, changement d'état par menu déroulant, édition des informations via action contextuelle, QR code affiché.
- **Fiche détail Équipement** : informations générales, documents (upload, consultation, suppression), formulaire de déclaration de panne, export de la fiche de vie en PDF.
- **Page Alertes** : centralisation des échéances critiques (étalonnage, garantie, contrat de maintenance), codification par couleur selon le délai avant échéance, icône de notification de type cloche (SVG) avec compteur.
- **Gestion du Personnel** : CRUD des comptes utilisateurs et de leurs rôles.
- **Gestion des laboratoires d'étalonnage** : CRUD des fournisseurs externes.
- **Gestion des demandes de prestation** : liste, création, modification, téléchargement PDF.
- **Profil utilisateur** : consultation des informations personnelles et déconnexion depuis une fenêtre modale.
- **Afficher / masquer le mot de passe** avec icônes SVG professionnelles.
- **Confirmation avant modification significative** (`confirm(...)`) pour éviter les actions intempestives.

### 7.4 Écrans principaux réalisés

1. **Tableau de bord** : 5 cartes indicateurs (Total matériel, Actif, Bientôt dû, En retard, Critique), bandeau d'alerte visible en cas d'appareils en retard/critique, liste des prochains étalonnages cliquable vers la fiche appareil.

2. **Liste des Équipements** : table avec recherche multicritère et export CSV. Par ligne : designation, marque, modèle, numéro de série, code identifiant, code emplacement, état coloré, QR code. Actions : ouverture de la fiche détail, modification des informations, changement d'état par menu déroulant.

3. **Fiche détail Équipement** : onglets ou sections pour les informations générales, les documents attachés (certificats, contrats) avec formulaire d'ajout, l'historique des interventions, la fiche de vie exportable en PDF, les accessoires associés. Actions : signaler une panne, marquer résolu, exporter la fiche de vie.

4. **Page Alertes** : centralisation des échéances critiques — étalonnage en retard, contrat de maintenance non valide, garantie échue. Chaque ligne précise la raison exacte (ex. « en retard de 12 jours ») plutôt qu'un simple badge, pour faciliter la priorisation. Codification par couleur selon le délai avant échéance (rouge ≤ 5 jours, orange ≤ 15 jours, vert au-delà).

5. **Gestion du Personnel (Utilisateurs)** : liste des comptes avec rôle attribué, création, modification, activation/désactivation, et suppression par l'administrateur avec confirmation.

6. **Gestion des laboratoires d'étalonnage** : fiche fournisseur (nom, spécialité, contact, téléphone, délai moyen), liaison entre une opération d'étalonnage/réparation et son laboratoire prestataire.

7. **Login / Inscription / Mot de passe oublié** : authentification par identifiant/mot de passe, jeton JWT, redirection selon le rôle. Inscription avec vérification par email. Réinitialisation du mot de passe avec formulaire dédié.

### 7.5 Livrable complémentaire : inventaire Excel

En parallèle de l'application, un **classeur Excel d'inventaire** a été produit à partir des registres réels du laboratoire, avec :

- **21 instruments réels recensés** (désignation, marque, modèle, numéro de série) ;
- Un modèle de fiche de vie structuré ;
- Des formules `EDATE` calculant automatiquement les dates d'échéance d'étalonnage à partir de la date du dernier étalonnage et de la périodicité.

Ce livrable a servi de **pont pendant la période de transition**, le temps que l'équipe du laboratoire prenne en main l'application web.

### 7.6 Extrait représentatif des données réelles du parc

| Désignation | Marque | Modèle | N° de série | Laboratoire d'étalonnage | Périodicité | Contrat maintenance | État étalonnage |
|-------------|--------|--------|-------------|--------------------------|-------------|---------------------|-----------------|
| Analyseur de spectre portable | Keysight | N9010B | MY57471634 | TTM | 02 ans | Non | Expiré |
| Générateur RF | Agilent | E4433B | GB40050807 | TTM | 03 ans | Oui | Expiré |
| Analyseur de spectre | Agilent | N9020A | MY51160129 | TTM | 02 ans | Non | Expiré |
| Analyseur de spectre Viavi portatif | Viavi | — | — | DEFNAT | 02 ans | Non | Valide (à date) |
| Wattmètre avec 1 port | — | — | — | DEFNAT | 02 ans | Non | Expirée |

Ce relevé a confirmé de façon très concrète le constat de départ : plusieurs appareils critiques du parc étaient déjà en dépassement d'étalonnage sans qu'aucun mécanisme d'alerte n'existe — c'est l'argument principal ayant justifié la priorité « Must » donnée au module d'alertes dans le cahier des charges.

---

## 8. Tests et validation

Les tests ont été réalisés directement avec le personnel du laboratoire, par scénario d'usage :

- Ajout et modification d'un équipement.
- Enregistrement d'une opération d'étalonnage et vérification du calcul de la prochaine échéance.
- Vérification du déclenchement des alertes (bientôt dû / en retard / critique).
- Contrôle des permissions selon le rôle connecté.
- Export d'une fiche de vie en PDF et vérification de sa complétude.
- Recherche multicritère sur les équipements.
- Upload, consultation et suppression de documents.
- Gestion des demandes de prestation (création, modification, téléchargement PDF).

**Critères d'acceptation :**

- Conformité aux besoins fonctionnels priorisés « Must ».
- Fiabilité et cohérence des données produites (dates calculées, statuts).
- Accès strictement conforme au rôle de l'utilisateur connecté.
- Traçabilité des actions via l'historique des interventions et les journaux de connexion.

---

## 9. Retours et itérations

Les présentations intermédiaires au chef de labo ont donné lieu à des ajustements concrets par rapport à la version initiale du cahier des charges :

1. **Suppression du rôle « Consultation »** — jugé sans usage réel dans le fonctionnement quotidien du laboratoire.
2. **Fusion des tables Étalonnage / Maintenance / Panne en une table Intervention unique** — simplification de la logique d'historique après retour d'expérience sur la première maquette de base de données.
3. **Validation de l'usage du prototype HTML initial** comme point de départ efficace pour cadrer les attentes réelles du personnel, au-delà de ce que les entretiens seuls avaient révélé.
4. **Recherche multicritère élargie** : passage d'une recherche sur la seule désignation à une recherche sur designation, marque, modèle, codeIdentifiant, numeroSerie et codeEmplacement.
5. **Changement d'état par menu déroulant** dans la colonne « ÉTAT » plutôt que par l'action sur les points de suspension.
6. **Action sur les points de suspension (3 points)** : modification des informations de l'équipement plutôt que changement d'état.
7. **Contact du personnel** : clic direct sur l'adresse email individuelle (`mailto:`) plutôt qu'une action générique globale.
8. **Suppression du compte par l'administrateur** en plus de l'activation/désactivation.
9. **Modification de la demande de prestation** accessible à tous les rôles authentifiés.
10. **Séparation claire des coordonnées** : les coordonnées globales du laboratoire (contactNom, contactPrenom, contactEmail, contactTelephone) restent rattachées au laboratoire, tandis que les coordonnées individuelles du personnel (nom, prenom, email, telephone) restent rattachées au personnel.
11. **Remplacement des symboles informels (emoji)** par des icônes SVG professionnelles dans les zones sensibles (mot de passe, notification).
12. **Confirmation avant modification significative** pour éviter les actions intempestives.

---

## 10. Déploiement

Déploiement local sur le poste du laboratoire : **backend Spring Boot sur Apache Tomcat (port 8081)**, **base PostgreSQL locale**.

Ce choix privilégie la simplicité d'installation et de maintenance pour une petite équipe technique, sans dépendance à une infrastructure cloud.

**Commandes d'exécution :**

- **Backend** (PowerShell) :
  ```powershell
  cd calibration-hub
  .\mvnw spring-boot:run
  ```
- **Frontend** (PowerShell) :
  ```powershell
  cd frontend
  npm install
  npm run dev
  ```

Prérequis : PostgreSQL opérationnel avec la base `calibration_hub_db` et le schéma compatibles, ainsi que les migrations Flyway appliquées.

Configuration dans `application.properties` : port 8081, URL de la base de données PostgreSQL, identifiants, dialecte Hibernate, Flyway activé, configuration SMTP pour les emails, secret et expiration JWT, répertoire d'upload des fichiers, et paramètres de la demande de prestation (référence, date/version, révision, client).

---

## 11. Maintenance et évolutions futures

### Maintenance

- Sauvegardes régulières de la base PostgreSQL.
- Corrections évolutives au fil de l'usage réel.
- Mise à jour du classeur Excel d'inventaire si de nouveaux instruments sont ajoutés.

### Évolutions envisagées

- **Notifications email automatiques** avant échéance d'étalonnage/maintenance (potentiellement via un scheduler Spring).
- **Génération de QR code** par appareil pour un accès rapide à la fiche via scan (déjà intégré dans le backend, avec affichage frontend).
- **Connexion directe aux instruments de mesure** (hors périmètre actuel).
- **Intégration avec un logiciel de métrologie externe** ou un ERP.
- **Application mobile** dédiée.
- **Interface multilingue** français/arabe.
- **Statistiques avancées** et reporting personnalisé.
- **Réservation/emprunt d'appareil** avec détection de conflits.

---

## 12. Conclusion

CertBench (Calibration Hub) répond directement à la problématique de départ constatée sur le terrain : des données dispersées, un suivi manuel des échéances d'étalonnage exposé au risque d'oubli, et une absence de vision globale du parc. En centralisant les informations, en automatisant le calcul des échéances et en générant des alertes, l'application renforce la traçabilité des opérations du laboratoire et soutient sa démarche d'accréditation, tout en restant simple à prendre en main pour une équipe technique restreinte.

Le mode de travail adopté — analyse de terrain courte appuyée sur un prototype simple, puis développement itératif nourri de retours directs du chef de labo — a permis de livrer, en un mois, un outil **ajusté aux besoins réels** plutôt qu'à une spécification théorique figée dès le départ.

Plusieurs décisions de conception ont été consolidées au fil des retours : la fusion des événements en une table Intervention unique, l'élargissement de la recherche multicritère, la séparation nette entre les coordonnées du laboratoire et celles du personnel, le remplacement des symboles informels par des icônes professionnelles, et la confirmation des actions sensibles. Ces ajustements témoignent d'une démarche centrée sur l'usage réel du laboratoire.

Sur le plan technique, le projet a permis de mettre en œuvre une architecture Spring Boot / React / PostgreSQL moderne et sécurisée (JWT, rôles, migrations Flyway), avec une attention particulière à la qualité d'interface et à la cohérence du code. Le livrable complémentaire (classeur Excel) a facilité la transition vers l'outil web en parallèle.

CertBench constitue ainsi une base fonctionnelle solide et évolutive pour le suivi métrologique du Laboratoire des Essais et de Mesure, avec des perspectives d'extension clairement identifiées (notifications, QR code, intégration ERP, application mobile, multilingue).

---

## Annexes

### Annexe A — Glossaire

| Terme | Définition |
|-------|-----------|
| **CERT** | Centre d'Études et de Recherches des Télécommunications |
| **Étalonnage** | Opération vérifiant ou ajustant la conformité métrologique d'un appareil de mesure |
| **Fiche de vie** | Document récapitulant l'historique complet d'un équipement (interventions, étalonnages, pannes, réparations, mise en service, mise hors service) |
| **Accréditation** | Reconnaissance officielle de la compétence d'un laboratoire selon un référentiel qualité |
| **JWT** | JSON Web Token — mécanisme d'authentification par jeton |
| **EMT** | Expressément Maximal de Tolérance — paramètre métrologique lié à l'étalonnage |
| **Bande de garde** | Intervalle autour de la valeur nominale utilisé dans les règles de décision métrologiques |
| **Flyway** | Outil de versioning des migrations de base de données |
| **AS-IS** | État actuel d'un processus ou d'un système (avant amélioration) |
| **TO-BE** | État cible d'un processus ou d'un système (après amélioration) |
| **EDATE** | Fonction Excel calculant une date à un nombre de mois donné avant/après une date de référence |
| **CSV** | Comma-Separated Values — format d'échange de données tabulaires |
| **PDF** | Portable Document Format — format de document fixe |
| **SVG** | Scalable Vector Graphics — format d'icône/graphisme vectoriel |

### Annexe B — Enumeration des types d'intervention (TypeIntervention)

```
ÉTALONNAGE
MAINTENANCE_PREVENTIVE
MAINTENANCE_CORRECTIVE
PANNE
VERIFICATION
```

### Annexe C — Enumeration des statuts d'intervention (StatutIntervention)

```
OUVERT
EN_COURS
RESOLU
```

### Annexe D — Enumeration des types de document (TypeDocument)

```
CERTIFICAT_ETALONNAGE
CONTRAT_MAINTENANCE
RAPPORT
MANUEL
GARANTIE
AUTRE
```

### Annexe E — Règles de décision (RegleDecision)

```
BINAIRES_BINAIRE_SIMPLE
BINAIRES_BINAIRE_BANDE_GARDE
NON_BINAIRE_BANDE_GARDE
```

### Annexe F — Niveaux d'incertitude (NiveauIncertitude)

```
SUPERIEUR_1E_04
INFERIEUR_1E_04
```

### Annexe G — États des équipements (EtatEquipement)

```
NOUVEAU
EN_SERVICES
EN_REPARATION
EN_ETALONNAGE_EXTERNE
REFORME
```

### Annexe H — Résultats d'étalonnage (ResultatEtalonnage)

```
EN_ATTENTE
CONFORME
NON_CONFORME
SANS_SUITE
```

### Annexe I — Rôles dans le système (Role)

```
ADMINISTRATEUR
CHEF_LABORATOIRE
PERSONNEL_AUTORISE
```

### Annexe J — Dictionnaire de données détaillé (entités principales)

**Equipement** : id, code_identifiant, code_emplacement, qr_code, designation, marque, modele, numero_serie, fournisseur, date_acquisition, date_mise_en_service, date_reforme, valeur_origine, periodicite_valeur, periodicite_unite, garantie_jusqu_au, etat, observations, responsable (Personnel), suppleant (Personnel), contact_technique (Personnel), labo_etalonnage (LaboEtalonnage), created_at, updated_at.

**Intervention** : id, equipement_id, type (TypeIntervention), date_intervention, nature_intervention, cout, numero_bon_intervention, intervenant_nom, intervenant_societe, labo_etalonnage_id (LaboEtalonnage), statut (StatutIntervention), created_at.

**Etalonnage** : id, intervention_id (unique, OneToOne), date_validite, emt, bande_garde, resultat (ResultatEtalonnage).

**ContratMaintenance** : id, equipement_id, date_debut, date_fin, periode, statut_validite, alerte_associee.

**Document** : id, equipement_id, type (TypeDocument), nom_fichier, date_ajout, fichier (BLOB/path).

**Personnel** : id, nom, prenom, email, telephone.

**LaboEtalonnage** : id, nom, specialite, contact_nom, contact_prenom, contact_email, contact_telephone, delai_moyen.

**Accessoire** : id, nom, reference, quantite, etat, equipement_id.

**DemandePrestation** : id, etalonnage_id (unique), labo_etalonnage_id, date_demande, etat_a_la_livraison, lieu (LABORATOIRE/SITE), comparaison_inter_labo, declaration_conformite, regle_decision, points_a_etalonner, niveau_incertitude, created_at.

**User (compte)** : id, username, password (haché), nom, prenom, email, role (Role), enabled, email_verified, created_at, updated_at.

**LoginHistory** : id, user_id, login_time, ip_address, success.

**VerificationToken** : id, user_id, token, expires_at, created_at.

**PasswordResetToken** : id, user_id, token, expires_at, used, created_at.

### Annexe K — Personnes et remerciements

Ce stage a été réalisé sous la supervision de **M. Karim Miladi**, Chef du Laboratoire des Essais et de Mesure, dont les orientations et retours directs ont été déterminants pour cadrer le projet sur les besoins réels du laboratoire.

Les remerciements vont également au personnel du laboratoire qui a participé aux entretiens et aux tests d'usage.

---

*Référence du dépôt GitHub :* `Nour-Elimen-Sbaiti/CERT-lab-manger-web-app`  
*Branche de travail :* `arena/019f9fde-cert-lab-manger-web-app`


# 📦 Parcel and Carrier Management System

> Système de gestion de colis et transporteurs avec authentification JWT

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📋 Table des matières

- [À propos](#-à-propos)
- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Démarrage](#-démarrage)
- [API Documentation](#-api-documentation)
- [Tests](#-tests)
- [Déploiement](#-déploiement)
- [Technologies](#-technologies)
- [Contributeurs](#-contributeurs)

---

## 🎯 À propos

Application REST API pour la gestion de colis avec différents types (STANDARD, FRAGILE, REFRIGERATED) et l'assignation automatique aux transporteurs selon leurs spécialités. Le système implémente une authentification JWT stateless et respecte les principes SOLID et les design patterns.

### Contexte du projet

Une entreprise de logistique souhaite moderniser son système de gestion de colis. L'API permet :
- La gestion de colis aux caractéristiques variables selon leur type
- Différents niveaux d'accès (ADMIN/TRANSPORTER) via JWT
- Architecture technique moderne avec Spring Boot et MongoDB
- Pratiques DevOps (Docker, CI/CD)

---

## ✨ Fonctionnalités

### 👨‍💼 Espace Administrateur

- ✅ Créer, modifier, supprimer des colis
- ✅ Lister tous les colis avec filtres (type, statut) et pagination
- ✅ Rechercher des colis par adresse
- ✅ Assigner des colis aux transporteurs (vérification de spécialité)
- ✅ Gérer les transporteurs (CRUD complet)
- ✅ Activer/désactiver des comptes utilisateurs

### 🚚 Espace Transporteur

- ✅ Consulter ses colis assignés
- ✅ Rechercher dans ses colis par adresse
- ✅ Mettre à jour le statut des colis (EN_TRANSIT, DELIVERED)
- ✅ Libération automatique après livraison

### 🔐 Sécurité

- ✅ Authentification JWT stateless
- ✅ Autorisation basée sur les rôles (RBAC)
- ✅ Hashage des mots de passe avec BCrypt
- ✅ Tokens avec expiration configurable
- ✅ Protection CORS

### 📊 Gestion des données

- ✅ Validation des données (Bean Validation)
- ✅ Schéma MongoDB flexible selon le type
- ✅ Pagination sur tous les endpoints de listing
- ✅ Gestion d'erreurs centralisée
- ✅ Transactions MongoDB

---

## 🏗️ Architecture

### Structure du projet

```
src/main/java/com/logistics/parcelandcarrier/
├── config/              # Configuration (Security, MongoDB, CORS)
├── controller/          # Controllers REST
├── dto/                 # Data Transfer Objects
│   ├── request/        # DTOs de requête
│   └── response/       # DTOs de réponse
├── entity/              # Entités MongoDB
├── enums/               # Énumérations avec logique métier
├── exception/           # Exceptions personnalisées
├── mapper/              # Mappers MapStruct
├── repository/          # Repositories Spring Data
├── security/            # Classes de sécurité JWT
└── service/             # Services métier
```

### Architecture en couches

```
┌─────────────────────────────────────────┐
│           Controllers (REST)            │
├─────────────────────────────────────────┤
│              Services                   │
├─────────────────────────────────────────┤
│            Repositories                 │
├─────────────────────────────────────────┤
│             MongoDB                     │
└─────────────────────────────────────────┘
```

### Diagrammes

Les diagrammes UML (cas d'utilisation et classes) sont disponibles dans le dossier `docs/diagrams/`.

---

## 🔧 Prérequis

- **Java 21** ou supérieur
- **Maven 3.8+**
- **MongoDB 7.0+** (ou Docker)
- **Git**
- **IDE** : IntelliJ IDEA, Eclipse, ou VS Code

---

## 📦 Installation

### 1. Cloner le repository

```bash
git clone https://github.com/votre-username/parcelandcarrier.git
cd parcelandcarrier
```

### 2. Installer les dépendances

```bash
mvn clean install
```

Cette commande va :
- Télécharger toutes les dépendances
- Générer les mappers MapStruct
- Compiler le projet
- Lancer les tests

---

## ⚙️ Configuration

### Profils disponibles

Le projet dispose de 3 profils :
- `dev` : Développement
- `prod` : Production
- `test` : Tests

### Configuration MongoDB

#### Option 1 : MongoDB local

```yaml
# application-dev.yml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: gestion_colis_dev
```

#### Option 2 : Docker

```bash
docker-compose up -d mongodb
```

### Variables d'environnement

Créer un fichier `.env` à la racine (ou configurer dans votre IDE) :

```bash
# MongoDB
MONGODB_HOST=localhost
MONGODB_PORT=27017
MONGODB_DATABASE=gestion_colis
MONGODB_USERNAME=admin
MONGODB_PASSWORD=admin123

# JWT
JWT_SECRET=VotreCleSecreteSuper123456789AChangerEnProduction
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
```

### Configuration JWT

```yaml
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000  # 24 heures en millisecondes
  issuer: parcel-and-carrier-api
```

---

## 🚀 Démarrage

### Méthode 1 : Maven

```bash
# Développement
mvn spring-boot:run

# Avec profil spécifique
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Méthode 2 : JAR

```bash
# Build
mvn clean package

# Run
java -jar target/parcelandcarrier-1.0.0.jar
```

### Méthode 3 : Docker Compose (Recommandé)

```bash
# Lancer tout l'environnement (app + MongoDB)
docker-compose up -d

# Voir les logs
docker-compose logs -f app

# Arrêter
docker-compose down
```

### Vérification

L'application démarre sur : **http://localhost:8080**

Test de santé : http://localhost:8080/actuator/health

---

## 📚 API Documentation

### Swagger UI

Une fois l'application lancée, accédez à la documentation interactive :

**http://localhost:8080/swagger-ui.html**

### OpenAPI JSON

**http://localhost:8080/api-docs**

### Endpoints principaux

#### 🔓 Authentification (Public)

```http
POST /api/auth/login
Content-Type: application/json

{
  "login": "admin",
  "password": "admin123"
}
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "login": "admin",
  "role": "ADMIN",
  "userId": "507f1f77bcf86cd799439011"
}
```

#### 👨‍💼 Admin - Gestion des colis

```http
# Créer un colis
POST /api/admin/packages
Authorization: Bearer {token}
Content-Type: application/json

{
  "type": "FRAGILE",
  "weight": 2.5,
  "destinationAddress": "123 Rue de Paris, 75001 Paris, France",
  "handlingInstructions": "Manipuler avec précaution"
}

# Lister les colis
GET /api/admin/packages?page=0&size=10&type=FRAGILE&status=PENDING

# Assigner à un transporteur
PATCH /api/admin/packages/{packageId}/assign/{transporterId}

# Changer le statut
PATCH /api/admin/packages/{packageId}/status
{
  "status": "IN_TRANSIT"
}
```

#### 🚚 Transporteur - Gestion des livraisons

```http
# Mes colis
GET /api/transporter/packages?status=IN_TRANSIT

# Rechercher par adresse
GET /api/transporter/packages/search?address=Paris

# Mettre à jour le statut
PATCH /api/transporter/packages/{packageId}/status
{
  "status": "DELIVERED"
}
```

### Comptes par défaut

Créés automatiquement au démarrage :

| Login | Password | Rôle | Spécialité |
|-------|----------|------|------------|
| `admin` | `admin123` | ADMIN | - |
| `transporteur1` | `trans123` | TRANSPORTER | STANDARD |
| `transporteur2` | `trans123` | TRANSPORTER | FRAGILE |
| `transporteur3` | `trans123` | TRANSPORTER | REFRIGERATED |

---

## 🧪 Tests

### Lancer tous les tests

```bash
mvn test
```

### Tests par catégorie

```bash
# Tests unitaires uniquement
mvn test -Dtest="*Test"

# Tests d'intégration
mvn test -Dtest="*IntegrationTest"

# Tests des repositories
mvn test -Dtest="*RepositoryTest"
```

### Rapport de couverture (JaCoCo)

```bash
mvn test jacoco:report

# Ouvrir le rapport
open target/site/jacoco/index.html
```

### Structure des tests

```
src/test/java/com/logistics/parcelandcarrier/
├── controller/          # Tests des controllers
├── service/             # Tests des services
├── repository/          # Tests des repositories
├── mapper/              # Tests des mappers
├── entity/              # Tests des entités
├── enums/               # Tests des enums
└── integration/         # Tests d'intégration
```

---

## 🐳 Déploiement

### Docker

#### Build de l'image

```bash
docker build -f docker/Dockerfile -t parcelandcarrier:latest .
```

#### Lancer avec Docker Compose

```bash
docker-compose up -d
```

Le fichier `docker-compose.yml` configure :
- **app** : Application Spring Boot
- **mongodb** : Base de données MongoDB
- **mongo-express** : Interface web MongoDB (dev)
- **n8n** : Workflow automation (bonus)

### Variables d'environnement en production

```yaml
environment:
  SPRING_PROFILES_ACTIVE: prod
  MONGODB_HOST: mongodb
  MONGODB_PORT: 27017
  MONGODB_USERNAME: ${MONGODB_USER}
  MONGODB_PASSWORD: ${MONGODB_PASS}
  JWT_SECRET: ${JWT_SECRET_PROD}
```

### CI/CD avec Jenkins

Le projet inclut un `Jenkinsfile` complet pour :
- ✅ Build automatique
- ✅ Tests unitaires et d'intégration
- ✅ Analyse de code (Checkstyle, JaCoCo)
- ✅ Scan de sécurité (OWASP)
- ✅ Build Docker
- ✅ Déploiement

```bash
# Lancer le pipeline
# (Configuré dans Jenkins)
```

---

## 🛠️ Technologies

### Backend

| Technologie | Version | Description |
|------------|---------|-------------|
| Spring Boot | 3.5.7 | Framework Java |
| Spring Security | 6.x | Authentification/Autorisation |
| Spring Data MongoDB | 4.x | Accès aux données |
| MapStruct | 1.5.5 | Mappers DTO/Entity |
| Lombok | 1.18.30 | Réduction du boilerplate |
| JWT (Auth0) | 4.4.0 | Tokens JWT |
| Bean Validation | 3.x | Validation des données |

### Database

| Technologie | Version | Description |
|------------|---------|-------------|
| MongoDB | 7.0 | Base NoSQL |
| Embedded MongoDB | 4.11.0 | Tests |

### Documentation

| Technologie | Version | Description |
|------------|---------|-------------|
| SpringDoc OpenAPI | 2.3.0 | Documentation Swagger |

### DevOps

| Technologie | Description |
|------------|-------------|
| Docker | Conteneurisation |
| Docker Compose | Orchestration |
| Jenkins | CI/CD |
| Maven | Build et dépendances |

### Tests

| Technologie | Description |
|------------|-------------|
| JUnit 5 | Tests unitaires |
| Mockito | Mocking |
| Spring Test | Tests d'intégration |
| JaCoCo | Couverture de code |

---

## 📁 Structure des données

### Collections MongoDB

#### Users
```json
{
  "_id": "ObjectId",
  "login": "string",
  "password": "string (hashed)",
  "role": "ADMIN | TRANSPORTER",
  "active": "boolean",
  "specialty": "STANDARD | FRAGILE | REFRIGERATED (if TRANSPORTER)",
  "status": "AVAILABLE | ON_DELIVERY (if TRANSPORTER)",
  "createdAt": "DateTime",
  "updatedAt": "DateTime"
}
```

#### Packages
```json
{
  "_id": "ObjectId",
  "type": "STANDARD | FRAGILE | REFRIGERATED",
  "weight": "double",
  "destinationAddress": "string",
  "status": "PENDING | IN_TRANSIT | DELIVERED | CANCELLED",
  "transporterId": "string (optional)",
  "handlingInstructions": "string (if FRAGILE)",
  "minTemperature": "double (if REFRIGERATED)",
  "maxTemperature": "double (if REFRIGERATED)",
  "createdAt": "DateTime",
  "updatedAt": "DateTime"
}
```

---

## 🔐 Sécurité

### Bonnes pratiques implémentées

- ✅ Hashage des mots de passe (BCrypt, cost=12)
- ✅ JWT avec expiration configurable
- ✅ Validation des entrées utilisateur
- ✅ Protection CSRF désactivée (API stateless)
- ✅ CORS configuré
- ✅ Gestion des exceptions sécurisée
- ✅ Utilisateurs inactifs ne peuvent pas se connecter
- ✅ Pas d'exposition des mots de passe dans les réponses

### Recommandations pour la production

1. **Changer le JWT secret** dans les variables d'environnement
2. **Réduire la durée d'expiration** des tokens (actuellement 24h)
3. **Utiliser HTTPS** en production
4. **Configurer les CORS** pour les origines autorisées uniquement
5. **Activer le rate limiting** pour éviter le brute force
6. **Logs sécurisés** (pas de données sensibles)

---

## 📊 Métriques et Monitoring

### Actuator Endpoints

```bash
# Health check
curl http://localhost:8080/actuator/health

# Métriques
curl http://localhost:8080/actuator/metrics

# Informations de l'application
curl http://localhost:8080/actuator/info
```

---

## 🤝 Contributeurs

- **Moustapha** - Développeur principal

**Développé avec ❤️ et ☕**

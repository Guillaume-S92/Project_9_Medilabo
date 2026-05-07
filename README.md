# Medilabo P9 - Microservices Spring Boot + MySQL + MongoDB

Projet de fin de parcours réalisé en **Java 21 / Spring Boot 3.5.13** avec une architecture **microservices**, une **gateway Spring Cloud Gateway**, une base **MySQL** pour les données patients, des bases **MongoDB** pour l'authentification et les notes médicales, ainsi que **Spring Security + JWT** pour protéger l'accès aux données.

L'application permet de gérer les patients d'une clinique, d'ajouter des notes médicales, puis de calculer un niveau de risque de diabète de type 2 à partir des informations du patient et des termes déclencheurs présents dans ses notes.

L'ensemble de l'application peut être lancé avec Docker Compose : front Angular, gateway, microservices back-end et bases de données.

---

## Fonctionnalités livrées

### Sprint 1 - Gestion des patients

- consultation des informations personnelles d'un patient ;
- création d'un patient ;
- mise à jour des informations personnelles d'un patient.

Les informations personnelles gérées sont :

- prénom ;
- nom ;
- date de naissance ;
- genre ;
- adresse postale ;
- numéro de téléphone.

L'adresse postale et le numéro de téléphone sont optionnels.

### Sprint 2 - Gestion des notes médicales

- consultation de l'historique médical d'un patient ;
- ajout d'une note médicale pour un patient.

### Sprint 3 - Calcul du risque de diabète

- calcul du risque de diabète de type 2 ;
- restitution d'un niveau de risque parmi :
    - `None`
    - `Borderline`
    - `In Danger`
    - `Early onset`

---

## Architecture du projet

```text
[ Client Angular dockerisé avec Nginx ]
        |
        | /api/**
        v
[ Spring Cloud Gateway ]
        |
        +--> auth-service       -> MongoDB authdb
        +--> patient-service    -> MySQL patientdb
        +--> note-service       -> MongoDB notedb
        +--> assessment-service -> calcule le risque en appelant patient-service et note-service
```

Le front Angular est servi par un conteneur Nginx.  
Les appels API du front passent par `/api/**`, puis sont redirigés vers la gateway.  
La gateway redirige ensuite les requêtes vers les microservices concernés.

---

## Technologies utilisées

### Back-end

- Java 21
- Spring Boot 3.5.13
- Spring Cloud Gateway
- Spring Security
- JWT
- BCrypt
- Spring Data JPA
- MySQL
- MongoDB
- Maven

### Front-end

- Angular
- TypeScript
- Node.js
- npm
- Nginx pour servir le build Angular dans Docker

### Environnement

- Docker
- Docker Compose

---

## Structure du projet

```text
Project_9_Medilabo/
│
├── auth-service/          # Authentification, utilisateurs, JWT, MongoDB
├── patient-service/       # Gestion des patients, MySQL
├── note-service/          # Gestion des notes médicales, MongoDB
├── assessment-service/    # Calcul du risque de diabète
├── gateway-service/       # Gateway Spring Cloud
├── medilabo-front/        # Application Angular dockerisée avec Nginx
│
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## Prérequis

### Pour lancer toute l'application avec Docker

Il faut installer :

- Docker Desktop
- Docker Compose

Vérification :

```bash
docker --version
docker compose version
```

Avec Docker, il n'est pas nécessaire d'installer Java, Maven, MySQL, MongoDB, Node.js ou Angular localement pour lancer l'application complète.

---

### Pour lancer le front Angular en local sans Docker

Il faut installer :

- Node.js
- npm

Node.js est nécessaire pour installer les dépendances Angular et lancer le serveur de développement.

Vérification :

```bash
node -v
npm -v
```

Angular CLI peut aussi être installé globalement, mais ce n'est pas obligatoire si le projet utilise les scripts npm.

Installation optionnelle d'Angular CLI :

```bash
npm install -g @angular/cli
```

Vérification :

```bash
ng version
```

---

### Pour lancer les services Spring Boot en local sans Docker

Il faut installer :

- Java 21
- Maven
- MySQL
- MongoDB ou disposer d'une URI MongoDB Atlas

Vérification :

```bash
java -version
mvn -version
```

---

## Configuration du projet

Le projet utilise un fichier `.env` pour centraliser les variables d'environnement utilisées par Docker Compose.

À la racine du projet, copier le fichier d'exemple :

```bash
cp .env.example .env
```

Sous Windows PowerShell :

```powershell
Copy-Item .env.example .env
```

Exemple de configuration minimale pour lancer le projet avec Docker :

```env
JWT_SECRET=change-me-with-at-least-32-characters
JWT_EXPIRATION_MINUTES=120

APP_DEFAULT_ORGANIZER_USERNAME=organizer
APP_DEFAULT_ORGANIZER_PASSWORD=Organizer123!
APP_DEFAULT_PRACTITIONER_USERNAME=practitioner
APP_DEFAULT_PRACTITIONER_PASSWORD=Practitioner123!

PATIENT_DB_URL=jdbc:mysql://mysql-patient:3306/patientdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
PATIENT_DB_USERNAME=patient
PATIENT_DB_PASSWORD=patientpassword
```

Le fichier `.env` ne doit pas être versionné.  
Le fichier `.env.example` sert uniquement d'exemple pour documenter les variables nécessaires.

Les bases MongoDB utilisées par `auth-service` et `note-service` sont lancées par Docker Compose.

La base MySQL utilisée par `patient-service` est également lancée par Docker Compose.

---

## Démarrage de l'application complète avec Docker

Depuis la racine du projet :

```bash
docker compose --env-file .env up --build
```

Cette commande lance :

- le front Angular ;
- la gateway ;
- les microservices back-end ;
- les bases MongoDB ;
- la base MySQL.

Le front est accessible sur :

```text
http://localhost:4200
```

La gateway est accessible sur :

```text
http://localhost:8080
```

---

## Ports exposés

```text
medilabo-front      -> http://localhost:4200
gateway-service     -> http://localhost:8080
auth-service        -> http://localhost:8081
patient-service     -> http://localhost:8082
note-service        -> http://localhost:8083
assessment-service  -> http://localhost:8084
```

Les bases de données exposées sont :

```text
MongoDB auth        -> localhost:27017
MongoDB notes       -> localhost:27018
MySQL patients      -> localhost:3307
```

---

## Arrêt de l'application

Pour arrêter les conteneurs :

```bash
docker compose down
```

Pour arrêter les conteneurs et supprimer les volumes de données :

```bash
docker compose down -v
```

---

## Comptes de démonstration

Deux comptes sont disponibles pour tester l'application :

```text
organizer / Organizer123!
practitioner / Practitioner123!
```

### Rôle ORGANIZER

L'organisateur peut :

- consulter les informations personnelles des patients ;
- créer un patient ;
- modifier les informations personnelles d'un patient.

### Rôle PRACTITIONER

Le praticien peut :

- consulter les patients ;
- consulter l'historique médical d'un patient ;
- ajouter une note médicale ;
- consulter le niveau de risque de diabète d'un patient.

---

## Sécurité

Le projet utilise Spring Security avec une authentification stateless basée sur JWT.

- `auth-service` gère l'authentification des utilisateurs.
- Les mots de passe sont encodés avec BCrypt.
- Après connexion, un token JWT est généré avec les rôles de l'utilisateur.
- `patient-service`, `note-service` et `assessment-service` vérifient le JWT avant d'autoriser l'accès aux endpoints protégés.
- Les rôles fonctionnels utilisés sont `ORGANIZER` et `PRACTITIONER`.

---

## Flux API conseillé

Les appels API passent par la gateway :

```text
http://localhost:8080
```

Depuis le front dockerisé, les appels passent par des URL relatives :

```text
/api/auth/**
/api/patients/**
/api/notes/**
/api/assessments/**
```

---

### 1. Se connecter

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"organizer","password":"Organizer123!"}'
```

La réponse contient un token JWT à utiliser dans les appels suivants.

---

### 2. Créer un patient

```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Martin",
    "birthDate": "1995-02-14",
    "gender": "F",
    "address": "12 rue des Lilas, Paris",
    "phone": "0102030405"
  }'
```

---

### 3. Ajouter une note médicale

```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Authorization: Bearer <TOKEN_PRACTITIONER>" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "<PATIENT_ID>",
    "content": "Patient avec Vertiges, Cholestérol élevé, Fumeuse, Réaction au traitement."
  }'
```

---

### 4. Calculer le risque de diabète

```bash
curl http://localhost:8080/api/assessments/patient/<PATIENT_ID> \
  -H "Authorization: Bearer <TOKEN_PRACTITIONER>"
```

Exemple de réponse :

```json
{
  "patientId": "123456",
  "riskLevel": "Borderline",
  "triggerCount": 4
}
```

---

## Règles de calcul du risque

Le service `assessment-service` calcule le niveau de risque à partir :

- de l'âge du patient ;
- du genre du patient ;
- du nombre de termes déclencheurs trouvés dans les notes médicales.

Les niveaux possibles sont :

```text
None
Borderline
In Danger
Early onset
```

Les termes déclencheurs recherchés dans les notes sont notamment :

```text
Hémoglobine A1C
Microalbumine
Taille
Poids
Fumeur / Fumeuse
Anormal
Cholestérol
Vertiges
Rechute
Réaction
Anticorps
```

---

## Détail des bases de données

### auth-service

- Base : MongoDB
- Base logique : `authdb`
- Contenu : utilisateurs, rôles, informations nécessaires à l'authentification.

### patient-service

- Base : MySQL
- Base logique : `patientdb`
- Contenu : informations personnelles des patients.

### note-service

- Base : MongoDB
- Base logique : `notedb`
- Contenu : notes médicales associées aux patients.

### assessment-service

- Pas de base de données dédiée.
- Le service récupère les informations du patient auprès de `patient-service`.
- Le service récupère les notes auprès de `note-service`.
- Il calcule ensuite le niveau de risque.

---

## Lancer le front Angular en local sans Docker

Le front Angular se trouve dans le dossier `medilabo-front`.

Aller dans le dossier du front :

```bash
cd medilabo-front
```

Installer les dépendances :

```bash
npm install
```

Lancer l'application :

```bash
npm start
```

Le front est ensuite disponible sur :

```text
http://localhost:4200
```

Si besoin, il est aussi possible de lancer Angular avec :

```bash
npx ng serve
```

Attention : en lancement local, il faut que la gateway soit disponible sur `http://localhost:8080`.

---

## Lancer les services Spring Boot en local sans Docker

Chaque microservice peut être lancé séparément avec Maven.

Exemple avec le service d'authentification :

```bash
cd auth-service
mvn spring-boot:run
```

Faire la même chose pour les autres services :

```text
patient-service
note-service
assessment-service
gateway-service
```

Dans ce mode, les URL des services doivent pointer vers `localhost`.

Exemple :

```env
AUTH_SERVICE_URL=http://localhost:8081
PATIENT_SERVICE_URL=http://localhost:8082
NOTE_SERVICE_URL=http://localhost:8083
ASSESSMENT_SERVICE_URL=http://localhost:8084
```

Pour `patient-service`, la base MySQL locale peut être configurée avec :

```env
PATIENT_DB_URL=jdbc:mysql://localhost:3306/patientdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
PATIENT_DB_USERNAME=patient
PATIENT_DB_PASSWORD=patientpassword
```

---

## Tests

### Back-end

Pour lancer les tests d'un service Spring Boot :

```bash
mvn test
```

Exemple :

```bash
cd patient-service
mvn test
```

### Front-end

Pour lancer les tests du front :

```bash
cd medilabo-front
npm test
```

---

## Commandes Docker utiles

Reconstruire et lancer tout le projet :

```bash
docker compose --env-file .env up --build
```

Lancer en arrière-plan :

```bash
docker compose --env-file .env up --build -d
```

Voir les conteneurs actifs :

```bash
docker ps
```

Voir les logs de tous les services :

```bash
docker compose logs -f
```

Voir les logs d'un service précis :

```bash
docker compose logs -f patient-service
```

Voir les logs du front :

```bash
docker compose logs -f medilabo-front
```

Arrêter les conteneurs :

```bash
docker compose down
```

Arrêter les conteneurs et supprimer les volumes :

```bash
docker compose down -v
```

---

## Remarques importantes

- Toute l'application peut être lancée avec Docker Compose.
- Le front Angular est servi par Nginx dans un conteneur Docker.
- Le front Angular appelle les API via `/api/**`.
- Les appels `/api/**` sont redirigés vers la gateway.
- Les microservices ne sont pas appelés directement par le front.
- Le token JWT doit être envoyé dans le header `Authorization`.
- Les données patients sont stockées dans MySQL.
- Les notes médicales sont stockées dans MongoDB.
- Les utilisateurs et rôles sont stockés dans MongoDB.
- Le calcul du risque est effectué par `assessment-service`, qui récupère les informations nécessaires auprès de `patient-service` et `note-service`.
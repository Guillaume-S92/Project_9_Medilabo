# Medilabo Front

Ce dossier contient l'application Angular du projet Medilabo P9.

La documentation complète du projet se trouve dans le README principal à la racine du dépôt.

---

## Rôle du front

Le front permet de :

- se connecter à l'application ;
- consulter le tableau de bord ;
- consulter la liste des patients ;
- créer ou modifier un patient avec le rôle `ORGANIZER` ;
- consulter les notes médicales avec le rôle `PRACTITIONER` ;
- ajouter une note médicale avec le rôle `PRACTITIONER` ;
- consulter l'assessment d'un patient.

---

## Lancement recommandé avec Docker

Dans le projet complet, le front est dockerisé.

Il est construit avec Node.js, puis servi par Nginx dans un conteneur Docker.

Pour lancer toute l'application, il faut se placer à la racine du projet et exécuter :

```bash
docker compose --env-file .env up --build
```

Le front est ensuite disponible sur :

```text
http://localhost:4200
```

Les appels API du front passent par des URL relatives :

```text
/api/auth/**
/api/patients/**
/api/notes/**
/api/assessments/**
```

Ces appels sont redirigés par Nginx vers la gateway Spring Cloud.

---

## Lancer le front en local sans Docker

Prérequis :

- Node.js
- npm

Installation des dépendances :

```bash
npm install
```

Lancement de l'application :

```bash
npm start
```

Ou, si besoin :

```bash
npx ng serve
```

L'application est disponible sur :

```text
http://localhost:4200
```

Attention : pour que le front fonctionne en local, la gateway doit être disponible sur :

```text
http://localhost:8080
```

---

## Build Angular

Pour générer le build de production :

```bash
npm run build
```

Le build est généré dans le dossier :

```text
dist/
```

Dans Docker, ce build est ensuite copié dans une image Nginx pour être servi en HTTP.

---

## Tests front

Pour lancer les tests :

```bash
npm test
```

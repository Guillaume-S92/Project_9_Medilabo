# Briefing Green Code - Medilabo P9

## Objectif du document

Ce document présente la réflexion Green Code appliquée à mon projet Medilabo P9.

L'objectif n'est pas de dire que l'application est totalement optimisée ou parfaite d'un point de vue environnemental, mais d'expliquer les choix que j'ai faits pour limiter les traitements inutiles, garder une architecture compréhensible et prévoir des pistes d'amélioration réalistes.

Pour moi, le Green Code ne se limite pas à réduire la consommation CPU. Il consiste surtout à concevoir une application :

- utile ;
- sobre ;
- maintenable ;
- mesurable ;
- adaptée au besoin réel.

Dans ce projet, l'application répond à un besoin métier précis : permettre à une clinique de gérer ses patients, ses notes médicales et d'obtenir un niveau de risque de diabète de type 2.

---

## Application au projet Medilabo

Mon projet est organisé en architecture microservices :

```text
[ Client Angular ]
        |
        v
[ Spring Cloud Gateway ]
        |
        +--> auth-service       -> MongoDB authdb
        +--> patient-service    -> MySQL patientdb
        +--> note-service       -> MongoDB notedb
        +--> assessment-service -> calcul du risque
```

Cette séparation permet d'isoler les responsabilités :

- `auth-service` gère l'authentification et les utilisateurs ;
- `patient-service` gère les informations personnelles des patients ;
- `note-service` gère les notes médicales ;
- `assessment-service` calcule le niveau de risque ;
- `gateway-service` sert de point d'entrée unique pour les appels API.

Cette architecture peut être plus coûteuse qu'une application monolithique si elle est mal utilisée, car elle implique plusieurs services et plusieurs appels réseau. C'est pour cela qu'il faut éviter les échanges inutiles entre services et garder des payloads simples.

---

## Choix cohérents avec une démarche Green Code

### 1. Séparation des responsabilités

J'ai séparé les services par domaine fonctionnel. Cela permet de ne pas charger toute l'application pour une seule responsabilité.

Par exemple :

- les données patients sont gérées dans `patient-service` ;
- les notes médicales sont gérées dans `note-service` ;
- le calcul du risque est isolé dans `assessment-service`.

Cette séparation rend le projet plus lisible et plus maintenable. Un code plus maintenable évite aussi les corrections complexes, les régressions et les traitements mal maîtrisés.

---

### 2. Calcul du risque à la demande

Le niveau de risque n'est pas recalculé en permanence en arrière-plan.

Il est calculé lorsqu'un utilisateur consulte l'assessment d'un patient. Cette approche évite de lancer des traitements réguliers ou des batchs inutiles.

Dans mon cas, c'est adapté car le calcul dépend :

- des informations du patient ;
- des notes médicales existantes ;
- du nombre de termes déclencheurs trouvés.

Le calcul à la demande est donc plus sobre qu'un recalcul automatique permanent.

---

### 3. Utilisation de bases adaptées au type de données

J'ai utilisé deux types de bases de données selon le besoin :

- MySQL pour les informations personnelles des patients ;
- MongoDB pour les utilisateurs et les notes médicales.

Les données patients sont structurées : nom, prénom, date de naissance, genre, adresse et téléphone. MySQL est donc adapté pour ce type de données relationnelles.

Les notes médicales sont moins structurées, car elles contiennent du texte libre. MongoDB est donc cohérent pour stocker ce type de document.

Cette séparation évite de forcer un seul modèle de données pour tous les usages.

---

### 4. Point d'entrée unique avec la gateway

Le front Angular ne contacte pas directement chaque microservice.

Les appels passent par la gateway :

```text
/api/auth/**
/api/patients/**
/api/notes/**
/api/assessments/**
```

Cela centralise les routes et évite de multiplier les points d'entrée côté front. C'est plus simple à maintenir et plus clair pour l'évolution du projet.

---

### 5. Sécurité stateless avec JWT

L'application utilise une authentification stateless basée sur JWT.

Cela évite de stocker une session serveur pour chaque utilisateur connecté. Chaque requête contient le token nécessaire à l'authentification.

Ce choix est cohérent avec une architecture microservices, car les services peuvent vérifier le token sans dépendre d'une session centralisée.

---

### 6. Configuration externalisée

Les variables sensibles et les paramètres d'environnement sont externalisés via un fichier `.env`.

Cela permet d'éviter de mettre les secrets directement dans le code source. Le fichier `.env` n'est pas versionné, tandis que `.env.example` documente les variables nécessaires.

Cette approche permet aussi de garder le même code pour plusieurs environnements :

- local ;
- test ;
- staging ;
- production.

---

## Points de vigilance Green Code

Même si l'architecture est fonctionnelle, une architecture microservices peut entraîner une consommation plus importante si elle est mal maîtrisée.

Les principaux points de vigilance sont :

- éviter les appels inter-services inutiles ;
- limiter la taille des réponses JSON ;
- ne pas renvoyer des objets complets si seuls quelques champs sont nécessaires ;
- éviter les logs trop verbeux en production ;
- surveiller le temps de réponse des endpoints ;
- éviter de lancer tous les services si un seul module doit être testé ;
- ne pas multiplier les dépendances inutiles.

---

## Exemples concrets dans mon projet

### Assessment

Le service `assessment-service` ne possède pas sa propre base de données.

Il récupère les informations nécessaires auprès de :

- `patient-service` pour les informations du patient ;
- `note-service` pour les notes médicales.

Il calcule ensuite le niveau de risque.

Ce choix évite de dupliquer inutilement les données patients ou les notes dans une troisième base.

---

### Patients

Les informations patients sont stockées dans MySQL, car elles sont structurées et correspondent bien à un modèle relationnel.

Cela permet de garder des données propres et cohérentes.

---

### Notes

Les notes sont stockées dans MongoDB, car elles sont composées principalement de texte médical libre.

Ce choix permet de gérer plus facilement des documents dont le contenu peut varier d'un patient à l'autre.

---

### Front Angular

Le front Angular consomme uniquement les routes exposées par la gateway.

Cela évite de disperser la logique d'appel aux microservices dans l'interface utilisateur.

Le front reste donc plus simple à maintenir.

---

## Limites actuelles

Le projet pourrait encore être amélioré d'un point de vue Green Code.

Les limites actuelles sont :

- il n'y a pas encore de mesure précise de la consommation mémoire ou CPU ;
- il n'y a pas encore de dashboard d'observabilité complet ;
- il n'y a pas encore de tests de charge ;
- il n'y a pas encore de cache applicatif ;
- il n'y a pas encore de pagination avancée si la volumétrie augmente fortement ;
- les images Docker pourraient être optimisées davantage ;
- les logs pourraient être mieux adaptés selon l'environnement.

Ces limites ne bloquent pas le projet, mais elles représentent des axes d'amélioration pour une version plus avancée.

---

## Pistes d'amélioration

Pour aller plus loin, je pourrais ajouter :

- mettre en place une solution d'observabilité, par exemple avec Micrometer / Prometheus / Grafana, ELK ou New Relic ;
- suivre les temps de réponse, les erreurs, les logs, la mémoire utilisée et le comportement des appels entre microservices ;
- des tests de charge pour mesurer le comportement de l'application ;
- une pagination sur les listes si la volumétrie augmente ;
- une stratégie d'archivage des anciennes notes médicales ;
- une réduction de la taille des images Docker ;
- une analyse des dépendances inutilisées ;
- un cache ciblé sur certaines données peu modifiées ;
- des DTO plus spécialisés pour éviter de renvoyer trop de données.

---

## Conclusion

Dans ce projet, j'ai surtout cherché à garder une architecture claire, utile et maintenable.

La démarche Green Code se retrouve dans plusieurs choix :

- calculer le risque uniquement lorsque c'est nécessaire ;
- éviter de dupliquer les données entre les services ;
- utiliser une base adaptée au type de données ;
- centraliser les appels via une gateway ;
- externaliser la configuration ;
- garder des responsabilités séparées entre les microservices.

Le projet pourrait encore être amélioré avec plus de mesures, d'observabilité et d'optimisation Docker, mais il pose déjà une base saine pour faire évoluer l'application de manière plus sobre et plus maîtrisée.
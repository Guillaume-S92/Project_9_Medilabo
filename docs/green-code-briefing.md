# Briefing Green Code

## Message principal
Le Green Code ne consiste pas seulement à "faire moins de CPU". Il s'agit surtout de concevoir un service numérique :
- utile ;
- sobre ;
- mesurable ;
- maintenable dans le temps.

## Application au projet
Sur cette architecture, les leviers les plus pertinents sont :
1. réduire les appels inter-services inutiles ;
2. limiter la taille des payloads JSON ;
3. éviter la duplication de données ;
4. indexer seulement les champs réellement interrogés ;
5. paginer les listes si la volumétrie augmente ;
6. limiter les logs en production ;
7. surveiller temps de réponse, mémoire et taille des réponses ;
8. archiver les données anciennes si elles ne sont plus consultées souvent.

## Exemples concrets à citer à l'oral
- ne pas renvoyer des objets trop volumineux ;
- éviter les bibliothèques inutiles ;
- garder des images Docker légères ;
- factoriser les règles métier au bon endroit ;
- éviter les batchs inutiles ;
- calculer le risque à la demande plutôt qu'en permanence ;
- documenter un budget de performance.

## Limites actuelles
- pas encore de mesure automatisée de consommation ;
- pas encore de pagination ;
- pas encore de cache applicatif ;
- pas encore de dashboard d'observabilité.

## Pistes d'amélioration
- ajouter Micrometer / Prometheus ;
- ajouter des tests de charge ;
- mesurer le nombre d'appels par requête métier ;
- mettre en place une politique d'archivage des notes ;
- documenter des SLO techniques et de sobriété.

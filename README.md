
  

# Stage CHU 2024

Voici un projet qui m'a été demandé lors de mon stage. Le but de celui-ci est de créer une application web ayant comme vocation de centraliser tout le processus de demande de matériel, de la demande en elle-même jusqu'à la facture de livraison, tout en comprenant les validations par les autorités compétentes

  
  
  

## Libs utilisé :

- JWT, pour avoir une connexion sécurisée, ainsi qu'un accès aux pages seulement si la personne a été authentifié

- iText, pour la création de pdf

- gson

- my sql connector

  

## Comment se connecter à sa BDD ?

  

- Cela se fait dans le fichier MySQLDatabase.java, ainsi que SomethingDatabase.

- Pour la création des DAO, on crée les *models* en adéquation avec les tables présentes dans la BDD.

- Il y a aussi la présence de *script.sql* si l'on veut modifier la bdd en elle même.

  

## Comment ajouter/modifier des méthodes ?

  

- Pour créer une API, du côté Back, on crée la route dans App.java, route qui appelle un Controller qui lui même appelle le Dao adéquat. Du côté Front, l'appel à cette API se fera dans un fichier service correspondant à la page HTML, puis le nécessaire se fera en respectant le modèle MVC.

  

- Pour créer un évènement SSE, on créé un "*.emit()*" dans la méthode du Controller voulu sur un channel qu'on nommera. Pour s'y abonner, on utilisera un "*.connect()*" et un "*.subscribe()*" dans le script js de la page HTML nécessitant l'information.

  

- Afin de faire fonctionner le jwt, veuillez un fichier application.properties (voir *l.30 UtilisateurController*) , avec pour contenu :

>jwt.secret.key="insérer votre clé secrète"

  

## Comment modifier le hashage ?

  

Pour modifier le hashage appliqué au mot de passe, on va du côté de

*HashUtil.java*.

  

## Fonction implémentée :

- Le système de connexion utilisant un hashage du mot de passe, ainsi que la création d'un JWT dans le Local Storage.

- La possibilité de réaliser une demande en rapport avec un projet ou non.

- Le tri des demandes par mois.

- Le total fonctionnement de la zone de recherche.

- La possibilité de voir l'état d'une demande en fonction de si l'on est membre ou autre

- La possibilité d'avoir des fichiers PDF (cf section Bug pour plus d'informations)

- Implémentation de 7 groupes différents (= autorisations)

- Affichage dynamique de la page en fonction des autorisations

- La répartition d'actions possibles en fonction des autorisations possédées

- Réaliser toute la validation d'une demande du début à la fin

- Notifications tout au long du processus


  

## Fonction à implémenter :

- Le rejet durant n'importe quelle étape de la demande

- Certaines notifications n'arrivent pas à être lu et bloque l'affichage 

- L'envoi des mails lors des notifications

- L'affichages des bonnes informations sur la page "état de la commande"

- Ouvrir les PDF via les liens de la page "état de la commande"

- Les lettres du domaines pour former l'id de la demande (exemple: SI)

- Tri des pdf en dossier en fonction des demandes

- L'aspect "Questionner les stocks"

- Les demandes pour la téléphonie

- Une nouvelle affichant toutes les notifications en cours (La méthode afin de créer l'API est déjà présente dans le code)






  

## Bug connu :

- La génération de PDF via generatePdf et savePdf rencontre un problème lié à la logique de fonctionnement. En effet, lors du premier appui, rien ne fonctionne, ce n'est qu'au second appui que les boutons menant aux PDF fonctionnent.

  

# Perspective d'avenir :

  

Afin d'améliorer ce projet, un portage vers un framework est envisagé afin d'avoir une application plus robuste.

Parmi les choix possibles, mon attention s'est tourné vers Node.js et Spring, pour finalement choisir **Spring** afin de normalement avoir un portage plus simple et rapide, dû à la conservation du Java.

  

  

```

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
- La possibilité de voir l'état d'une demande (cf section suivante pour plus d'informations)
- La possibilité d'avoir des fichiers PDF (cf section Bug pour plus d'informations)

## Fonction à implémenter :
- La gestion des rôles afin de permettre l'avancée d'une demande, ou bien de permettre aux responsables de voir toutes les demandes au lieu de seulement celles qu'ils ont demandés.
- Le système de SSE pour gérer les notifications.
- Ouvrir les PDF via les liens de la page "état de la commande"
- Finaliser la page "état de la demande" afin de permettre l'avancée d'une demande via des boutons.

## Bug connu :
- Le système d'authentification n'est pas encore entièrement pris en charge (cf *auth.js*). En effet, pour l'instant le client vérifie seulement la présence d'un JWT quelconque.


- La génération de PDF via generatePdf et savePdf rencontre un problème lié à la logique de fonctionnement. En effet, lors du premier appui, rien ne fonctionne, ce n'est qu'au second appui que les boutons menant aux PDF fonctionnent.

- Le serveur se DDOS tout seul dû à l'instabilité de l'utilisation de XAMPP

# Perspective d'avenir :

Afin d'améliorer ce projet, un portage vers un framework est envisagé afin d'avoir une application plus robuste.
Parmi les choix possibles, mon attention s'est tourné vers Node.js et Spring, pour finalement choisir **Spring** afin de normalement avoir un portage plus simple et rapide, dû à la conservation du Java.

 

```
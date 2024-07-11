/* 
Utilisateur (id, nom, prenom, email,username, mot_de_passe, role)
Projet (id, nom, description)
Fournisseur (id, nom, adresse, email, telephone)
Demande (id, #utilisateur_id, #projet_id,referant,domaine,typeof,marque,reference,pour,ou,marche,justification, descriptif, quantite, urgence, etat, date_demande)
Devis (id, #demande_id, #fournisseur_id, montant, fichier_pdf, etat, date_devis)
BonCommande (id, #devis_id, #utilisateur_id, etat, fichier_pdf, date_creation)
Facture (id, #bon_commande_id, montant, fichier_pdf, etat, date_facture)
Relance (id, #bon_commande_id, date_relance, message, reponse)
Stock (id, #bon_commande_id, date_livraison, etat)
Notif (id, #utilisateur_id, message, lu, date_notification)
*/

CREATE DATABASE IF NOT EXISTS GestionMateriel;
USE GestionMateriel;

-- Table Utilisateur
CREATE TABLE Utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('membre', 'responsable', 'directeur', 'admin') NOT NULL
);

-- Table Projet
CREATE TABLE Projet (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    description TEXT
);

-- Table Fournisseur
CREATE TABLE Fournisseur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    adresse VARCHAR(255),
    email VARCHAR(255),
    telephone VARCHAR(20)
);

-- Table Demande
CREATE TABLE Demande (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id INT,
    projet_id INT,
    referant VARCHAR(255),
    domaine VARCHAR(255),
    typeof VARCHAR(255),
    marque VARCHAR(255),
    reference VARCHAR(255),
    pour VARCHAR(255),
    ou VARCHAR(255),
    marche VARCHAR(255),
    justification TEXT,
    descriptif TEXT,
    quantite INT NOT NULL,
    urgence ENUM('basse', 'moyenne', 'haute') NOT NULL,
    etat ENUM('envoyée', 'en cours de traitement', 'annulée', 'finalisée') DEFAULT 'envoyée',
    date_demande TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table Devis
CREATE TABLE Devis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    demande_id INT,
    fournisseur_id INT,
    montant DECIMAL(10, 2) NOT NULL,
    fichier_pdf VARCHAR(255),
    etat ENUM('à valider', 'validé', 'refusé') DEFAULT 'à valider',
    date_devis TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table BonCommande
CREATE TABLE BonCommande (
    id INT AUTO_INCREMENT PRIMARY KEY,
    devis_id INT,
    utilisateur_id INT,
    etat ENUM('en édition', 'à valider', 'validé', 'envoyé', 'annulé', 'livré') DEFAULT 'en édition',
    fichier_pdf VARCHAR(255),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table Facture
CREATE TABLE Facture (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bon_commande_id INT,
    montant DECIMAL(10, 2) NOT NULL,
    fichier_pdf VARCHAR(255),
    etat ENUM('à valider', 'validée', 'refusée') DEFAULT 'à valider',
    date_facture TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table Relance
CREATE TABLE Relance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bon_commande_id INT,
    date_relance TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    message TEXT,
    reponse BOOLEAN DEFAULT FALSE
);



-- Table Notif
CREATE TABLE Notif (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id INT,
    message TEXT,
    lu BOOLEAN DEFAULT FALSE,
    date_notification TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Ajout des clés étrangères
ALTER TABLE Demande ADD CONSTRAINT fk_demande_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id);
ALTER TABLE Demande ADD CONSTRAINT fk_demande_projet FOREIGN KEY (projet_id) REFERENCES Projet(id);

ALTER TABLE Devis ADD CONSTRAINT fk_devis_demande FOREIGN KEY (demande_id) REFERENCES Demande(id);
ALTER TABLE Devis ADD CONSTRAINT fk_devis_fournisseur FOREIGN KEY (fournisseur_id) REFERENCES Fournisseur(id);

ALTER TABLE BonCommande ADD CONSTRAINT fk_boncommande_devis FOREIGN KEY (devis_id) REFERENCES Devis(id);
ALTER TABLE BonCommande ADD CONSTRAINT fk_boncommande_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id);

ALTER TABLE Facture ADD CONSTRAINT fk_facture_boncommande FOREIGN KEY (bon_commande_id) REFERENCES BonCommande(id);

ALTER TABLE Relance ADD CONSTRAINT fk_relance_boncommande FOREIGN KEY (bon_commande_id) REFERENCES BonCommande(id);


ALTER TABLE Notif ADD CONSTRAINT fk_notification_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id);

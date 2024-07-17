/* 
Utilisateur (id, nom, prenom, email,username, mot_de_passe, role)
Projet (id, nom, description)
Fournisseur (id, nom, adresse, email, telephone)
Demande (id, #utilisateur_id, #projet_id,referant,domaine,typeof,marque,reference,pour,ou,marche,justification, descriptif, quantite, urgence, etat, date_demande,pdfPath)
Devis (id, #demande_id, #fournisseur_id, montant, fichier_pdf, etat, date_devis)
BonCommande (id, #devis_id, #utilisateur_id, etat, fichier_pdf, date_creation)
Facture (id, #bon_commande_id, montant, fichier_pdf, etat, date_facture)
Relance (id, #bon_commande_id, date_relance, message, reponse)
Stock (id, #bon_commande_id, description,quantite)
Notif (id, #utilisateur_id, message, lu, date_notification)
*/
DROP DATABASE IF EXISTS GestionMateriel;
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
    role ENUM('membre', 'responsable','respDevis','respDevisTel','stock', 'directeur', 'adminteam') NOT NULL
);

-- Table Projet
CREATE TABLE Projet (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) UNIQUE NOT NULL,
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
    projet_nom VARCHAR(255),
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
    date_demande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pdfPath VARCHAR(255)
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

-- Table Stock
CREATE TABLE Stock (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bon_commande_id INT,
    description TEXT,
    quantite INT NOT NULL
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
ALTER TABLE Demande ADD CONSTRAINT fk_demande_projet FOREIGN KEY (projet_nom) REFERENCES Projet(nom);

ALTER TABLE Devis ADD CONSTRAINT fk_devis_demande FOREIGN KEY (demande_id) REFERENCES Demande(id);
ALTER TABLE Devis ADD CONSTRAINT fk_devis_fournisseur FOREIGN KEY (fournisseur_id) REFERENCES Fournisseur(id);

ALTER TABLE BonCommande ADD CONSTRAINT fk_boncommande_devis FOREIGN KEY (devis_id) REFERENCES Devis(id);
ALTER TABLE BonCommande ADD CONSTRAINT fk_boncommande_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id);

ALTER TABLE Facture ADD CONSTRAINT fk_facture_boncommande FOREIGN KEY (bon_commande_id) REFERENCES BonCommande(id);

ALTER TABLE Relance ADD CONSTRAINT fk_relance_boncommande FOREIGN KEY (bon_commande_id) REFERENCES BonCommande(id);

ALTER TABLE Stock ADD CONSTRAINT fk_stock_boncommande FOREIGN KEY (bon_commande_id) REFERENCES BonCommande(id);

ALTER TABLE Notif ADD CONSTRAINT fk_notification_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id);

-- Remplissage Table

INSERT INTO Utilisateur (nom, prenom, email, username, mot_de_passe, role) VALUES 
('random','un','random@chu.re','machin','51962cb4910a4e69486f0b4b3ac1fa148a5f9456cdb975e568e0b415f155b6e7','membre'),
('responsable','un','responsable@chur.re','geredestrucs','4fd7fde6445787d3b0994d476c2fa6da1d89e8edb20bc5ce37e733b24ee45fee','responsable'),
('responsable','devis','responsabledevis@chu.re','geredestrucsdevis','5d049da9dbece353143dfbf6e4dd5a9b5386e1a297b4c531c2479b7917430362','respDevis'),
('responsable','devistel','responsabledevistel@chu.re','geredestrucsdevistel','ce2667b45246fdfebf9ce4718d6e916ae22815f6540c29e7236c5bad73334b9c','respDevisTel'),
('stock','un','stock@chu.re','gerelesstoks','d928bc3b82d8eb7df1130a822e15e1065fe80fb4898eb7b36a9ebe43034d7cfb','stock'),
('directeur','un','directeur@chu.re','dirigedestrucs','aa07deca25865fcc95fd7a610533507cf3c36d6e012a58b9053cc406868864b6','directeur'),
('admin','team','adiminteam@chu.re','faitdestrucs','0c2e9e5290b94e9e913f1a64cfcabed420d4c28cbc85601130d14224cec7b3f4','adminteam');

-- Insérer un projet pour l'utilisateur avec ID 1
INSERT INTO Projet (nom, description) VALUES
('', ''),
 ('Stage', 'Faut que ça fonctionne');


-- Insérer une demande pour l'utilisateur avec ID 1
INSERT INTO Demande (utilisateur_id, projet_nom, referant, domaine, typeof, marque, reference, pour, ou, marche, justification, descriptif, quantite, urgence)
VALUES (1, "", 'Jinx', 'Oskour', 'Test', 'Mayday', 'xoxo', 'unjour', 'site', 'Marché de référence', 'Justification détaillée', 'Description de la demande', 10, 'haute');

-- Insérer une notification pour l'utilisateur avec ID 1
INSERT INTO Notif (utilisateur_id, message, lu)
VALUES (1, 'Tu me vois', FALSE);



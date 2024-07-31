/* 
Utilisateur (id, nom, prenom, email,username, mot_de_passe, role)
Groupe(id,nom)
UtlisateurGroupe(#utilisateur_id,#groupe_id)
Projet (id, nom, description)
Fournisseur (id, nom, adresse, email, telephone)
Demande (id, #utilisateur_id, #projet_id,referant,domaine,typeof,marque,reference,pour,ou,marche,justification, descriptif,additional_details quantite, urgence, etat, date_demande,pdfPath)
Devis (id, #demande_id, #fournisseur_id,  fichier_pdf, etat, date_devis,nom_valideur)
BonCommande (id, #devis_id,  etat, fichier_pdf, date_creation,nom_editeur)
Facture (id, #bon_commande_id, fichier_pdf, etat, date_facture,date_livraison,lieu_livraison,nom_signataire,nom_transitaire)
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
    additional_details TEXT,
    quantite INT NOT NULL,
    urgence ENUM('basse', 'moyenne', 'haute') NOT NULL,
    etat ENUM('envoyée', 'en_cours_de_traitement', 'devis_a_valider', 'devis_en_cours_de_validation', 'bc_a_editer', 'bc_en_cours_dedition', 'bc_a_valider', 'bc_en_cours_de_validation', 'bc_valide_envoi_fournisseur','envoi_fournisseur_en_cours', 'bc_envoye_attente_livraison', 'commande_annulee', 'commande_livree_finalisee') DEFAULT 'envoyée',
    date_demande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pdfPath VARCHAR(255)
);

-- Table Devis
CREATE TABLE Devis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    demande_id INT,
    fournisseur_id INT DEFAULT NULL,
    fichier_pdf VARCHAR(255),
    etat ENUM('à_valider', 'validé', 'refusé') DEFAULT 'à_valider',
    date_devis TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    nom_valideur VARCHAR(255) DEFAULT NULL
);

-- Table BonCommande
CREATE TABLE BonCommande (
    id INT AUTO_INCREMENT PRIMARY KEY,
    devis_id INT,
    etat ENUM('en_édition', 'à_valider', 'validé', 'envoyé', 'annulé', 'livré') DEFAULT 'à_valider',
    fichier_pdf VARCHAR(255),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    nom_editeur VARCHAR(255) DEFAULT NULL
);

-- Table Facture
CREATE TABLE Facture (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bon_commande_id INT,
    fichier_pdf VARCHAR(255),
    etat ENUM('à_valider', 'validée', 'refusée') DEFAULT 'à_valider',
    date_facture TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_livraison TIMESTAMP,
    lieu_livraison VARCHAR(255) DEFAULT NULL,
    nom_signataire VARCHAR(255) DEFAULT NULL,
    nom_transitaire VARCHAR(255) DEFAULT NULL
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
    demande_id INT,
    message TEXT,
    type ENUM('demande_envoyee', 'demande_en_cours_de_traitement', 'devis_a_valider', 'devis_en_cours_de_validation', 'bc_a_editer', 'bc_en_cours_dedition', 'bc_a_valider', 'bc_en_cours_de_validation', 'bc_valide_envoi_fournisseur','envoi_fournisseur_en_cours', 'bc_envoye_attente_livraison','facture_a_valider', 'commande_annulee', 'commande_livree_finalisee') NOT NULL,
    lu BOOLEAN DEFAULT FALSE,
    date_notification TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table Groupe
CREATE TABLE Groupe (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL
);

-- Insérer les groupes prédéfinis
INSERT INTO Groupe (nom) VALUES
('createDemande'),
('treatDevis'),
('validateDevis'),
('treatBc'),
('validateBc'),
('notifBcSend'),
('inventory');

-- Table intermédiaire pour la relation many-to-many entre Utilisateur et Groupe
CREATE TABLE UtilisateurGroupe (
    utilisateur_id INT,
    groupe_id INT,
    PRIMARY KEY (utilisateur_id, groupe_id),
    FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id),
    FOREIGN KEY (groupe_id) REFERENCES Groupe(id)
);

-- Ajout des clés étrangères
ALTER TABLE Demande ADD CONSTRAINT fk_demande_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id);
ALTER TABLE Demande ADD CONSTRAINT fk_demande_projet FOREIGN KEY (projet_nom) REFERENCES Projet(nom);

ALTER TABLE Devis ADD CONSTRAINT fk_devis_demande FOREIGN KEY (demande_id) REFERENCES Demande(id);
ALTER TABLE Devis ADD CONSTRAINT fk_devis_fournisseur FOREIGN KEY (fournisseur_id) REFERENCES Fournisseur(id);

ALTER TABLE BonCommande ADD CONSTRAINT fk_boncommande_devis FOREIGN KEY (devis_id) REFERENCES Devis(id);


ALTER TABLE Facture ADD CONSTRAINT fk_facture_boncommande FOREIGN KEY (bon_commande_id) REFERENCES BonCommande(id);

ALTER TABLE Relance ADD CONSTRAINT fk_relance_boncommande FOREIGN KEY (bon_commande_id) REFERENCES BonCommande(id);

ALTER TABLE Stock ADD CONSTRAINT fk_stock_boncommande FOREIGN KEY (bon_commande_id) REFERENCES BonCommande(id);

ALTER TABLE Notif ADD CONSTRAINT fk_notification_demande FOREIGN KEY (demande_id) REFERENCES Demande(id);

-- Remplissage Table

INSERT INTO Utilisateur (nom, prenom, email, username, mot_de_passe, role) VALUES 
('random','un','random@chu.re','machin','51962cb4910a4e69486f0b4b3ac1fa148a5f9456cdb975e568e0b415f155b6e7','membre'),
('responsable','un','responsable@chur.re','geredestrucs','4fd7fde6445787d3b0994d476c2fa6da1d89e8edb20bc5ce37e733b24ee45fee','responsable'),
('responsable','devis','responsabledevis@chu.re','geredestrucsdevis','5d049da9dbece353143dfbf6e4dd5a9b5386e1a297b4c531c2479b7917430362','respDevis'),
('responsable','devistel','responsabledevistel@chu.re','geredestrucsdevistel','ce2667b45246fdfebf9ce4718d6e916ae22815f6540c29e7236c5bad73334b9c','respDevisTel'),
('stock','un','stock@chu.re','gerelesstoks','d928bc3b82d8eb7df1130a822e15e1065fe80fb4898eb7b36a9ebe43034d7cfb','stock'),
('directeur','un','directeur@chu.re','dirigedestrucs','aa07deca25865fcc95fd7a610533507cf3c36d6e012a58b9053cc406868864b6','directeur'),
('administration','team','adiminteam@chu.re','faitdestrucs','0c2e9e5290b94e9e913f1a64cfcabed420d4c28cbc85601130d14224cec7b3f4','adminteam'),
('Bruce', 'Tout Puissant', 'admin@chu.re', 'admin', '1e8b523b8f96fbf5e1a3408a97538c29d660be96ca51b5559e04fd637a8318b1', 'directeur');
-- Insérer un projet pour l'utilisateur avec ID 1
INSERT INTO Projet (nom, description) VALUES
('', ''),
('ProjetA', 'Projet de développement logiciel'),
('ProjetB', 'Projet de recherche en biotechnologie'),
('ProjetC', 'Projet de centralisation des données');

 -- Insérer un fournisseur
INSERT INTO Fournisseur (nom, adresse, email, telephone)
VALUES ('FournisseurA', '123 Rue du Fournisseur', 'contact@fournisseura.com', '+33 123456789'),
('FournisseurB', '456 Rue du Fournisseur', 'contact@fournisseurb.com', '+33 987654321'),
('FournisseurC', '789 Rue du Fournisseur', 'contact@fournisseurc.com', '+33 112233445');

-- Insérer une demande pour l'utilisateur avec ID 1
INSERT INTO Demande (utilisateur_id, projet_nom, referant, domaine, typeof, marque, reference, pour, ou, marche, justification, descriptif,additional_details, quantite, urgence)
VALUES (1, 'ProjetA', 'Responsable A', 'Informatique', 'Matériel', 'MarqueA', 'RefA', 'Département A', 'Lieu A', 'Marché A', 'Justification A', 'Descriptif A', 'Détails A', 10, 'haute'),
(2, 'ProjetB', 'Jinx', 'Biotechnologie', 'Consommables', 'MarqueB', 'RefB', 'Département B', 'Lieu B', 'Marché B', 'Justification B', 'Descriptif B', 'Détails B', 20, 'moyenne'),
(3, 'ProjetC', 'Responsable C', 'Donnée', 'Équipement', 'MarqueC', 'RefC', 'Département C', 'Lieu C', 'Marché C', 'Justification C', 'Descriptif C', 'Détails C', 30, 'basse');

-- Insérer une notification pour l'utilisateur avec ID 1
INSERT INTO Notif (demande_id, message, lu)
VALUES (1, 'La demande', FALSE),
(2, 'Notification 2 pour Utilisateur 2', TRUE),
(3, 'Notification 3 pour Utilisateur 3', FALSE);

-- Insérer un devis pour la demande avec ID 1
INSERT INTO Devis (demande_id, fournisseur_id,fichier_pdf, etat, nom_valideur)
VALUES 
(1, 1,'back/src/pdf/Devis/devis_demandeur_date_domaine.pdf', 'à_valider', 'Validateur A'),
(2, 2,'back/src/pdf/Devis/devis_demandeur_date_domaine.pdf', 'validé', 'Validateur B'),
(3, 3,'back/src/pdf/Devis/devis_demandeur_date_domaine.pdf', 'refusé', 'Validateur C');

-- Insérer un bon de commande pour le devis avec ID 1 et l'utilisateur avec ID 1
INSERT INTO BonCommande (devis_id, etat, fichier_pdf,nom_editeur)
VALUES 
(1,'en_édition', 'back/src/pdf/BonCommande/bon_commande_demandeur_date.pdf', 'Editeur A'),
(2, 'à_valider', 'back/src/pdf/BonCommande/bon_commande_demandeur_date.pdf', 'Editeur B'),
(3, 'validé', 'back/src/pdf/BonCommande/bon_commande_demandeur_date.pdf', 'Editeur C');

-- Insérer une facture pour le bon de commande avec ID 1
INSERT INTO Facture (bon_commande_id, fichier_pdf, etat, date_livraison, lieu_livraison, nom_signataire, nom_transitaire)
VALUES 
(1, 'back/src/pdf/Facture/facture_demandeur_date.pdf', 'à_valider', '2023-07-01 00:00:00', 'Lieu A', 'Signataire A', 'Transitaire A'),
(2, 'back/src/pdf/Facture/facture_demandeur_date.pdf', 'validée', '2023-08-01 00:00:00', 'Lieu B', 'Signataire B', 'Transitaire B'),
(3, 'back/src/pdf/Facture/facture_demandeur_date.pdf', 'refusée', '2023-09-01 00:00:00', 'Lieu C', 'Signataire C', 'Transitaire C');

-- Insérer des relances
INSERT INTO Relance (bon_commande_id, date_relance, message, reponse) VALUES
(1, '2023-07-10 00:00:00', 'Message de relance 1', FALSE),
(2, '2023-08-10 00:00:00', 'Message de relance 2', TRUE),
(3, '2023-09-10 00:00:00', 'Message de relance 3', FALSE);

-- Insérer des stocks
INSERT INTO Stock (bon_commande_id, description, quantite) VALUES
(1, 'Description du stock 1', 100),
(2, 'Description du stock 2', 200),
(3, 'Description du stock 3', 300);

-- Assigner des utilisateurs à des groupes
INSERT INTO UtilisateurGroupe (utilisateur_id, groupe_id) VALUES
(1, 1),
(1, 2),
(2, 3),
(3, 4),
(4, 5),
(5, 6),
(6, 7),
(8, 1),
(8, 2),
(8, 3),
(8, 4),
(8, 5),
(8, 6),
(8, 7);




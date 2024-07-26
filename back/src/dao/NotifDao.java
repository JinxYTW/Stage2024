package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import database.SomethingDatabase;
import models.Notif;

public class NotifDao {
    public NotifDao() {
    }

    public int countNotifForUser(int userId) {
        int nbNotif = 0;
        try {
            SomethingDatabase database = new SomethingDatabase();
            
            // Obtenir les groupes de l'utilisateur
            String getUserGroupsQuery = "SELECT g.nom FROM UtilisateurGroupe ug " +
                                        "JOIN Groupe g ON ug.groupe_id = g.id " +
                                        "WHERE ug.utilisateur_id = ?";
            PreparedStatement groupsStatement = database.prepareStatement(getUserGroupsQuery);
            groupsStatement.setInt(1, userId);
            ResultSet groupsResultSet = groupsStatement.executeQuery();
            Set<String> userGroups = new HashSet<>();
            while (groupsResultSet.next()) {
                userGroups.add(groupsResultSet.getString("nom"));
            }
            
            // Construire la requête de comptage des notifications
            // Inclure tous les états spécifiés
            String query = "SELECT COUNT(*) FROM Notif n " +
                           "JOIN Demande d ON n.demande_id = d.id " +
                           "WHERE d.utilisateur_id = ? " + // Notification pour le créateur de la demande
                           
                           // Notifications pour différents états de demande et groupes associés
                           "OR (d.etat = 'demande_envoyee' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                           "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                           "AND g.nom = 'treatDevis')) " +
                           
                           "OR (d.etat = 'demande_en_cours_de_traitement' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                           "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                           "AND g.nom = 'treatDevis')) " +
                           
                           "OR (d.etat = 'devis_a_valider' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                           "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                           "AND g.nom = 'validateDevis')) " +
                           
                           "OR (d.etat = 'devis_en_cours_de_validation' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                           "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                           "AND g.nom = 'validateDevis')) " +
                           
                           "OR (d.etat = 'bc_a_editer' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                           "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                           "AND g.nom = 'treatBc')) " +
                           
                           "OR (d.etat = 'bc_en_cours_dedition' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                           "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                           "AND g.nom = 'treatBc')) " +
                           
                           "OR (d.etat = 'bc_a_valider' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                           "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                           "AND g.nom = 'validateBc')) " +
                           
                           "OR (d.etat = 'bc_en_cours_de_validation' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                           "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                           "AND g.nom = 'validateBc')) " +
                           
                           "OR (d.etat = 'bc_valide_envoi_fournisseur' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                           "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                           "AND g.nom = 'notifBcSend')) " +
                           
                           "OR (d.etat = 'bc_envoye_attente_livraison' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                           "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                           "AND g.nom = 'notifBcSend')) " +

                            "OR (d.etat = 'facture_a_valider' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                            "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                            "AND g.nom = 'validateFacture')) " +
                           
                           "OR (d.etat = 'commande_annulee' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                           "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                           "AND g.nom = 'notifBcSend')) " +
                           
                           "OR (d.etat = 'commande_livree_finalisee' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
                           "JOIN Groupe g ON ug.groupe_id = g.id WHERE ug.utilisateur_id = ? " +
                           "AND g.nom = 'inventory'))";
                           
            PreparedStatement statement = database.prepareStatement(query);
            for (int i = 1; i <= 13; i++) {
                statement.setInt(i, userId); // Remplacer les valeurs pour chaque état et groupe
            }
            
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                nbNotif = resultSet.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nbNotif;
    }
    
}
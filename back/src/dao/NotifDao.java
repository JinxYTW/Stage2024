package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import database.SomethingDatabase;
import models.Notif;

public class NotifDao {
    public NotifDao() {
    }

    public static boolean isOneNotifOnState(String demandeId, String type) {
        try {
            int demandeIdInt = Integer.parseInt(demandeId);
            
            SomethingDatabase database = new SomethingDatabase();
            String query = "SELECT COUNT(*) FROM Notif WHERE demande_id = ? AND type = ?";
            PreparedStatement statement = database.prepareStatement(query);
            statement.setInt(1, demandeIdInt);
            statement.setString(2, type);
            ResultSet resultSet = statement.executeQuery();

            
            if (resultSet.next()) {
                
                return resultSet.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateNotificationType(int demandeId, String newType) {
        try {
            SomethingDatabase database = new SomethingDatabase();
            String query = "UPDATE Notif SET type = ? WHERE demande_id = ?";
            PreparedStatement statement = database.prepareStatement(query);
            statement.setString(1, newType);
            statement.setInt(2, demandeId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean updateNotificationTypeRead(int notifId, boolean newLuStatus, String newType) {
        try {
            SomethingDatabase database = new SomethingDatabase();
            String query = "UPDATE Notif SET lu = ?, type = ? WHERE id = ?";
            PreparedStatement statement = database.prepareStatement(query);
            statement.setBoolean(1, newLuStatus);
            statement.setString(2, newType);
            statement.setInt(3, notifId);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Notif getNotificationById(int notifId) {
        Notif notif = null;
        try {
            SomethingDatabase database = new SomethingDatabase();
            String query = "SELECT * FROM Notif WHERE id = ?";
            PreparedStatement statement = database.prepareStatement(query);
            statement.setInt(1, notifId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                notif = new Notif(
                    resultSet.getInt("id"),
                    resultSet.getInt("demande_id"),
                    resultSet.getString("message"),
                    Notif.Type.valueOf(resultSet.getString("type")),
                    resultSet.getBoolean("lu"),
                    resultSet.getTimestamp("date_notification")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notif;
    }

    public static void markAsRead(int notifId) {
        try {
            SomethingDatabase database = new SomethingDatabase();
            String query = "UPDATE Notif SET lu = TRUE WHERE id = ?";
            PreparedStatement statement = database.prepareStatement(query);
            statement.setInt(1, notifId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int countNotifForUser(int userId) {
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
            "WHERE n.lu = FALSE " + // Condition pour le champ 'lu'
            
            // Notifications pour le créateur de la demande
            "AND (d.utilisateur_id = ? " +
            
            // Notifications pour différents états de demande et groupes associés
            "OR (d.etat = 'envoyée' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
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

            "OR (d.etat = 'envoi_fournisseur_en_cours' AND EXISTS (SELECT 1 FROM UtilisateurGroupe ug " +
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
            "AND g.nom = 'inventory')))";
                           
            PreparedStatement statement = database.prepareStatement(query);
            for (int i = 1; i <= 15; i++) {
                statement.setInt(i, userId); 
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

    
    public static Notif getOldestUrgentNotification(int userId) {
        Notif notif = null;
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
    
            // Construire la condition pour les groupes
            StringBuilder groupConditions = new StringBuilder();
            for (String group : userGroups) {
                if (groupConditions.length() > 0) {
                    groupConditions.append(" OR ");
                }
                groupConditions.append("(g.nom = '").append(group).append("')");
            }
    
            // Construire la requête SQL avec les conditions pour les notifications
            String query = "SELECT n.*, d.urgence " +
                       "FROM Notif n " +
                       "JOIN Demande d ON n.demande_id = d.id " +
                       "JOIN UtilisateurGroupe ug ON d.utilisateur_id = ug.utilisateur_id " +
                       "JOIN Groupe g ON ug.groupe_id = g.id " +
                       "WHERE n.lu = FALSE " + // Ajouter condition pour les notifications non lues
                       "AND (d.utilisateur_id = ? " + // Notifications pour le créateur de la demande
                       "OR (d.etat IN ('envoyée', 'demande_en_cours_de_traitement', 'devis_a_valider', 'devis_en_cours_de_validation', 'bc_a_editer', 'bc_en_cours_dedition', 'bc_a_valider', 'bc_en_cours_de_validation', 'bc_valide_envoi_fournisseur','envoi_fournisseur_en_cours', 'bc_envoye_attente_livraison', 'commande_annulee', 'commande_livree_finalisee') AND (" +
                       groupConditions.toString() + "))) " + // Notifications pour les groupes autorisés
                       "ORDER BY " +
                       "    CASE d.urgence " +
                       "        WHEN 'haute' THEN 1 " +
                       "        WHEN 'moyenne' THEN 2 " +
                       "        WHEN 'basse' THEN 3 " +
                       "    END, " +
                       "    n.date_notification ASC " +
                       "LIMIT 1";
            PreparedStatement statement = database.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                notif = new Notif(
                    resultSet.getInt("id"),
                    resultSet.getInt("demande_id"),
                    resultSet.getString("message"),
                    Notif.Type.valueOf(resultSet.getString("type")),
                    resultSet.getBoolean("lu"),
                    resultSet.getTimestamp("date_notification")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notif;
    }
    
    //A optimiser
    public static List<Notif> getNotificationsForUser(int userId) {
        List<Notif> notifications = new ArrayList<>();
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
            
            // Construire la condition pour les groupes
            StringBuilder groupConditions = new StringBuilder();
            for (String group : userGroups) {
                if (groupConditions.length() > 0) {
                    groupConditions.append(" OR ");
                }
                groupConditions.append("(g.nom = '").append(group).append("')");
            }
            
            // Construire la requête SQL avec les conditions pour les notifications
            String query = "SELECT n.*, d.urgence " +
                       "FROM Notif n " +
                       "JOIN Demande d ON n.demande_id = d.id " +
                       "JOIN UtilisateurGroupe ug ON d.utilisateur_id = ug.utilisateur_id " +
                       "JOIN Groupe g ON ug.groupe_id = g.id " +
                       "WHERE n.lu = FALSE " + // Ajouter condition pour les notifications non lues
                       "AND (d.utilisateur_id = ? " + // Notifications pour le créateur de la demande
                       "OR (d.etat IN ('envoyée', 'demande_en_cours_de_traitement', 'devis_a_valider', 'devis_en_cours_de_validation', 'bc_a_editer', 'bc_en_cours_dedition', 'bc_a_valider', 'bc_en_cours_de_validation', 'bc_valide_envoi_fournisseur','envoi_fournisseur_en_cours', 'bc_envoye_attente_livraison', 'facture_a_valider', 'commande_annulee', 'commande_livree_finalisee') AND (" + 
                       groupConditions.toString() + ")))"; // Notifications pour les groupes autorisés
            
            PreparedStatement statement = database.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Notif notif = new Notif(
                    resultSet.getInt("id"),
                    resultSet.getInt("demande_id"),
                    resultSet.getString("message"),
                    Notif.Type.valueOf(resultSet.getString("type")),
                    resultSet.getBoolean("lu"),
                    resultSet.getTimestamp("date_notification")
                );
                
                notifications.add(notif);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifications;
    }

    
    
    
    
}
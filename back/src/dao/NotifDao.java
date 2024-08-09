package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;

import java.util.Set;


import java.sql.Statement;


import database.SomethingDatabase;
import models.Notif;

public class NotifDao {
    public NotifDao() {
    }

    // Test pour implémentation d'un nouveau système de notification****************************************************************************************************************

    // Créer une notification pour un demandeur et pour les utilisateurs du groupe 'TreatDevis' au sein de la table 'UtilisateurNotification'
    public static int createNotificationForDemandeurAndForTreatDevisGroup(int utilisateurId, int demandeId, String message, String type, java.sql.Timestamp date) {
        int notifId = -1;
        try {
            SomethingDatabase database = new SomethingDatabase();
            
            // Créer la notification
            String insertNotifQuery = "INSERT INTO Notif (demande_id, message, type, date_notification) VALUES (?, ?, ?, ?)";
            try (PreparedStatement notifStatement = database.prepareStatement(insertNotifQuery, Statement.RETURN_GENERATED_KEYS)) {
                notifStatement.setInt(1, demandeId);
                notifStatement.setString(2, message);
                notifStatement.setString(3, type);
                notifStatement.setTimestamp(4, date);
                notifStatement.executeUpdate();
                
                try (ResultSet resultSet = notifStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        notifId = resultSet.getInt(1);
                    }
                }
            }
            
            if (notifId != -1) {
                // Supprimer les notifications existantes pour le demandeur
                String deleteUserNotifQuery1 = "DELETE FROM UtilisateurNotification WHERE utilisateur_id = ? AND notification_id = ?";
                try (PreparedStatement deleteStatement1 = database.prepareStatement(deleteUserNotifQuery1)) {
                    deleteStatement1.setInt(1, utilisateurId);
                    deleteStatement1.setInt(2, notifId);
                    deleteStatement1.executeUpdate();
                }
                
                // Ajouter l'entrée pour le demandeur
                String insertUserNotifQuery1 = "INSERT INTO UtilisateurNotification (utilisateur_id, notification_id) VALUES (?, ?)";
                try (PreparedStatement userNotifStatement1 = database.prepareStatement(insertUserNotifQuery1)) {
                    userNotifStatement1.setInt(1, utilisateurId);
                    userNotifStatement1.setInt(2, notifId);
                    userNotifStatement1.executeUpdate();
                }
                
                // Supprimer les notifications existantes pour le groupe 'TreatDevis'
                String deleteUserNotifQuery2 = "DELETE FROM UtilisateurNotification WHERE utilisateur_id IN " +
                                               "(SELECT utilisateur_id FROM UtilisateurGroupe WHERE groupe_id = 2) " +
                                               "AND notification_id = ?";
                try (PreparedStatement deleteStatement2 = database.prepareStatement(deleteUserNotifQuery2)) {
                    deleteStatement2.setInt(1, notifId);
                    deleteStatement2.executeUpdate();
                }
                
                // Ajouter les utilisateurs du groupe 'TreatDevis'
                String insertUserNotifQuery2 = "INSERT INTO UtilisateurNotification (utilisateur_id, notification_id) " +
                                               "SELECT u.id, ? FROM Utilisateur u " +
                                               "JOIN UtilisateurGroupe ug ON u.id = ug.utilisateur_id " +
                                               "WHERE ug.groupe_id = 2";
                try (PreparedStatement userNotifStatement2 = database.prepareStatement(insertUserNotifQuery2)) {
                    userNotifStatement2.setInt(1, notifId);
                    userNotifStatement2.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifId;
    }
    

    //Permet de compter le nombre de notifications non lues pour un utilisateur spécifique grâce à la table 'UtilisateurNotification'
    public static int countUnreadNotificationsForUser(int utilisateurId) {
        int nbNotif = 0;
        try {
            SomethingDatabase database = new SomethingDatabase();
            
            // Requête SQL pour compter les notifications non lues pour un utilisateur spécifique
            String query = "SELECT COUNT(*) FROM UtilisateurNotification WHERE utilisateur_id = ? AND lu = FALSE";
            
            try (PreparedStatement statement = database.prepareStatement(query)) {
                statement.setInt(1, utilisateurId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        nbNotif = resultSet.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nbNotif;
    }
    
    //Permet de marquer une notification comme lue pour un utilisateur spécifique grâce à la table 'UtilisateurNotification'
    public static void markAsReadForUser(int utilisateurId, int notifId) {
        
        try {
            SomethingDatabase database = new SomethingDatabase();
            String query = "UPDATE UtilisateurNotification SET lu = TRUE WHERE utilisateur_id = ? AND notification_id = ?";
            PreparedStatement statement = database.prepareStatement(query);
            System.out.println("utilisateurId : " + utilisateurId);
            statement.setInt(1, utilisateurId);
            System.out.println("notifId : " + notifId);
            statement.setInt(2, notifId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //L'idée sera de créer les notifications pour les différents groupes de traitement de demande au sein de la table 'UtilisateurNotification' au fur et à mesure de l'avancement de la demande

    //Permet d'ajouter à la table 'UtilisateurNotification' une notification les utilisateurs du groupe 'TreatDevis' pour une demande spécifique ainsi que de réinitialiser le champ 'lu' pour le demandeur
    public static void addNotificationForTreatDevis(int demandeId, int demandeurId, int notifId) {
        try {
            SomethingDatabase database = new SomethingDatabase();
            
            // Supprimer les notifications existantes pour les groupes 'validateDevis', 'treatDevis'
            String deletePreviousNotifsQuery = "DELETE FROM UtilisateurNotification " +
                                               "WHERE utilisateur_id IN (SELECT utilisateur_id " +
                                               "FROM UtilisateurGroupe ug JOIN Groupe g ON ug.groupe_id = g.id " +
                                               "WHERE g.nom = 'validateDevis') " +
                                               "AND notification_id IN (SELECT id FROM Notif WHERE demande_id = ?)"+
                                               "AND utilisateur_id != ?";
            PreparedStatement deletePreviousNotifsStatement = database.prepareStatement(deletePreviousNotifsQuery);
            deletePreviousNotifsStatement.setInt(1, demandeId);
            deletePreviousNotifsStatement.setInt(2, demandeurId);
            deletePreviousNotifsStatement.executeUpdate();
    
            

            
            System.out.println("Le champ lu a été réinitialisé pour le demandeur");
            // Réinitialiser le champ 'lu' pour le demandeur
            String resetUserNotifQuery = "UPDATE UtilisateurNotification " +
                                     "SET lu = FALSE " +
                                     "WHERE notification_id IN (SELECT id FROM Notif WHERE demande_id = ?) " +
                                     "AND utilisateur_id = ?";
            PreparedStatement resetUserNotifStatement = database.prepareStatement(resetUserNotifQuery);
            resetUserNotifStatement.setInt(1, demandeId);
            resetUserNotifStatement.setInt(2, demandeurId);
            resetUserNotifStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    //Permet d'ajouter à la table 'UtilisateurNotification' une notification les utilisateurs du groupe 'ValidateDevis' pour une demande spécifique ainsi que de réinitialiser le champ 'lu' pour le demandeur
    public static void addNotificationForValidateDevis(int demandeId, int demandeurId, int notifId) {
        try {
            SomethingDatabase database = new SomethingDatabase();
            
            // Supprimer les notifications existantes pour les groupes 'treatDevis', 'validateDevis'
            String deletePreviousNotifsQuery = "DELETE FROM UtilisateurNotification " +
                                               "WHERE utilisateur_id IN (SELECT utilisateur_id " +
                                               "FROM UtilisateurGroupe ug JOIN Groupe g ON ug.groupe_id = g.id " +
                                               "WHERE g.nom IN ('treatDevis', 'validateDevis')) " +
                                               "AND notification_id IN (SELECT id FROM Notif WHERE demande_id = ?)"+
                                               "AND utilisateur_id != ?";
            PreparedStatement deletePreviousNotifsStatement = database.prepareStatement(deletePreviousNotifsQuery);
            deletePreviousNotifsStatement.setInt(1, demandeId);
            deletePreviousNotifsStatement.setInt(2, demandeurId);
            deletePreviousNotifsStatement.executeUpdate();
    
            // Ajouter des entrées pour le groupe 'validateDevis'
            String insertUserNotifQuery = "INSERT INTO UtilisateurNotification (utilisateur_id, notification_id, lu) " +
                                           "SELECT u.id, ?, FALSE FROM Utilisateur u " +
                                           "JOIN UtilisateurGroupe ug ON u.id = ug.utilisateur_id " +
                                           "JOIN Groupe g ON ug.groupe_id = g.id " +
                                           "WHERE g.nom = 'validateDevis'";
            PreparedStatement userNotifStatement = database.prepareStatement(insertUserNotifQuery);
            userNotifStatement.setInt(1, notifId);
            userNotifStatement.executeUpdate();
    
            // Réinitialiser le champ 'lu' pour le demandeur
            String resetUserNotifQuery = "UPDATE UtilisateurNotification " +
                                     "SET lu = FALSE " +
                                     "WHERE notification_id IN (SELECT id FROM Notif WHERE demande_id = ?) " +
                                     "AND utilisateur_id = ?";
            PreparedStatement resetUserNotifStatement = database.prepareStatement(resetUserNotifQuery);
            resetUserNotifStatement.setInt(1, demandeId);
            resetUserNotifStatement.setInt(2, demandeurId);
            resetUserNotifStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    //Permet d'ajouter à la table 'UtilisateurNotification' une notification les utilisateurs du groupe 'TreatBc' pour une demande spécifique ainsi que de réinitialiser le champ 'lu' pour le demandeur
    public static void addNotificationForTreatBc(int demandeId, int demandeurId, int notifId) {
        try {
            SomethingDatabase database = new SomethingDatabase();
            
            // Supprimer les notifications existantes pour les groupes 'validateDevis', 'treatBc'
            String deletePreviousNotifsQuery = "DELETE FROM UtilisateurNotification " +
                                               "WHERE utilisateur_id IN (SELECT utilisateur_id " +
                                               "FROM UtilisateurGroupe ug JOIN Groupe g ON ug.groupe_id = g.id " +
                                               "WHERE g.nom IN ('validateDevis', 'treatBc')) " +
                                               "AND notification_id IN (SELECT id FROM Notif WHERE demande_id = ?)"+
                                               "AND utilisateur_id != ?";
            PreparedStatement deletePreviousNotifsStatement = database.prepareStatement(deletePreviousNotifsQuery);
            deletePreviousNotifsStatement.setInt(1, demandeId);
            deletePreviousNotifsStatement.setInt(2, demandeurId);
            deletePreviousNotifsStatement.executeUpdate();
    
            // Ajouter des entrées pour le groupe 'treatBc'
            String insertUserNotifQuery = "INSERT INTO UtilisateurNotification (utilisateur_id, notification_id, lu) " +
                                           "SELECT u.id, ?, FALSE FROM Utilisateur u " +
                                           "JOIN UtilisateurGroupe ug ON u.id = ug.utilisateur_id " +
                                           "JOIN Groupe g ON ug.groupe_id = g.id " +
                                           "WHERE g.nom = 'treatBc'";
            PreparedStatement userNotifStatement = database.prepareStatement(insertUserNotifQuery);
            userNotifStatement.setInt(1, notifId);
            userNotifStatement.executeUpdate();
    
            // Réinitialiser le champ 'lu' pour le demandeur
            String resetUserNotifQuery = "UPDATE UtilisateurNotification " +
                                     "SET lu = FALSE " +
                                     "WHERE notification_id IN (SELECT id FROM Notif WHERE demande_id = ?) " +
                                     "AND utilisateur_id = ?";
            PreparedStatement resetUserNotifStatement = database.prepareStatement(resetUserNotifQuery);
            resetUserNotifStatement.setInt(1, demandeId);
            resetUserNotifStatement.setInt(2, demandeurId);
            resetUserNotifStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    //Permet d'ajouter à la table 'UtilisateurNotification' une notification les utilisateurs du groupe 'ValidateBc' pour une demande spécifique ainsi que de réinitialiser le champ 'lu' pour le demandeur
    public static void addNotificationForValidateBc(int demandeId, int demandeurId, int notifId) {
        try {
            SomethingDatabase database = new SomethingDatabase();
            
            // Supprimer les notifications existantes pour les groupes 'treatBc', 'validateBc'
            String deletePreviousNotifsQuery = "DELETE FROM UtilisateurNotification " +
                                               "WHERE utilisateur_id IN (SELECT utilisateur_id " +
                                               "FROM UtilisateurGroupe ug JOIN Groupe g ON ug.groupe_id = g.id " +
                                               "WHERE g.nom IN ('treatBc', 'validateBc')) " +
                                               "AND notification_id IN (SELECT id FROM Notif WHERE demande_id = ?)"+
                                               "AND utilisateur_id != ?";
            PreparedStatement deletePreviousNotifsStatement = database.prepareStatement(deletePreviousNotifsQuery);
            deletePreviousNotifsStatement.setInt(1, demandeId);
            deletePreviousNotifsStatement.setInt(2, demandeurId);
            deletePreviousNotifsStatement.executeUpdate();
    
            // Ajouter des entrées pour le groupe 'validateBc'
            String insertUserNotifQuery = "INSERT INTO UtilisateurNotification (utilisateur_id, notification_id, lu) " +
                                           "SELECT u.id, ?, FALSE FROM Utilisateur u " +
                                           "JOIN UtilisateurGroupe ug ON u.id = ug.utilisateur_id " +
                                           "JOIN Groupe g ON ug.groupe_id = g.id " +
                                           "WHERE g.nom = 'validateBc'";
            PreparedStatement userNotifStatement = database.prepareStatement(insertUserNotifQuery);
            userNotifStatement.setInt(1, notifId);
            userNotifStatement.executeUpdate();
    
            // Réinitialiser le champ 'lu' pour le demandeur
            String resetUserNotifQuery = "UPDATE UtilisateurNotification " +
                                     "SET lu = FALSE " +
                                     "WHERE notification_id IN (SELECT id FROM Notif WHERE demande_id = ?) " +
                                     "AND utilisateur_id = ?";
            PreparedStatement resetUserNotifStatement = database.prepareStatement(resetUserNotifQuery);
            resetUserNotifStatement.setInt(1, demandeId);
            resetUserNotifStatement.setInt(2, demandeurId);
            resetUserNotifStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    //Permet d'ajouter à la table 'UtilisateurNotification' une notification les utilisateurs du groupe 'NotifBcSend' pour une demande spécifique ainsi que de réinitialiser le champ 'lu' pour le demandeur
    public static void addNotificationForNotifBcSend(int demandeId, int demandeurId, int notifId) {
        try {
            SomethingDatabase database = new SomethingDatabase();
            
            // Supprimer les notifications existantes pour les groupes 'treatBc', 'validateBc', 'notifBcSend'
            String deletePreviousNotifsQuery = "DELETE FROM UtilisateurNotification " +
                                               "WHERE utilisateur_id IN (SELECT utilisateur_id " +
                                               "FROM UtilisateurGroupe ug JOIN Groupe g ON ug.groupe_id = g.id " +
                                               "WHERE g.nom IN ('treatBc', 'validateBc', 'notifBcSend')) " +
                                               "AND notification_id IN (SELECT id FROM Notif WHERE demande_id = ?)"+
                                               "AND utilisateur_id != ?";
            PreparedStatement deletePreviousNotifsStatement = database.prepareStatement(deletePreviousNotifsQuery);
            deletePreviousNotifsStatement.setInt(1, demandeId);
            deletePreviousNotifsStatement.setInt(2, demandeurId);
            deletePreviousNotifsStatement.executeUpdate();
    
            // Ajouter des entrées pour le groupe 'notifBcSend'
            String insertUserNotifQuery = "INSERT INTO UtilisateurNotification (utilisateur_id, notification_id, lu) " +
                                           "SELECT u.id, ?, FALSE FROM Utilisateur u " +
                                           "JOIN UtilisateurGroupe ug ON u.id = ug.utilisateur_id " +
                                           "JOIN Groupe g ON ug.groupe_id = g.id " +
                                           "WHERE g.nom = 'notifBcSend'";
            PreparedStatement userNotifStatement = database.prepareStatement(insertUserNotifQuery);
            userNotifStatement.setInt(1, notifId);
            userNotifStatement.executeUpdate();
    
            // Réinitialiser le champ 'lu' pour le demandeur
            String resetUserNotifQuery = "UPDATE UtilisateurNotification " +
                                     "SET lu = FALSE " +
                                     "WHERE notification_id IN (SELECT id FROM Notif WHERE demande_id = ?) " +
                                     "AND utilisateur_id = ?";
            PreparedStatement resetUserNotifStatement = database.prepareStatement(resetUserNotifQuery);
            resetUserNotifStatement.setInt(1, demandeId);
            resetUserNotifStatement.setInt(2, demandeurId);
            resetUserNotifStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Permet d'ajouter à la table 'UtilisateurNotification' une notification les utilisateurs du groupe 'ValidateFacture' pour une demande spécifique ainsi que de réinitialiser le champ 'lu' pour le demandeur
    public static void addNotificationForInventory(int demandeId, int demandeurId, int notifId) {
        try {
            SomethingDatabase database = new SomethingDatabase();
            
            // Supprimer les notifications existantes pour les groupes 'notifBcSend', 'inventory'
            String deletePreviousNotifsQuery = "DELETE FROM UtilisateurNotification " +
                                               "WHERE utilisateur_id IN (SELECT utilisateur_id " +
                                               "FROM UtilisateurGroupe ug JOIN Groupe g ON ug.groupe_id = g.id " +
                                               "WHERE g.nom IN ('notifBcSend', 'inventory')) " +
                                               "AND notification_id IN (SELECT id FROM Notif WHERE demande_id = ?)"+
                                               "AND utilisateur_id != ?";
            PreparedStatement deletePreviousNotifsStatement = database.prepareStatement(deletePreviousNotifsQuery);
            deletePreviousNotifsStatement.setInt(1, demandeId);
            deletePreviousNotifsStatement.setInt(2, demandeurId);
            deletePreviousNotifsStatement.executeUpdate();
    
            // Ajouter des entrées pour le groupe 'inventory'
            String insertUserNotifQuery = "INSERT INTO UtilisateurNotification (utilisateur_id, notification_id, lu) " +
                                           "SELECT u.id, ?, FALSE FROM Utilisateur u " +
                                           "JOIN UtilisateurGroupe ug ON u.id = ug.utilisateur_id " +
                                           "JOIN Groupe g ON ug.groupe_id = g.id " +
                                           "WHERE g.nom = 'inventory'";
            PreparedStatement userNotifStatement = database.prepareStatement(insertUserNotifQuery);
            userNotifStatement.setInt(1, notifId);
            userNotifStatement.executeUpdate();
    
            // Réinitialiser le champ 'lu' pour le demandeur
            String resetUserNotifQuery ="UPDATE UtilisateurNotification " +
                                     "SET lu = FALSE " +
                                     "WHERE notification_id IN (SELECT id FROM Notif WHERE demande_id = ?) " +
                                     "AND utilisateur_id = ?";
            PreparedStatement resetUserNotifStatement = database.prepareStatement(resetUserNotifQuery);
            resetUserNotifStatement.setInt(1, demandeId);
            resetUserNotifStatement.setInt(2, demandeurId);
            resetUserNotifStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public static int getDemandeurId(int notifId) {
        int demandeurId = -1;
        try {
            SomethingDatabase database = new SomethingDatabase();
            String query = "SELECT d.utilisateur_id FROM Notif n " +
                           "JOIN Demande d ON n.demande_id = d.id " +
                           "WHERE n.id = ?";
            PreparedStatement statement = database.prepareStatement(query);
            statement.setInt(1, notifId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                demandeurId = resultSet.getInt("utilisateur_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return demandeurId;
    }
    
    
    public static Notif getNotificationByDemandeId(int demandeIdInt) {
        Notif notif = null;
        try {
            SomethingDatabase database = new SomethingDatabase();
            String query = "SELECT * FROM Notif WHERE demande_id = ?";
            PreparedStatement statement = database.prepareStatement(query);
            statement.setInt(1, demandeIdInt);
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

    public static Notif getMostImportantNotificationForUser(int utilisateurId) {
        Notif notif = null;
        try {
            SomethingDatabase database = new SomethingDatabase();
            
            // Requête SQL pour obtenir la notification la plus importante pour l'utilisateur
            String query = "SELECT n.*, d.urgence " +
                           "FROM Notif n " +
                           "JOIN Demande d ON n.demande_id = d.id " +
                           "JOIN UtilisateurNotification un ON n.id = un.notification_id " +
                           "WHERE un.utilisateur_id = ? AND un.lu = FALSE " + // Notifications non lues pour l'utilisateur
                           "ORDER BY " +
                           "    CASE d.urgence " +
                           "        WHEN 'haute' THEN 1 " +
                           "        WHEN 'moyenne' THEN 2 " +
                           "        WHEN 'basse' THEN 3 " +
                           "    END, " +
                           "    n.date_notification ASC " + // Plus ancienne en premier
                           "LIMIT 1";
            
            PreparedStatement statement = database.prepareStatement(query);
            statement.setInt(1, utilisateurId);
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


    //Méthode pour obtenir les adresses email des utilisateurs pour lesquels une notification a été créée    
    public static List<String> getEmailAddressesForNotification(int notifId) {
        List<String> emailAddresses = new ArrayList<>();
        try {
            SomethingDatabase database = new SomethingDatabase();
            String query = "SELECT u.email " +
                           "FROM Utilisateur u " +
                           "JOIN UtilisateurNotification un ON u.id = un.utilisateur_id " +
                           "WHERE un.notification_id = ?";
            PreparedStatement statement = database.prepareStatement(query);
            statement.setInt(1, notifId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                emailAddresses.add(resultSet.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailAddresses;
    }
       

    

    

    // A optimiser
    public static List<Notif> getNotificationsForUserInde(int utilisateurId) {
        List<Notif> notifications = new ArrayList<>();
        try {
            SomethingDatabase database = new SomethingDatabase();
            String query = "SELECT n.* FROM Notif n " +
                           "JOIN UtilisateurNotification un ON n.id = un.notification_id " +
                           "WHERE un.utilisateur_id = ? " +
                           "ORDER BY n.date_notification DESC";
            PreparedStatement statement = database.prepareStatement(query);
            statement.setInt(1, utilisateurId);
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
    
    

    //*********************************************************************************************** */


   

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
    
   

    

    
    
    
    
}
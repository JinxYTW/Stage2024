package dao;

import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import database.SomethingDatabase;
import models.Facture;

public class FactureDao {

    public static int isOneInvoiceValidate(int demandeId) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "SELECT COUNT(*) FROM Facture WHERE bon_commande_id = (SELECT id FROM BonCommande WHERE devis_id = (SELECT id FROM Devis WHERE demande_id = ? AND etat = 'validé') AND etat = 'validé')AND etat = 'validée'";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, demandeId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getInvoicePathsFromDemandId(int demandeId) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();
            String query = "SELECT fichier_pdf FROM Facture WHERE bon_commande_id = (SELECT id FROM BonCommande WHERE devis_id = (SELECT id FROM Devis WHERE demande_id = ? AND etat = 'validé') AND etat = 'validé')";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, demandeId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("fichier_pdf");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String changeSignataireNameThanksToUserId(int userId,String pdfPath){
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "UPDATE Facture SET nom_signataire = (SELECT nom FROM Utilisateur WHERE id = ?) WHERE fichier_pdf = ? ";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, pdfPath);

            statement.executeUpdate();

            return "Nom du signataire changé avec succès";

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors du changement du nom du signataire";
        }
    }


    public static String validateInvoice(String pdfPath) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();
    
            String query = "UPDATE Facture SET etat = 'validée' WHERE fichier_pdf = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setString(1, pdfPath);
    
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                return "Facture validée avec succès";
            } else {
                return "Aucune facture trouvée pour le chemin spécifié";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la validation de la facture";
        }
    }
    

    public static int getFactureCountFromDemandId(int demandeId) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();
            String query = "SELECT COUNT(*) FROM Facture WHERE bon_commande_id = (SELECT id FROM BonCommande WHERE devis_id = (SELECT id FROM Devis WHERE demande_id = ? AND etat = 'validé') AND etat = 'validé')";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, demandeId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void saveFactureToDatabase(int demandeId, String pdfPath) {
    try {
        SomethingDatabase myDatabase = new SomethingDatabase();

        // Récupérer l'ID du bon de commande
        int bcId = getBcIdFromDemandId(demandeId);

        // Calculer la date de livraison (aujourd'hui + 1 mois)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deliveryDate = now.plusMonths(1);
        Timestamp dateFacture = Timestamp.valueOf(now);
        Timestamp dateLivraison = Timestamp.valueOf(deliveryDate);

        String query = "INSERT INTO Facture (bon_commande_id, fichier_pdf, etat, date_facture, date_livraison) VALUES (?, ?, 'à_valider', ?, ?)";
        PreparedStatement statement = myDatabase.prepareStatement(query);
        statement.setInt(1, bcId);
        statement.setString(2, pdfPath);
        statement.setTimestamp(3, dateFacture);
        statement.setTimestamp(4, dateLivraison);

        statement.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public static int getBcIdFromDemandId(int demandeId) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();
            String query = "SELECT id FROM BonCommande WHERE devis_id = (SELECT id FROM Devis WHERE demande_id = ? AND etat = 'validé') AND etat = 'validé'";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, demandeId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    
    public Facture findValideInvoiceFromDemandId(int userId){
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "SELECT * FROM Facture WHERE bon_commande_id = (SELECT id FROM BonCommande WHERE devis_id = (SELECT id FROM Devis WHERE demande_id = ? AND etat = 'validé') AND etat = 'validé') AND etat = 'validée'";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Facture(
                    resultSet.getInt("id"),
                    resultSet.getInt("bon_commande_id"),
                    resultSet.getString("fichier_pdf"),
                    Facture.Etat.valueOf(resultSet.getString("etat")),
                    resultSet.getTimestamp("date_facture"),
                    resultSet.getTimestamp("date_livraison"),
                    resultSet.getString("lieu_livraison"),
                    resultSet.getString("nom_signataire"),
                    resultSet.getString("nom_transitaire")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Facture findById(int id) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "SELECT * FROM Facture WHERE id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Facture(
                    resultSet.getInt("id"),
                    resultSet.getInt("bon_commande_id"),
                    resultSet.getString("fichier_pdf"),
                    Facture.Etat.valueOf(resultSet.getString("etat")),
                    resultSet.getTimestamp("date_facture"),
                    resultSet.getTimestamp("date_livraison"),
                    resultSet.getString("lieu_livraison"),
                    resultSet.getString("nom_signataire"),
                    resultSet.getString("nom_transitaire")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
}

package dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;


import database.SomethingDatabase;
import models.BonCommande;

public class BonCommandeDao {

    public static boolean isOneBcValidate(int demandeId) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "SELECT COUNT(*) FROM BonCommande WHERE devis_id = (SELECT id FROM Devis WHERE demande_id = ? AND etat = 'validé') AND etat = 'validé'";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, demandeId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String validateBc(String pdfPath) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "UPDATE BonCommande SET etat = 'validé' WHERE fichier_pdf = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setString(1, pdfPath);

            statement.executeUpdate();

            return "Bon de commande validé avec succès";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erreur lors de la validation du bon de commande";
    }


    public static String getBcPdfPathFromDemandId(int demandeId) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();
            String query = "SELECT fichier_pdf FROM BonCommande WHERE devis_id = (SELECT id FROM Devis WHERE demande_id = ? AND etat = 'validé') AND etat = 'à_valider'";
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

    public static int getBcCountFromDemandId(int demandeId) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();
            String query = "SELECT COUNT(*) FROM BonCommande WHERE devis_id = (SELECT id FROM Devis WHERE demande_id = ? AND etat = 'validé')";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, demandeId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Méthode pour obtenir le devis_id à partir du demandeId
    private static int getDevisIdFromDemandeId(int demandeId) {
        int devisId = -1; // Valeur par défaut en cas d'erreur
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();
            String query = "SELECT id FROM Devis WHERE demande_id = ? AND etat = 'validé'";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, demandeId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                devisId = resultSet.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return devisId;
    }

    // Méthode pour sauvegarder le bon de commande dans la base de données
    public static void saveBcToDatabase(int demandeId, String pdfPath) {
        try {
            int devisId = getDevisIdFromDemandeId(demandeId);

            if (devisId == -1) {
                System.err.println("Aucun devis trouvé pour la demande ID " + demandeId);
                return;
            }

            SomethingDatabase myDatabase = new SomethingDatabase();
            String query = "INSERT INTO BonCommande (devis_id, etat, fichier_pdf, date_creation) VALUES (?, ?, ?, NOW())";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, devisId);
            statement.setString(2, "à_valider"); // État par défaut
            statement.setString(3, pdfPath);
            
            statement.executeUpdate();
            System.out.println("Bon de commande enregistré avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public BonCommande findById(int id) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "SELECT * FROM BonCommande WHERE id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new BonCommande(
                    resultSet.getInt("id"),
                    resultSet.getInt("devis_id"),
                    BonCommande.Etat.valueOf(resultSet.getString("etat")),
                    resultSet.getString("fichier_pdf"),
                    resultSet.getTimestamp("date_creation"),
                    resultSet.getString("nom_editeur")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void savePdfPath(int id, String pdfPath) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "UPDATE BonCommande SET fichier_pdf = ? WHERE id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setString(1, pdfPath);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
}

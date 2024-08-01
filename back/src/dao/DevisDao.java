package dao;

import java.io.FileOutputStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.borders.SolidBorder;

import database.SomethingDatabase;
import models.Devis;

public class DevisDao {

    public boolean isOneDevisValidate (int demandeId){
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "SELECT COUNT(*) FROM Devis WHERE demande_id = ? AND etat = 'validé'";
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

    public String changeValideurNameThanksToUserId(int userId,String pdfPath){
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "UPDATE Devis SET nom_valideur = (SELECT nom FROM Utilisateur WHERE id = ?) WHERE fichier_pdf = ? ";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, pdfPath);

            statement.executeUpdate();

            return "Nom du valideur changé avec succès";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erreur lors du changement du nom du valideur";
    }

    public String validateDevis(String pdfPath){
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "UPDATE Devis SET etat = 'validé' WHERE fichier_pdf = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setString(1, pdfPath);

            statement.executeUpdate();

            

            return "Devis validé avec succès";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erreur lors de la validation du devis";
    }

    public  List<String> getDevisPdfPath(int demandeId){
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "SELECT fichier_pdf FROM Devis WHERE demande_id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, demandeId);

            ResultSet resultSet = statement.executeQuery();

            List<String> pdfPaths = new ArrayList<>();
            while (resultSet.next()) {
                pdfPaths.add(resultSet.getString("fichier_pdf"));
            }

            return pdfPaths;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getDevisCount(int demandeId){
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "SELECT COUNT(*) FROM Devis WHERE demande_id = ?";
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

    public static void saveDevisToDatabase(int demandeId, String fileName) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "INSERT INTO Devis (demande_id, fichier_pdf) VALUES (?, ?)";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, demandeId);
            statement.setString(2, fileName);

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public Devis FindValideDevisFromDemandId(int demandId){
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "SELECT * FROM Devis WHERE demande_id = ? AND etat = 'validé'";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, demandId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Devis(
                    resultSet.getInt("id"),
                    resultSet.getInt("demande_id"),
                    resultSet.getInt("fournisseur_id"),
                    
                    resultSet.getString("fichier_pdf"),
                    Devis.Etat.valueOf(resultSet.getString("etat")),
                    resultSet.getTimestamp("date_devis"),
                    resultSet.getString("nom_valideur")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // Méthode pour trouver un devis par ID
    public Devis findById(int id) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "SELECT * FROM Devis WHERE id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Devis(
                    resultSet.getInt("id"),
                    resultSet.getInt("demande_id"),
                    resultSet.getInt("fournisseur_id"),
                    
                    resultSet.getString("fichier_pdf"),
                    Devis.Etat.valueOf(resultSet.getString("etat")),
                    resultSet.getTimestamp("date_devis"),
                    resultSet.getString("nom_valideur")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Autres méthodes pour manipuler les devis
}

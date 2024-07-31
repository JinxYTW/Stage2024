package dao;

import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.borders.SolidBorder;

import database.SomethingDatabase;
import models.Facture;

public class FactureDao {

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

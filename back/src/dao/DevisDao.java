package dao;

import java.io.FileOutputStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


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

    // Méthode pour sauvegarder le chemin du fichier PDF
    public void savePdfPath(int id, String pdfPath) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "UPDATE Devis SET fichier_pdf = ? WHERE id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setString(1, pdfPath);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour générer un PDF pour un devis
    public String generatePdf(Devis devis, String demandeurName) {
        // Récupérer la date du devis et la formater
        LocalDate devisDate = devis.date_devis().toLocalDateTime().toLocalDate();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = devisDate.format(dateFormatter);
        
        // Remplacer les espaces et caractères non valides par des underscores
        String sanitizedDemandeurName = demandeurName.replaceAll("[^a-zA-Z0-9]", "_");
        String pdfFileName = "devis_" + sanitizedDemandeurName + "_" + formattedDate + ".pdf";
        String pdfPath = "back/src/pdf/Devis/" + pdfFileName;

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfPath));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
    
            // Ajouter le titre
            Paragraph title = new Paragraph("Devis de " + demandeurName + " - " + formattedDate)
                    .setFontSize(20)
                    .setBold()
                    .setMarginBottom(20);
            document.add(title);
    
            // Ajouter une table
            float[] columnWidths = {1, 3}; // Largeurs des colonnes
            Table table = new Table(columnWidths);
            
            table.addCell(createCell("ID:"));
            table.addCell(createCell(devis.id() + ""));
            table.addCell(createCell("ID Demande:"));
            table.addCell(createCell(devis.demande_id() + ""));
            table.addCell(createCell("ID Fournisseur:"));
            table.addCell(createCell(devis.fournisseur_id() + ""));
            table.addCell(createCell("Etat:"));
            table.addCell(createCell(devis.etat().name()));
            table.addCell(createCell("Date de devis:"));
            table.addCell(createCell(devis.date_devis().toString()));
            table.addCell(createCell("Nom valideur:"));
            table.addCell(createCell(devis.nom_valideur()));
    
            document.add(table);
    
            document.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return pdfPath;
    }
    
    private com.itextpdf.layout.element.Cell createCell(String content) {
        com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell().add(new Paragraph(content));
        cell.setPadding(5);
        cell.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        return cell;
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

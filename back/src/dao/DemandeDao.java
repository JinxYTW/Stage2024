package dao;

import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import database.SomethingDatabase;
import models.Demande;

public class DemandeDao {

    public Demande findById(int id) {
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();

            String query = "SELECT * FROM Demande WHERE id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Demande(
                    resultSet.getInt("id"),
                    resultSet.getInt("utilisateur_id"),
                    resultSet.getInt("projet_id"),
                    resultSet.getString("referant"),
                    resultSet.getString("domaine"),
                    resultSet.getString("typeof"),
                    resultSet.getString("marque"),
                    resultSet.getString("reference"),
                    resultSet.getString("pour"),
                    resultSet.getString("ou"),
                    resultSet.getString("marche"),
                    resultSet.getString("justification"),
                    resultSet.getString("descriptif"),
                    resultSet.getInt("quantite"),
                    Demande.Urgence.valueOf(resultSet.getString("urgence")),
                    Demande.Etat.valueOf(resultSet.getString("etat")),
                    resultSet.getTimestamp("date_demande"),
                    resultSet.getString("pdfPath")
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

            String query = "UPDATE Demande SET pdfPath = ? WHERE id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setString(1, pdfPath);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generatePdf(Demande demande, String demandeurName) {
        String pdfPath = "back/src/pdf/Devis/demande_" + demande.id() + ".pdf";
        try {
            System.out.println("Initializing PDF writer...");
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfPath));
            System.out.println("Initializing PDF document...");
            PdfDocument pdfDoc = new PdfDocument(writer);
            System.out.println("Initializing document...");
            Document document = new Document(pdfDoc);
            System.out.println("Adding title to PDF...");
    
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String formattedDate = currentDate.format(formatter);

            System.out.println("Demandeur name: " + demandeurName);
            System.out.println("Formatted date: " + formattedDate);
    
            String title = "Demande par " + demandeurName + " le " + formattedDate;
    
            System.out.println("Adding title to PDF: " + title);
            document.add(new Paragraph(title));
            
            System.out.println("Adding fields to PDF...");
            document.add(new Paragraph("ID: " + demande.id()));
            document.add(new Paragraph("Référant: " + demande.referant()));
            document.add(new Paragraph("Domaine: " + demande.domaine()));
            document.add(new Paragraph("Type: " + demande.typeof()));
            document.add(new Paragraph("Marque: " + demande.marque()));
            document.add(new Paragraph("Référence: " + demande.reference()));
            document.add(new Paragraph("Pour: " + demande.pour()));
            document.add(new Paragraph("Où: " + demande.ou()));
            document.add(new Paragraph("Marché: " + demande.marche()));
            document.add(new Paragraph("Justification: " + demande.justification()));
            document.add(new Paragraph("Descriptif: " + demande.descriptif()));
            document.add(new Paragraph("Quantité: " + demande.quantite()));
            document.add(new Paragraph("Urgence: " + demande.urgence().name()));
            document.add(new Paragraph("État: " + demande.etat().name()));
            document.add(new Paragraph("Date de demande: " + demande.date_demande().toString()));
    
            System.out.println("Closing document...");
            document.close();
            System.out.println("PDF generated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return pdfPath;
    }
}

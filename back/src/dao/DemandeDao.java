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
        // Récupérer la date de la demande et la formater
        LocalDate demandeDate = demande.date_demande().toLocalDateTime().toLocalDate();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = demandeDate.format(dateFormatter);
        
        // Remplacer les espaces et caractères non valides par des underscores
        String sanitizedDemandeurName = demandeurName.replaceAll("[^a-zA-Z0-9]", "_");
        String sanitizedDomaine = demande.domaine().replaceAll("[^a-zA-Z0-9]", "_");
        
        // Construire le nom du fichier PDF
        String pdfFileName = "demande_" + sanitizedDemandeurName + "_" + formattedDate + "_" + sanitizedDomaine + ".pdf";
        String pdfPath = "back/src/pdf/Devis/" + pdfFileName;

        try {
            System.out.println("Initializing PDF writer...");
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfPath));
            System.out.println("Initializing PDF document...");
            PdfDocument pdfDoc = new PdfDocument(writer);
            System.out.println("Initializing document...");
            Document document = new Document(pdfDoc);
            
            // Adding title
            Paragraph title = new Paragraph("Demande de " + demandeurName + " - " + formattedDate)
                    .setFontSize(20)
                    .setBold()
                    .setMarginBottom(20);
            document.add(title);
    
            // Adding a table
            float[] columnWidths = {1, 3}; // Largeurs des colonnes
            Table table = new Table(columnWidths);
            
            table.addCell(createCell("ID:"));
            table.addCell(createCell(demande.id() + ""));
            table.addCell(createCell("Référant:"));
            table.addCell(createCell(demande.referant()));
            table.addCell(createCell("Domaine:"));
            table.addCell(createCell(demande.domaine()));
            table.addCell(createCell("Type:"));
            table.addCell(createCell(demande.typeof()));
            table.addCell(createCell("Marque:"));
            table.addCell(createCell(demande.marque()));
            table.addCell(createCell("Référence:"));
            table.addCell(createCell(demande.reference()));
            table.addCell(createCell("Pour:"));
            table.addCell(createCell(demande.pour()));
            table.addCell(createCell(demande.ou()));
            table.addCell(createCell(demande.marche()));
            table.addCell(createCell("Justification:"));
            table.addCell(createCell(demande.justification()));
            table.addCell(createCell("Descriptif:"));
            table.addCell(createCell(demande.descriptif()));
            table.addCell(createCell("Quantité:"));
            table.addCell(createCell(demande.quantite() + ""));
            table.addCell(createCell("Urgence:"));
            table.addCell(createCell(demande.urgence().name()));
            table.addCell(createCell("État:"));
            table.addCell(createCell(demande.etat().name()));
            table.addCell(createCell("Date de demande:"));
            table.addCell(createCell(demande.date_demande().toString()));
    
            document.add(table);
    
            System.out.println("Closing document...");
            document.close();
            System.out.println("PDF generated successfully.");
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
}

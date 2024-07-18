package dao;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import models.BonCommande;

public class BonCommandeDao {

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
                    resultSet.getInt("utilisateur_id"),
                    BonCommande.Etat.valueOf(resultSet.getString("etat")),
                    resultSet.getString("fichier_pdf"),
                    resultSet.getTimestamp("date_creation")
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

    public String generatePdf(BonCommande bonCommande, String utilisateurName) {
        LocalDate creationDate = bonCommande.date_creation().toLocalDateTime().toLocalDate();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = creationDate.format(dateFormatter);

        String sanitizedUtilisateurName = utilisateurName.replaceAll("[^a-zA-Z0-9]", "_");

        String pdfFileName = "bon_commande_" + sanitizedUtilisateurName + "_" + formattedDate + ".pdf";
        String pdfPath = "back/src/pdf/BonCommande/" + pdfFileName;

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfPath));

            PdfDocument pdfDoc = new PdfDocument(writer);

            Document document = new Document(pdfDoc);

            Paragraph title = new Paragraph("Bon de Commande de " + utilisateurName + " - " + formattedDate)
                    .setFontSize(20)
                    .setBold()
                    .setMarginBottom(20);
            document.add(title);

            float[] columnWidths = {1, 3};
            Table table = new Table(columnWidths);

            table.addCell(createCell("ID:"));
            table.addCell(createCell(bonCommande.id() + ""));
            table.addCell(createCell("Devis ID:"));
            table.addCell(createCell(bonCommande.devis_id() + ""));
            table.addCell(createCell("Utilisateur ID:"));
            table.addCell(createCell(bonCommande.utilisateur_id() + ""));
            table.addCell(createCell("État:"));
            table.addCell(createCell(bonCommande.etat().name()));
            table.addCell(createCell("Date de création:"));
            table.addCell(createCell(bonCommande.date_creation().toString()));

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
}

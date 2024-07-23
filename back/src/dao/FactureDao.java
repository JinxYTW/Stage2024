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
import models.Facture;

public class FactureDao {

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
                    resultSet.getDouble("montant"),
                    resultSet.getString("fichier_pdf"),
                    Facture.Etat.valueOf(resultSet.getString("etat")),
                    resultSet.getTimestamp("date_facture")
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

            String query = "UPDATE Facture SET fichier_pdf = ? WHERE id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setString(1, pdfPath);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generatePdf(Facture facture, String utilisateurName) {
        LocalDate factureDate = facture.date_facture().toLocalDateTime().toLocalDate();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = factureDate.format(dateFormatter);

        String sanitizedUtilisateurName = utilisateurName.replaceAll("[^a-zA-Z0-9]", "_");

        String pdfFileName = "facture_" + sanitizedUtilisateurName + "_" + formattedDate + ".pdf";
        String pdfPath = "back/src/pdf/Facture/" + pdfFileName;

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfPath));

            PdfDocument pdfDoc = new PdfDocument(writer);

            Document document = new Document(pdfDoc);

            Paragraph title = new Paragraph("Facture de " + utilisateurName + " - " + formattedDate)
                    .setFontSize(20)
                    .setBold()
                    .setMarginBottom(20);
            document.add(title);

            float[] columnWidths = {1, 3};
            Table table = new Table(columnWidths);

            table.addCell(createCell("ID:"));
            table.addCell(createCell(facture.id() + ""));
            table.addCell(createCell("Bon de Commande ID:"));
            table.addCell(createCell(facture.bon_commande_id() + ""));
            table.addCell(createCell("Montant:"));
            table.addCell(createCell(facture.montant() + ""));
            table.addCell(createCell("État:"));
            table.addCell(createCell(facture.etat().name()));
            table.addCell(createCell("Date de Facture:"));
            table.addCell(createCell(facture.date_facture().toString()));

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

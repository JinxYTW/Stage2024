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

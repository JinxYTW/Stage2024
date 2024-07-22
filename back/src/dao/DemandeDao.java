package dao;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.borders.SolidBorder;

import database.SomethingDatabase;
import models.Demande;

import dao.ProjetDao;

public class DemandeDao {

    public Demande getDetailsDemande(int id){
        Demande demande = null;
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();
            String query = "SELECT * FROM Demande WHERE id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                demande = new Demande(
                    resultSet.getInt("id"),
                    resultSet.getInt("utilisateur_id"),
                    resultSet.getString("projet_nom"),
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
                    resultSet.getString("additional_details"),
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
        return demande;
    }
    

    public static List<Demande> searchDemands(String orderNumber, String orderDate, String orderArticle, String orderDomain, String orderClient) {
    System.out.println("Dao searchDemands");    
    List<Demande> demandes = new ArrayList<>();
    String sql = "SELECT * FROM Demande WHERE 1=1";

    if (orderNumber != null && !orderNumber.isEmpty()) {
        sql += " AND id = ?";
    }
    if (orderDate != null && !orderDate.isEmpty()) {
        sql += " AND date_demande >= ? AND date_demande < ?";
    }
    if (orderArticle != null && !orderArticle.isEmpty()) {
        sql += " AND typeof LIKE ? OR marche LIKE ?";
    }
    if (orderDomain != null && !orderDomain.isEmpty()) {
        sql += " AND domaine = ?";
    }
    if (orderClient != null && !orderClient.isEmpty()) {
        orderClient = orderClient.trim(); // Nettoyer les espaces
        sql += " AND utilisateur_id IN (SELECT id FROM Utilisateur WHERE nom LIKE ?)";
    }

    try {
        SomethingDatabase myDatabase = new SomethingDatabase();
        
        PreparedStatement stmt = myDatabase.prepareStatement(sql);
        
        int index = 1;
        if (orderNumber != null && !orderNumber.isEmpty()) {
            stmt.setInt(index++, Integer.parseInt(orderNumber));
        }
        if (orderDate != null && !orderDate.isEmpty()) {
            // Convertir la date du format dd/MM/yyyy au format yyyy-MM-dd
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputFormat.parse(orderDate);
            String formattedDate = outputFormat.format(date);
            System.out.println("formattedDate: " + formattedDate);
            // Utiliser une plage de dates pour inclure toutes les heures de ce jour
            stmt.setString(index++, formattedDate + " 00:00:00");
            stmt.setString(index++, formattedDate + " 23:59:59");
        }
        if (orderArticle != null && !orderArticle.isEmpty()) {
            stmt.setString(index++, "%" + orderArticle + "%");
            stmt.setString(index++, "%" + orderArticle + "%");
        }
        if (orderDomain != null && !orderDomain.isEmpty()) {
            stmt.setString(index++, orderDomain);
        }
        if (orderClient != null && !orderClient.isEmpty()) {
            stmt.setString(index++, "%" + orderClient + "%");
        }

        System.out.println("Dao searchDemands stmt: " + stmt);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Demande demande = new Demande(
                rs.getInt("id"),
                rs.getInt("utilisateur_id"),
                rs.getString("projet_nom"),
                rs.getString("referant"),
                rs.getString("domaine"),
                rs.getString("typeof"),
                rs.getString("marque"),
                rs.getString("reference"),
                rs.getString("pour"),
                rs.getString("ou"),
                rs.getString("marche"),
                rs.getString("justification"),
                rs.getString("descriptif"),
                rs.getString("additional_details"),
                rs.getInt("quantite"),
                Demande.Urgence.valueOf(rs.getString("urgence")),
                Demande.Etat.valueOf(rs.getString("etat")),
                rs.getTimestamp("date_demande"),
                rs.getString("pdfPath")
            );
            demandes.add(demande);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    System.out.println("Dao searchDemands demandes.size(): " + demandes.size());
    System.out.println("Dao searchDemands demandes: " + demandes);
    return demandes;
}

    public List<Demande> findDemandesByUtilisateurId(String utilisateurId) {
        
        List<Demande> demandes = new ArrayList<>();
        int utilisateurIdInt = Integer.parseInt(utilisateurId);
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();
    
            String query = "SELECT * FROM Demande WHERE utilisateur_id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, utilisateurIdInt);
    
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                Demande demande = new Demande(
                    resultSet.getInt("id"),
                    resultSet.getInt("utilisateur_id"),
                    resultSet.getString("projet_nom"),
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
                    resultSet.getString("additional_details"),
                    resultSet.getInt("quantite"),
                    Demande.Urgence.valueOf(resultSet.getString("urgence")),
                    Demande.Etat.valueOf(resultSet.getString("etat")),
                    resultSet.getTimestamp("date_demande"),
                    resultSet.getString("pdfPath")
                );
                demandes.add(demande);
                
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return demandes;
    }

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
                    resultSet.getString("projet_nom"),
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
                    resultSet.getString("additional_details"),
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
        String pdfPath = "back/src/pdf/Demande/" + pdfFileName;

        try {
            
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfPath));
            
            PdfDocument pdfDoc = new PdfDocument(writer);
            
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

    public int createDemande(Demande demande) throws Exception {
        int demandeId = -1;

        
        try {
            
            SomethingDatabase myDatabase = new SomethingDatabase();

            

            String query = "INSERT INTO Demande (utilisateur_id, projet_nom, referant, domaine, typeof, marque, reference, pour, ou, marche, justification, descriptif,additional_details, quantite, urgence, etat, date_demande,pdfPath) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement statement = myDatabase.prepareStatement(query);

            statement.setInt(1, demande.utilisateur_id());
            System.out.println("demande.utilisateur_id() : " + demande.utilisateur_id());

            statement.setString(2, demande.projet_nom());
            System.out.println("demande.projet_nom() : " + demande.projet_nom());

            statement.setString(3, demande.referant());
            System.out.println("demande.referant() : " + demande.referant());

            statement.setString(4, demande.domaine());
            System.out.println("demande.domaine() : " + demande.domaine());

            statement.setString(5, demande.typeof());
            System.out.println("demande.typeof() : " + demande.typeof());

            statement.setString(6, demande.marque());
            System.out.println("demande.marque() : " + demande.marque());

            statement.setString(7, demande.reference());
            System.out.println("demande.reference() : " + demande.reference());

            statement.setString(8, demande.pour());
            System.out.println("demande.pour() : " + demande.pour());

            statement.setString(9, demande.ou());
            System.out.println("demande.ou() : " + demande.ou());

            statement.setString(10, demande.marche());
            System.out.println("demande.marche() : " + demande.marche());

            statement.setString(11, demande.justification());
            System.out.println("demande.justification() : " + demande.justification());

            statement.setString(12, demande.descriptif());
            System.out.println("demande.descriptif() : " + demande.descriptif());

            statement.setString(13, demande.additional_details());
            System.out.println("demande.additional_details() : " + demande.additional_details());

            statement.setInt(14, demande.quantite());
            System.out.println("demande.quantite() : " + demande.quantite());

            statement.setString(15, demande.urgence().name());
            System.out.println("demande.urgence().name() : " + demande.urgence().name());

            statement.setString(16, demande.etat().name());
            System.out.println("demande.etat().name() : " + demande.etat().name());

            statement.setTimestamp(17, demande.date_demande());
            System.out.println("demande.date_demande() : " + demande.date_demande());

            statement.setString(18, demande.pdfPath());
            System.out.println("demande.pdfPath() : " + demande.pdfPath());
            
            


            int rowsInserted = statement.executeUpdate();

        if (rowsInserted > 0) {
            // Exécuter une requête pour obtenir l'ID généré
            String getIdQuery = "SELECT MAX(id) AS last_id FROM Demande";
            PreparedStatement getIdStatement = myDatabase.prepareStatement(getIdQuery);
            ResultSet resultSet = getIdStatement.executeQuery();

            if (resultSet.next()) {
                
                demandeId = resultSet.getInt("last_id");
                
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return demandeId;
}
}

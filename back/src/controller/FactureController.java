package controller;

import com.google.gson.JsonObject;
import dao.FactureDao;
import dao.UtilisateurDao;
import models.Facture;
import webserver.WebServerContext;
import webserver.WebServerResponse;

public class FactureController {

    public void generatePdf(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        
        try {
            String idParam = context.getRequest().getQueryParams().get("id");
            
            if (idParam == null) {
                response.serverError("Paramètre 'id' manquant dans la requête");
                return;
            }
    
            int factureId = Integer.parseInt(idParam);
            
            FactureDao factureDao = new FactureDao();
            Facture facture = factureDao.findById(factureId);
            if (facture == null) {
                response.notFound("Facture non trouvée");
                return;
            }
    
            UtilisateurDao utilisateurDao = new UtilisateurDao();
            String utilisateurName = utilisateurDao.getNames(facture.bon_commande_id());
    
            String pdfPath = factureDao.generatePdf(facture, utilisateurName);
            
            if (pdfPath == null) {
                response.serverError("Échec de la génération du PDF");
                return;
            }
    
            factureDao.savePdfPath(factureId, pdfPath);
    
            JsonObject json = new JsonObject();
            json.addProperty("pdfPath", pdfPath);
            
            response.json(json);
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.serverError("Format incorrect pour le paramètre 'id'");
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur serveur");
        }
    }
}

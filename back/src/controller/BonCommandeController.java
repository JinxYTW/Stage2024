package controller;

import com.google.gson.JsonObject;
import dao.BonCommandeDao;
import dao.UtilisateurDao;
import models.BonCommande;
import webserver.WebServerContext;
import webserver.WebServerResponse;

public class BonCommandeController {

    public void generatePdfBonCommande(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        
        try {
            String idParam = context.getRequest().getQueryParams().get("id");
            
            if (idParam == null) {
                response.serverError("Paramètre 'id' manquant dans la requête");
                return;
            }
    
            int bonCommandeId = Integer.parseInt(idParam);
            
            BonCommandeDao bonCommandeDao = new BonCommandeDao();
            BonCommande bonCommande = bonCommandeDao.findById(bonCommandeId);
            if (bonCommande == null) {
                response.notFound("Bon de commande non trouvé");
                return;
            }
    
            UtilisateurDao utilisateurDao = new UtilisateurDao();
            String utilisateurName = utilisateurDao.getNames(bonCommande.utilisateur_id());
    
            String pdfPath = bonCommandeDao.generatePdf(bonCommande, utilisateurName);
            
            if (pdfPath == null) {
                response.serverError("Échec de la génération du PDF");
                return;
            }
    
            bonCommandeDao.savePdfPath(bonCommandeId, pdfPath);
    
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

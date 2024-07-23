package controller;

import com.google.gson.JsonObject;

import webserver.WebServerContext;
import webserver.WebServerResponse;
import dao.DevisDao;
import dao.UtilisateurDao;
import models.Devis;

public class DevisController {
    public DevisController() {
    }

    public void generatePdf(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        
        try {
            String idParam = context.getRequest().getQueryParams().get("id");
            
            if (idParam == null) {
                response.serverError("Paramètre 'id' manquant dans la requête");
                return;
            }
    
            int devisId = Integer.parseInt(idParam);
            
            DevisDao devisDao = new DevisDao();
            Devis devis = devisDao.findById(devisId);
            if (devis == null) {
                response.notFound("Devis non trouvé");
                return;
            }
    
            UtilisateurDao utilisateurDao = new UtilisateurDao();
            String demandeurName = utilisateurDao.getNames(devis.demande_id());
    
            String pdfPath = devisDao.generatePdf(devis, demandeurName);
            
            if (pdfPath == null) {
                response.serverError("Échec de la génération du PDF");
                return;
            }
    
            devisDao.savePdfPath(devisId, pdfPath);
    
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

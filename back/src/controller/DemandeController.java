package controller;

import dao.DemandeDao;
import dao.UtilisateurDao;
import models.Demande;
import webserver.WebServerContext;
import webserver.WebServerResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class DemandeController {

    public void createDemande(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        System.out.println("DemandeController.createDemande");
        try {
            String demandeJson = context.getRequest().getQueryParams().get("demande");
            System.out.println("demandeJson: " + demandeJson);
    
            if (demandeJson == null) {
                response.serverError("Paramètre 'demande' manquant dans la requête");
                return;
            }
    
            Demande demande = new Gson().fromJson(demandeJson, Demande.class);
            System.out.println("demande: " + demande);
    
            DemandeDao demandeDao = new DemandeDao();
            int demandeId = demandeDao.createDemande(demande);
            System.out.println("demandeId: " + demandeId);
    
            JsonObject json = new JsonObject();
            json.addProperty("demandeId", demandeId);
            System.out.println("Sending JSON response: " + json);
            response.json(json);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur serveur");
        }
    }


    public void generatePdf(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        System.out.println("DemandeController.generatePdf");
        try {
            String idParam = context.getRequest().getQueryParams().get("id");
            System.out.println("idParam: " + idParam);
    
            if (idParam == null) {
                response.serverError("Paramètre 'id' manquant dans la requête");
                return;
            }
    
            int demandeId = Integer.parseInt(idParam);
            System.out.println("demandeId: " + demandeId);
    
            DemandeDao demandeDao = new DemandeDao();
            Demande demande = demandeDao.findById(demandeId);
            if (demande == null) {
                response.notFound("Demande non trouvée");
                return;
            }
    
            UtilisateurDao utilisateurDao = new UtilisateurDao();
            String demandeurName = utilisateurDao.getNames(demande.utilisateur_id());
    
            System.out.println("Generating PDF...");
            String pdfPath = demandeDao.generatePdf(demande, demandeurName);
            System.out.println("pdfPath: " + pdfPath);
            if (pdfPath == null) {
                response.serverError("Échec de la génération du PDF");
                return;
            }
    
            System.out.println("Saving PDF path...");
            demandeDao.savePdfPath(demandeId, pdfPath);
            System.out.println("PDF path saved");
    
            JsonObject json = new JsonObject();
            json.addProperty("pdfPath", pdfPath);
            System.out.println("PDF path sent");
            System.out.println("Sending JSON response: " + json);
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

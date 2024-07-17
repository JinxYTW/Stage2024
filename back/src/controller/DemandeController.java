package controller;

import dao.DemandeDao;
import dao.UtilisateurDao;

import models.Demande;
import models.Demande.Urgence;
import webserver.WebServerContext;
import webserver.WebServerResponse;

import java.sql.Timestamp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class DemandeController {

    public Urgence mapUrgenceLevel(String level) {
    return switch (level) {
        case "1" -> Urgence.basse;
        case "2" -> Urgence.moyenne;
        case "3" -> Urgence.haute;
        default -> null; // ou lancez une exception si nécessaire
    };
}

    public void createDemande(WebServerContext context) {
    WebServerResponse response = context.getResponse();
    System.out.println("DemandeController.createDemande");
    try {
        // Lire le corps de la requête comme une chaîne JSON
        String demandeJson = context.getRequest().getBodyAsString();
        System.out.println("demandeJson: " + demandeJson);

        if (demandeJson == null || demandeJson.isEmpty()) {
            response.serverError("Corps de la requête 'demande' manquant ou vide");
            return;
        }

        // Désérialiser le JSON en objet Demande
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(demandeJson, JsonObject.class);

        // Mappage de la demande JSON à l'objet Demande
        Demande demande = new Demande(
            0, // id auto-incrémenté
            jsonObject.get("utilisateur_id").getAsInt(), // utilisateur_id
            jsonObject.get("project_switch").getAsBoolean() ? jsonObject.get("project_nom").getAsString() : "", // projet_id
            jsonObject.get("referent_select").getAsString(), // referant
            jsonObject.get("project_domain").isJsonNull() ? null : jsonObject.get("project_domain").getAsString(), // domaine
            jsonObject.get("aricle_select").getAsString(), // typeof
            jsonObject.get("brand").getAsString(), // marque
            jsonObject.get("reference").getAsString(), // reference
            jsonObject.get("details_w").getAsString(), // pour
            jsonObject.get("site").getAsString(), // ou
            jsonObject.get("market_select").getAsString(), // marche
            jsonObject.get("justification").getAsString(), // justification
            jsonObject.get("details").getAsString(), // descriptif
            jsonObject.get("quantity").getAsInt(), // quantite
            mapUrgenceLevel(jsonObject.get("urgency_level").getAsString()), // urgence
            Demande.Etat.envoyée, // etat par défaut
            new Timestamp(System.currentTimeMillis()), // date_demande
            ""// pdfPath""
        );

        // Logique pour créer la demande
        DemandeDao demandeDao = new DemandeDao();
        int demandeId = demandeDao.createDemande(demande);
        System.out.println("demandeId: " + demandeId);

        // Construire la réponse JSON
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("demandeId", demandeId);
        System.out.println("Sending JSON response: " + responseJson);
        response.json(responseJson);

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

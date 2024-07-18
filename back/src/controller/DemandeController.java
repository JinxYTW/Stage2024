package controller;

import dao.DemandeDao;
import dao.UtilisateurDao;

import models.Demande;
import models.Demande.Urgence;
import models.Utilisateur;
import webserver.WebServerContext;
import webserver.WebServerResponse;

import java.sql.Timestamp;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DemandeController {

    public void emitDemandesUtilisateur(WebServerContext context) {
        try {
            String body = context.getRequest().getBodyAsString();
            JsonObject requestBody = new Gson().fromJson(body, JsonObject.class);
            int utilisateurId = requestBody.get("utilisateurId").getAsInt();
    
            DemandeDao demandeDao = new DemandeDao();
            UtilisateurDao utilisateurDao = new UtilisateurDao();
            List<Demande> demandes = demandeDao.findDemandesByUtilisateurId(utilisateurId);
    
            JsonArray demandesJsonArray = new JsonArray();
            for (Demande demande : demandes) {
                JsonObject json = new JsonObject();
                json.addProperty("demandeId", demande.id());
                json.addProperty("projet_nom", demande.projet_nom());
                json.addProperty("etat", demande.etat().toString());
                json.addProperty("date_demande", demande.date_demande().toString());
    
                String demandeurNomPrenom = utilisateurDao.getNames(demande.utilisateur_id());
                json.addProperty("demandeur_nom_prenom", demandeurNomPrenom);
    
                demandesJsonArray.add(json);
            }
    
            context.getSSE().emit("demandesUtilisateur", demandesJsonArray);
            context.getResponse().ok("Demandes utilisateur émises avec succès");
    
        } catch (Exception e) {
            e.printStackTrace();
            context.getResponse().serverError("Erreur lors de l'émission des demandes utilisateur");
        }
    }

    public void getDemandesByUtilisateur(WebServerContext context) {
    WebServerResponse response = context.getResponse();
    int utilisateurId = 1;

    try {
        DemandeDao demandeDao = new DemandeDao();
        UtilisateurDao utilisateurDao = new UtilisateurDao();
        List<Demande> demandes = demandeDao.findDemandesByUtilisateurId(utilisateurId);

        Gson gson = new Gson();
        JsonArray demandesJsonArray = new JsonArray();

        for (Demande demande : demandes) {
            JsonObject json = new JsonObject();
            json.addProperty("demandeId", demande.id());
            json.addProperty("projet_nom", demande.projet_nom());
            json.addProperty("etat", demande.etat().toString());
            json.addProperty("date_demande", demande.date_demande().toString());

            // Utiliser getNames pour obtenir les informations de l'utilisateur
            String demandeurNomPrenom = utilisateurDao.getNames(demande.id());
            json.addProperty("demandeur_nom_prenom", demandeurNomPrenom);

            demandesJsonArray.add(json);
        }

        response.json(demandesJsonArray);
        System.out.println("Demandes récupérées avec succès");
        System.out.println("demandesJsonArray: " + demandesJsonArray);

        // Émettre les demandes via SSE
        System.out.println("Emitting demandesUtilisateur event: " + demandesJsonArray.toString());
        context.getSSE().emit("demandesUtilisateur", demandesJsonArray);
        System.out.println("Émission des demandes via SSE");

    } catch (Exception e) {
        e.printStackTrace();
        response.serverError("Erreur serveur");
    }
}


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
    
    try {
        // Lire le corps de la requête comme une chaîne JSON
        String demandeJson = context.getRequest().getBodyAsString();
      

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
        
        response.json(responseJson);

        emitNewDemandeEvent(context, demandeId, demande);
        System.out.println("Demande créée avec succès");

    } catch (Exception e) {
        e.printStackTrace();
        response.serverError("Erreur serveur");
    }
}

private void emitNewDemandeEvent(WebServerContext context, int demandeId, Demande demande) {
    System.out.println("Emitting newDemande event");
    try {
        JsonObject json = new JsonObject();
        json.addProperty("utilisateur_id", demande.utilisateur_id());
        json.addProperty("demandeId", demandeId);
        json.addProperty("projet_nom", demande.projet_nom());
        json.addProperty("etat", demande.etat().toString());

        System.out.println("json: " + json);

        context.getSSE().emit("newDemande", json);
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    public void generatePdf(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        
        try {
            String idParam = context.getRequest().getQueryParams().get("id");
            
    
            if (idParam == null) {
                response.serverError("Paramètre 'id' manquant dans la requête");
                return;
            }
    
            int demandeId = Integer.parseInt(idParam);
            
    
            DemandeDao demandeDao = new DemandeDao();
            Demande demande = demandeDao.findById(demandeId);
            if (demande == null) {
                response.notFound("Demande non trouvée");
                return;
            }
    
            UtilisateurDao utilisateurDao = new UtilisateurDao();
            String demandeurName = utilisateurDao.getNames(demande.utilisateur_id());
    
            
            String pdfPath = demandeDao.generatePdf(demande, demandeurName);
            
            if (pdfPath == null) {
                response.serverError("Échec de la génération du PDF");
                return;
            }
    
            
            demandeDao.savePdfPath(demandeId, pdfPath);
            
    
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

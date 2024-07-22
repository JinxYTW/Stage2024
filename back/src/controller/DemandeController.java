package controller;

import dao.DemandeDao;
import dao.UtilisateurDao;

import models.Demande;
import models.Demande.Urgence;

import webserver.WebServerContext;
import webserver.WebServerResponse;

import java.sql.Timestamp;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DemandeController {

    
    public void demandeDetails(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        String demandeId = context.getRequest().getQueryParams().get("id");

        if (demandeId == null || demandeId.isEmpty()) {
            response.status(400, "ID de la demande manquant");
            return;
        }

        try {
            int id = Integer.parseInt(demandeId);
            UtilisateurDao utilisateurDao = new UtilisateurDao();
            DemandeDao demandeDao = new DemandeDao();
            Demande demande = demandeDao.getDetailsDemande(id);
            if (demande != null) {
                JsonObject demandeJson = new JsonObject();
                demandeJson.addProperty("id", demande.id());
                demandeJson.addProperty("utilisateur_id", demande.utilisateur_id());
                demandeJson.addProperty("demandeur_nom_prenom", utilisateurDao.getNames(demande.utilisateur_id()));
                demandeJson.addProperty("projet_nom", demande.projet_nom());
                demandeJson.addProperty("referant", demande.referant());
                demandeJson.addProperty("domaine", demande.domaine());
                demandeJson.addProperty("typeof", demande.typeof());
                demandeJson.addProperty("marque", demande.marque());
                demandeJson.addProperty("reference", demande.reference());
                demandeJson.addProperty("pour", demande.pour());
                demandeJson.addProperty("ou", demande.ou());
                demandeJson.addProperty("marche", demande.marche());
                demandeJson.addProperty("justification", demande.justification());
                demandeJson.addProperty("descriptif", demande.descriptif());
                demandeJson.addProperty("additional_details", demande.additional_details());
                demandeJson.addProperty("quantite", demande.quantite());
                demandeJson.addProperty("urgence", demande.urgence().toString());
                demandeJson.addProperty("etat", demande.etat().toString());
                demandeJson.addProperty("date_demande", demande.date_demande().toString());
                demandeJson.addProperty("pdfPath", demande.pdfPath());

                // Ajout des informations supplémentaires requises par le front-end
                demandeJson.addProperty("fournisseur", "Nom du fournisseur");
                JsonObject devisJson = new JsonObject();
                devisJson.addProperty("validePar", "Nom du valideur");
                devisJson.addProperty("date", "Date du devis");
                devisJson.addProperty("numero", "Numéro du devis");
                devisJson.addProperty("path", "chemin/vers/le/devis.pdf");
                demandeJson.add("devis", devisJson);

                JsonObject bcJson = new JsonObject();
                bcJson.addProperty("editePar", "Nom de l'éditeur du BC");
                bcJson.addProperty("date", "Date du BC");
                bcJson.addProperty("numero", "Numéro du BC");
                bcJson.addProperty("path", "chemin/vers/le/bc.pdf");
                demandeJson.add("bc", bcJson);

                JsonObject livraisonJson = new JsonObject();
                livraisonJson.addProperty("date", "Date de livraison");
                livraisonJson.addProperty("lieu", "Lieu de livraison");
                livraisonJson.addProperty("signePar", "Nom du signataire du BL");
                livraisonJson.addProperty("transitaire", "Nom du transitaire");
                livraisonJson.addProperty("numero", "Numéro du BL");
                livraisonJson.addProperty("path", "chemin/vers/le/bl.pdf");
                demandeJson.add("livraison", livraisonJson);

                demandeJson.addProperty("articles", "Liste des articles");
                demandeJson.addProperty("commentaires", "Commentaires supplémentaires");

                response.json(demandeJson);
            } else {
                response.status(404, "Demande non trouvée");
            }
        } catch (NumberFormatException e) {
            response.status(400, "ID de la demande invalide");
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur lors de la récupération des détails de la demande");
        }
    }

    public void searchDemandes(WebServerContext context) {
        System.out.println("searchDemandes");
        WebServerResponse response = context.getResponse();

        try {
            // Récupérer les paramètres de la requête
            String orderNumber = context.getRequest().getQueryParams().get("orderNumber");
            System.out.println("orderNumber: " + orderNumber);
            String orderDate = context.getRequest().getQueryParams().get("orderDate");
            System.out.println("orderDate: " + orderDate);
            String orderArticle = context.getRequest().getQueryParams().get("orderArticle");
            System.out.println("orderArticle: " + orderArticle);
            String orderDomain = context.getRequest().getQueryParams().get("orderDomain");
            System.out.println("orderDomain: " + orderDomain);
            String orderClient = context.getRequest().getQueryParams().get("orderClient");
            System.out.println("orderClient: " + orderClient);

            // Effectuer la recherche des demandes
            List<Demande> demandes = DemandeDao.searchDemands(orderNumber, orderDate, orderArticle, orderDomain, orderClient);

            // Convertir les demandes en JSON
            JsonArray demandesJsonArray = new JsonArray();
            UtilisateurDao utilisateurDao = new UtilisateurDao();
            for (Demande demande : demandes) {
                JsonObject json = new JsonObject();
                json.addProperty("demandeId", demande.id());
                //Besoin du nom et prénom du demandeur
                json.addProperty("utilisateur_id", demande.utilisateur_id());
                String demandeurNomPrenom = utilisateurDao.getNames(demande.utilisateur_id());
                json.addProperty("demandeur_nom_prenom", demandeurNomPrenom);

                json.addProperty("projet_nom", demande.projet_nom());
                json.addProperty("referant", demande.referant());
                json.addProperty("domaine", demande.domaine());
                json.addProperty("typeof", demande.typeof());
                json.addProperty("marque", demande.marque());
                json.addProperty("reference", demande.reference());
                json.addProperty("pour", demande.pour());
                json.addProperty("ou", demande.ou());
                json.addProperty("marche", demande.marche());
                json.addProperty("justification", demande.justification());
                json.addProperty("descriptif", demande.descriptif());
                json.addProperty("quantite", demande.quantite());
                json.addProperty("urgence", demande.urgence().toString());
                json.addProperty("etat", demande.etat().toString());
                json.addProperty("date_demande", demande.date_demande().toString());
                json.addProperty("pdfPath", demande.pdfPath());

                demandesJsonArray.add(json);
            }

            // Envoyer la réponse JSON
            response.json(demandesJsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur lors de la recherche des demandes");
        }
    }

    public void emitDemandesUtilisateur(WebServerContext context) {
        try {
            String body = context.getRequest().getBodyAsString();
            JsonObject requestBody = new Gson().fromJson(body, JsonObject.class);
            String utilisateurId = context.getRequest().getQueryParams().get("id");
    
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
    String utilisateurId = "1";

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
            jsonObject.get("project_switch").getAsBoolean() ? jsonObject.get("project_name").getAsString() : "", // projet_id
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
            jsonObject.get("additional_details").getAsString(), // additional_info
            jsonObject.get("quantity").getAsInt(), // quantite
            mapUrgenceLevel(jsonObject.get("urgency_level").getAsString()), // urgence
            Demande.Etat.envoyée, // etat par défaut
            new Timestamp(System.currentTimeMillis()), // date_demande
            ""// pdfPath""
        );

        System.out.println("demande: " + demande);

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

package controller;

import dao.BonCommandeDao;
import dao.DemandeDao;
import dao.DevisDao;
import dao.FactureDao;
import dao.NotifDao;
import dao.UtilisateurDao;
import models.BonCommande;
import models.Demande;
import models.Demande.Urgence;
import models.Devis;
import models.Facture;
import webserver.WebServerContext;
import webserver.WebServerResponse;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * The controller class responsible for handling requests related to demandes.
 */
public class DemandeController {

    public void getEtatDemande(WebServerContext context) {
        try {
            // Extraire les données du corps de la requête
            
            String demandeId = context.getRequest().getQueryParams().get("demandeId");
            int demandeIdInt = Integer.parseInt(demandeId);
            String etat = DemandeDao.getEtatDemande(demandeIdInt);
            JsonObject json = new JsonObject();
            json.addProperty("etat", etat);
            context.getResponse().json(json);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            context.getResponse().serverError("Erreur lors de la récupération de l'état de la demande");
        }
    }

    public void updateDemandeEtat(WebServerContext context) {
    try {
        // Extraire les données du corps de la requête
        Map<String, String> requestBody = context.extractBody(Map.class);
        String demandeId = requestBody.get("demandeId");
        int demandeIdInt = Integer.parseInt(demandeId);
        String newType = requestBody.get("newEtat");

        DemandeDao.updateDemandeEtat(demandeIdInt, newType);



        // Logique pour mettre à jour le type de notification
        // Par exemple, mettre à jour dans la base de données
        // notificationService.updateNotificationType(demandeId, newType);

        // Répondre au client avec succès
        context.getResponse().ok("Demande mise à jour avec succès");
    } catch (Exception e) {
        e.printStackTrace();
        context.getResponse().serverError("Erreur lors de la mise à jour de la demande");
    }
}

    
    /**
     * Retrieves the details of a demande and sends the response as a JSON object.
     * The details include information about the demande, associated devis, bon de commande, and facture.
     *
     * @param context The WebServerContext object containing the request and response objects.
     */
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
            DevisDao devisDao = new DevisDao();
            BonCommandeDao bonCommandeDao = new BonCommandeDao();
            FactureDao factureDao = new FactureDao();


            //A completer avec autre DAO pour obtenir info vis à vis validation etc etc

            Demande demande = demandeDao.getDetailsDemande(id);
            Devis devis = devisDao.FindValideDevisFromDemandId(id);
            BonCommande bonCommande = bonCommandeDao.FindValideBcFromDemandId(id);
            Facture facture = factureDao.findValideInvoiceFromDemandId(id);


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
            JsonObject devisJson = new JsonObject();
            devisJson.addProperty("validePar", devis != null ? devis.nom_valideur() : "non défini");
            devisJson.addProperty("date_devis", devis != null ? devis.date_devis().toString() : "non défini");
            devisJson.addProperty("numero", devis != null ? devis.id() : -1);
            devisJson.addProperty("fournisseur_id", devis != null ? devis.fournisseur_id() :-1);
            devisJson.addProperty("fichier_pdf", devis != null ? devis.fichier_pdf() : "non défini");
            demandeJson.add("devis", devisJson);

            JsonObject bcJson = new JsonObject();
            bcJson.addProperty("editePar", bonCommande != null ? bonCommande.nom_editeur() : "non défini");
            bcJson.addProperty("date", bonCommande != null ? bonCommande.date_creation().toString() : "non défini");
            bcJson.addProperty("numero", bonCommande != null ? bonCommande.id() : -1);
            bcJson.addProperty("path", bonCommande != null ? bonCommande.fichier_pdf() : "non défini");
            demandeJson.add("bc", bcJson);

            JsonObject factureJson = new JsonObject();
            factureJson.addProperty("date", facture != null ? facture.date_livraison().toString() : "non défini");
            factureJson.addProperty("lieu", facture != null ? facture.lieu_livraison() : "non défini");
            factureJson.addProperty("signePar", facture != null ? facture.nom_signataire() : "non défini");
            factureJson.addProperty("transitaire", facture != null ? facture.nom_transitaire() : "non défini");
            factureJson.addProperty("numero", facture != null ? facture.id() : -1);
            factureJson.addProperty("path", facture != null ? facture.fichier_pdf() : "non défini");
            demandeJson.add("livraison", factureJson);

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

    /**
     * Searches for demandes based on the provided query parameters and returns the results as a JSON array.
     *
     * @param context The WebServerContext object containing the request and response objects.
     */
    public void searchDemandes(WebServerContext context) {
        System.out.println("searchDemandes");
        WebServerResponse response = context.getResponse();

        try {
            // Récupérer les paramètres de la requête
            String orderNumber = context.getRequest().getQueryParams().get("orderNumber");
            
            String orderDate = context.getRequest().getQueryParams().get("orderDate");
            
            String orderArticle = context.getRequest().getQueryParams().get("orderArticle");
            
            String orderDomain = context.getRequest().getQueryParams().get("orderDomain");
            
            String orderClient = context.getRequest().getQueryParams().get("orderClient");
            

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

    /**
     * Emits the demandes utilisateur as a Server-Sent Event (SSE) to the client.
     * Retrieves the demandes for a specific utilisateur and sends them as a JSON array.
     * Each demande is represented as a JSON object with properties such as demandeId, projet_nom, etat, date_demande, and demandeur_nom_prenom.
     * The emitted SSE event is named "demandesUtilisateur".
     * If successful, the response status is set to OK (200) with a success message.
     * If an error occurs, the response status is set to Server Error (500) with an error message.
     *
     * @param context The WebServerContext object containing the request and response objects.
     */
    public void emitDemandesUtilisateur(WebServerContext context) {
        try {
            String body = context.getRequest().getBodyAsString();
            JsonObject requestBody = new Gson().fromJson(body, JsonObject.class);
            String utilisateurId = context.getRequest().getQueryParams().get("id");
    
            if (utilisateurId == null) {
                context.getResponse().status(400, "ID utilisateur manquant");
                return;
            }
    
            UtilisateurDao utilisateurDao = new UtilisateurDao();
            String role = utilisateurDao.getRole(Integer.parseInt(utilisateurId));
            
            DemandeDao demandeDao = new DemandeDao();
            List<Demande> demandes;
    
            if ("membre".equalsIgnoreCase(role)) {
                demandes = demandeDao.findDemandesByUtilisateurId(utilisateurId);
            } else {
                demandes = demandeDao.findAllDemandes();
            }
    
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

    /**
     * Retrieves the demandes (requests) associated with a specific utilisateur (user).
     * 
     * @param context The WebServerContext object containing the request and response objects.
     */
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
        

        // Émettre les demandes via SSE
        
        context.getSSE().emit("demandesUtilisateur", demandesJsonArray);
        

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

    /**
     * Creates a new demande based on the provided JSON data in the request body.
     * 
     * @param context The WebServerContext object containing the request and response objects.
     */
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

        // Créer une notification pour la demande
        int utilisateurId = demande.utilisateur_id();
        String notificationMessage = "La demande";
        String notificationType = "demande_envoyee"; // Vous pouvez ajuster le type selon vos besoins
        Timestamp notificationDate = new Timestamp(System.currentTimeMillis());

        int notifId = NotifDao.createNotification(demandeId, notificationMessage, notificationType, notificationDate);
        //int notifId = NotifDao.createNotificationForUser(utilisateurId,demandeId, notificationMessage, notificationType, notificationDate);
        System.out.println("notificationId: " + notifId);

        // Construire la réponse JSON
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("demandeId", demandeId);
        
        response.json(responseJson);

        emitNewDemandeEvent(context, demandeId, demande);
        

    } catch (Exception e) {
        e.printStackTrace();
        response.serverError("Erreur serveur");
    }
}

/**
 * Emits a new demande event to the web server context.
 *
 * @param context The web server context.
 * @param demandeId The ID of the demande.
 * @param demande The demande object.
 */
private void emitNewDemandeEvent(WebServerContext context, int demandeId, Demande demande) {
    
    try {
        JsonObject json = new JsonObject();
        json.addProperty("utilisateur_id", demande.utilisateur_id());
        json.addProperty("demandeId", demandeId);
        json.addProperty("projet_nom", demande.projet_nom());
        json.addProperty("etat", demande.etat().toString());

        

        context.getSSE().emit("newDemande", json);
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    /**
     * Generates a PDF for a specific demand and saves the PDF path in the database.
     * 
     * @param context The WebServerContext object containing the request and response objects.
     */
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

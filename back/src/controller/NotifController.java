package controller;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import webserver.WebServerContext;
import webserver.WebServerResponse;

import dao.NotifDao;
import models.Notif;

public class NotifController {
    public NotifController() {
    }

    //Test pour implémentation nouveau système de notification********************************************************************************************
    public void countUnreadNotificationsForUser(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int userId = Integer.parseInt(context.getRequest().getQueryParams().get("userId"));
            int count = NotifDao.countUnreadNotificationsForUser(userId);
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("count", count);
            response.json(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500, "Internal Server Error");
        }
    }


    public void updateNotificationTypeReadForUser(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int notifId = Integer.parseInt(context.getRequest().getQueryParams().get("notifId"));
            Notif notif = NotifDao.getNotificationById(notifId);
            int demandeurId = NotifDao.getDemandeurId(notifId);
    
            if (notif == null) {
                response.status(404, "Notification non trouvée");
                return;
            }
    
            boolean newLuStatus = notif.lu();
            String newType = notif.type().toString();
    
            if (notif.lu()) {
                newLuStatus = false;
                newType = determineNewTypeRead(notif.type().toString());
            }
    
            // Mise à jour du type et du statut 'lu'
            boolean updateSuccess = NotifDao.updateNotificationTypeRead(notifId, newLuStatus, newType);
            System.out.println("updateSuccess: " + updateSuccess);
    
            if (updateSuccess) {
                // Déterminer et exécuter les actions en fonction du nouveau type
                switch (newType) {
                    case "demande_en_cours_de_traitement":
                        NotifDao.addNotificationForTreatDevis(notif.demandeId(), demandeurId, notifId);
                        break;
                    case "devis_en_cours_de_validation":
                        NotifDao.addNotificationForValidateDevis(notif.demandeId(), demandeurId, notifId);
                        break;
                    case "bc_en_cours_dedition":
                        NotifDao.addNotificationForTreatBc(notif.demandeId(), demandeurId, notifId);
                        break;
                    case "bc_en_cours_de_validation":
                        NotifDao.addNotificationForValidateBc(notif.demandeId(), demandeurId, notifId);
                        break;
                    case "envoi_fournisseur_en_cours":
                        NotifDao.addNotificationForNotifBcSend(notif.demandeId(), demandeurId, notifId);
                        break;
                    default:
                        // Si le type ne correspond à aucun des cas définis, ne pas effectuer d'action supplémentaire
                        break;
                }
    
                response.json(updateSuccess);
            } else {
                response.status(500, "Échec de la mise à jour de la notification");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500, "Internal Server Error");
        }
    }


    
    public void markAsReadForUser(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int userId = Integer.parseInt(context.getRequest().getQueryParams().get("userId"));
            
            int notifId = Integer.parseInt(context.getRequest().getQueryParams().get("notifId"));
            

            NotifDao.markAsReadForUser(userId,notifId);
            response.json("{\"success\": true}");
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500, "Internal Server Error");
        }
    }
    

    

    public void updateNotificationType(WebServerContext context) {
    try {
        // Extraire les données du corps de la requête
        Map<String, String> requestBody = context.extractBody(Map.class);
        String demandeId = requestBody.get("demandeId");
        int demandeIdInt = Integer.parseInt(demandeId);
        String newType = requestBody.get("newType");

        // Mise à jour du type de notification dans la base de données
        NotifDao.updateNotificationType(demandeIdInt, newType);

        // Obtenir l'ID de la notification mise à jour
        Notif notif = NotifDao.getNotificationByDemandeId(demandeIdInt); // Méthode à créer si elle n'existe pas déjà
        if (notif == null) {
            context.getResponse().serverError("Notification non trouvée après mise à jour");
            return;
        }

        int notifId = notif.id(); // ID de la notification mise à jour
        int demandeurId = NotifDao.getDemandeurId(notifId); // ID du demandeur, à adapter selon votre structure

        // Déterminer l'action appropriée en fonction du nouveau type
        switch (newType) {
            case "devis_a_valider":
                NotifDao.addNotificationForValidateDevis(demandeIdInt, demandeurId, notifId);
                break;
            case "bc_a_editer":
                NotifDao.addNotificationForTreatBc(demandeIdInt, demandeurId, notifId);
                break;
            case "bc_a_valider":
                NotifDao.addNotificationForValidateBc(demandeIdInt, demandeurId, notifId);
                break;
            case "bc_valide_envoi_fournisseur":
                NotifDao.addNotificationForNotifBcSend(demandeIdInt, demandeurId, notifId);
                break;
            case "bc_envoye_attente_livraison":
            case "facture_a_valider":
                NotifDao.addNotificationForInventory(demandeIdInt, demandeurId, notifId);
                break;
            default:
                // Pas d'action définie pour ce type
                break;
        }

        // Récupérer les e-mails des utilisateurs concernés
        List<String> emailAddresses = NotifDao.getEmailAddressesForNotification(notifId);

        // Envoyer les e-mails
        //sendEmails(emailAddresses, "Notification mise à jour", "Une notification a été mise à jour. Veuillez consulter le système pour plus de détails.");

        // Répondre au client avec succès
        context.getResponse().ok("Notification mise à jour avec succès et e-mails envoyés.");
    } catch (Exception e) {
        e.printStackTrace();
        context.getResponse().serverError("Erreur lors de la mise à jour de la notification");
    }
}

    
    
   public void getMostImportantNotificationForUser(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int userId = Integer.parseInt(context.getRequest().getQueryParams().get("userId"));
            Notif notif = NotifDao.getMostImportantNotificationForUser(userId);
            if (notif == null) {
                response.status(204, "No Content");
                return;
            }
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("id", notif.id());
            jsonResponse.addProperty("demandeId", notif.demandeId());
            jsonResponse.addProperty("message", notif.message());
            jsonResponse.addProperty("type", notif.type().toString());
            jsonResponse.addProperty("lu", notif.lu());
            jsonResponse.addProperty("dateNotification", notif.dateNotification().toString());
            response.json(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500, "Internal Server Error");
        }
    }




    public void isOneNotifOnState(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            String demandeId = context.getRequest().getQueryParams().get("demandeId");
            String type = context.getRequest().getQueryParams().get("type");
            boolean isOneNotifOnState = NotifDao.isOneNotifOnState(demandeId, type);
            JsonObject json = new JsonObject();
            json.addProperty("isOneNotifOnState", isOneNotifOnState);
            response.json(json);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.serverError("Format incorrect pour le paramètre 'demandeId'");
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur serveur");
        }
    }


    
    
    
    private String determineNewTypeRead(String currentType) {
        switch (currentType) {
            case "demande_envoyee":
                return "demande_en_cours_de_traitement";
            case "devis à valider":
                return "devis_en_cours_de_validation";
            case "bc_a_editer":
                return "bc_en_cours_dedition";
            case "bc_a_valider":
                return "bc_en_cours_de_validation";
            case "bc_valide_envoi_fournisseur":
                return "envoi_fournisseur_en_cours";
            default:
                return currentType; // Retourne le type actuel si aucune correspondance n'est trouvée
        }
    }
    

    public void getOldestUrgentNotification(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int userId = Integer.parseInt(context.getRequest().getQueryParams().get("userId"));
            Notif notif = NotifDao.getOldestUrgentNotification(userId);
            
            if (notif == null) {
                // Aucune notification trouvée
                response.status(204, "No Content"); // Vous pouvez aussi choisir d'envoyer une réponse vide JSON ici
                return;
            }
            
            // Préparation de la réponse JSON
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("id", notif.id());
            jsonResponse.addProperty("demandeId", notif.demandeId());
            jsonResponse.addProperty("message", notif.message());
            jsonResponse.addProperty("type", notif.type().toString());
            jsonResponse.addProperty("dateNotification", notif.dateNotification().toString());

            
            response.json(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500, "Internal Server Error");
        }
    }

    public void getNotificationsForUserInde(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int userId = Integer.parseInt(context.getRequest().getQueryParams().get("userId"));
            List<Notif> notifs = NotifDao.getNotificationsForUserInde(userId);
            JsonArray jsonResponse = new JsonArray();
            for (Notif notif : notifs) {
                JsonObject notifJson = new JsonObject();
                notifJson.addProperty("id", notif.id());
                notifJson.addProperty("demandeId", notif.demandeId());
                notifJson.addProperty("message", notif.message());
                notifJson.addProperty("type", notif.type().toString());
                notifJson.addProperty("lu", notif.lu());
                notifJson.addProperty("dateNotification", notif.dateNotification().toString());
                jsonResponse.add(notifJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500, "Internal Server Error");
        }
    }
    
}

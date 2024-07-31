package controller;
import java.util.ArrayList;
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

    public void updateNotificationType(WebServerContext context) {
    try {
        // Extraire les données du corps de la requête
        Map<String, String> requestBody = context.extractBody(Map.class);
        String demandeId = requestBody.get("demandeId");
        int demandeIdInt = Integer.parseInt(demandeId);
        String newType = requestBody.get("newType");

        NotifDao.updateNotificationType(demandeIdInt, newType);



        // Logique pour mettre à jour le type de notification
        // Par exemple, mettre à jour dans la base de données
        // notificationService.updateNotificationType(demandeId, newType);

        // Répondre au client avec succès
        context.getResponse().ok("Notification mise à jour avec succès");
    } catch (Exception e) {
        e.printStackTrace();
        context.getResponse().serverError("Erreur lors de la mise à jour de la notification");
    }
}


    public void updateNotificationTypeRead(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int notifId = Integer.parseInt(context.getRequest().getQueryParams().get("notifId"));
            Notif notif = NotifDao.getNotificationById(notifId);
    
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
    
            
            boolean updateSuccess = NotifDao.updateNotificationTypeRead(notifId, newLuStatus, newType);
            System.out.println("updateSuccess: " + updateSuccess);
    
            if (updateSuccess) {
                response.json(updateSuccess);
            } else {
                response.status(500, "Échec de la mise à jour de la notification");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500, "Internal Server Error");
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
    

    public void markAsRead(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int notifId = Integer.parseInt(context.getRequest().getQueryParams().get("notifId"));
            NotifDao.markAsRead(notifId);
            response.json("{\"success\": true}");
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500, "Internal Server Error");
        }
    }

    public void countNotifForUser(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int userId = Integer.parseInt(context.getRequest().getQueryParams().get("userId"));
            int count = NotifDao.countNotifForUser(userId);
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("count", count);
            response.json(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500, "Internal Server Error");
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

    public void getNotificationsForUser(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int userId = Integer.parseInt(context.getRequest().getQueryParams().get("userId"));
            List<Notif> notifs = NotifDao.getNotificationsForUser(userId);
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

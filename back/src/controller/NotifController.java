package controller;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import webserver.WebServerContext;
import webserver.WebServerResponse;

import dao.NotifDao;
import models.Notif;

public class NotifController {
    public NotifController() {
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

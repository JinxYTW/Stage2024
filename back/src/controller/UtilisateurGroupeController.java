package controller;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;



import dao.UtilisateurGroupeDao;

import webserver.WebServerContext;
import webserver.WebServerResponse;

public class UtilisateurGroupeController {

    public void getGroupesNamesByUtilisateurId(WebServerContext context) {

        WebServerResponse response = context.getResponse();
        try {
            UtilisateurGroupeDao utilisateurGroupeDao = new UtilisateurGroupeDao();
            int utilisateurId = Integer.parseInt(context.getRequest().getQueryParams().get("utilisateurId"));
            

            List<String> groupeNames = utilisateurGroupeDao.getGroupesNamesByUtilisateurId(utilisateurId);
            

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.add("groupes", new Gson().toJsonTree(groupeNames));
            

            response.json(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500, e.getMessage());
        } 
    }
    
}

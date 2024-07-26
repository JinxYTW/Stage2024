package controller;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.sun.net.httpserver.Headers;

import dao.UtilisateurGroupeDao;
import models.UtilisateurGroupe;
import utils.HashUtil;
import webserver.WebServerContext;
import webserver.WebServerResponse;

public class UtilisateurGroupeController {

    public void getGroupesNamesByUtilisateurId(WebServerContext context) {

        WebServerResponse response = context.getResponse();
        try {
            UtilisateurGroupeDao utilisateurGroupeDao = new UtilisateurGroupeDao();
            int utilisateurId = Integer.parseInt(context.getRequest().getQueryParams().get("utilisateurId"));
            System.out.println("utilisateurId: " + utilisateurId);

            List<String> groupeNames = utilisateurGroupeDao.getGroupesNamesByUtilisateurId(utilisateurId);
            System.out.println("groupeNames: " + groupeNames);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.add("groupes", new Gson().toJsonTree(groupeNames));
            System.out.println("jsonResponse: " + jsonResponse);

            response.json(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500, e.getMessage());
        } 
    }
    
}

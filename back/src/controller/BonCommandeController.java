package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dao.BonCommandeDao;
import dao.DevisDao;
import dao.UtilisateurDao;
import models.BonCommande;
import webserver.WebServerContext;
import webserver.WebServerResponse;

public class BonCommandeController {

    public void isOneBcValidate(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int demandeId = Integer.parseInt(context.getRequest().getQueryParams().get("demandeId"));
            boolean isOneBcValidate = BonCommandeDao.isOneBcValidate(demandeId);
            JsonObject json = new JsonObject();
            json.addProperty("isOneBcValidate", isOneBcValidate);
            response.json(json);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.serverError("Format incorrect pour le paramètre 'demandeId'");
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur serveur");
        }
    }

    public void validateBc(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            // Lire le corps de la requête en tant que chaîne JSON
            String bodyAsString = context.getRequest().getBodyAsString();
            JsonObject requestBody = JsonParser.parseString(bodyAsString).getAsJsonObject();
            String pdfPath = requestBody.get("pdfPath").getAsString();
            System.out.println("pdfPath: " + pdfPath);

            // Valider le bon de commande
            String success = BonCommandeDao.validateBc(pdfPath);

            // Envoyer la réponse
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", success);
            response.json(jsonResponse);

            
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur lors de la validation du bon de commande");
        }
    }

    public void getBcPdfPathFromDemandId(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int demandeId = Integer.parseInt(context.getRequest().getQueryParams().get("demandeId"));
    
            // Récupérer le chemin du bon de commande à partir de la demande
            String bcPdfPath = BonCommandeDao.getBcPdfPathFromDemandId(demandeId);
    
            // Préparer la réponse JSON
            JsonArray jsonArray = new JsonArray();
            if (bcPdfPath != null) {
                jsonArray.add(bcPdfPath);
            }
    
            // Envoyer le tableau JSON directement
            response.json(jsonArray);
    
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.serverError("Format incorrect pour le paramètre 'demandeId'");
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur serveur");
        }
    }

    public void getBcCountFromDemandId(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int demandeId = Integer.parseInt(context.getRequest().getQueryParams().get("demandeId"));
            int bcCount = BonCommandeDao.getBcCountFromDemandId(demandeId);
            JsonObject json = new JsonObject();
            json.addProperty("bcCount", bcCount);
            response.json(json);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.serverError("Format incorrect pour le paramètre 'demandeId'");
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur serveur");
        }
    }

    public void uploadBc(WebServerContext context) {
        try {
            // Récupérer le fichier envoyé dans la requête
            InputStream fileInputStream = context.getRequest().getInputStream();
            String fileName = context.getRequest().getHeader("filename");
            System.out.println("Nom du fichier: " + fileName);
            int demandeId = Integer.parseInt(context.getRequest().getQueryParams().get("demandeId"));
            
            // Définir le répertoire où les fichiers seront stockés
            File directory = new File("back/src/pdf/BonCommande");
            if (!directory.exists()) {
                directory.mkdirs(); // Crée le répertoire s'il n'existe pas
            }
    
            File file = new File(directory, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
    
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
    
            fileInputStream.close();
            fileOutputStream.close();
    
            // Obtenez le chemin relatif du fichier
            String relativePath = getRelativePath(file.getCanonicalPath(), directory.getCanonicalPath());
            System.out.println("Relative path: " + relativePath);
    
            // Enregistrez le chemin relatif dans la base de données
            BonCommandeDao.saveBcToDatabase(demandeId, relativePath);
    
            context.getResponse().ok("Fichier téléchargé avec succès");
        } catch (IOException e) {
            e.printStackTrace();
            context.getResponse().serverError("Erreur lors du téléchargement du fichier");
        }
    }
    
    private String getRelativePath(String filePath, String baseDir) {
        // Convertir les chemins en chaînes de caractères avec des barres obliques pour compatibilité multiplateforme
        String absolutePath = new File(filePath).getAbsolutePath().replace("\\", "/");
        String basePath = new File(baseDir).getAbsolutePath().replace("\\", "/");
    
        // Assurez-vous que basePath se termine par /
        if (!basePath.endsWith("/")) {
            basePath += "/";
        }
    
        // Assurez-vous que basePath est un préfixe de absolutePath
        if (absolutePath.startsWith(basePath)) {
            // Extraire le chemin relatif en utilisant substring
            String relativePath = absolutePath.substring(basePath.length());
    
            // Assurez-vous que le chemin relatif commence par /
            if (!relativePath.startsWith("/")) {
                relativePath = "/" + relativePath;
            }
    
            // Construire le chemin relatif personnalisé
            String customRelativePath = "/back/src/pdf/BonCommande" + relativePath;
    
            
            System.out.println("Custom Relative path: " + customRelativePath);
    
            return customRelativePath;
        } else {
            // Le basePath n'est pas un préfixe du absolutePath, retournez une chaîne vide ou gérez l'erreur
            System.err.println("Base path is not a prefix of the absolute path.");
            return "";
        }
    }

   
}

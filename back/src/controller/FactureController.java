package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dao.BonCommandeDao;
import dao.FactureDao;
import dao.UtilisateurDao;
import models.Facture;
import webserver.WebServerContext;
import webserver.WebServerResponse;

public class FactureController {

     public void getInvoicePathsFromDemandId(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int demandeId = Integer.parseInt(context.getRequest().getQueryParams().get("demandeId"));
    
            // Récupérer le chemin du bon de commande à partir de la demande
            String bcPdfPath = FactureDao.getInvoicePathsFromDemandId(demandeId);
    
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

    public void isOneInvoiceValidate(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int demandeId = Integer.parseInt(context.getRequest().getQueryParams().get("demandeId"));
            int isOneInvoiceValidate = FactureDao.isOneInvoiceValidate(demandeId);
            JsonObject json = new JsonObject();
            json.addProperty("isOneInvoiceValidate", isOneInvoiceValidate);
            response.json(json);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.serverError("Format incorrect pour le paramètre 'demandeId'");
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur serveur");
        }
    }

    public void validateInvoice(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            // Lire le corps de la requête en tant que chaîne JSON
            JsonObject requestBody = context.extractBody(JsonObject.class);
            String pdfPath = requestBody.get("pdfPath").getAsString();
            System.out.println("pdfPath: " + pdfPath);
    
            // Valider la facture
            String message = FactureDao.validateInvoice(pdfPath);
    
            response.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur lors de la validation de la facture");
        }
    }
    

    public void getFactureCountFromDemandId(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            int demandeId = Integer.parseInt(context.getRequest().getQueryParams().get("demandeId"));
            int factureCount = FactureDao.getFactureCountFromDemandId(demandeId);
            JsonObject json = new JsonObject();
            json.addProperty("factureCount", factureCount);
            response.json(json);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.serverError("Format incorrect pour le paramètre 'demandeId'");
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur serveur");
        }
    }

    public void uploadInvoice(WebServerContext context) {
        try {
            // Récupérer le fichier envoyé dans la requête
            InputStream fileInputStream = context.getRequest().getInputStream();
            String fileName = context.getRequest().getHeader("filename");
            System.out.println("Nom du fichier: " + fileName);
            int demandeId = Integer.parseInt(context.getRequest().getQueryParams().get("demandeId"));
            
            // Définir le répertoire où les fichiers seront stockés
            File directory = new File("back/src/pdf/Facture");
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
            FactureDao.saveFactureToDatabase(demandeId, relativePath);
    
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
            String customRelativePath = "/back/src/pdf/Facture" + relativePath;
    
            
            System.out.println("Custom Relative path: " + customRelativePath);
    
            return customRelativePath;
        } else {
            // Le basePath n'est pas un préfixe du absolutePath, retournez une chaîne vide ou gérez l'erreur
            System.err.println("Base path is not a prefix of the absolute path.");
            return "";
        }
    }
}

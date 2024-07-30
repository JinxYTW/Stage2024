package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import webserver.WebServerContext;
import webserver.WebServerResponse;
import dao.DevisDao;
import dao.UtilisateurDao;
import models.Devis;

public class DevisController {
    public DevisController() {
    }

    public void isOneDevisValidate(WebServerContext context){
        WebServerResponse response = context.getResponse();
        try {
            String demandeId = context.getRequest().getQueryParams().get("demandeId");
            int demandeIdInt = Integer.parseInt(demandeId);
    
            DevisDao devisDao = new DevisDao();
            boolean isOneDevisValidate = devisDao.isOneDevisValidate(demandeIdInt);
    
            JsonObject json = new JsonObject();
            json.addProperty("isOneDevisValidate", isOneDevisValidate);
            
            response.json(json);
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.serverError("Format incorrect pour le paramètre 'demandeId'");
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur serveur");
        }
    }

    public void validateDevis(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            // Lire le corps de la requête en tant que chaîne JSON
            String bodyAsString = context.getRequest().getBodyAsString();
            JsonObject requestBody = JsonParser.parseString(bodyAsString).getAsJsonObject();
            String pdfPath = requestBody.get("pdfPath").getAsString();

            // Valider le devis
            DevisDao devisDao = new DevisDao();
            String success = devisDao.validateDevis(pdfPath);

            // Envoyer la réponse
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", success);
            response.json(jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur lors de la validation du devis");
        }
    }

    public void getDevisPdfPath(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        Gson gson = new Gson();
        try {
            String demandeId = context.getRequest().getQueryParams().get("demandeId");
            int demandeIdInt = Integer.parseInt(demandeId);

            DevisDao devisDao = new DevisDao();
            List<String> pdfPaths = devisDao.getDevisPdfPath(demandeIdInt);

            // Convertir la liste des chemins en JSON
            JsonArray jsonArray = new JsonArray();
            for (String path : pdfPaths) {
                jsonArray.add(path);
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

    public void getDevisCount(WebServerContext context){
        WebServerResponse response = context.getResponse();
        try {
            String demandeId = context.getRequest().getQueryParams().get("demandeId");
            int demandeIdInt = Integer.parseInt(demandeId);
    
            DevisDao devisDao = new DevisDao();
            int devisCount = devisDao.getDevisCount(demandeIdInt);
    
            JsonObject json = new JsonObject();
            json.addProperty("devisCount", devisCount);
            
            
            response.json(json);
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.serverError("Format incorrect pour le paramètre 'demandeId'");
        } catch (Exception e) {
            e.printStackTrace();
            response.serverError("Erreur serveur");
        }
    }

    public void uploadDevis(WebServerContext context) {
        try {
            // Récupérer le fichier envoyé dans la requête
            InputStream fileInputStream = context.getRequest().getInputStream();
            String fileName = context.getRequest().getHeader("filename");
            int demandeId = Integer.parseInt(context.getRequest().getQueryParams().get("demandeId"));
            
            // Définir le répertoire où les fichiers seront stockés
            File directory = new File("back/src/pdf/Devis");
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
            DevisDao.saveDevisToDatabase(demandeId, relativePath);
    
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
            String customRelativePath = "/back/src/pdf/Devis" + relativePath;
    
            
            System.out.println("Custom Relative path: " + customRelativePath);
    
            return customRelativePath;
        } else {
            // Le basePath n'est pas un préfixe du absolutePath, retournez une chaîne vide ou gérez l'erreur
            System.err.println("Base path is not a prefix of the absolute path.");
            return "";
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
    
            int devisId = Integer.parseInt(idParam);
            
            DevisDao devisDao = new DevisDao();
            Devis devis = devisDao.findById(devisId);
            if (devis == null) {
                response.notFound("Devis non trouvé");
                return;
            }
    
            UtilisateurDao utilisateurDao = new UtilisateurDao();
            String demandeurName = utilisateurDao.getNames(devis.demande_id());
    
            String pdfPath = devisDao.generatePdf(devis, demandeurName);
            
            if (pdfPath == null) {
                response.serverError("Échec de la génération du PDF");
                return;
            }
    
            devisDao.savePdfPath(devisId, pdfPath);
    
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

package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.gson.JsonObject;

import webserver.WebServerContext;
import webserver.WebServerResponse;
import dao.DevisDao;
import dao.UtilisateurDao;
import models.Devis;

public class DevisController {
    public DevisController() {
    }

    public void uploadDevis(WebServerContext context) {
        try {
            // Récupérer le fichier envoyé dans la requête
            InputStream fileInputStream = context.getRequest().getInputStream();
            String fileName = context.getRequest().getHeader("filename"); // Vous devez envoyer le nom du fichier dans l'en-tête de la requête
            System.out.println("Nom du fichier : " + fileName);
            File file = new File("back/src/pdf/Devis/" + fileName);
            System.out.println("Chemin du fichier : " + file.getAbsolutePath());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            System.out.println("Début de la copie du fichier");

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Fin de la copie du fichier");

            fileInputStream.close();
            fileOutputStream.close();
            
            context.getResponse().ok("Fichier téléchargé avec succès");
        } catch (IOException e) {
            e.printStackTrace();
            context.getResponse().serverError("Erreur lors du téléchargement du fichier");
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

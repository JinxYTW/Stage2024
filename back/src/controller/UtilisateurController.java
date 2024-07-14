package controller;

import com.google.gson.Gson;
import dao.UtilisateurDao;
import models.Utilisateur;
import utils.HashUtil;
import webserver.WebServerContext;
import webserver.WebServerResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import io.jsonwebtoken.*;
import org.apache.commons.codec.binary.Base64;

public class UtilisateurController {
    private String secretKey;

    public UtilisateurController() {
        loadSecretKey();
    }

     private void loadSecretKey() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("back/config/application.properties")) {
            properties.load(input);
            secretKey = properties.getProperty("jwt.secret.key");
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'erreur de chargement du fichier properties
            // Vous pouvez choisir de logguer l'erreur ou de lancer une exception
        }
    }

    public void login(WebServerContext context) {
        WebServerResponse response = context.getResponse();
        try {
            System.out.println("Entering login method");

            // Extraction des données du corps de la requête
            String body = context.getRequest().getBodyAsString();
            System.out.println("Request body: " + body);

            // Désérialisation du JSON en objet Java
            Gson gson = new Gson();
            LoginRequest loginRequest = gson.fromJson(body, LoginRequest.class);

            if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
                throw new IllegalArgumentException("Missing username or password");
            }

            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            System.out.println("Username: " + username);
            System.out.println("Password: " + password);

            String hashedPassword = HashUtil.jinxHash(password);
            System.out.println("Hashed password: " + hashedPassword);

            UtilisateurDao utilisateurDao = new UtilisateurDao();
            Utilisateur utilisateur = utilisateurDao.findByUsernameAndPassword(username, hashedPassword);
            System.out.println("Utilisateur: " + utilisateur);

            if (utilisateur != null) {
                String jwt = createJWT(utilisateur.id());
                System.out.println("JWT: " + jwt);
                String jsonResponse = "{ \"status\": \"success\", \"message\": \"Login successful\", \"token\": \"" + jwt + "\", \"userId\": " + utilisateur.id() + " }";
                //String jsonResponse = "{ \"status\": \"success\", \"message\": \"Login successful\" }";
                response.json(jsonResponse);
                System.out.println("Login successful");
            } 
            else {
                String jsonResponse = "{ \"status\": \"fail\", \"message\": \"Invalid username or password\" }";
                response.json(jsonResponse);
                System.out.println("Invalid username or password");
            }
        } 
        catch (Exception e) {
            String jsonResponse = "{ \"status\": \"error\", \"message\": \"An error occurred: " + e.getMessage() + "\" }";
            response.json(jsonResponse);
            e.printStackTrace();
        }
    }

    private String createJWT(int userId) {
        System.out.println("Creating JWT for user ID: " + userId);
    
        long currentTimeMillis = System.currentTimeMillis();
        Date now = new Date(currentTimeMillis);
    
        long expirationTime = currentTimeMillis + 3600 * 1000; // 1 hour
        Date expiration = new Date(expirationTime);
    
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
    
        System.out.println("JWT claims: " + claims);
        System.out.println("JWT secret key: " + secretKey);
        System.out.println("JWT issued at: " + now);
        System.out.println("JWT expiration: " + expiration);
    
        String jwt;
        try {
            byte[] secretKeyBytes = Base64.decodeBase64(secretKey);
            jwt = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .signWith(SignatureAlgorithm.HS256, secretKeyBytes)
                    .compact();
    
            System.out.println("JWT created: " + jwt);
        } catch (Exception e) {
            System.err.println("Error creating JWT: " + e.getMessage());
            e.printStackTrace();
            jwt = null; // Ou une gestion d'erreur appropriée
        }
        
        return jwt;
    }

    private Claims decodeJWT(String jwt) {
        
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody();
        } catch (Exception e) {
            return null; // Invalid JWT or signature
        }
    }

    private boolean isValidJWT(String jwt) {
        Claims claims = decodeJWT(jwt);
        return claims != null && !claims.isEmpty();
    }

    // Classe interne pour représenter la requête de connexion
    private static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

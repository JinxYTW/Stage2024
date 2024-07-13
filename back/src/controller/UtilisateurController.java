package controller;

import com.google.gson.Gson;
import dao.UtilisateurDao;
import models.Utilisateur;
import utils.HashUtil;
import webserver.WebServerContext;
import webserver.WebServerResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class UtilisateurController {

    public UtilisateurController() {}

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
                //String jwt = createJWT(utilisateur.id());
                //String jsonResponse = "{ \"status\": \"success\", \"message\": \"Login successful\", \"token\": \"" + jwt + "\", \"userId\": " + utilisateur.id() + " }";
                String jsonResponse = "{ \"status\": \"success\", \"message\": \"Login successful\" }";
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
        String secretKey = System.getenv("JWT_SECRET_KEY");
        long currentTimeMillis = System.currentTimeMillis();
        Date now = new Date(currentTimeMillis);

        long expirationTime = currentTimeMillis + 3600 * 1000; // 1 hour
        Date expiration = new Date(expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        System.out.println("JWT secret key: " + secretKey);
        System.out.println("JWT issued at: " + now);
        System.out.println("JWT expiration: " + expiration);


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private Claims decodeJWT(String jwt) {
        String secretKey = System.getenv("JWT_SECRET_KEY");
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

package controller;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import webserver.WebServerContext;
import webserver.WebServerResponse;

import dao.UtilisateurDao;
import models.Utilisateur;
import utils.HashUtil;

public class UtilisateurController {

    public UtilisateurController() {
        
    }
    public void login(WebServerContext context) {
        System.out.println("Entering login method");
        JsonObject requestBody = context.getRequest().extractBody(JsonObject.class);
        String username = requestBody.get("username").getAsString();
        String password = requestBody.get("password").getAsString();
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        String hashedPassword = HashUtil.jinxHash(password);
        System.out.println("Hashed password: " + hashedPassword);

        UtilisateurDao utilisateurDao = new UtilisateurDao();
        Utilisateur utilisateur = utilisateurDao.findByUsernameAndPassword(username, hashedPassword);

        if (utilisateur != null) {
            String jwt = createJWT(utilisateur.id());
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("status", "success");
            responseJson.addProperty("message", "Login successful");
            responseJson.addProperty("token", jwt);
            responseJson.addProperty("userId", utilisateur.id());
            context.getResponse().json(responseJson);
            System.out.println("Login successful");
        } 
        else {
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("status", "fail");
            responseJson.addProperty("message", "Invalid username or password");
            context.getResponse().json(responseJson);
            System.out.println("Invalid username or password");
        }
    }

    private String createJWT(int userId) {
        String secretKey = System.getenv("JWT_SECRET_KEY");
        long currentTimeMillis = System.currentTimeMillis();
        Date now = new Date(currentTimeMillis);

        // Set expiration to 1 hour from now
        long expirationTime = currentTimeMillis + 3600 * 1000; // 1 hour
        Date expiration = new Date(expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

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
    
}

package models;

public record Utilisateur(
        int id,
        String nom,
        String prenom,
        String email,
        String motDePasse,
        String role) {
}

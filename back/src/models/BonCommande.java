package models;

import java.sql.Timestamp;

public record BonCommande(
    int id,
    int devis_id,
    int utilisateur_id,
    Etat etat,
    String fichier_pdf,
    Timestamp date_creation
) {
    public enum Etat {
        en_édition, à_valider, validé, envoyé, annulé, livré
    }
}

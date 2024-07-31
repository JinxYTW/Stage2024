package models;

import java.sql.Timestamp;

public record BonCommande(
    int id,
    int devis_id,
    Etat etat,
    String fichier_pdf,
    Timestamp date_creation,
    String nom_editeur
) {
    public enum Etat {
        en_édition, à_valider, validé, envoyé, annulé, livré
    }
}

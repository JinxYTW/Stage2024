package models;

import java.sql.Timestamp;

public record Demande(
    int id,
    int utilisateur_id,
    String projet_nom,
    String referant,
    String domaine,
    String typeof,
    String marque,
    String reference,
    String pour,
    String ou,
    String marche,
    String justification,
    String descriptif,
    String additional_details,
    int quantite,
    Urgence urgence,
    Etat etat,
    Timestamp date_demande,
    String pdfPath
) {
    public enum Urgence {
        basse, moyenne, haute
    }

    public enum Etat {
        envoyée, en_cours_de_traitement, annulée, finalisée
    }
}

package models;

import java.sql.Timestamp;

public record Devis(
    int id,
    int demande_id,
    int fournisseur_id,
    double montant,
    String fichier_pdf,
    Etat etat,
    Timestamp date_devis
) {
    public enum Etat {
        à_valider, validé, refusé
    }
}

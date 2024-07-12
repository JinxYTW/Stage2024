package models;

import java.sql.Timestamp;

public record Facture(
    int id,
    int bon_commande_id,
    double montant,
    String fichier_pdf,
    Etat etat,
    Timestamp date_facture
) {
    public enum Etat {
        à_valider, validée, refusée
    }
}

package models;

import java.sql.Timestamp;

public record Facture(
    int id,
    int bon_commande_id,
    String fichier_pdf,
    Etat etat,
    Timestamp date_facture,
    Timestamp date_livraison,
    String lieu_livraison,
    String nom_signataire,
    String nom_transitaire
) {
    public enum Etat {
        à_valider, validée, refusée
    }
}

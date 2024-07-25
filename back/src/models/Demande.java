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
        envoyée, en_cours_de_traitement,devis_a_valider,devis_en_cours_de_validation,bc_a_editer,bc_en_cours_dedition,bc_a_valider,bc_en_cours_de_validation,bc_valide_envoi_fournisseur,bc_envoye_attente_livraison,commande_annulee,commande_livree_finalisee
    }
}

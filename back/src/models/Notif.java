package models;

import java.sql.Timestamp;


public record Notif(
    int id,
    int demandeId,
    String message,
    Type type,
    boolean lu,
    Timestamp dateNotification
) {
    public enum Type {
        demande_envoyee,
        demande_en_cours_de_traitement,
        devis_a_valider,
        devis_en_cours_de_validation,
        bc_a_editer,
        bc_en_cours_dedition,
        bc_a_valider,
        bc_en_cours_de_validation,
        bc_valide_envoi_fournisseur,
        envoi_fournisseur_en_cours,
        bc_envoye_attente_livraison,
        facture_a_valider,
        commande_annulee,
        commande_livree_finalisee
    }

    
}

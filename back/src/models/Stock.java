package models;

public record Stock(
    int id,
    int bon_commande_id,
    String description,
    int quantite
) {}

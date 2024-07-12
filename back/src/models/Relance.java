package models;

import java.sql.Timestamp;

public record Relance(
    int id,
    int bon_commande_id,
    Timestamp date_relance,
    String message,
    boolean reponse
) {}

package models;

import java.sql.Timestamp;

public record Notif(
    int id,
    int utilisateur_id,
    String message,
    boolean lu,
    Timestamp date_notification
) {}

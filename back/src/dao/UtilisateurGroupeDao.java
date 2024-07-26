package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import database.SomethingDatabase;
import models.UtilisateurGroupe;

public class UtilisateurGroupeDao {

     public List<String> getGroupesNamesByUtilisateurId(int utilisateurId) {
        List<String> groupeNames = new ArrayList<>();
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();
            
            
            String query = "SELECT G.nom FROM Groupe G INNER JOIN UtilisateurGroupe UG ON G.id = UG.groupe_id WHERE UG.utilisateur_id = ?";
            
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, utilisateurId);
            ResultSet resultSet = statement.executeQuery();
            
            
            while (resultSet.next()) {
                groupeNames.add(resultSet.getString("nom"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupeNames;
    }

    public UtilisateurGroupe getUtilisateurGroupeById(int utilisateurId) {
        UtilisateurGroupe utilisateurGroupe = null;
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();
            String query = "SELECT groupe_id FROM UtilisateurGroupe WHERE utilisateur_id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, utilisateurId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                utilisateurGroupe = new UtilisateurGroupe(utilisateurId, resultSet.getInt("groupe_id"));

            }
        
    

        }
        catch (Exception e) {
            e.printStackTrace();
            
        }
        return utilisateurGroupe;
}
}

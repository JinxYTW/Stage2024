package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.SomethingDatabase;
import models.Groupe;

public class GroupeDao {

    public Groupe getGroupeById(int id) {
        Groupe groupe = null;
        try {
            SomethingDatabase myDatabase = new SomethingDatabase();
            String query = "SELECT nom FROM Groupe WHERE id = ?";
            PreparedStatement statement = myDatabase.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                groupe = new Groupe(id, resultSet.getString("nom"));
            }
        


    }
    catch (Exception e) {
        e.printStackTrace();
    }
    return groupe;
    }
    
    
}

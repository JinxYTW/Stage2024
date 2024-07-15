package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import database.SomethingDatabase;
import models.Utilisateur;

public class UtilisateurDao {
    public UtilisateurDao() {
    }

     public Utilisateur findByUsernameAndPassword(String username, String password) {
        try  {
            SomethingDatabase my_Database = new SomethingDatabase();

            String request = "SELECT * FROM Utilisateur WHERE username = ? AND mot_de_passe = ?";
            PreparedStatement statement = my_Database.prepareStatement(request);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Utilisateur(
                    resultSet.getInt("id"),
                    resultSet.getString("nom"),
                    resultSet.getString("prenom"),
                    resultSet.getString("email"),
                    resultSet.getString("mot_de_passe"),
                    resultSet.getString("role")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getNames (int id) {
        String res="";
        try {
            SomethingDatabase my_Database = new SomethingDatabase();

            String request = "SELECT nom, prenom FROM Utilisateur WHERE id = ?";
            PreparedStatement statement = my_Database.prepareStatement(request);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                res = resultSet.getString("nom") + " " + resultSet.getString("prenom");
                return res;
            }

        } catch (Exception e) {
            e.printStackTrace();
            
}
        return res;
        
    }

    public String getRole(int id) {
        String role = "";
        try {
            SomethingDatabase my_Database = new SomethingDatabase();

            String request = "SELECT role FROM Utilisateur WHERE id = ?";
            PreparedStatement statement = my_Database.prepareStatement(request);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                role = resultSet.getString("role");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return role;
    }
}

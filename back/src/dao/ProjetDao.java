package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import database.SomethingDatabase;
import models.Projet;

public class ProjetDao {
    public ProjetDao() {
    }

    public int getNonSpecifiedProjetId() throws Exception {
        SomethingDatabase myDatabase = new SomethingDatabase();

        // Vérifier si le projet "non spécifié" existe déjà dans la base de données
        String query = "SELECT id FROM Projet WHERE nom = 'Non spécifié'";
        PreparedStatement statement = myDatabase.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("id"); // Retourner l'ID du projet "non spécifié"
        } 

        // En cas d'erreur ou si l'insertion échoue, retourner -1 ou gérer l'exception selon la logique de votre application
        return -1;
    }

    // Méthode pour récupérer l'ID d'un projet par son nom
    public int getProjectId(String projectName) throws Exception {
        SomethingDatabase myDatabase = new SomethingDatabase();

        String query = "SELECT id FROM Projet WHERE nom = ?";
        PreparedStatement statement = myDatabase.prepareStatement(query);
        statement.setString(1, projectName);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("id"); // Retourner l'ID du projet correspondant
        }

        // Si aucun projet correspondant n'est trouvé, retourner -1 ou gérer l'exception selon la logique de votre application
        return -1;
    }
    
}

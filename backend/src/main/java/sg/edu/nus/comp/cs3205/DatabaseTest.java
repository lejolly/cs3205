package sg.edu.nus.comp.cs3205;

import com.google.gson.Gson;
import com.sun.tools.javac.jvm.ClassFile;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.get;
import static spark.Spark.port;

public class DatabaseTest {

    // adapted from: http://zetcode.com/db/postgresqljavatutorial/
    // for use with the sample pagila database:
    // http://pgfoundry.org/frs/?group_id=1000150&release_id=998#pagila-pagila-title-content
    public static void main(String[] args) {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String url = "";
        String user = "";
        String password = "";

        Gson gson = new Gson();
        try {
            DBCredentials dbCredentials = gson.fromJson(new FileReader(DatabaseTest.class.getClassLoader()
                    .getResource("db-credentials.json").getFile()), DBCredentials.class);
            url = "jdbc:postgresql://" + dbCredentials.getHost() + "/" + dbCredentials.getDbname();
            user = dbCredentials.getUsername();
            password = dbCredentials.getPassword();
        } catch (IOException e ) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT VERSION()");
            if (resultSet.next()) {
                Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
                lgr.log(Level.INFO, "PostgreSQL version: " + resultSet.getString(1));
            }

            port(8080);
            final Connection innerConnection = DriverManager.getConnection(url, user, password);
            get("/", (request, response) -> listActors(innerConnection));
            get("/actor/:actor", (request, response) -> getActor(innerConnection, request.params(":actor")));
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    private static String listActors(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM actor");
        Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
        lgr.log(Level.INFO, "Listing actors");
        Gson gson = new Gson();
        ArrayList<Actor> actors = new ArrayList<>();
        while (resultSet.next()) {
            actors.add(new Actor(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)));
        }
        return gson.toJson(actors);
    }

    private static String getActor(Connection connection, String actor_id) throws SQLException {
        int id = 0;
        try {
            id = Integer.parseInt(actor_id);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "Invalid actor id";
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM actor WHERE actor_id = " + id);
        Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
        lgr.log(Level.INFO, "Listing actor " + id);
        Gson gson = new Gson();
        if (resultSet.next()) {
            return gson.toJson(new Actor(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)));
        }
        return "No actors with that id found";
    }

    private class DBCredentials {

        private String host;
        private String dbname;
        private String username;
        private String password;

        String getHost() {
            return host;
        }

        String getDbname() {
            return dbname;
        }

        String getUsername() {
            return username;
        }

        String getPassword() {
            return password;
        }

    }

    private static class Actor {

        private int actor_id;
        private String first_name;
        private String last_name;

        Actor(int actor_id, String first_name, String last_name) {
            this.actor_id = actor_id;
            this.first_name = first_name;
            this.last_name = last_name;
        }

    }

}

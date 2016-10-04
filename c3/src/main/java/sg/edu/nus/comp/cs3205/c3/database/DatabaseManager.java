package sg.edu.nus.comp.cs3205.c3.database;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class DatabaseManager {

    private static Logger logger = LoggerFactory.getLogger(DatabaseManager.class.getSimpleName());
    private static Connection dbConnection = null;

    public DatabaseManager() {
        if (dbConnection == null) {
            logger.info("Initializing database.");
            initializeConnection();
            if (dbConnection != null) {
                logger.info("Database connection initialised.");
            } else {
                logger.error("Unable to initialise database connection.");
            }
        } else {
            logger.info("Database already initialized.");
        }
    }

    private void initializeConnection() {
        Optional<DBCredentials> credentials = readDBCredentialsFromFile();
        if (credentials.isPresent()) {
            try {
                String url = "jdbc:postgresql://" + credentials.get().getHost() + "/" + credentials.get().getDbname();
                String username = credentials.get().getUsername();
                String password = credentials.get().getPassword();
                dbConnection = DriverManager.getConnection(url, username, password);
                logger.info("Getting database version.");
                Statement statement = dbConnection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT VERSION()");
                if (resultSet.next()) {
                    logger.info(resultSet.getString(1));
                } else {
                    logger.warn("Unable to get database version.");
                }
            } catch (SQLException e) {
                logger.error("SQLException: ", e);
            }
        } else {
            logger.error("Unable to get database credentials.");
        }
    }

    private Optional<DBCredentials> readDBCredentialsFromFile() {
        logger.info("Reading database credentials from file.");
        Gson gson = new Gson();
        try {
            return Optional.of(gson.fromJson(new FileReader(DatabaseManager.class.getClassLoader()
                    .getResource("db-credentials.json").getFile()), DBCredentials.class));
        } catch (IOException e ) {
            logger.error("IOException: ", e);
            return Optional.empty();
        }
    }

}

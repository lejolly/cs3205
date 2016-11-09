package sg.edu.nus.comp.cs3205.c3.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.config.DBCredentialsConfig;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.sql.*;
import java.util.Optional;

public class C3DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(C3DatabaseManager.class);
    public static Connection dbConnection = null;

    public C3DatabaseManager() {
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
        Optional<DBCredentialsConfig> credentials = readDBCredentialsFromFile();
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

    private Optional<DBCredentialsConfig> readDBCredentialsFromFile() {
        logger.info("Reading database credentials from file.");
        return JsonUtils.readJsonFile("config/db-credentials.json", DBCredentialsConfig.class);
    }

    public Connection getDbConnection() {
        return dbConnection;
    }

}

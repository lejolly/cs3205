package sg.edu.nus.comp.cs3205.c3.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.core.AbstractManager;
import sg.edu.nus.comp.cs3205.common.data.config.DBCredentialsConfig;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class C3DatabaseManager extends AbstractManager {

    private static final Logger logger = LoggerFactory.getLogger(C3DatabaseManager.class);
    private static Connection dbConnection = null;

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

    public static int getActorCount() {
        try {
            logger.info("Getting number of actors");
            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM actor");
            if (resultSet.next()) {
                logger.info("Number of actors: " + resultSet.getInt(1));
                return resultSet.getInt(1);
            } else {
                logger.warn("Unable to get number of actors.");
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return 0;
    }

    public static String getActorInfo(int id) {
        try {
            logger.info("Getting info of actor id: " + id);
            PreparedStatement preparedStatement =
                    dbConnection.prepareStatement("SELECT *  FROM actor WHERE actor_id=?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                logger.info("Info of actor id " + id + ": " + resultSet.getString(2) + ", " + resultSet.getString(3)
                        + ", " + resultSet.getString(4));
                Map<String, String> map = new LinkedHashMap<>();
                map.put("id", resultSet.getString(1));
                map.put("first_name", resultSet.getString(2));
                map.put("last_name", resultSet.getString(3));
                map.put("last_update", resultSet.getString(4));
                return JsonUtils.toJsonString(map);
            } else {
                logger.warn("Unable to get number of actors.");
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return "";
    }

    public Connection getDbConnection() {
        return dbConnection;
    }

}

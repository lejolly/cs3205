package sg.edu.nus.comp.cs3205.c3.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class C3LoginQueries {

    private static final Logger logger = LoggerFactory.getLogger(C3LoginQueries.class);

    public static boolean doesUserExist(Connection dbConnection, String user) {
        try {
            logger.info("Checking for the existence of user: " + user);
            PreparedStatement preparedStatement =
                    dbConnection.prepareStatement("SELECT exists (SELECT 1 FROM users WHERE username = ? LIMIT 1)");
            preparedStatement.setString(1, user);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                logger.info(user + " exists: " + resultSet.getBoolean(1));
                return resultSet.getBoolean(1);
            } else {
                logger.warn("Unable to determine existence of user: " + user);
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return false;
    }

    public static String getUserSalt(Connection dbConnection, String user) {
        return getUserDetail(dbConnection, user, "salt");
    }

    public static String getUserHash(Connection dbConnection, String user) {
        return getUserDetail(dbConnection, user, "hash");
    }

    public static String getUserRole(Connection dbConnection, String user) {
        return getUserDetail(dbConnection, user, "role");
    }

    private static String getUserDetail(Connection dbConnection, String user, String column) {
        try {
            logger.info("Getting " + column + " for: " + user);
            PreparedStatement preparedStatement =
                    dbConnection.prepareStatement("SELECT " + column + " FROM users WHERE username = ? LIMIT 1");
            preparedStatement.setString(1, user);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                logger.info(user + "'s " + column + ": " + resultSet.getString(1));
                return resultSet.getString(1);
            } else {
                logger.warn("Unable to get " + column + " for: " + user);
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return null;
    }

}

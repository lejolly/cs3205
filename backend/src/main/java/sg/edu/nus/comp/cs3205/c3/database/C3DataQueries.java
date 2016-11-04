package sg.edu.nus.comp.cs3205.c3.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class C3DataQueries {

    private static final Logger logger = LoggerFactory.getLogger(C3DataQueries.class);

    public static String getItemName(Connection dbConnection, int id) {
        return getItemDetail(dbConnection, id, "name");
    }

    public static String getItemComment(Connection dbConnection, int id) {
        return getItemDetail(dbConnection, id, "comment");
    }

    public static int getItemQuantity(Connection dbConnection, int id) {
        try {
            return Integer.parseInt(getItemDetail(dbConnection, id, "quantity"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid item quantity returned.");
            return -1;
        }
    }

    private static String getItemDetail(Connection dbConnection, int id, String column) {
        try {
            logger.info("Getting " + column + " for item: " + id);
            PreparedStatement preparedStatement =
                    dbConnection.prepareStatement("SELECT " + column + " FROM users WHERE id = ? LIMIT 1");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                logger.info(id + "'s " + column + ": " + resultSet.getString(1));
                return resultSet.getString(1);
            } else {
                logger.warn("Unable to get " + column + " for: " + id);
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return null;
    }

}

package sg.edu.nus.comp.cs3205.c3.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.database.DataItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                    dbConnection.prepareStatement("SELECT " + column + " FROM data WHERE id = ? LIMIT 1");
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

    public static DataItem getDataObject(Connection dbConnection, int id) {
        try {
            logger.info("Getting item: " + id);
            PreparedStatement preparedStatement =
                    dbConnection.prepareStatement("SELECT * FROM data WHERE id = ? LIMIT 1");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString(2);
                int quantity = resultSet.getInt(3);
                String comment = resultSet.getString(4);
                logger.info(id + ": " + name + " quantity: " + quantity + " comment: " + comment);
                return new DataItem(id, name, quantity, comment);
            } else {
                logger.warn("Unable to get item: " + id);
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return null;
    }

    public static List<DataItem> getAllDataObjects(Connection dbConnection) {
        try {
            logger.info("Getting all items");
            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * from data");
            List<DataItem> items = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int quantity = resultSet.getInt(3);
                String comment = resultSet.getString(4);
                logger.info(id + ": " + name + " quantity: " + quantity + " comment: " + comment);
                items.add(new DataItem(id, name, quantity, comment));
            }
            if (items.size() > 0) {
                return items;
            } else {
                logger.warn("No items found");
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return null;
    }

    public static boolean updateItem(Connection dbConnection, DataItem item) {
        try {
            logger.info("Updating item: " + item.getName());
            PreparedStatement preparedStatement =
                    dbConnection.prepareStatement("UPDATE data SET name = ?, quantity = ?, comment = ? WHERE id = ?");
            preparedStatement.setString(1, item.getName());
            preparedStatement.setInt(2, item.getQuantity());
            preparedStatement.setString(3, item.getComment());
            preparedStatement.setInt(4, item.getId());
            preparedStatement.execute();
            logger.info("Updated: " + item.getId() + ": " + item.getName() +
                    " quantity: " + item.getQuantity() + " comment: " + item.getComment());
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return false;
    }

}

package sg.edu.nus.comp.cs3205.c3.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.database.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class C3ItemQueries {

    private static final Logger logger = LoggerFactory.getLogger(C3ItemQueries.class);

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
                    dbConnection.prepareStatement("SELECT " + column + " FROM items WHERE id = ? LIMIT 1");
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

    public static Item getItem(Connection dbConnection, int id) {
        try {
            logger.info("Getting item: " + id);
            PreparedStatement preparedStatement =
                    dbConnection.prepareStatement("SELECT * FROM items WHERE id = ? LIMIT 1");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString(2);
                int quantity = resultSet.getInt(3);
                String comment = resultSet.getString(4);
                logger.info(id + ": " + name + " quantity: " + quantity + " comment: " + comment);
                return new Item(id, name, quantity, comment);
            } else {
                logger.warn("Unable to get item: " + id);
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return null;
    }

    public static List<Item> getAllItems(Connection dbConnection) {
        try {
            logger.info("Getting all items");
            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * from items");
            List<Item> items = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int quantity = resultSet.getInt(3);
                String comment = resultSet.getString(4);
                logger.info(id + ": " + name + " quantity: " + quantity + " comment: " + comment);
                items.add(new Item(id, name, quantity, comment));
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

    public static boolean updateItem(Connection dbConnection, Item item) {
        try {
            logger.info("Updating item: " + item.getName());
            PreparedStatement preparedStatement =
                    dbConnection.prepareStatement("UPDATE items SET name = ?, quantity = ?, comment = ? WHERE id = ?");
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

    public static boolean addItem(Connection dbConnection, Item item) {
        // ignores id
        try {
            logger.info("Adding item: " + item.getName());
            PreparedStatement preparedStatement = dbConnection.prepareStatement("INSERT INTO " +
                    "items (name, quantity, comment) VALUES (?, ?, ?)");
            preparedStatement.setString(1, item.getName());
            preparedStatement.setInt(2, item.getQuantity());
            preparedStatement.setString(3, item.getComment());
            preparedStatement.execute();
            logger.info("Added: " + item.getName() + " quantity: " + item.getQuantity() +
                    " comment: " + item.getComment());
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return false;
    }

    public static boolean deleteItem(Connection dbConnection, int id) {
        try {
            logger.info("Deleting item: " + id);
            PreparedStatement preparedStatement = dbConnection.prepareStatement("DELETE FROM items WHERE id = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            logger.info("Deleted item: " + id);
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return false;
    }

}

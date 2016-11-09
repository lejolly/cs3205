package sg.edu.nus.comp.cs3205.c3.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.database.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class C3ItemQueries {

    private static final Logger logger = LoggerFactory.getLogger(C3ItemQueries.class);

    public static String getItemComment(String name) {
        return getItemDetail(name, "comment");
    }

    public static int getItemQuantity(String name) {
        try {
            return Integer.parseInt(getItemDetail(name, "quantity"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid item quantity returned.");
            return -1;
        }
    }

    private static String getItemDetail(String name, String column) {
        try {
            logger.info("Getting " + column + " for item: " + name);
            PreparedStatement preparedStatement =
                    C3DatabaseManager.dbConnection.prepareStatement("SELECT " + column + " FROM items " +
                            "WHERE name = ? LIMIT 1");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                logger.info(name + "'s " + column + ": " + resultSet.getString(1));
                return resultSet.getString(1);
            } else {
                logger.warn("Unable to get " + column + " for: " + name);
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return null;
    }

    public static Item getItem(String name) {
        try {
            logger.info("Getting item: " + name);
            PreparedStatement preparedStatement =
                    C3DatabaseManager.dbConnection.prepareStatement("SELECT * FROM items WHERE name = ? LIMIT 1");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                int quantity = resultSet.getInt(3);
                String comment = resultSet.getString(4);
                logger.info(id + ": " + name + " quantity: " + quantity + " comment: " + comment);
                return new Item(id, name, quantity, comment);
            } else {
                logger.warn("Unable to get item: " + name);
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return null;
    }

    public static List<Item> getAllItems() {
        try {
            logger.info("Getting all items");
            Statement statement = C3DatabaseManager.dbConnection.createStatement();
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

    public static boolean updateItem(Item item) {
        try {
            logger.info("Updating item: " + item.getName());
            PreparedStatement preparedStatement =
                    C3DatabaseManager.dbConnection.prepareStatement("UPDATE items SET name = ?, quantity = ?, " +
                            "comment = ? WHERE id = ?");
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

    public static boolean addItem(Item item) {
        // ignores id
        try {
            logger.info("Adding item: " + item.getName());
            PreparedStatement preparedStatement = C3DatabaseManager.dbConnection.prepareStatement("INSERT INTO " +
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

    public static boolean deleteItem(String name) {
        try {
            logger.info("Deleting item: " + name);
            PreparedStatement preparedStatement = C3DatabaseManager.dbConnection.prepareStatement(
                    "DELETE FROM items WHERE name = ?");
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            logger.info("Deleted item: " + name);
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return false;
    }

    public static boolean doesItemExist(String name) {
        try {
            logger.info("Checking for the existence of item: " + name);
            PreparedStatement preparedStatement =
                    C3DatabaseManager.dbConnection.prepareStatement("SELECT exists (SELECT 1 FROM items " +
                            "WHERE name = ? LIMIT 1)");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                logger.info(name + " exists: " + resultSet.getBoolean(1));
                return resultSet.getBoolean(1);
            } else {
                logger.warn("Unable to determine existence of item: " + name);
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return false;
    }

}

package sg.edu.nus.comp.cs3205.c3.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.database.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class C3UserQueries {

    private static final Logger logger = LoggerFactory.getLogger(C3UserQueries.class);

    public static boolean doesUserExist(String user) {
        try {
            logger.info("Checking for the existence of user: " + user);
            PreparedStatement preparedStatement =
                    C3DatabaseManager.dbConnection.prepareStatement("SELECT exists (SELECT 1 FROM users WHERE username = ? LIMIT 1)");
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

    public static String getUserSalt(String user) {
        return getUserDetail(user, "salt");
    }

    public static String getUserHash(String user) {
        return getUserDetail(user, "hash");
    }

    public static String getUserRole(String user) {
        return getUserDetail(user, "role");
    }

    public static String getUserOtpSeed(String user) {
        return getUserDetail(user, "otp_seed");
    }

    public static String getUserFullName(String user) {
        return getUserDetail(user, "full_name");
    }

    public static String getUserNumber(String user) {
        return getUserDetail(user, "number");
    }

    private static String getUserDetail(String user, String column) {
        try {
            logger.info("Getting " + column + " for: " + user);
            PreparedStatement preparedStatement =
                    C3DatabaseManager.dbConnection.prepareStatement("SELECT " + column + " FROM users WHERE username = ? LIMIT 1");
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

    public static List<User> getAllUsers() {
        try {
            logger.info("Getting all users");
            Statement statement = C3DatabaseManager.dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * from users");
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String username = resultSet.getString(2);
                String hash = resultSet.getString(3);
                String salt = resultSet.getString(4);
                String otp_seed = resultSet.getString(5);
                String role = resultSet.getString(6);
                String full_name = resultSet.getString(7);
                int number = resultSet.getInt(8);
                logger.info(id + ": " + username + " hash: " + hash + " salt: " + salt + " otp_seed: "
                        + otp_seed + " role: " + role + " full_name: " + full_name + " number: " + number);
                users.add(new User(id, username, hash, salt, otp_seed, role, full_name, number));
            }
            if (users.size() > 0) {
                return users;
            } else {
                logger.warn("No users found");
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return null;
    }

    public static User getUser(String user) {
        try {
            logger.info("Getting user: " + user);
            PreparedStatement preparedStatement =
                    C3DatabaseManager.dbConnection.prepareStatement("SELECT * FROM users WHERE username = ? LIMIT 1");
            preparedStatement.setString(1, user);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                String username = resultSet.getString(2);
                String hash = resultSet.getString(3);
                String salt = resultSet.getString(4);
                String otp_seed = resultSet.getString(5);
                String role = resultSet.getString(6);
                String full_name = resultSet.getString(7);
                int number = resultSet.getInt(8);
                logger.info(id + ": " + username + " hash: " + hash + " salt: " + salt + " otp_seed: "
                        + otp_seed + " role: " + role + " full_name: " + full_name + " number: " + number);
                return new User(id, username, hash, salt, otp_seed, role, full_name, number);
            } else {
                logger.warn("Unable to get user: " + user);
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return null;
    }

    public static boolean addUser(User user) {
        // ignores user id and role
        try {
            logger.info("Adding user: " + user.getUsername());
            PreparedStatement preparedStatement = C3DatabaseManager.dbConnection.prepareStatement("INSERT INTO " +
                    "users (username, hash, salt, otp_seed, role, full_name, number) VALUES (?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getHash());
            preparedStatement.setString(3, user.getSalt());
            preparedStatement.setString(4, user.getOtp_seed());
            preparedStatement.setString(5, user.getRole());
            preparedStatement.setString(6, user.getFull_name());
            preparedStatement.setInt(7, user.getNumber());
            preparedStatement.execute();
            logger.info("Added user: " + user.getUsername() + " hash: " + user.getHash() + " salt: "
                    + user.getSalt() + " otp_seed: " + user.getOtp_seed() + " role: " + user.getRole()
                    + " full_name: " + user.getFull_name() + " number: " + user.getNumber());
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return false;
    }

    public static boolean deleteUser(String user) {
        try {
            logger.info("Deleting user: " + user);
            PreparedStatement preparedStatement =
                    C3DatabaseManager.dbConnection.prepareStatement("DELETE FROM users WHERE username = ?");
            preparedStatement.setString(1, user);
            preparedStatement.execute();
            logger.info("Deleted user: " + user);
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return false;
    }

    // does not include changing password
    public static boolean updateUser(User user) {
        try {
            logger.info("Updating user: " + user.getUsername());
            PreparedStatement preparedStatement =
                    C3DatabaseManager.dbConnection.prepareStatement("UPDATE users SET full_name = ?, " +
                            "number = ? WHERE id = ?");
            preparedStatement.setString(1, user.getFull_name());
            preparedStatement.setInt(2, user.getNumber());
            preparedStatement.setInt(3, user.getId());
            preparedStatement.execute();
            logger.info("Updated: " + user.getId() + ": " + user.getUsername() +
                    " full_name: " + user.getFull_name() + " number: " + user.getNumber());
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return false;
    }

}

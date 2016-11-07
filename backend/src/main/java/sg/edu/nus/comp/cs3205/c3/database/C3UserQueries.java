package sg.edu.nus.comp.cs3205.c3.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.database.SanitizedUser;
import sg.edu.nus.comp.cs3205.common.data.database.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class C3UserQueries {

    private static final Logger logger = LoggerFactory.getLogger(C3UserQueries.class);

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

    public static String getUserOtpSeed(Connection dbConnection, String user) {
        return getUserDetail(dbConnection, user, "otp_seed");
    }

    public static String getUserFullName(Connection dbConnection, String user) {
        return getUserDetail(dbConnection, user, "full_name");
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

    public static List<User> getAllUsers(Connection dbConnection) {
        try {
            logger.info("Getting all users");
            Statement statement = dbConnection.createStatement();
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
                logger.info(id + ": " + username + " hash: " + hash + " salt: " + salt + " otp_seed: "
                        + otp_seed + " role: " + role + " full_name: " + full_name);
                users.add(new User(id, username, hash, salt, otp_seed, role, full_name));
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

    public static User getUser(Connection dbConnection, String user) {
        try {
            logger.info("Getting user: " + user);
            PreparedStatement preparedStatement =
                    dbConnection.prepareStatement("SELECT * FROM users WHERE username = ? LIMIT 1");
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
                logger.info(id + ": " + username + " hash: " + hash + " salt: " + salt + " otp_seed: "
                        + otp_seed + " role: " + role + " full_name: " + full_name);
                return new User(id, username, hash, salt, otp_seed, role, full_name);
            } else {
                logger.warn("Unable to get user: " + user);
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return null;
    }

    public static boolean addUser(Connection dbConnection, User user) {
        // ignores user id and role
        try {
            logger.info("Adding user: " + user.getUsername());
            PreparedStatement preparedStatement = dbConnection.prepareStatement("INSERT INTO " +
                    "users (username, hash, salt, otp_seed, role, full_name) VALUES (?, ?, ?, ?, 'user', ?)");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getHash());
            preparedStatement.setString(3, user.getSalt());
            preparedStatement.setString(4, user.getOtp_seed());
            preparedStatement.setString(5, user.getFull_name());
            preparedStatement.execute();
            logger.info("Added normal user: " + user.getUsername() + " hash: " + user.getHash() + " salt: "
                    + user.getSalt() + " otp_seed: " + user.getOtp_seed() + " full_name: " + user.getFull_name());
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return false;
    }

    public static boolean deleteUser(Connection dbConnection, String user) {
        try {
            logger.info("Deleting user: " + user);
            PreparedStatement preparedStatement =
                    dbConnection.prepareStatement("DELETE FROM users WHERE username = ?");
            preparedStatement.setString(1, user);
            preparedStatement.execute();
            logger.info("Deleted user: " + user);
            return true;
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
        }
        return false;
    }

    public static Map<String, String> getSanitizedUserMap(SanitizedUser sanitizedUser) {
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(sanitizedUser.getId()));
        map.put("username", sanitizedUser.getUsername());
        map.put("role", sanitizedUser.getRole());
        map.put("full_name", sanitizedUser.getFull_name());
        return map;
    }

}

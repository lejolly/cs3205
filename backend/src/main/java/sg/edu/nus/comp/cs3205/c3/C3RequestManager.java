package sg.edu.nus.comp.cs3205.c3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.database.C3DatabaseManager;
import sg.edu.nus.comp.cs3205.c3.database.C3ItemQueries;
import sg.edu.nus.comp.cs3205.c3.database.C3LoginManager;
import sg.edu.nus.comp.cs3205.c3.database.C3UserQueries;
import sg.edu.nus.comp.cs3205.c3.key.C3KeyManager;
import sg.edu.nus.comp.cs3205.c3.network.C3NetworkManager;
import sg.edu.nus.comp.cs3205.c3.network.C3ServerChannelHandler;
import sg.edu.nus.comp.cs3205.c3.session.C3SessionManager;
import sg.edu.nus.comp.cs3205.common.data.database.Item;
import sg.edu.nus.comp.cs3205.common.data.database.SanitizedUser;
import sg.edu.nus.comp.cs3205.common.data.database.User;
import sg.edu.nus.comp.cs3205.common.data.json.*;
import sg.edu.nus.comp.cs3205.common.sms.SMSManager;
import sg.edu.nus.comp.cs3205.common.utils.HashUtils;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// the main brains behind this thing
public class C3RequestManager {

    private static final Logger logger = LoggerFactory.getLogger(C3ServerChannelHandler.class);

    private SMSManager smsManager;
    public C3SessionManager c3SessionManager;
    private C3LoginManager c3LoginManager;
    private C3NetworkManager c3NetworkManager;

    public C3RequestManager() {
        // checks for keys + database connection
        if (C3KeyManager.c2RsaPublicKey == null ||
                C3KeyManager.c3RsaPrivateKey == null ||
                C3DatabaseManager.dbConnection == null) {
            logger.error("Keys or database connection not setup, exiting.");
        } else {
//            smsManager = new SMSManager();
            c3SessionManager = new C3SessionManager();
            c3LoginManager = new C3LoginManager(this);
            c3NetworkManager = new C3NetworkManager(this);
        }
    }

    public BaseJsonFormat handleRequestFromC2(BaseJsonFormat baseJsonFormat) throws NoSuchAlgorithmException {
        BaseJsonFormat response = null;
        BaseJsonFormat.JSON_FORMAT format = JsonUtils.getJsonFormat(baseJsonFormat);
        logger.info("Received " + format);
        if (format == BaseJsonFormat.JSON_FORMAT.SALT_REQUEST) {
            SaltRequest saltRequest = SaltRequest.fromBaseFormat(baseJsonFormat);
            if (saltRequest != null) {
                response = c3LoginManager.getUserSalt(saltRequest);
            }
        } else if (format == BaseJsonFormat.JSON_FORMAT.LOGIN_REQUEST) {
            LoginRequest loginRequest = LoginRequest.fromBaseFormat(baseJsonFormat);
            if (loginRequest != null) {
                response = c3LoginManager.getLoginResponse(loginRequest);
            }
        } else if (format == BaseJsonFormat.JSON_FORMAT.RETRIEVE_REQUEST) {
            RetrieveRequest retrieveRequest = RetrieveRequest.fromBaseFormat(baseJsonFormat);
            if (retrieveRequest != null) {
                //TODO: check for auth
                response = parseRetrieveRequest(retrieveRequest);
            }
        } else if (format == BaseJsonFormat.JSON_FORMAT.CREATE_REQUEST) {
            CreateRequest createRequest = CreateRequest.fromBaseFormat(baseJsonFormat);
            if (createRequest != null) {
                //TODO: check for auth
                response = parseCreateRequest(createRequest);
            }
        } else if (format == BaseJsonFormat.JSON_FORMAT.DELETE_REQUEST) {
            DeleteRequest deleteRequest = DeleteRequest.fromBaseFormat(baseJsonFormat);
            if (deleteRequest != null) {
                //TODO: check for auth
                response = parseDeleteRequest(deleteRequest);
            }
        } else if (format == BaseJsonFormat.JSON_FORMAT.UPDATE_REQUEST) {
            UpdateRequest updateRequest = UpdateRequest.fromBaseFormat(baseJsonFormat);
            if (updateRequest != null) {
                //TODO: check for auth
                response = parseUpdateRequest(updateRequest);
            }
        } else if (format == BaseJsonFormat.JSON_FORMAT.LOGOUT_REQUEST) {
            LogoutRequest logoutRequest = LogoutRequest.fromBaseFormat(baseJsonFormat);
            if (logoutRequest != null) {
                response = c3LoginManager.getLogoutResponse(logoutRequest);
            }
        }
        return response;
    }

    private RetrieveResponse parseRetrieveRequest(RetrieveRequest retrieveRequest) {
        RetrieveResponse retrieveResponse = new RetrieveResponse();
        if (retrieveRequest.getData().containsKey("table_id")) {
            if (retrieveRequest.getData().get("table_id").equals("users")) {
                // TODO: check that user has admin role
                logger.info("Received request for users table");
                List<Map<String, String>> sanitizedUsers = C3UserQueries.getAllUsers().stream()
                        .map(SanitizedUser::new).map(SanitizedUser::getSanitizedUserMap).collect(Collectors.toList());
                retrieveResponse.setRows(sanitizedUsers);
                return retrieveResponse;
            } else if (retrieveRequest.getData().get("table_id").equals("items")) {
                logger.info("Received request for items table");
                List<Map<String, String>> items = C3ItemQueries.getAllItems().stream()
                        .map(Item::getItemMap).collect(Collectors.toList());
                retrieveResponse.setRows(items);
                return retrieveResponse;
            }
        }
        retrieveResponse.setError("Invalid retrieve request. ");
        return retrieveResponse;
    }

    private CreateResponse parseCreateRequest(CreateRequest createRequest) {
        CreateResponse createResponse = new CreateResponse();
        if (createRequest.getData().containsKey("table_id")) {
            if (createRequest.getData().get("table_id").equals("users") &&
                    createRequest.getData().containsKey("username") &&
                    !C3UserQueries.doesUserExist(createRequest.getData().get("username"))) {
                // TODO: check that user has admin role
                logger.info("Received request to add new user");
                User user = new User(0, createRequest.getData().get("username"),
                        createRequest.getData().get("hash"),
                        createRequest.getData().get("salt"),
                        HashUtils.getNewOtpSeed(),
                        createRequest.getData().get("role"),
                        createRequest.getData().get("full_name"),
                        Integer.parseInt(createRequest.getData().get("number")));
                if (user.getRole().equals("user") || user.getRole().equals("admin")) {
                    if (C3UserQueries.addUser(user) && C3UserQueries.doesUserExist(user.getUsername())) {
                        SanitizedUser sanitizedUser = new SanitizedUser(C3UserQueries.getUser(user.getUsername()));
                        Map<String, String> map = new HashMap<>();
                        map.put("username", sanitizedUser.getUsername());
                        map.put("role", sanitizedUser.getRole());
                        createResponse.setData(map);
                        createResponse.setId("c3");
                        return createResponse;
                    }
                }
            } else if (createRequest.getData().get("table_id").equals("items") &&
                    createRequest.getData().containsKey("name") &&
                    !C3ItemQueries.doesItemExist(createRequest.getData().get("name"))) {
                logger.info("Received request to add new item");
                Item item = new Item(0, createRequest.getData().get("name"),
                        Integer.parseInt(createRequest.getData().get("quantity")),
                        createRequest.getData().get("comment"));
                if (C3ItemQueries.addItem(item) && C3ItemQueries.doesItemExist(item.getName())) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", item.getName());
                    createResponse.setData(map);
                    createResponse.setId("c3");
                    return createResponse;
                }
            }
        }
        createResponse.setError("Invalid create request. " +
                "Please check that username/item name does not already exist and that all fields have been properly" +
                "filled up. ");
        return createResponse;
    }

    private UpdateResponse parseUpdateRequest(UpdateRequest updateRequest) {
        UpdateResponse updateResponse = new UpdateResponse();
        if (updateRequest.getData().containsKey("table_id")) {
            if (updateRequest.getData().get("table_id").equals("users") &&
                    updateRequest.getData().containsKey("username") &&
                    C3UserQueries.doesUserExist(updateRequest.getData().get("username"))) {
                // TODO: check that user has proper permissions
                boolean success;
                String username = updateRequest.getData().get("username");
                if (updateRequest.getData().containsKey("hash")) {
                    logger.info("Received request to change user password");
                    User user = new User(0, updateRequest.getData().get("username"),
                            updateRequest.getData().get("hash"),
                            updateRequest.getData().get("salt"),
                            "", "", "", 0);
                    success = C3UserQueries.changeUserPassword(user);
                } else {
                    logger.info("Received request to update user");
                    User user = new User(0, updateRequest.getData().get("username"),
                            "", "", "", "", updateRequest.getData().get("full_name"),
                            Integer.parseInt(updateRequest.getData().get("number")));
                    success = C3UserQueries.updateUser(user);
                }
                if (success) {
                    Map<String, String> map = new HashMap<>();
                    map.put("username", username);
                    updateResponse.setData(map);
                    updateResponse.setId("c3");
                    return updateResponse;
                }
            } else if (updateRequest.getData().get("table_id").equals("items") &&
                    updateRequest.getData().containsKey("name") &&
                    C3ItemQueries.doesItemExist(updateRequest.getData().get("name"))) {
                logger.info("Received request to update item");
                Item item = new Item(0, updateRequest.getData().get("name"),
                        Integer.parseInt(updateRequest.getData().get("quantity")),
                        updateRequest.getData().get("comment"));
                if (C3ItemQueries.updateItem(item)) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", item.getName());
                    updateResponse.setData(map);
                    updateResponse.setId("c3");
                    return updateResponse;
                }
            }
        }
        updateResponse.setError("Invalid update request. Please check that user/item to update actually exists " +
                "and that all fields have been properly filled up. ");
        return updateResponse;
    }

    private DeleteResponse parseDeleteRequest(DeleteRequest deleteRequest) {
        DeleteResponse deleteResponse = new DeleteResponse();
        if (deleteRequest.getData().containsKey("table_id")) {
            if (deleteRequest.getData().get("table_id").equals("users") &&
                    deleteRequest.getData().containsKey("username") &&
                    C3UserQueries.doesUserExist(deleteRequest.getData().get("username"))) {
                // TODO: check that user is admin
                logger.info("Received request to delete user");
                if (C3UserQueries.deleteUser(deleteRequest.getData().get("username"))) {
                    Map<String, String> map = new HashMap<>();
                    map.put("username", deleteRequest.getData().get("username"));
                    deleteResponse.setData(map);
                    deleteResponse.setId("c3");
                    return deleteResponse;
                }
            } else if (deleteRequest.getData().get("table_id").equals("items") &&
                    deleteRequest.getData().containsKey("name") &&
                    C3ItemQueries.doesItemExist(deleteRequest.getData().get("name"))) {
                // TODO: check that user is admin
                logger.info("Received request to delete item");
                if (C3ItemQueries.deleteItem(deleteRequest.getData().get("name"))) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", deleteRequest.getData().get("name"));
                    deleteResponse.setData(map);
                    deleteResponse.setId("c3");
                    return deleteResponse;
                }
            }
        }
        deleteResponse.setError("Invalid delete request. Please check that user/item actually exists. ");
        return deleteResponse;
    }

}

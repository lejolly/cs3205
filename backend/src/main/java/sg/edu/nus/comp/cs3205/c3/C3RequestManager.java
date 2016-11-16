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
import sg.edu.nus.comp.cs3205.common.utils.TotpUtils;

import java.security.NoSuchAlgorithmException;
import java.util.*;
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
            smsManager = new SMSManager();
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
            if (checkNotNullAndIsLoggedIn(retrieveRequest)) {
                response = parseRetrieveRequest(retrieveRequest);
            }
        } else if (format == BaseJsonFormat.JSON_FORMAT.CREATE_REQUEST) {
            CreateRequest createRequest = CreateRequest.fromBaseFormat(baseJsonFormat);
            if (checkNotNullAndIsLoggedIn(createRequest)) {
                response = parseCreateRequest(createRequest);
            }
        } else if (format == BaseJsonFormat.JSON_FORMAT.DELETE_REQUEST) {
            DeleteRequest deleteRequest = DeleteRequest.fromBaseFormat(baseJsonFormat);
            if (checkNotNullAndIsLoggedIn(deleteRequest)) {
                response = parseDeleteRequest(deleteRequest);
            }
        } else if (format == BaseJsonFormat.JSON_FORMAT.UPDATE_REQUEST) {
            UpdateRequest updateRequest = UpdateRequest.fromBaseFormat(baseJsonFormat);
            if (checkNotNullAndIsLoggedIn(updateRequest)) {
                response = parseUpdateRequest(updateRequest);
            }
        } else if (format == BaseJsonFormat.JSON_FORMAT.LOGOUT_REQUEST) {
            LogoutRequest logoutRequest = LogoutRequest.fromBaseFormat(baseJsonFormat);
            if (logoutRequest != null) {
                response = c3LoginManager.getLogoutResponse(logoutRequest);
            }
        } else if (format == BaseJsonFormat.JSON_FORMAT.SMS_CHALLENGE) {
            SmsChallenge smsChallenge = SmsChallenge.fromBaseFormat(baseJsonFormat);
            if (checkNotNullAndIsLoggedIn(smsChallenge)) {
                response = parseSmsChallenge(smsChallenge);
            }
        }
        return response;
    }

    private boolean checkNotNullAndIsLoggedIn(BaseJsonFormat baseJsonFormat) {
        try {
            if (baseJsonFormat != null && baseJsonFormat.getData().containsKey("auth_token")
                    && c3SessionManager.isAuth_tokenInAuth_tokens(baseJsonFormat.getData().get("auth_token"))) {
                return true;
            } else {
                logger.warn("Invalid request or auth_token.");
            }
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }
        return false;
    }

    private SmsResult parseSmsChallenge(SmsChallenge smsChallenge) {
        SmsResult smsResult = new SmsResult();
        String challenge = smsChallenge.getData().get("challenge");
        String username = smsChallenge.getData().get("username");
        String action = smsChallenge.getData().get("action");
        String authUsername = c3SessionManager.getUsernameFromAuth_token(smsChallenge.getData().get("auth_token"));
        if (c3SessionManager.isInSms_tokens(challenge) && authUsername != null &&
                C3UserQueries.getUserRole(authUsername).equals("admin")) {
            BaseJsonFormat baseJsonFormat = c3SessionManager.getActionFromSms_token(challenge);
            if (baseJsonFormat.getData().get("username").equals(username) &&
                    baseJsonFormat.getData().get("table_id").equals("users")) {
                if (baseJsonFormat.getAction().equals("create_request") &&
                        action.equals("add")) {
                    logger.info("Received request to add new user");
                    User user = new User(0, username,
                            baseJsonFormat.getData().get("hash"),
                            baseJsonFormat.getData().get("salt"),
                            HashUtils.getNewOtpSeed(),
                            baseJsonFormat.getData().get("role"),
                            baseJsonFormat.getData().get("full_name"),
                            Integer.parseInt(baseJsonFormat.getData().get("number")));
                    if (user.getRole().equals("user") || user.getRole().equals("admin")) {
                        if (C3UserQueries.addUser(user) && C3UserQueries.doesUserExist(user.getUsername())) {
                            SanitizedUser sanitizedUser = new SanitizedUser(C3UserQueries.getUser(user.getUsername()));
                            Map<String, String> map = new HashMap<>();
                            map.put("username", sanitizedUser.getUsername());
                            map.put("action", "add");
                            map.put("result", "true");
                            smsResult.setData(map);
                            smsResult.setId("c3");
                            c3SessionManager.removeSms_token(challenge);
                            return smsResult;
                        }
                    }
                } else if (baseJsonFormat.getAction().equals("delete_request") &&
                        action.equals("delete")) {
                    if (C3UserQueries.doesUserExist(username)) {
                        logger.info("Received request to delete user");
                        if (C3UserQueries.deleteUser(username)) {
                            Map<String, String> map = new HashMap<>();
                            map.put("username", username);
                            map.put("action", "delete");
                            map.put("result", "true");
                            smsResult.setData(map);
                            smsResult.setId("c3");
                            c3SessionManager.removeSms_token(challenge);
                            return smsResult;
                        }
                    }
                }
            }
        }
        c3SessionManager.removeUsernameFromSms_tokens(username);
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("action", action);
        map.put("result", "false");
        smsResult.setData(map);
        smsResult.setError("Invalid sms challenge.");
        return smsResult;
    }

    private RetrieveResponse parseRetrieveRequest(RetrieveRequest retrieveRequest) {
        RetrieveResponse retrieveResponse = new RetrieveResponse();
        if (retrieveRequest.getData().get("table_id").equals("users")) {
            String username = c3SessionManager.getUsernameFromAuth_token(
                    retrieveRequest.getData().get("auth_token"));
            if (retrieveRequest.getData().containsKey("record_id")) {
                int id = Integer.parseInt(retrieveRequest.getData().get("record_id"));
                SanitizedUser sanitizedUser = C3UserQueries.getSanitizedUserById(id);
                if (username != null && (username.equals(sanitizedUser.getUsername()) ||
                        C3UserQueries.getUserRole(username).equals("admin"))) {
                    logger.info("Received request for user: " + id);
                    List<Map<String, String>> sanitizedUsers = new ArrayList<>();
                    sanitizedUsers.add(SanitizedUser.getSanitizedUserMap(sanitizedUser));
                    retrieveResponse.setRows(sanitizedUsers);
                    return retrieveResponse;
                }
            } else {
                if (username != null && C3UserQueries.getUserRole(username).equals("admin")) {
                    logger.info("Received request for users table");
                    List<Map<String, String>> sanitizedUsers = C3UserQueries.getAllUsers().stream()
                            .map(SanitizedUser::new).map(SanitizedUser::getSanitizedUserMap)
                            .collect(Collectors.toList());
                    Collections.sort(sanitizedUsers, (a, b) ->
                            Integer.parseInt(a.get("id")) < Integer.parseInt(b.get("id")) ? -1 :
                                    Integer.parseInt(a.get("id")) == Integer.parseInt(b.get("id")) ? 0 : 1);
                    retrieveResponse.setRows(sanitizedUsers);
                    return retrieveResponse;
                }
            }
        } else if (retrieveRequest.getData().get("table_id").equals("items")) {
            if (retrieveRequest.getData().containsKey("record_id")) {
                int id = Integer.parseInt(retrieveRequest.getData().get("record_id"));
                logger.info("Received request for item: " + id);
                List<Map<String, String>> items = new ArrayList<>();
                items.add(Item.getItemMap(C3ItemQueries.getItemById(id)));
                retrieveResponse.setRows(items);
                return retrieveResponse;
            } else {
                logger.info("Received request for items table");
                List<Map<String, String>> items = C3ItemQueries.getAllItems().stream()
                        .map(Item::getItemMap).collect(Collectors.toList());
                Collections.sort(items, (a, b) ->
                        Integer.parseInt(a.get("id")) < Integer.parseInt(b.get("id")) ? -1 :
                                Integer.parseInt(a.get("id")) == Integer.parseInt(b.get("id")) ? 0 : 1);
                retrieveResponse.setRows(items);
                return retrieveResponse;
            }
        }
        retrieveResponse.setError("Invalid retrieve request. Check that you have the correct permissions. ");
        return retrieveResponse;
    }

    private CreateResponse parseCreateRequest(CreateRequest createRequest) throws NoSuchAlgorithmException {
        CreateResponse createResponse = new CreateResponse();
        if (createRequest.getData().get("table_id").equals("users")) {
            if (!C3UserQueries.doesUserExist(createRequest.getData().get("username"))) {
                String username = c3SessionManager.getUsernameFromAuth_token(createRequest.getData().get("auth_token"));
                if (username != null && C3UserQueries.getUserRole(username).equals("admin")) {
                    logger.info("Received request to add new user");
                    User user = new User(0, createRequest.getData().get("username"),
                            createRequest.getData().get("hash"),
                            createRequest.getData().get("salt"),
                            HashUtils.getNewOtpSeed(),
                            createRequest.getData().get("role"),
                            createRequest.getData().get("full_name"),
                            Integer.parseInt(createRequest.getData().get("number")));
                    if (user.getRole().equals("user") || user.getRole().equals("admin")) {
                        c3SessionManager.removeUsernameFromSms_tokens(user.getUsername());
                        String challenge = TotpUtils.getOTPS(HashUtils.getShaNonce()).get(0);
                        smsManager.sendSMS(C3UserQueries.getUserNumber(username), "Challenge: " + challenge);
                        c3SessionManager.addSms_token(challenge, createRequest);
                        Map<String, String> map = new HashMap<>();
                        map.put("username", user.getUsername());
                        map.put("role", user.getRole());
                        createResponse.setData(map);
                        createResponse.setId("c3");
                        return createResponse;
                    }
                }
            } else {
                createResponse.setError("Username already exists!");
                return createResponse;
            }
        } else if (createRequest.getData().get("table_id").equals("items")) {
            if (!C3ItemQueries.doesItemExist(createRequest.getData().get("name"))) {
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
            } else {
                createResponse.setError("Item name already exists!");
                return createResponse;
            }
        }
        createResponse.setError("Invalid create request. " +
                "Please check that username/item name does not already exist and that all fields have been properly" +
                "filled up. ");
        return createResponse;
    }

    private UpdateResponse parseUpdateRequest(UpdateRequest updateRequest) {
        UpdateResponse updateResponse = new UpdateResponse();
        if (updateRequest.getData().get("table_id").equals("users") &&
                C3UserQueries.doesUserExist(updateRequest.getData().get("username"))) {
            String authUserName = c3SessionManager.getUsernameFromAuth_token(
                    updateRequest.getData().get("auth_token"));
            String username = updateRequest.getData().get("username");
            boolean success = false;
            if (C3UserQueries.getUserRole(authUserName).equals("admin") ||
                    authUserName.equals(username)) {
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
            }
            if (success) {
                Map<String, String> map = new HashMap<>();
                map.put("username", username);
                updateResponse.setData(map);
                updateResponse.setId("c3");
                return updateResponse;
            }
        } else if (updateRequest.getData().get("table_id").equals("items") &&
                C3ItemQueries.doesItemExist(updateRequest.getData().get("name"))) {
            logger.info("Received request to update item");
            Item item = new Item(Integer.parseInt(updateRequest.getData().get("id")),
                    updateRequest.getData().get("name"),
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
        updateResponse.setError("Invalid update request. Please check that user/item to update actually exists " +
                "and that all fields have been properly filled up. If not, please check that you have the correct" +
                "permissions. ");
        return updateResponse;
    }

    private DeleteResponse parseDeleteRequest(DeleteRequest deleteRequest) throws NoSuchAlgorithmException {
        DeleteResponse deleteResponse = new DeleteResponse();
        String authUsername = c3SessionManager.getUsernameFromAuth_token(deleteRequest.getData().get("auth_token"));
        if (authUsername != null && C3UserQueries.getUserRole(authUsername).equals("admin") &&
                !authUsername.equals(deleteRequest.getData().get("username"))) {
            if (deleteRequest.getData().get("table_id").equals("users") &&
                    C3UserQueries.doesUserExist(deleteRequest.getData().get("username"))) {
                logger.info("Received request to delete user");
                c3SessionManager.removeUsernameFromSms_tokens(deleteRequest.getData().get("username"));
                String challenge = TotpUtils.getOTPS(HashUtils.getShaNonce()).get(0);
                smsManager.sendSMS(C3UserQueries.getUserNumber(authUsername), "Challenge: " + challenge);
                c3SessionManager.addSms_token(challenge, deleteRequest);
                Map<String, String> map = new HashMap<>();
                map.put("username", deleteRequest.getData().get("username"));
                deleteResponse.setData(map);
                deleteResponse.setId("c3");
                return deleteResponse;
            } else if (deleteRequest.getData().get("table_id").equals("items") &&
                    C3ItemQueries.doesItemExist(deleteRequest.getData().get("name"))) {
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

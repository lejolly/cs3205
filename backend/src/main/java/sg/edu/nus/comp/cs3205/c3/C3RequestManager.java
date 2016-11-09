package sg.edu.nus.comp.cs3205.c3;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.database.C3DatabaseManager;
import sg.edu.nus.comp.cs3205.c3.database.C3ItemQueries;
import sg.edu.nus.comp.cs3205.c3.database.C3UserManager;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// the main brains behind this thing
public class C3RequestManager {

    private static final Logger logger = LoggerFactory.getLogger(C3ServerChannelHandler.class);

    private SMSManager smsManager;
    public C3SessionManager c3SessionManager;
    private C3UserManager c3UserManager;
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
            c3UserManager = new C3UserManager(this);
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
                response = c3UserManager.getUserSalt(saltRequest);
            }
        } else if (format == BaseJsonFormat.JSON_FORMAT.LOGIN_REQUEST) {
            LoginRequest loginRequest = LoginRequest.fromBaseFormat(baseJsonFormat);
            if (loginRequest != null) {
                response = c3UserManager.getLoginResponse(loginRequest);
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
        }
        return response;
    }

    private RetrieveResponse parseRetrieveRequest(RetrieveRequest retrieveRequest) {
        Gson gson = new Gson();
        RetrieveResponse retrieveResponse = new RetrieveResponse();
        if (retrieveRequest.getData().containsKey("table_id")) {
            if (retrieveRequest.getData().get("table_id").equals("users")) {
                // TODO: check that user has admin role
                logger.info("Received request for users table");
                List<Map<String, String>> sanitizedUsers = C3UserQueries.getAllUsers().stream()
                        .map(SanitizedUser::new).map(SanitizedUser::getSanitizedUserMap).collect(Collectors.toList());
                retrieveResponse.setRows(sanitizedUsers);
            } else if (retrieveRequest.getData().get("table_id").equals("items")) {
                logger.info("Received request for items table");
                List<Map<String, String>> items = C3ItemQueries.getAllItems().stream()
                        .map(Item::getItemMap).collect(Collectors.toList());
                retrieveResponse.setRows(items);
            }
        }
        return retrieveResponse;
    }

    private CreateResponse parseCreateRequest(CreateRequest createRequest) {
        CreateResponse createResponse = new CreateResponse();
        if (createRequest.getData().containsKey("table_id")) {
            if (createRequest.getData().get("table_id").equals("users")) {
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
                    C3UserQueries.addUser(user);
                }
            }
        }
        return createResponse;
    }

}

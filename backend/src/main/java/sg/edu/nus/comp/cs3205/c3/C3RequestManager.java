package sg.edu.nus.comp.cs3205.c3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.database.C3DatabaseManager;
import sg.edu.nus.comp.cs3205.c3.database.C3ItemManager;
import sg.edu.nus.comp.cs3205.c3.database.C3UserManager;
import sg.edu.nus.comp.cs3205.c3.key.C3KeyManager;
import sg.edu.nus.comp.cs3205.c3.network.C3NetworkManager;
import sg.edu.nus.comp.cs3205.c3.network.C3ServerChannelHandler;
import sg.edu.nus.comp.cs3205.c3.session.C3SessionManager;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;
import sg.edu.nus.comp.cs3205.common.data.json.LoginRequest;
import sg.edu.nus.comp.cs3205.common.data.json.RetrieveRequest;
import sg.edu.nus.comp.cs3205.common.data.json.SaltRequest;
import sg.edu.nus.comp.cs3205.common.sms.SMSManager;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.security.NoSuchAlgorithmException;

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
                response = C3ItemManager.parseRetrieveRequest(retrieveRequest);
            }
        }
        return response;
    }

}

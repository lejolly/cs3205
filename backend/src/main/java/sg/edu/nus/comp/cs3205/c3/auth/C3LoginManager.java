package sg.edu.nus.comp.cs3205.c3.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.session.C3SessionManager;
import sg.edu.nus.comp.cs3205.common.core.AbstractManager;
import sg.edu.nus.comp.cs3205.common.data.json.LoginRequest;
import sg.edu.nus.comp.cs3205.common.data.json.LoginResponse;
import sg.edu.nus.comp.cs3205.common.data.json.SaltRequest;
import sg.edu.nus.comp.cs3205.common.data.json.SaltResponse;
import sg.edu.nus.comp.cs3205.common.utils.ByteUtils;
import sg.edu.nus.comp.cs3205.common.utils.HashUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class C3LoginManager extends AbstractManager {

    private static Logger logger = LoggerFactory.getLogger(C3LoginManager.class.getSimpleName());

    private C3SessionManager c3SessionManager;

    public C3LoginManager(C3SessionManager c3SessionManager) {
        this.c3SessionManager = c3SessionManager;
    }

    public SaltResponse getUserSalt(SaltRequest saltRequest) {
        if (saltRequest.getData().containsKey("username")
                && saltRequest.getData().get("username").equals(c3SessionManager.getTestUser())) {
            try {
                SaltResponse saltResponse = new SaltResponse();
                Map<String, String> map = new HashMap<>();
                map.put("username", c3SessionManager.getTestUser());
                map.put("salt", c3SessionManager.getTestSalt());
                String challenge = HashUtils.get32CharNonce();
                c3SessionManager.addChallenge(challenge);
                map.put("challenge", challenge);
                saltResponse.setData(map);
                saltResponse.setId("c3");
                return saltResponse;
            } catch (NoSuchAlgorithmException e) {
                logger.error("NoSuchAlgorithmException: ", e);
                return null;
            }
        }
        return null;
    }

    // http://openwall.info/wiki/people/solar/algorithms/challenge-response-authentication
    public LoginResponse getLoginResponse(LoginRequest loginRequest) throws NoSuchAlgorithmException {
        // check for challenge
        if (loginRequest.getData().containsKey("challenge")
                && c3SessionManager.isInChallenges(loginRequest.getData().get("challenge"))) {
            // check username and response
            if (loginRequest.getData().containsKey("username")
                    && loginRequest.getData().get("username").equals(c3SessionManager.getTestUser())
                    && loginRequest.getData().containsKey("response")
                    && loginRequest.getData().get("response").length() == 32) {
                byte[] array1 = HashUtils.getMD5Hash(
                        c3SessionManager.getTestPasswordHash() + loginRequest.getData().get("challenge")).getBytes();
                byte[] array2 = loginRequest.getData().get("response").getBytes();
                byte[] array3 = ByteUtils.xorByteArrays(array1, array2);
                if (Arrays.equals(array3, c3SessionManager.getTestPasswordHash().getBytes())) {
                    LoginResponse loginResponse = new LoginResponse();
                    String auth_token = HashUtils.get32CharNonce();
                    String username = loginRequest.getData().get("username");
                    if (c3SessionManager.isUsernameInAuth_tokens(username)) {
                        c3SessionManager.removeUsernameFromAuth_tokens(username);
                    }
                    c3SessionManager.addAuth_token(username, auth_token);
                    Map<String, String> map = new HashMap<>();
                    map.put("auth_token", auth_token);
                    loginResponse.setData(map);
                    loginResponse.setId("c3");
                    return loginResponse;
                }
            }
        }
        return null;
    }

}

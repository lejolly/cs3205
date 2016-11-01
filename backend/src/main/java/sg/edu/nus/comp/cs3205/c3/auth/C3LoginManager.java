package sg.edu.nus.comp.cs3205.c3.auth;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.session.C3SessionManager;
import sg.edu.nus.comp.cs3205.common.core.AbstractManager;
import sg.edu.nus.comp.cs3205.common.data.json.LoginRequest;
import sg.edu.nus.comp.cs3205.common.data.json.LoginResponse;
import sg.edu.nus.comp.cs3205.common.data.json.SaltRequest;
import sg.edu.nus.comp.cs3205.common.data.json.SaltResponse;
import sg.edu.nus.comp.cs3205.common.utils.HashUtils;
import sg.edu.nus.comp.cs3205.common.utils.XorUtils;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class C3LoginManager extends AbstractManager {

    private static Logger logger = LoggerFactory.getLogger(C3LoginManager.class.getSimpleName());

    private C3SessionManager c3SessionManager;
    private C3TotpManager c3TotpManager;

    public C3LoginManager(C3SessionManager c3SessionManager, C3TotpManager c3TotpManager) {
        this.c3SessionManager = c3SessionManager;
        this.c3TotpManager = c3TotpManager;
    }

    public SaltResponse getUserSalt(SaltRequest saltRequest) {
        try {
            SaltResponse saltResponse = new SaltResponse();
            Map<String, String> map = new HashMap<>();
            if (saltRequest.getData().containsKey("username")) {
                if (saltRequest.getData().get("username").equals(c3SessionManager.getTestUser())) {
                    map.put("username", c3SessionManager.getTestUser());
                    map.put("salt", c3SessionManager.getTestSalt());
                    String challenge = HashUtils.getShaNonce();
                    c3SessionManager.addChallenge(challenge);
                    map.put("challenge", challenge);
                    saltResponse.setData(map);
                    saltResponse.setId("c3");
                    return saltResponse;
                } else {
                    // just generate a random salt for invalid usernames
                    logger.warn("Invalid SaltRequest received.");
                    map.put("username", saltRequest.getData().get("username"));
                    map.put("salt", BCrypt.gensalt());
                    String challenge = HashUtils.getShaNonce();
                    c3SessionManager.addChallenge(challenge);
                    map.put("challenge", challenge);
                    saltResponse.setData(map);
                    saltResponse.setId("c3");
                    return saltResponse;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException: ", e);
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
                    && loginRequest.getData().get("response").length() == 80
                    && loginRequest.getData().containsKey("otp")
                    && c3TotpManager.checkOTP(loginRequest.getData().get("otp"))) {
                String challenge = loginRequest.getData().get("challenge");
                String hashPlusChallenge = HashUtils.getSha256HashFromString(
                        c3SessionManager.getTestPasswordHash() + challenge);
                String response = loginRequest.getData().get("response");
                String server = HashUtils.getSha256HashFromString(XorUtils.stringXOR(hashPlusChallenge, response));
                if (server.compareTo(c3SessionManager.getTestPasswordHash()) == 0) {
                    LoginResponse loginResponse = new LoginResponse();
                    String auth_token = HashUtils.getShaNonce();
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
        logger.warn("Invalid LoginRequest received.");
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setError("Incorrect credentials");
        return loginResponse;
    }

}

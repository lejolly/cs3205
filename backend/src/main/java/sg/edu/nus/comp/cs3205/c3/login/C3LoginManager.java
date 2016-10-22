package sg.edu.nus.comp.cs3205.c3.login;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.core.AbstractManager;
import sg.edu.nus.comp.cs3205.common.data.json.LoginRequest;
import sg.edu.nus.comp.cs3205.common.data.json.LoginResponse;
import sg.edu.nus.comp.cs3205.common.data.json.SaltRequest;
import sg.edu.nus.comp.cs3205.common.data.json.SaltResponse;
import sg.edu.nus.comp.cs3205.common.utils.HashUtils;

import java.security.NoSuchAlgorithmException;
import java.util.*;

public class C3LoginManager extends AbstractManager {

    private static Logger logger = LoggerFactory.getLogger(C3LoginManager.class.getSimpleName());

    private static final String TEST_USER = "user";
    private static final String TEST_PASS = "pass";
    private static final String TEST_SALT = "$2a$10$HfD19HLiOQPT1vhpgKYCFO"; // from BCrypt.gensalt()
    private String testPasswordHash;
    private Set<String> challenges;
    private Map<String, String> auth_tokens;

    public C3LoginManager() {
        challenges = new HashSet<>();
        try {
            testPasswordHash = HashUtils.getMD5Hash(BCrypt.hashpw(TEST_PASS, TEST_SALT));
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException: ", e);
        }
    }

    public SaltResponse getUserSalt(SaltRequest saltRequest) {
        if (saltRequest.getData().containsKey("username")
                && saltRequest.getData().get("username").equals(TEST_USER)) {
            try {
                SaltResponse saltResponse = new SaltResponse();
                Map<String, String> map = new HashMap<>();
                map.put("username", TEST_USER);
                map.put("salt", TEST_SALT);
                String challenge = HashUtils.get32CharNonce();
                challenges.add(challenge);
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
    public LoginResponse login(LoginRequest loginRequest) throws NoSuchAlgorithmException {
        // check for challenge
        if (loginRequest.getData().containsKey("challenge")
                && challenges.contains(loginRequest.getData().get("challenge"))) {
            // check username and response
            if (loginRequest.getData().containsKey("username")
                    && loginRequest.getData().get("username").equals(TEST_USER)
                    && loginRequest.getData().containsKey("response")
                    && loginRequest.getData().get("response").length() == 32) {
                // http://stackoverflow.com/questions/14243922/java-xor-over-two-arrays/14244006#14244006
                byte[] array1 = HashUtils.getMD5Hash(
                        testPasswordHash + loginRequest.getData().get("challenge")).getBytes();
                byte[] array2 = loginRequest.getData().get("response").getBytes();
                byte[] array3 = new byte[32];
                int i = 0;
                for (byte b : array1) {
                    array3[i] = (byte) (b ^ array2[i++]);
                }
                if (Arrays.equals(array3, testPasswordHash.getBytes())) {
                    LoginResponse loginResponse = new LoginResponse();
                    String auth_token = HashUtils.get32CharNonce();
                    String username = loginRequest.getData().get("username");
                    if (auth_tokens.containsKey(username)) {
                        auth_tokens.remove(username);
                    }
                    auth_tokens.put(username, auth_token);
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

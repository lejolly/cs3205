package sg.edu.nus.comp.cs3205.c3.session;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.core.AbstractManager;
import sg.edu.nus.comp.cs3205.common.utils.HashUtils;

import java.security.NoSuchAlgorithmException;
import java.util.*;

public class C3SessionManager extends AbstractManager {

    private static Logger logger = LoggerFactory.getLogger(C3SessionManager.class.getSimpleName());

    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    private static final String TEST_USER = "user";
    private static final String TEST_PASS = "pass";
    private static final String TEST_SALT = "$2a$10$HfD19HLiOQPT1vhpgKYCFO"; // from BCrypt.gensalt()

    private String testPasswordHash;
    private Set<String> challenges;
    private Map<String, String> auth_tokens;

    public C3SessionManager() {
        challenges = new HashSet<>();
        auth_tokens = new HashMap<>();
        try {
            String secret = ENCODER.encodeToString(BCrypt.hashpw(TEST_PASS, TEST_SALT).getBytes());
            testPasswordHash = HashUtils.getSha256HashFromString(secret);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException: ", e);
        }
    }

    public void addChallenge(String challenge) {
        challenges.add(challenge);
    }

    public boolean isInChallenges(String challenge) {
        return challenges.contains(challenge);
    }

    public void addAuth_token(String username, String auth_token) {
        auth_tokens.put(username, auth_token);
    }

    public boolean isUsernameInAuth_tokens(String username) {
        return auth_tokens.containsKey(username);
    }

    public void removeUsernameFromAuth_tokens(String username) {
        auth_tokens.remove(username);
    }

    public String getTestUser() {
        return TEST_USER;
    }

    public String getTestPass() {
        return TEST_PASS;
    }

    public String getTestSalt() {
        return TEST_SALT;
    }

    public String getTestPasswordHash() {
        return testPasswordHash;
    }

}

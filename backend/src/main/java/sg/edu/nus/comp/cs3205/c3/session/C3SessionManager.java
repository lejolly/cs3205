package sg.edu.nus.comp.cs3205.c3.session;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.core.AbstractManager;
import sg.edu.nus.comp.cs3205.common.utils.XorUtils;
import sg.edu.nus.comp.cs3205.common.utils.HashUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class C3SessionManager extends AbstractManager {

    private static Logger logger = LoggerFactory.getLogger(C3SessionManager.class.getSimpleName());

    private static final String TEST_USER = "user";
    private static final String TEST_PASS = "pass";
    private static final String TEST_SALT = "$2a$10$HfD19HLiOQPT1vhpgKYCFO"; // from BCrypt.gensalt()

    private String testPasswordHash;
    private Set<String> challenges;
    private Map<String, String> auth_tokens;

    public C3SessionManager() {
        challenges = new HashSet<>();
        try {
            String challenge = "437d9417244e7dbe497088c7678034bbc31ce9c2a148bce60234fe28de073d4f";
            System.out.println("challenge: " + challenge);
            String secret = Base64.getEncoder().encodeToString(BCrypt.hashpw(TEST_PASS, TEST_SALT).getBytes());
            System.out.println("secret: " + secret);
            testPasswordHash = HashUtils.getSha256HashFromString(secret);
            System.out.println("hash: " + testPasswordHash);
            String hashPlusChallenge = HashUtils.getSha256HashFromString(testPasswordHash + challenge);
            System.out.println("hash+challenge: " + hashPlusChallenge);
            String response = XorUtils.xorBase64ByteArrays(hashPlusChallenge.getBytes(), secret.getBytes());
            System.out.println("response: " + response);
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

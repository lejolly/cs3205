package sg.edu.nus.comp.cs3205.c3.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.core.AbstractManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class C3SessionManager extends AbstractManager {

    private static Logger logger = LoggerFactory.getLogger(C3SessionManager.class);

    private Set<String> challenges;
    private Map<String, String> auth_tokens;

    public C3SessionManager() {
        challenges = new HashSet<>();
        auth_tokens = new HashMap<>();
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

}

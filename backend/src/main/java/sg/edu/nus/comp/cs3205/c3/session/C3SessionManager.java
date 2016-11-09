package sg.edu.nus.comp.cs3205.c3.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class C3SessionManager {

    private static Logger logger = LoggerFactory.getLogger(C3SessionManager.class);

    private Set<String> challenges;
    // Map<username, auth_token)
    private Map<String, String> auth_tokens;

    public C3SessionManager() {
        challenges = new HashSet<>();
        auth_tokens = new HashMap<>();
    }

    public synchronized void addChallenge(String challenge) {
        challenges.add(challenge);
    }

    public synchronized boolean isInChallenges(String challenge) {
        boolean isInChallenges = challenges.contains(challenge);
        if (isInChallenges) {
            challenges.remove(challenge);
        }
        return isInChallenges;
    }

    public synchronized void addAuth_token(String username, String auth_token) {
        auth_tokens.put(username, auth_token);
    }

    public synchronized boolean isUsernameInAuth_tokens(String username) {
        return auth_tokens.containsKey(username);
    }

    public synchronized boolean isAuth_tokenInAuth_tokens(String auth_token) {
        return auth_tokens.containsValue(auth_token);
    }

    public synchronized void removeAuth_tokenFromAuth_tokens(String auth_token) {
        Set<String> usernames = auth_tokens.entrySet().stream()
                .filter(entry -> entry.getValue().equals(auth_token))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        if (usernames.size() == 1) {
            auth_tokens.remove(usernames.iterator().next());
        }
    }

    public synchronized void removeUsernameFromAuth_tokens(String username) {
        auth_tokens.remove(username);
    }

}

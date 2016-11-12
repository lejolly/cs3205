package sg.edu.nus.comp.cs3205.c3.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class C3SessionManager {

    private static Logger logger = LoggerFactory.getLogger(C3SessionManager.class);

    private Set<String> challenges;
    // Map<auth_token, username>
    private Map<String, String> auth_tokens;
    // Map<sms_token, Create/DeleteRequest>
    private Map<String, BaseJsonFormat> sms_tokens;

    public C3SessionManager() {
        challenges = new HashSet<>();
        auth_tokens = new HashMap<>();
    }

    public synchronized boolean isInSms_tokens(String sms_token) {
        return sms_tokens.containsKey(sms_token);
    }

    public synchronized BaseJsonFormat getActionFromSms_token(String sms_token) {
        return sms_tokens.get(sms_token);
    }

    public synchronized void removeSms_token(String sms_token) {
        sms_tokens.remove(sms_token);
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

    public synchronized void addAuth_token(String auth_token, String username) {
        auth_tokens.put(auth_token, username);
    }

    public synchronized boolean isUsernameInAuth_tokens(String username) {
        return auth_tokens.containsValue(username);
    }

    public synchronized boolean isAuth_tokenInAuth_tokens(String auth_token) {
        return auth_tokens.containsKey(auth_token);
    }

    public synchronized void removeAuth_tokenFromAuth_tokens(String auth_token) {
        auth_tokens.remove(auth_token);
    }

    public synchronized void removeUsernameFromAuth_tokens(String username) {
        Set<String> auth_token = auth_tokens.entrySet().stream()
                .filter(entry -> entry.getValue().equals(username))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        if (auth_token.size() == 1) {
            auth_tokens.remove(auth_token.iterator().next());
        }
    }

    public synchronized String getUsernameFromAuth_token(String auth_token) {
        return auth_tokens.get(auth_token);
    }

}

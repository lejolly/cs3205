package sg.edu.nus.comp.cs3205.common.data.database;

import java.util.HashMap;
import java.util.Map;

// User object without sensitive information
public class SanitizedUser {

    private final int id;
    private final String username;
    private final String otp_seed;
    private final String role;
    private final String full_name;
    private final int number;

    public SanitizedUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.otp_seed = user.getOtp_seed();
        this.role = user.getRole();
        this.full_name = user.getFull_name();
        this.number = user.getNumber();
    }

    public static Map<String, String> getSanitizedUserMap(SanitizedUser sanitizedUser) {
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(sanitizedUser.getId()));
        map.put("username", sanitizedUser.getUsername());
        map.put("otp_seed", sanitizedUser.getOtp_seed());
        map.put("role", sanitizedUser.getRole());
        map.put("full_name", sanitizedUser.getFull_name());
        map.put("number", String.valueOf(sanitizedUser.getNumber()));
        return map;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getFull_name() {
        return full_name;
    }

    public int getNumber() {
        return number;
    }

    public String getOtp_seed() {
        return otp_seed;
    }

}

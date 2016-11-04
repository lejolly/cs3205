package sg.edu.nus.comp.cs3205.common.data.database;

public class User {

    private final int id;
    private final String username;
    private final String hash;
    private final String salt;
    private final String otp_seed;
    private final String role;
    private final String full_name;

    public User(int id, String username, String hash, String salt, String otp_seed, String role, String full_name) {
        this.id = id;
        this.username = username;
        this.hash = hash;
        this.salt = salt;
        this.otp_seed = otp_seed;
        this.role = role;
        this.full_name = full_name;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getHash() {
        return hash;
    }

    public String getSalt() {
        return salt;
    }

    public String getOtp_seed() {
        return otp_seed;
    }

    public String getRole() {
        return role;
    }

    public String getFull_name() {
        return full_name;
    }

}

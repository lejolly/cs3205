package sg.edu.nus.comp.cs3205.common.data.database;

// User object without sensitive information
public class SanitizedUser {

    private final int id;
    private final String username;
    private final String role;
    private final String full_name;
    private final int number;

    public SanitizedUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.full_name = user.getFull_name();
        this.number = user.getNumber();
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

}

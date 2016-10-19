package sg.edu.nus.comp.cs3205.common.data.json;

import java.util.Map;

public class LoginResponse {

    private String action;
    private Map<String, String> data;
    private String error;
    private String id;
    private String input;

    public LoginResponse() {
        setAction("login_response");
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

}

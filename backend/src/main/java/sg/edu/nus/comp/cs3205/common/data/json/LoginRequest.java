package sg.edu.nus.comp.cs3205.common.data.json;

import java.util.Map;

public class LoginRequest {

    private String action;
    private Map<String, String> data;
    private String error;
    private String id;

    public String getAction() {
        return action;
    }

    public Map<String, String> getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public String getId() {
        return id;
    }

}

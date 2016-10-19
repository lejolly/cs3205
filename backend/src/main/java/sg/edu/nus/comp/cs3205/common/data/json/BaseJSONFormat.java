package sg.edu.nus.comp.cs3205.common.data.json;

import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class BaseJSONFormat {

    private String action;
    private Map<String, String> data;
    private String error;
    private String id;
    private String input;

    public BaseJSONFormat(String action, Map<String, String> data, String error, String id, String input) {
        this.action = action;
        this.data = data;
        this.error = error;
        this.id = id;
        this.input = input;
    }

    public BaseJSONFormat() {
        this.action = "";
        this.data = new HashMap<>();
        this.error = "";
        this.id = "";
        this.input = "";
    }

    public String getJsonString() {
        return JsonUtils.toJsonString(this);
    }

    // public static void setEverythingExceptAction(BaseJsonFormat baseJsonFormat, )

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

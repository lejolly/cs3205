package sg.edu.nus.comp.cs3205.common.data.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseJsonFormat {

    private static Logger logger = LoggerFactory.getLogger(BaseJsonFormat.class);

    public enum JSON_FORMAT {SALT_REQUEST, SALT_RESPONSE, LOGIN_REQUEST, LOGIN_RESPONSE, ERROR,
        RETRIEVE_REQUEST, RETRIEVE_RESPONSE, CREATE_REQUEST, CREATE_RESPONSE, UPDATE_REQUEST, UPDATE_RESPONSE,
        DELETE_REQUEST, DELETE_RESPONSE, LOGOUT_REQUEST, LOGOUT_RESPONSE}

    private String action;
    protected Map<String, String> data;
    private String error;
    private String id;
    private String input;
    private List<Map<String, String>> rows;

    public BaseJsonFormat(String action, Map<String, String> data, String error, String id, String input) {
        this.action = action;
        this.data = data;
        this.error = error;
        this.id = id;
        this.input = input;
        this.rows = new ArrayList<>();
    }

    public BaseJsonFormat() {
        this.action = "";
        this.data = new HashMap<>();
        this.error = "";
        this.id = "";
        this.input = "";
        this.rows = new ArrayList<>();
    }

    public String getJsonString() {
        return JsonUtils.toJsonString(this);
    }

    public static void setEverythingExceptAction(BaseJsonFormat source, BaseJsonFormat destination) {
        destination.setData(source.getData());
        destination.setError(source.getError());
        destination.setId(source.getId());
        destination.setInput(source.getInput());
        destination.setRows(source.getRows());
    }

    public static <T extends BaseJsonFormat> T fromBaseFormat(BaseJsonFormat source, T destination) {
        try {
            if (source.getAction().equals(destination.getAction())) {
                setEverythingExceptAction(source, destination);
                return destination;
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Exception: ", e);
            return null;
        }
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

    public List<Map<String, String>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, String>> rows) {
        this.rows = rows;
    }

}

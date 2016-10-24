package sg.edu.nus.comp.cs3205.common.data.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class BaseJsonFormat {

    private static Logger logger = LoggerFactory.getLogger(BaseJsonFormat.class.getSimpleName());

    private String action;
    private Map<String, String> data;
    private String error;
    private String id;
    private String input;

    public BaseJsonFormat(String action, Map<String, String> data, String error, String id, String input) {
        this.action = action;
        this.data = data;
        this.error = error;
        this.id = id;
        this.input = input;
    }

    public BaseJsonFormat() {
        this.action = "";
        this.data = new HashMap<>();
        this.error = "";
        this.id = "";
        this.input = "";
    }

    public String getJsonString() {
        return JsonUtils.toJsonString(this);
    }

    public static void setEverythingExceptAction(BaseJsonFormat source, BaseJsonFormat destination) {
        destination.setData(source.getData());
        destination.setError(source.getError());
        destination.setId(source.getId());
        destination.setInput(source.getInput());
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

}

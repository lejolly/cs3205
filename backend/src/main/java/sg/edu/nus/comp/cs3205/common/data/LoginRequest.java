package sg.edu.nus.comp.cs3205.common.data;

import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Request for authentication
 */
public class LoginRequest extends BaseUniversalPacketFormat {

    public LoginRequest(String username, String password, String csrf_token, String id) {
        super("login_request", "", "", id, "", "");
        Map<String, String> map = new LinkedHashMap<>();
        map.put("username", username);
        map.put("password", password);
        map.put("csrf_token", csrf_token);
        setData(JsonUtils.toJsonString(map));
    }

}

package sg.edu.nus.comp.cs3205.common.data;

import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Request for CSRF token (authenticated)
 */
public class CSRFAuthenticated extends BaseUniversalPacketFormat {

    public CSRFAuthenticated(String username, String auth_token, String id) {
        super("csrf_request_auth", "", "", id, "", "");
        Map<String, String> map = new LinkedHashMap<>();
        map.put("username", username);
        map.put("auth_token", auth_token);
        setData(JsonUtils.toJsonString(map));
    }

}

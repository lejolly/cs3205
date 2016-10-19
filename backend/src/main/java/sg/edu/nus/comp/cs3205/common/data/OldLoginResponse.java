package sg.edu.nus.comp.cs3205.common.data;

import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Response for authentication
 */
public class OldLoginResponse extends BaseUniversalPacketFormat {

    public OldLoginResponse(String auth_token, String csrf_token, String id) {
        super("login_response", null, null, id, "");
        Map<String, String> map = new LinkedHashMap<>();
        map.put("auth_token", auth_token);
        map.put("csrf_token", csrf_token);
        setData(JsonUtils.toJsonString(map));
    }

}

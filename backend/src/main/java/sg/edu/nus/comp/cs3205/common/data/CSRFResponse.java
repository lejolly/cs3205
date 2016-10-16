package sg.edu.nus.comp.cs3205.common.data;

import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Response to CSRF token request
 */
public class CSRFResponse extends BaseUniversalPacketFormat {

    public CSRFResponse(String csrf_token, String id) {
        super("csrf_response", "", "", id, "", "");
        Map<String, String> map = new LinkedHashMap<>();
        map.put("csrf_token", csrf_token);
        setData(JsonUtils.toJsonString(map));
    }

}

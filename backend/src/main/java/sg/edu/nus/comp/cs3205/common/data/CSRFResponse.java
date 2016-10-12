package sg.edu.nus.comp.cs3205.common.data;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Response to CSRF token request
 */
public class CSRFResponse extends BaseUniversalPacketFormat {

    public CSRFResponse(String csrf_token, String id) {
        super("csrf_response", "", "", id, "", "");
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("csrf_token", csrf_token);
        setData(gson.toJson(map));
    }

}

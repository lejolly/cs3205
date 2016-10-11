package sg.edu.nus.comp.cs3205.c2.data;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Request for authentication
 */
public class LoginRequest extends BaseUniversalPacketFormat {

    public LoginRequest(String username, String password, String csrf_token, String id) {
        super("login_request", "", "", id, "", "");
        Gson gson = new Gson();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("username", username);
        map.put("password", password);
        map.put("csrf_token", csrf_token);
        setData(gson.toJson(map));
    }

}

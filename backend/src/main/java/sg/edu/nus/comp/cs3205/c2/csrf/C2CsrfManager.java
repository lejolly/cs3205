package sg.edu.nus.comp.cs3205.c2.csrf;

import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;
import sg.edu.nus.comp.cs3205.common.utils.HashUtils;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class C2CsrfManager {

    // Map<challenge/auth_token, csrf_token>
    private Map<String, String> csrfMap;

    public C2CsrfManager() {
        csrfMap = new HashMap<>();
    }

    // coming from C1
    public synchronized boolean checkCsrf(BaseJsonFormat baseJsonFormat) {
        if (JsonUtils.getJsonFormat(baseJsonFormat) == BaseJsonFormat.JSON_FORMAT.SALT_REQUEST) {
               return true;
        }
        if (baseJsonFormat.getData().containsKey("csrf_token") &&
                csrfMap.containsValue(baseJsonFormat.getData().get("csrf_token"))) {
            if (JsonUtils.getJsonFormat(baseJsonFormat) == BaseJsonFormat.JSON_FORMAT.LOGIN_REQUEST &&
                    baseJsonFormat.getData().containsKey("challenge") &&
                    csrfMap.containsKey(baseJsonFormat.getData().get("challenge"))) {
                csrfMap.remove(baseJsonFormat.getData().get("challenge"));
                return true;
            }
            if (baseJsonFormat.getData().containsKey("auth_token") &&
                    csrfMap.containsKey(baseJsonFormat.getData().get("auth_token"))) {
                csrfMap.remove(baseJsonFormat.getData().get("auth_token"));
                return true;
            }
        }
        return false;
    }

    // going to C1
    public synchronized BaseJsonFormat addCsrf(BaseJsonFormat baseJsonFormat) throws NoSuchAlgorithmException {
        String csrf = HashUtils.getShaNonce();
        if (JsonUtils.getJsonFormat(baseJsonFormat) == BaseJsonFormat.JSON_FORMAT.SALT_RESPONSE) {
            csrfMap.put(baseJsonFormat.getData().get("challenge"), csrf);
        } else {
            csrfMap.put(baseJsonFormat.getData().get("auth_token"), csrf);
        }
        Map<String, String> map = new HashMap<>(baseJsonFormat.getData());
        map.put("csrf_token", csrf);
        baseJsonFormat.setData(map);
        return baseJsonFormat;
    }

}

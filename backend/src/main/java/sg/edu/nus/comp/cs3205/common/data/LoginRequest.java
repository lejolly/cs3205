package sg.edu.nus.comp.cs3205.common.data;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Request for authentication
 */
public class LoginRequest extends BaseUniversalPacketFormat {

    public LoginRequest(String username, String password, String otp, String csrf_token, String id) {
        super("login_request", null, null, id, "");
        Map<String, String> map = new LinkedHashMap<>();
        map.put("username", username);
        map.put("password", password);
        map.put("otp", otp);
        map.put("csrf_token", csrf_token);
        setData(JsonUtils.toJsonString(map));
    }

    public LoginRequest(JwtClaims jwtClaims) {
        super("login_request", null, null, null, "");
        if (jwtClaims.hasClaim("data")) {
            setData((String) jwtClaims.getClaimsMap().get("data"));
        }
        if (jwtClaims.hasClaim("id")) {
            setId((String) jwtClaims.getClaimsMap().get("id"));
        }
    }

    public static LoginRequest parseJSON(String json) throws InvalidJwtException {
        JwtClaims jwtClaims = JwtClaims.parse(json);
        return new LoginRequest(jwtClaims);
    }

}

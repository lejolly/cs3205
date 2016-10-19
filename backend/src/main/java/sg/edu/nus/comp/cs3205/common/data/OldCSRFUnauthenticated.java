package sg.edu.nus.comp.cs3205.common.data;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;

/**
 * Request for CSRF token (unauthenticated)
 */
public class OldCSRFUnauthenticated extends BaseUniversalPacketFormat {

    public OldCSRFUnauthenticated(String id) {
        super("csrf_request", null, null, id, "");
    }

    public OldCSRFUnauthenticated(JwtClaims jwtClaims) {
        super("csrf_request", null, null, null, "");
        if (jwtClaims.hasClaim("id")) {
            setId((String) jwtClaims.getClaimsMap().get("id"));
        }
    }

    public static OldCSRFUnauthenticated parseJSON(String json) throws InvalidJwtException {
        JwtClaims jwtClaims = JwtClaims.parse(json);
        return new OldCSRFUnauthenticated(jwtClaims);
    }

}

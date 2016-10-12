package sg.edu.nus.comp.cs3205.common.data;

/**
 * Request for CSRF token (unauthenticated)
 */
public class CSRFUnauthenticated extends BaseUniversalPacketFormat {

    public CSRFUnauthenticated(String id) {
        super("csrf_request", "", "", id, "", "");
    }

}

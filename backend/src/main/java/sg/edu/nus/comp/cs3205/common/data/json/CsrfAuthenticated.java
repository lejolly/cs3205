package sg.edu.nus.comp.cs3205.common.data.json;

public class CsrfAuthenticated extends BaseJsonFormat {

    private static final String ACTION = "csrf_request_auth";

    public CsrfAuthenticated() {
        setAction(ACTION);
    }

    public static CsrfAuthenticated fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new CsrfAuthenticated());
    }

}

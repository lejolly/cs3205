package sg.edu.nus.comp.cs3205.common.data.json;

public class CsrfUnauthenticated extends BaseJsonFormat {

    private static final String ACTION = "csrf_request";

    public CsrfUnauthenticated() {
        setAction(ACTION);
    }

    public static CsrfUnauthenticated fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new CsrfUnauthenticated());
    }

}

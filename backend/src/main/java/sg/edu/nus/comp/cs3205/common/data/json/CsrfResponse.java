package sg.edu.nus.comp.cs3205.common.data.json;

public class CsrfResponse extends BaseJsonFormat {

    private static final String ACTION = "csrf_response";

    public CsrfResponse() {
        setAction(ACTION);
    }

    public static CsrfResponse fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new CsrfResponse());
    }

}

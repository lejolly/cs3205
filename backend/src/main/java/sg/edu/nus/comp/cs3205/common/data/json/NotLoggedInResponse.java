package sg.edu.nus.comp.cs3205.common.data.json;

public class NotLoggedInResponse extends BaseJsonFormat {

    private static final String ACTION = "not_logged_in_response";

    public NotLoggedInResponse() {
        setAction(ACTION);
    }

    public static NotLoggedInResponse fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new NotLoggedInResponse());
    }

}

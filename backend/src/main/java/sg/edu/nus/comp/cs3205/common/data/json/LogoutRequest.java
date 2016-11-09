package sg.edu.nus.comp.cs3205.common.data.json;

public class LogoutRequest extends BaseJsonFormat {

    private static final String ACTION = "logout_request";

    public LogoutRequest() {
        setAction(ACTION);
    }

    public static LogoutRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new LogoutRequest());
    }

}

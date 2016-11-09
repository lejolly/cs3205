package sg.edu.nus.comp.cs3205.common.data.json;

public class LogoutResponse extends BaseJsonFormat {

    private static final String ACTION = "logout_response";

    public LogoutResponse() {
        setAction(ACTION);
    }

    public static LogoutResponse fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new LogoutResponse());
    }

}

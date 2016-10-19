package sg.edu.nus.comp.cs3205.common.data.json;

public class LoginRequest extends BaseJsonFormat {

    private static final String ACTION = "login_request";

    public LoginRequest() {
        setAction(ACTION);
    }

    public static LoginRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new LoginRequest());
    }

}

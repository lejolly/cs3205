package sg.edu.nus.comp.cs3205.common.data.json;

public class LoginResponse extends BaseJsonFormat {

    private static final String ACTION = "login_response";

    public LoginResponse() {
        setAction(ACTION);
    }

    public static LoginResponse fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new LoginResponse());
    }

}

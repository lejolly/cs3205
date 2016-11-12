package sg.edu.nus.comp.cs3205.common.data.json;

public class LoginRequest extends BaseJsonFormat {

    private static final String ACTION = "login_request";

    public LoginRequest() {
        setAction(ACTION);
    }

    public static LoginRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        if (baseJsonFormat.getData().containsKey("challenge") &&
                baseJsonFormat.getData().containsKey("username") &&
                baseJsonFormat.getData().containsKey("response") &&
                baseJsonFormat.getData().get("response").length() == 80 &&
                baseJsonFormat.getData().containsKey("otp")) {
            return fromBaseFormat(baseJsonFormat, new LoginRequest());
        }
        return null;
    }

}

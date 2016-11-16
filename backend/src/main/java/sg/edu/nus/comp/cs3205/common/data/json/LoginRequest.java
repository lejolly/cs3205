package sg.edu.nus.comp.cs3205.common.data.json;

import sg.edu.nus.comp.cs3205.common.utils.InputUtils;

public class LoginRequest extends BaseJsonFormat {

    private static final String ACTION = "login_request";

    public LoginRequest() {
        setAction(ACTION);
    }

    public static LoginRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        if (baseJsonFormat.getData().containsKey("challenge") &&
                InputUtils.noWhitespace(baseJsonFormat.getData().get("challenge")) &&
                baseJsonFormat.getData().containsKey("username") &&
                InputUtils.noWhitespace(baseJsonFormat.getData().get("username")) &&
                baseJsonFormat.getData().containsKey("response") &&
                baseJsonFormat.getData().get("response").length() == 80 &&
                InputUtils.forHashSaltOtpSeedResponse(baseJsonFormat.getData().get("response")) &&
                baseJsonFormat.getData().containsKey("otp") &&
                InputUtils.numbersOnly(baseJsonFormat.getData().get("otp"))) {
            return fromBaseFormat(baseJsonFormat, new LoginRequest());
        }
        return null;
    }

}

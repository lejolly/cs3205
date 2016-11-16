package sg.edu.nus.comp.cs3205.common.data.json;

import sg.edu.nus.comp.cs3205.common.utils.InputUtils;

public class LogoutRequest extends BaseJsonFormat {

    private static final String ACTION = "logout_request";

    public LogoutRequest() {
        setAction(ACTION);
    }

    public static LogoutRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        if (baseJsonFormat.getData().containsKey("auth_token") &&
                InputUtils.noWhitespace(baseJsonFormat.getData().get("auth_token"))) {
            return fromBaseFormat(baseJsonFormat, new LogoutRequest());
        }
        return null;
    }

}

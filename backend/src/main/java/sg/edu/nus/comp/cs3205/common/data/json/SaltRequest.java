package sg.edu.nus.comp.cs3205.common.data.json;

import sg.edu.nus.comp.cs3205.common.utils.InputUtils;

public class SaltRequest extends BaseJsonFormat {

    private static final String ACTION = "salt_request";

    public SaltRequest() {
        setAction(ACTION);
    }

    public static SaltRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        if (baseJsonFormat.getData().containsKey("username") &&
                InputUtils.noWhitespace(baseJsonFormat.getData().get("username"))) {
            return fromBaseFormat(baseJsonFormat, new SaltRequest());
        }
        return null;
    }

}

package sg.edu.nus.comp.cs3205.common.data.json;

import sg.edu.nus.comp.cs3205.common.utils.InputUtils;

public class SmsChallenge extends BaseJsonFormat {

    private static final String ACTION = "sms_challenge";

    public SmsChallenge() {
        setAction(ACTION);
    }

    public static SmsChallenge fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        if (baseJsonFormat.getData().containsKey("username") &&
                InputUtils.noWhitespace(baseJsonFormat.getData().get("username")) &&
                baseJsonFormat.getData().containsKey("action") &&
                InputUtils.noWhitespace(baseJsonFormat.getData().get("action")) &&
                baseJsonFormat.getData().containsKey("challenge") &&
                InputUtils.numbersOnly(baseJsonFormat.getData().get("challenge"))) {
            return fromBaseFormat(baseJsonFormat, new SmsChallenge());
        }
        return null;
    }

}

package sg.edu.nus.comp.cs3205.common.data.json;

public class SmsChallenge extends BaseJsonFormat {

    private static final String ACTION = "sms_challenge";

    public SmsChallenge() {
        setAction(ACTION);
    }

    public static SmsChallenge fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        if (baseJsonFormat.getData().containsKey("username") &&
                baseJsonFormat.getData().containsKey("action") &&
                baseJsonFormat.getData().containsKey("challenge")) {
            return fromBaseFormat(baseJsonFormat, new SmsChallenge());
        }
        return null;
    }

}

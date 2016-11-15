package sg.edu.nus.comp.cs3205.common.data.json;

public class SmsResult extends BaseJsonFormat {

    private static final String ACTION = "sms_result";

    public SmsResult() {
        setAction(ACTION);
    }

    public static SmsResult fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new SmsResult());
    }

}

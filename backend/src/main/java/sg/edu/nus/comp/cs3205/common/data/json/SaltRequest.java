package sg.edu.nus.comp.cs3205.common.data.json;

public class SaltRequest extends BaseJsonFormat {

    private static final String ACTION = "salt_request";

    public SaltRequest() {
        setAction(ACTION);
    }

    public static SaltRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new SaltRequest());
    }

}
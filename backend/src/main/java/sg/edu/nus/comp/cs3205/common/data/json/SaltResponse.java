package sg.edu.nus.comp.cs3205.common.data.json;

public class SaltResponse extends BaseJsonFormat {

    private static final String ACTION = "salt_response";

    public SaltResponse() {
        setAction(ACTION);
    }

    public static SaltResponse fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new SaltResponse());
    }

}

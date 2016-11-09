package sg.edu.nus.comp.cs3205.common.data.json;

public class UpdateRequest extends BaseJsonFormat {

    private static final String ACTION = "update_request";

    public UpdateRequest() {
        setAction(ACTION);
    }

    public static UpdateRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new UpdateRequest());
    }

}

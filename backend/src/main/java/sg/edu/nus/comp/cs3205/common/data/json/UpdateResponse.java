package sg.edu.nus.comp.cs3205.common.data.json;

public class UpdateResponse extends BaseJsonFormat {

    private static final String ACTION = "update_response";

    public UpdateResponse() {
        setAction(ACTION);
    }

    public static UpdateResponse fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new UpdateResponse());
    }

}

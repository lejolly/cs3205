package sg.edu.nus.comp.cs3205.common.data.json;

public class DeleteRequest extends BaseJsonFormat {

    private static final String ACTION = "delete_request";

    public DeleteRequest() {
        setAction(ACTION);
    }

    public static DeleteRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new DeleteRequest());
    }

}

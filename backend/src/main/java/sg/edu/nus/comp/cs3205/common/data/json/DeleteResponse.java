package sg.edu.nus.comp.cs3205.common.data.json;

public class DeleteResponse extends BaseJsonFormat {

    private static final String ACTION = "delete_response";

    public DeleteResponse() {
        setAction(ACTION);
    }

    public static DeleteResponse fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new DeleteResponse());
    }

}

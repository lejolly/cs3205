package sg.edu.nus.comp.cs3205.common.data.json;

public class DeleteRequest extends BaseJsonFormat {

    private static final String ACTION = "delete_request";

    public DeleteRequest() {
        setAction(ACTION);
    }

    public static DeleteRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        if (baseJsonFormat.getData().containsKey("table_id")) {
            if (baseJsonFormat.getData().get("table_id").equals("users")) {
                if (baseJsonFormat.getData().containsKey("username")) {
                    return fromBaseFormat(baseJsonFormat, new DeleteRequest());
                }
            } else if (baseJsonFormat.getData().get("table_id").equals("items")) {
                if (baseJsonFormat.getData().containsKey("name")) {
                    return fromBaseFormat(baseJsonFormat, new DeleteRequest());
                }
            }
        }
        return null;
    }

}

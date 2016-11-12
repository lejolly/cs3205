package sg.edu.nus.comp.cs3205.common.data.json;

public class CreateRequest extends BaseJsonFormat {

    private static final String ACTION = "create_request";

    public CreateRequest() {
        setAction(ACTION);
    }

    public static CreateRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        if (baseJsonFormat.getData().containsKey("table_id")) {
            if (baseJsonFormat.getData().get("table_id").equals("users")) {
                if (baseJsonFormat.getData().containsKey("username") &&
                        baseJsonFormat.getData().containsKey("hash") &&
                        baseJsonFormat.getData().containsKey("salt") &&
                        baseJsonFormat.getData().containsKey("role") &&
                        baseJsonFormat.getData().containsKey("full_name") &&
                        baseJsonFormat.getData().containsKey("number")) {
                    return fromBaseFormat(baseJsonFormat, new CreateRequest());
                }
            } else if (baseJsonFormat.getData().get("table_id").equals("items")) {
                if (baseJsonFormat.getData().containsKey("name") &&
                        baseJsonFormat.getData().containsKey("quantity") &&
                        baseJsonFormat.getData().containsKey("comment")) {
                    return fromBaseFormat(baseJsonFormat, new CreateRequest());
                }
            }
        }
        return null;
    }

}

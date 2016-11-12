package sg.edu.nus.comp.cs3205.common.data.json;

public class UpdateRequest extends BaseJsonFormat {

    private static final String ACTION = "update_request";

    public UpdateRequest() {
        setAction(ACTION);
    }

    public static UpdateRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        if (baseJsonFormat.getData().containsKey("table_id")) {
            if (baseJsonFormat.getData().get("table_id").equals("users")) {
                if (baseJsonFormat.getData().containsKey("username")) {
                    if (baseJsonFormat.getData().containsKey("hash") &&
                            baseJsonFormat.getData().containsKey("salt")) {
                        return fromBaseFormat(baseJsonFormat, new UpdateRequest());
                    } else if (baseJsonFormat.getData().containsKey("full_name") &&
                            baseJsonFormat.getData().containsKey("number")) {
                        return fromBaseFormat(baseJsonFormat, new UpdateRequest());
                    }
                }
            } else if (baseJsonFormat.getData().get("table_id").equals("items")) {
                if (baseJsonFormat.getData().containsKey("name") &&
                        baseJsonFormat.getData().containsKey("quantity") &&
                        baseJsonFormat.getData().containsKey("comment")) {
                    return fromBaseFormat(baseJsonFormat, new UpdateRequest());
                }
            }
        }
        return null;
    }

}

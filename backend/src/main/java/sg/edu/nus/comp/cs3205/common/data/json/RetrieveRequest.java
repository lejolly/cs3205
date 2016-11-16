package sg.edu.nus.comp.cs3205.common.data.json;

import sg.edu.nus.comp.cs3205.common.utils.InputUtils;

public class RetrieveRequest extends BaseJsonFormat {

    private static final String ACTION = "retrieve_request";

    public RetrieveRequest() {
        setAction(ACTION);
    }

    public static RetrieveRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        if (baseJsonFormat.getData().containsKey("table_id") &&
                InputUtils.noWhitespace(baseJsonFormat.getData().get("table_id"))) {
            if (baseJsonFormat.getData().get("table_id").equals("users") ||
                    baseJsonFormat.getData().get("table_id").equals("items")) {
                return fromBaseFormat(baseJsonFormat, new RetrieveRequest());
            }
        }
        return null;
    }
}

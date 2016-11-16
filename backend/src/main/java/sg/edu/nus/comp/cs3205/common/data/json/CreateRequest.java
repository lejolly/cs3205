package sg.edu.nus.comp.cs3205.common.data.json;

import sg.edu.nus.comp.cs3205.common.utils.InputUtils;

public class CreateRequest extends BaseJsonFormat {

    private static final String ACTION = "create_request";

    public CreateRequest() {
        setAction(ACTION);
    }

    public static CreateRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        if (baseJsonFormat.getData().containsKey("table_id") &&
                InputUtils.noWhitespace(baseJsonFormat.getData().get("table_id"))) {
            if (baseJsonFormat.getData().get("table_id").equals("users")) {
                if (baseJsonFormat.getData().containsKey("username") &&
                        InputUtils.noWhitespace(baseJsonFormat.getData().get("username")) &&
                        baseJsonFormat.getData().containsKey("hash") &&
                        InputUtils.forHashSaltOtpSeedResponse(baseJsonFormat.getData().get("hash")) &&
                        baseJsonFormat.getData().containsKey("salt") &&
                        InputUtils.forHashSaltOtpSeedResponse(baseJsonFormat.getData().get("salt")) &&
                        baseJsonFormat.getData().containsKey("role") &&
                        InputUtils.noWhitespace(baseJsonFormat.getData().get("role")) &&
                        baseJsonFormat.getData().containsKey("full_name") &&
                        InputUtils.withWhitespace(baseJsonFormat.getData().get("full_name")) &&
                        baseJsonFormat.getData().containsKey("number") &&
                        InputUtils.noWhitespace(baseJsonFormat.getData().get("number"))) {
                    return fromBaseFormat(baseJsonFormat, new CreateRequest());
                }
            } else if (baseJsonFormat.getData().get("table_id").equals("items")) {
                if (baseJsonFormat.getData().containsKey("name") &&
                        InputUtils.withWhitespace(baseJsonFormat.getData().get("name")) &&
                        baseJsonFormat.getData().containsKey("quantity") &&
                        InputUtils.numbersOnly(baseJsonFormat.getData().get("quantity")) &&
                        baseJsonFormat.getData().containsKey("comment") &&
                        InputUtils.withWhitespace(baseJsonFormat.getData().get("comment"))) {
                    return fromBaseFormat(baseJsonFormat, new CreateRequest());
                }
            }
        }
        return null;
    }

}

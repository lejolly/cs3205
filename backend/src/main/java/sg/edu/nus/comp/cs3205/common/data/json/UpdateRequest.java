package sg.edu.nus.comp.cs3205.common.data.json;

import sg.edu.nus.comp.cs3205.common.utils.InputUtils;

public class UpdateRequest extends BaseJsonFormat {

    private static final String ACTION = "update_request";

    public UpdateRequest() {
        setAction(ACTION);
    }

    public static UpdateRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        if (baseJsonFormat.getData().containsKey("table_id") &&
                InputUtils.noWhitespace(baseJsonFormat.getData().get("table_id"))) {
            if (baseJsonFormat.getData().get("table_id").equals("users")) {
                if (baseJsonFormat.getData().containsKey("username") &&
                        InputUtils.noWhitespace(baseJsonFormat.getData().get("username"))) {
                    if (baseJsonFormat.getData().containsKey("hash") &&
                            InputUtils.forHashSaltOtpSeedResponse(baseJsonFormat.getData().get("hash")) &&
                            baseJsonFormat.getData().containsKey("salt") &&
                            InputUtils.forHashSaltOtpSeedResponse(baseJsonFormat.getData().get("salt"))) {
                        return fromBaseFormat(baseJsonFormat, new UpdateRequest());
                    } else if (baseJsonFormat.getData().containsKey("full_name") &&
                            InputUtils.withWhitespace(baseJsonFormat.getData().get("full_name")) &&
                            baseJsonFormat.getData().containsKey("number") &&
                            InputUtils.numbersOnly(baseJsonFormat.getData().get("number"))) {
                        return fromBaseFormat(baseJsonFormat, new UpdateRequest());
                    }
                }
            } else if (baseJsonFormat.getData().get("table_id").equals("items")) {
                if (baseJsonFormat.getData().containsKey("id") &&
                        InputUtils.numbersOnly(baseJsonFormat.getData().get("id")) &&
                        baseJsonFormat.getData().containsKey("name") &&
                        InputUtils.withWhitespace(baseJsonFormat.getData().get("name")) &&
                        baseJsonFormat.getData().containsKey("quantity") &&
                        InputUtils.numbersOnly(baseJsonFormat.getData().get("quantity")) &&
                        baseJsonFormat.getData().containsKey("comment") &&
                        InputUtils.forComments(baseJsonFormat.getData().get("comment"))) {
                    return fromBaseFormat(baseJsonFormat, new UpdateRequest());
                }
            }
        }
        return null;
    }

}

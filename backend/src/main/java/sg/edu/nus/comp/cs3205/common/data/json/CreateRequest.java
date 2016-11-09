package sg.edu.nus.comp.cs3205.common.data.json;

public class CreateRequest extends BaseJsonFormat {

    private static final String ACTION = "create_request";

    public CreateRequest() {
        setAction(ACTION);
    }

    public static CreateRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new CreateRequest());
    }

}

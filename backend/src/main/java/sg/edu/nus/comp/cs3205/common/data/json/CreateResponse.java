package sg.edu.nus.comp.cs3205.common.data.json;

public class CreateResponse extends BaseJsonFormat {

    private static final String ACTION = "create_response";

    public CreateResponse() {
        setAction(ACTION);
    }

    public static CreateResponse fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new CreateResponse());
    }

}

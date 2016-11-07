package sg.edu.nus.comp.cs3205.common.data.json;

public class RetrieveRequest extends BaseJsonFormat {

    private static final String ACTION = "retrieve_request";

    public RetrieveRequest() {
        setAction(ACTION);
    }

    public static RetrieveRequest fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new RetrieveRequest());
    }
}

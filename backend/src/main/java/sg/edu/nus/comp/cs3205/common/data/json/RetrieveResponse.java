package sg.edu.nus.comp.cs3205.common.data.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class RetrieveResponse extends BaseJsonFormat {

    private static Logger logger = LoggerFactory.getLogger(RetrieveResponse.class);

    private static final String ACTION = "retrieve_response";

    public RetrieveResponse() {
        setAction(ACTION);
        data = new HashMap<>();
    }

    public static RetrieveResponse fromBaseFormat(BaseJsonFormat baseJsonFormat) {
        return fromBaseFormat(baseJsonFormat, new RetrieveResponse());
    }

}

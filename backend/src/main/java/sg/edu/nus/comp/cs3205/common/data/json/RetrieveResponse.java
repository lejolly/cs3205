package sg.edu.nus.comp.cs3205.common.data.json;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public void setData(Map<String, String> data) {
        // do nothing
    }

    public void setHeaders(List<String> headers) {
        Gson gson = new Gson();
        String headersString = gson.toJson(headers);
        System.out.println(headersString);
        data.put("headers", headersString);
    }

    public void setRows(List<String> rows) {
        Gson gson = new Gson();
        String rowsString = gson.toJson(rows);
        rowsString = rowsString.replace("\\\"", "\"");
        rowsString = rowsString.replace("\"{\"", "{\"");
        rowsString = rowsString.replace("\"}\"", "\"}");
        System.out.println(rowsString);
        data.put("rows", rowsString);
    }

}

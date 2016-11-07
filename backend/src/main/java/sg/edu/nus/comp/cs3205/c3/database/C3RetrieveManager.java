package sg.edu.nus.comp.cs3205.c3.database;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.database.SanitizedUser;
import sg.edu.nus.comp.cs3205.common.data.json.RetrieveRequest;
import sg.edu.nus.comp.cs3205.common.data.json.RetrieveResponse;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class C3RetrieveManager {

    private static final Logger logger = LoggerFactory.getLogger(C3RetrieveManager.class);

    public static RetrieveResponse parseRetrieveRequest(Connection dbConnection, RetrieveRequest retrieveRequest) {
        Gson gson = new Gson();
        RetrieveResponse retrieveResponse = new RetrieveResponse();
        if (retrieveRequest.getData().containsKey("table_id")) {
            if (retrieveRequest.getData().get("table_id").equals("users")) {
                logger.info("Received request for users table");
                List<String> headers = new ArrayList<>();
                headers.add("Id");
                headers.add("Username");
                headers.add("Role");
                headers.add("Full Name");
                retrieveResponse.setHeaders(headers);
                List<Map<String, String>> sanitizedUsers = C3UserQueries.getAllUsers(dbConnection).stream()
                        .map(SanitizedUser::new).map(C3UserQueries::getSanitizedUserMap).collect(Collectors.toList());
                retrieveResponse.setRows(sanitizedUsers);
            } else if (retrieveRequest.getData().get("table_id").equals("items")) {
                logger.info("Received request for items table");
                List<String> headers = new ArrayList<>();
                headers.add("Id");
                headers.add("Name");
                headers.add("Quantity");
                headers.add("Comment");
                retrieveResponse.setHeaders(headers);
                List<Map<String, String>> items = C3ItemQueries.getAllItems(dbConnection).stream()
                        .map(C3ItemQueries::getItemMap).collect(Collectors.toList());
                retrieveResponse.setRows(items);
            }
        }
        return retrieveResponse;
    }

}

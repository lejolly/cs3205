package sg.edu.nus.comp.cs3205;

import com.google.gson.Gson;

import static spark.Spark.get;
import static spark.Spark.port;

public class TestServer {

    public static void main(String[] args) {
        port(8080);

        get("/", (request, response) -> "Hello World!");

        get("/echo/:echo", (request, response) -> "Echo: " + request.params(":echo"));

        Gson gson = new Gson();
        get("/json/:json", (request, response) -> {
            String params = request.params(":json");
            TestObject testObject = new TestObject();
            testObject.setTestString(params);
            return gson.toJson(testObject);
        });
    }

}

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

    private static class TestObject {

        private String testString;
        private String testString1;
        private String testString2;
        private String testString3;
        private String testString4;

        void setTestString(String testString) {
            this.testString = testString;
            this.testString1 = testString + "1";
            this.testString2 = testString + "2";
            this.testString3 = testString + "3";
            this.testString4 = testString + "4";
        }

    }

}

package sg.edu.nus.comp.cs3205;

import static spark.Spark.*;

public class TestServer {

    public static void main(String[] args) {
        port(8080);
        get("/", (request, response) -> "Hello World!");
        get("/echo/:echo", (request, response) -> "Echo: " + request.params(":echo"));
    }

}

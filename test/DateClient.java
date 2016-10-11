import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class DateClient {
    public static void main(String[] args) throws IOException {
        if(args.length != 2) {
            System.out.println("Usage: DateClient serverAddress port");
            System.exit(1);
        }
        String serverAddress = args[0];
        int port = Integer.parseInt(args[1]);
        Socket s = new Socket(serverAddress, port);
        BufferedReader input =
            new BufferedReader(new InputStreamReader(s.getInputStream()));
        String answer = input.readLine();
        System.out.println(answer);
    }
}

package sg.edu.nus.comp.cs3205;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

public class SMSTest {

    private static final String USER_AGENT = "CS3205";
    private static final String TOKEN_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";
    private static final String SMS_CONTENT_TYPE = "application/json";
    private static final String REQUEST_TOKEN_BODY = "grant_type=client_credentials";

    private static final String TOKEN_API_URL = "https://apiserver.sent.ly/oauth/token";
    private static final String SEND_SMS_URL = "https://apiserver.sent.ly/api/outboundmessage";

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String to = "";
        int toNumber = 0;
        while (to.length() != 8 && toNumber <= 0) {
            System.out.print("Receiver's 8-digit number: ");
            try {
                to = br.readLine();
                toNumber = Integer.parseInt(to);
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        String message = "";
        while (message.length() < 1 || message.length() > 160) {
            System.out.print("Message: ");
            try {
                message = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();
        String bearerToken = "";
        try {
            APIKeys apiKeys = gson.fromJson(new FileReader(
                    SMSTest.class.getClassLoader().getResource("sms-api-keys.json").getFile()), APIKeys.class);
            // http://docs.sentlyweb.apiary.io/#introduction/issuing-authenticated-requests/step-1:-encode-consumer-key-and-secret
            bearerToken = URLEncoder.encode(apiKeys.getApiKey(), "UTF-8") + ":" +
                    URLEncoder.encode(apiKeys.getApiSecret(), "UTF-8");
            bearerToken = Base64.getEncoder().encodeToString(bearerToken.getBytes());
        } catch (IOException e ) {
            e.printStackTrace();
        }

        String response = "";
        try {
            response = getAccessToken(bearerToken);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AccessToken accessToken = gson.fromJson(response, AccessToken.class);

        System.out.println(accessToken.getAccessToken());
        System.out.println(accessToken.getTokenType());

        try {
            response = sendSMS(accessToken.getAccessToken(), "CS3205", "+65" + String.valueOf(toNumber), message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String sendSMS(String accessToken, String from, String to, String text)
            throws IOException {
        URL urlObject = new URL(SEND_SMS_URL);
        HttpsURLConnection connection = (HttpsURLConnection) urlObject.openConnection();

        //add reuqest header
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Content-Type", SMS_CONTENT_TYPE);
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        // Send post request
        connection.setDoOutput(true);
        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
        Gson gson = new Gson();
        SMS sms = new SMS(from, to, text);
        String jsonSMS = gson.toJson(sms);
        System.out.println(jsonSMS);
        dos.writeBytes(jsonSMS);
        dos.flush();
        dos.close();

        int responseCode = connection.getResponseCode();
        System.out.println("Sending 'POST' request to URL: " + urlObject.toString());
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
        return response.toString();
    }

    // adapted from
    // https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
    private static String getAccessToken(String bearerToken) throws IOException {
        URL urlObject = new URL(TOKEN_API_URL);
        HttpsURLConnection connection = (HttpsURLConnection) urlObject.openConnection();

        //add reuqest header
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Content-Type", TOKEN_CONTENT_TYPE);
        connection.setRequestProperty("Authorization", "Basic " + bearerToken);

        // Send post request
        connection.setDoOutput(true);
        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
        dos.writeBytes(REQUEST_TOKEN_BODY);
        dos.flush();
        dos.close();

        connection.connect();
        int responseCode = connection.getResponseCode();
        System.out.println("Sending 'POST' request to URL: " + urlObject.toString());
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
        return response.toString();
    }

    private class AccessToken {

        private String access_token;
        private String token_type;

        public String getAccessToken() {
            return access_token;
        }

        public String getTokenType() {
            return token_type;
        }

    }

    private static class SMS {

        private String from;
        private String to;
        private String text;

        SMS(String from, String to, String text) {
            this.from = from;
            this.to = to;
            this.text = text;
        }

    }

    private class APIKeys {

        private String apiKey;
        private String apiSecret;

        public String getApiKey() {
            return apiKey;
        }

        public String getApiSecret() {
            return apiSecret;
        }

    }

}

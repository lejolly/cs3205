package sg.edu.nus.comp.cs3205.c3.sms;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.SMSTest;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Optional;

public class SMSManager {

    private static final Logger logger = LoggerFactory.getLogger(SMSManager.class.getSimpleName());

    private static final String USER_AGENT = "CS3205";
    private static final String TOKEN_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";
    private static final String SMS_CONTENT_TYPE = "application/json";
    private static final String REQUEST_TOKEN_BODY = "grant_type=client_credentials";
    private static final String TOKEN_API_URL = "https://apiserver.sent.ly/oauth/token";
    private static final String SEND_SMS_URL = "https://apiserver.sent.ly/api/outboundmessage";

    private static SMSAccessToken accessToken = null;

    public SMSManager() {
        if (accessToken == null) {
            logger.info("Initializing SMSManager.");
            initializeConnection();
            if (accessToken != null) {
                logger.info("SMS API connection initialised.");
            } else {
                logger.error("Unable to initialise connection to SMS API.");
            }
        } else {
            logger.info("SMSManager already initialized.");
        }
    }

    private boolean isValidSMSInput(String to, String text) {
        try {
            int toNumber = Integer.parseInt(to);
            return !(to.length() != 8 && toNumber <= 0) && !(text.length() < 1 || text.length() > 160);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException: ", e);
        }
        return false;
    }

    public boolean sendSMS(String to, String text) {
        if (!isValidSMSInput(to, text)) {
            logger.error("Invalid SMS parameters.");
        } else if (accessToken == null) {
            logger.error("SMSManager has not been initialized, unable to send SMS.");
        } else {
            try {
                logger.info("Sending: \"" + text + "\" To: " + to);
                URL urlObject = new URL(SEND_SMS_URL);
                HttpsURLConnection connection = (HttpsURLConnection) urlObject.openConnection();

                //add reuqest header
                connection.setRequestMethod("POST");
                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setRequestProperty("Content-Type", SMS_CONTENT_TYPE);
                connection.setRequestProperty("Authorization", "Bearer " + accessToken.getAccessToken());

                // Send post request
                connection.setDoOutput(true);
                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                Gson gson = new Gson();
                SMSMessage sms = new SMSMessage(USER_AGENT, "+65" + to, text);
                dos.writeBytes(gson.toJson(sms));
                dos.flush();
                dos.close();

                int responseCode = connection.getResponseCode();
                logger.info("Sending 'POST' request to URL: " + urlObject.toString());
                logger.info("Response Code: " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if (response.toString().contains("\"error_code\":0")) {
                    logger.info("SMS sent.");
                    return true;
                } else {
                    logger.error("Response: " + response.toString());
                }
            } catch (IOException e) {
                logger.error("IOException: ", e);
            }
        }
        logger.error("Error sending SMS.");
        return false;
    }

    private void initializeConnection() {
        Optional<SMSAPIKeys> apiKeys = readSMSAPIKeysFromFile();
        if (apiKeys.isPresent()) {
            getAccessToken(apiKeys.get());
        } else {
            logger.error("Unable to get SMS API keys.");
        }
    }

    private Optional<SMSAPIKeys> readSMSAPIKeysFromFile() {
        logger.info("Reading SMS API keys from file.");
        Gson gson = new Gson();
        try {
            // rename "sms-api-keys.json.sample" to "sms-api-keys.json" in the resources folder
            // and fill in the relevant api key details
            return Optional.of(gson.fromJson(new FileReader(
                    SMSTest.class.getClassLoader().getResource("sms-api-keys.json").getFile()), SMSAPIKeys.class));
        } catch (IOException e ) {
            logger.error("IOException: ", e);
            return Optional.empty();
        }
    }

    private String getBearerToken(SMSAPIKeys apiKeys) {
        try {
            // http://docs.sentlyweb.apiary.io/#introduction/issuing-authenticated-requests/step-1:-encode-consumer-key-and-secret
            String bearerToken = URLEncoder.encode(apiKeys.getApiKey(), "UTF-8") + ":" +
                    URLEncoder.encode(apiKeys.getApiSecret(), "UTF-8");
            return Base64.getEncoder().encodeToString(bearerToken.getBytes());
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException: ", e);
            return "";
        }
    }

    private void getAccessToken(SMSAPIKeys apiKeys) {
        logger.info("Getting SMS access token.");
        String bearerToken = getBearerToken(apiKeys);
        if (!bearerToken.isEmpty()) {
            try {
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
                logger.info("Sending 'POST' request to URL: " + urlObject.toString());
                logger.info("Response Code: " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if (response.toString().contains("\"token_type\":\"bearer\"")) {
                    logger.info("Received access token.");
                    Gson gson = new Gson();
                    accessToken = gson.fromJson(response.toString(), SMSAccessToken.class);
                    return;
                }
            } catch (IOException e) {
                logger.error("IOException: ", e);
            } catch (JsonSyntaxException e) {
                logger.error("JsonSyntaxException: ", e);
            }
            logger.error("Unable to get access token.");
        } else {
            logger.error("Unable to get bearer token.");
        }
    }

}

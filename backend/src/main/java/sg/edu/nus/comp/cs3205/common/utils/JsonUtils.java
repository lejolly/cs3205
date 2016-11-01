package sg.edu.nus.comp.cs3205.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat.JSON_FORMAT;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.Optional;

public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class.getSimpleName());

    public static <T> Optional<T> readJsonFile(String filePath, Class<T> classOfObjectToDeserialize) {
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            if (file.exists()) {
                T jsonFile;
                try {
                    jsonFile = deserializeObjectFromJsonFile(file, classOfObjectToDeserialize);
                    return Optional.of(jsonFile);
                } catch (IOException e) {
                    logger.error("IOException: Error reading from jsonFile file " + file + ": " + e);
                }
            }
            logger.error("Json file "  + file + " not found");
        }
        logger.error("Invalid file path: "  + filePath);
        return Optional.empty();
    }

    public static <T> T deserializeObjectFromJsonFile(File jsonFile, Class<T> classOfObjectToDeserialize)
            throws IOException {
        Gson gson = new Gson();
        return gson.fromJson(FileUtils.readFromFile(jsonFile), classOfObjectToDeserialize);
    }

    public static <T> T fromJsonString(String json, Class<T> instanceClass) throws JsonSyntaxException {
        Gson gson = new Gson();
        return gson.fromJson(json, instanceClass);
    }

    public static <T> String toJsonString(T instance) {
        Gson gson = new Gson();
        return gson.toJson(instance);
    }

    public static BaseJsonFormat fromJsonString(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, BaseJsonFormat.class);
    }

    public static JSON_FORMAT getJsonFormat(BaseJsonFormat baseJsonFormat) {
        try {
            for (JSON_FORMAT format : JSON_FORMAT.values()) {
                if (baseJsonFormat.getAction().equals(format.toString().toLowerCase())) {
                    return format;
                }
            }
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }
        return JSON_FORMAT.ERROR;
    }

    public static boolean hasJsonFormat(BaseJsonFormat baseJsonFormat) {
        return getJsonFormat(baseJsonFormat) != JSON_FORMAT.ERROR;
    }

    public static BaseJsonFormat consumeSignedBaseJsonFormat(Key key, String jws) throws InvalidJwtException {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder().setVerificationKey(key).build();
        JwtClaims jwtClaims = jwtConsumer.processToClaims(jws);
        if (jwtClaims.hasClaim("message")) {
            return JsonUtils.fromJsonString((String) jwtClaims.getClaimsMap().get("message"));
        } else {
            return null;
        }
    }

    public static String getSignedBaseJsonFormat(Key key, BaseJsonFormat baseJsonFormat) throws JoseException {
        JsonWebSignature jws = new JsonWebSignature();
        JwtClaims jwtClaims = new JwtClaims();
        jws.setKey(key);
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);
        jwtClaims.setClaim("message", baseJsonFormat.getJsonString());
        jws.setPayload(jwtClaims.toJson());
        return jws.getCompactSerialization();
    }

}

package sg.edu.nus.comp.cs3205.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;

import java.io.File;
import java.io.IOException;
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

}

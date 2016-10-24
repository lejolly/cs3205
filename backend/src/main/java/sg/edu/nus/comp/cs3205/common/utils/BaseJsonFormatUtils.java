package sg.edu.nus.comp.cs3205.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;

public class BaseJsonFormatUtils {

    private static final Logger logger = LoggerFactory.getLogger(BaseJsonFormatUtils.class.getSimpleName());

    public enum JSON_FORMAT {SALT_REQUEST, SALT_RESPONSE, LOGIN_REQUEST, LOGIN_RESPONSE, ERROR}

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

}

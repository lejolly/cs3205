package sg.edu.nus.comp.cs3205.c3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.database.C3DatabaseManager;
import sg.edu.nus.comp.cs3205.c3.key.C3KeyManager;
import sg.edu.nus.comp.cs3205.common.utils.LogUtils;

public class StartC3 {

    private static final Logger logger = LoggerFactory.getLogger(StartC3.class);

    public static void main(String[] args) {
        LogUtils.configureLogger("c3-logback.xml");
        logger.info("Starting C3");
        // keys and database connection are static
        C3KeyManager c3KeyManager = new C3KeyManager();
        C3DatabaseManager c3DatabaseManager = new C3DatabaseManager();

        // this is not
        C3RequestManager c3RequestManager = new C3RequestManager();
    }

}

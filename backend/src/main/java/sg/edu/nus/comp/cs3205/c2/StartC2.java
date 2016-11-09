package sg.edu.nus.comp.cs3205.c2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c2.key.C2KeyManager;
import sg.edu.nus.comp.cs3205.c2.network.C2NetworkManager;
import sg.edu.nus.comp.cs3205.common.utils.LogUtils;

public class StartC2 {

    private static Logger logger = LoggerFactory.getLogger(StartC2.class);

    private static C2KeyManager c2KeyManager;
    private static C2NetworkManager c2NetworkManager;
    
    public static void main(String[] args) {
        LogUtils.configureLogger("c2-logback.xml");
        logger.info("Starting C2");
        c2KeyManager = new C2KeyManager();

        // check for keys
        if (C2KeyManager.c1RsaPublicKey == null ||
                C2KeyManager.c2RsaPrivateKey == null ||
                C2KeyManager.c3RsaPublicKey == null) {
            logger.error("Keys not setup, exiting.");
        } else {
            c2NetworkManager = new C2NetworkManager();
        }
    }

}

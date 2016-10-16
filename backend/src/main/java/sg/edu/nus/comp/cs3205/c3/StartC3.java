package sg.edu.nus.comp.cs3205.c3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.database.C3DatabaseManager;
import sg.edu.nus.comp.cs3205.c3.network.C3NetworkManager;
import sg.edu.nus.comp.cs3205.common.sms.SMSManager;

public class StartC3 {

    private static final Logger logger = LoggerFactory.getLogger(StartC3.class.getSimpleName());

    private static C3DatabaseManager c3DatabaseManager;
    private static SMSManager smsManager;
    private static C3NetworkManager c3NetworkManager;

    public static void main(String[] args) {
        logger.info("Starting C3");
        c3DatabaseManager = new C3DatabaseManager();
//        smsManager = new SMSManager();
        c3NetworkManager = new C3NetworkManager();
    }

}

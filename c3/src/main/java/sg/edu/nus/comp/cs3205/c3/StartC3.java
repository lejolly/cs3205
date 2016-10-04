package sg.edu.nus.comp.cs3205.c3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.database.DatabaseManager;
import sg.edu.nus.comp.cs3205.c3.sms.SMSManager;

public class StartC3 {

    private static Logger logger = LoggerFactory.getLogger(StartC3.class.getSimpleName());
    private static DatabaseManager databaseManager;
    private static SMSManager smsManager;

    public static void main(String[] args) {
        logger.info("Starting C3");
        databaseManager = new DatabaseManager();
        smsManager = new SMSManager();

    }

}

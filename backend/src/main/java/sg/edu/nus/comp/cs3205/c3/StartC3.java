package sg.edu.nus.comp.cs3205.c3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.auth.C3TotpManager;
import sg.edu.nus.comp.cs3205.c3.database.C3DatabaseManager;
import sg.edu.nus.comp.cs3205.c3.key.C3KeyManager;
import sg.edu.nus.comp.cs3205.c3.auth.C3LoginManager;
import sg.edu.nus.comp.cs3205.c3.network.C3NetworkManager;
import sg.edu.nus.comp.cs3205.c3.session.C3SessionManager;
import sg.edu.nus.comp.cs3205.common.sms.SMSManager;

public class StartC3 {

    private static final Logger logger = LoggerFactory.getLogger(StartC3.class.getSimpleName());

    private static C3KeyManager c3KeyManager;
    private static C3DatabaseManager c3DatabaseManager;
    private static SMSManager smsManager;
    private static C3SessionManager c3SessionManager;
    private static C3LoginManager c3LoginManager;
    private static C3NetworkManager c3NetworkManager;
    private static C3TotpManager c3TotpManager;

    public static void main(String[] args) {
        logger.info("Starting C3");
//        c3KeyManager = new C3KeyManager();
//        c3DatabaseManager = new C3DatabaseManager();
//        smsManager = new SMSManager();
        c3SessionManager = new C3SessionManager();
        c3TotpManager = new C3TotpManager();
        c3LoginManager = new C3LoginManager(c3SessionManager, c3TotpManager);
        c3NetworkManager = new C3NetworkManager(c3SessionManager, c3LoginManager);
    }

}

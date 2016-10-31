package sg.edu.nus.comp.cs3205.c3.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.core.AbstractManager;
import sg.edu.nus.comp.cs3205.common.utils.TotpUtils;

public class C3TotpManager extends AbstractManager {

    private static final Logger logger = LoggerFactory.getLogger(C3TotpManager.class.getSimpleName());

    private static final String KEY = "Test";

    public C3TotpManager() {

    }

    public boolean checkOTP(String otp) {
        return TotpUtils.getOTPS(KEY).contains(otp);
    }

}

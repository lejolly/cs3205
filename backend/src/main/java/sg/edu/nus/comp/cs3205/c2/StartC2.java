package sg.edu.nus.comp.cs3205.c2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c2.network.C2NetworkManager;

public class StartC2 {

    private static Logger logger = LoggerFactory.getLogger(StartC2.class.getSimpleName());
    
    private static C2NetworkManager c2NetworkManager;
    
    public static void main(String[] args) {
        logger.info("Starting C2");
        c2NetworkManager = new C2NetworkManager();
    }

}

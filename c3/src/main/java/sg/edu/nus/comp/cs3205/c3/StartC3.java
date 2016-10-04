package sg.edu.nus.comp.cs3205.c3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.database.DatabaseManager;

public class StartC3 {

    private static DatabaseManager databaseManager;

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(StartC3.class.getClass().getName());
        logger.info("Starting C3");
        databaseManager = new DatabaseManager();
    }

}

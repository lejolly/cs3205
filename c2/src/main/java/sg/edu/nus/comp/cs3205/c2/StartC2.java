package sg.edu.nus.comp.cs3205.c2;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c2.network.NetworkManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class StartC2 {

    private static Logger logger = LoggerFactory.getLogger(StartC2.class.getSimpleName());
    
    private static NetworkManager networkManager;
    
    public static void main(String[] args) {
        logger.info("Starting C2");
        networkManager = new NetworkManager();
    }

}

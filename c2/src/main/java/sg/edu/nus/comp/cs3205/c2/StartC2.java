package sg.edu.nus.comp.cs3205.c2;

import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sg.edu.nus.comp.cs3205.c2.network.NetworkManager;

public class StartC2 {
    private static Logger logger = LoggerFactory.getLogger(StartC2.class);
    
    private static NetworkManager networkManager;
    
    public static void main(String[] args) {
        logger.info("Starting C2");
        try {
			networkManager = new NetworkManager();
		} catch (SocketException e) {
			logger.error("Unable to bind socket");
			e.printStackTrace();
		}
    }

}

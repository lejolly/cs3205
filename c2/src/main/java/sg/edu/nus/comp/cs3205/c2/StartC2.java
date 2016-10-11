package sg.edu.nus.comp.cs3205.c2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sg.edu.nus.comp.cs3205.c2.network.NetworkManager;

public class StartC2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartC2.class);
    private static final String USAGE = "Usage: StartC2 <tcp/udp> <port>";
    
    public static void main(String[] args) {
        if(args.length != 2 || (!args[0].equals("tcp") && !args[0].equals("udp"))) {
            LOGGER.error(USAGE);
        } else {
            String type = args[0];
            try {
                int port = Integer.parseInt(args[1]);
                LOGGER.info("starting C2 " + type + " on port " + port);
                new NetworkManager(port);
            } catch (NumberFormatException e) {
                LOGGER.error("invalid port number");
                LOGGER.error(USAGE);
            }
        }
    }

}

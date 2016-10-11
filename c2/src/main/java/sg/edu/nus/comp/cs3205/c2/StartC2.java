package sg.edu.nus.comp.cs3205.c2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sg.edu.nus.comp.cs3205.c2.network.TcpClient;
import sg.edu.nus.comp.cs3205.c2.network.TcpServer;

public class StartC2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartC2.class);
    private static final String TCP = "tcp";
    private static final String UDP = "udp";
    private static final String CLIENT = "client";
    private static final String SERVER = "server";
    private static final String USAGE = "Usage: StartC2 <" + TCP + "/" + UDP + "> <" + CLIENT + "/" + SERVER
            + "> <port>";

    public static void main(String[] args) {
        if (args.length != 3 || (!args[0].equals(TCP) && !args[0].equals(UDP))
                || (!args[1].equals(CLIENT) && !args[1].equals(SERVER))) {
            LOGGER.error(USAGE);
        } else {
            String transport = args[0];
            String type = args[1];
            int port = Integer.parseInt(args[2]);
            switch (transport) {
            case TCP:
                switch (type) {
                case CLIENT:
                    new TcpClient(port);
                    break;
                case SERVER:
                    new TcpServer(port);
                    break;
                }
                break;
            case UDP:
                break;
            }
        }
    }

}

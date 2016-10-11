package sg.edu.nus.comp.cs3205.c2.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Key;

public class NetworkManager {

    private static Logger logger = LoggerFactory.getLogger(NetworkManager.class.getSimpleName());

    private static final int SERVER_PORT = 8081;
    private static final int CLIENT_PORT = 8080;
    private static final String HOST = "localhost";

    public Key key = null;
    private EventLoopGroup group;

    public NetworkManager() {
        logger.info("Initializing network manager.");
        group = new NioEventLoopGroup();
        startNetworkClient();
    }

    public void startNetworkClient() {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer(this));

            // Start the connection attempt.
            Channel ch = b.connect(HOST, CLIENT_PORT).sync().channel();
            // request for key
            ch.writeAndFlush("key\r\n");

            // Read commands from the stdin.
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    lastWriteFuture = ch.writeAndFlush(line + "\r\n");
                    ch.closeFuture().sync();
                    break;
                } else {
                    // Sends the received line to the server.
                    if (key != null && !line.isEmpty()) {
                        JwtClaims jwtClaims = new JwtClaims();
                        JsonWebSignature jws = new JsonWebSignature();
                        jws.setKey(key);
                        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
                        try {
                            int actor_id = Integer.parseInt(line);
                            jwtClaims.setClaim("actor_id", actor_id);
                        } catch (NumberFormatException e) {
                            logger.info("Not a number, NumberFormatException.");
                            jwtClaims.setClaim("line", line);
                        }
                        jws.setPayload(jwtClaims.toJson());
                        lastWriteFuture = ch.writeAndFlush(jws.getCompactSerialization() + "\r\n");
                    } else {
                        lastWriteFuture = ch.writeAndFlush(line + "\r\n");
                    }
                }
            }

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        } catch (JoseException e) {
            logger.error("JoseException: ", e);
        }
    }

    public void stop() {
        group.shutdownGracefully();
    }

}

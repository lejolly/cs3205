package sg.edu.nus.comp.cs3205.c2.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;

public class NetworkClient {

    private static Logger logger = LoggerFactory.getLogger(NetworkClient.class.getSimpleName());

    private static final int CLIENT_PORT = 13205;
    private static final String HOST = "localhost";

    private ServerChannelHandler serverChannelHandler;
    public Key key = null;
    private ChannelFuture lastWriteFuture = null;
    private Channel ch;

    public NetworkClient(EventLoopGroup workerGroup, ServerChannelHandler serverChannelHandler) {
        this.serverChannelHandler = serverChannelHandler;
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer(this));

            // Start the connection attempt.
            ch = b.connect(HOST, CLIENT_PORT).sync().channel();
            // request for key
            ch.writeAndFlush("key\r\n");
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        }
    }

    public void sendInput(String line) {
        try {
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

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        } catch (JoseException e) {
            logger.error("JoseException: ", e);
        }
    }

    public void receiveReply(String reply) {
        serverChannelHandler.forwardReply(reply);
    }

    public void stopClient() {
        lastWriteFuture = ch.writeAndFlush("bye\r\n");
        try {
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        }
    }

}

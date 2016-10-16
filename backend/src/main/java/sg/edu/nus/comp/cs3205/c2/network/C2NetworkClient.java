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

public class C2NetworkClient {

    private static Logger logger = LoggerFactory.getLogger(C2NetworkClient.class.getSimpleName());

    private static final String HOST = "localhost";

    private C2ServerChannelHandler c2ServerChannelHandler;
    public Key key = null;
    private ChannelFuture lastWriteFuture = null;
    private Channel ch;

    public C2NetworkClient(EventLoopGroup workerGroup, C2ServerChannelHandler c2ServerChannelHandler, int c2ClientPort) {
        this.c2ServerChannelHandler = c2ServerChannelHandler;
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new C2ClientChannelInitializer(this));

            // Start the connection attempt.
            ch = b.connect(HOST, c2ClientPort).sync().channel();
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
        c2ServerChannelHandler.forwardReply(reply);
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

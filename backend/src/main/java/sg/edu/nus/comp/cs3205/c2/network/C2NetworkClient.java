package sg.edu.nus.comp.cs3205.c2.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c2.keys.C2KeyManager;
import sg.edu.nus.comp.cs3205.common.utils.JwsUtils;

public class C2NetworkClient {

    private static Logger logger = LoggerFactory.getLogger(C2NetworkClient.class.getSimpleName());

    private C2ServerChannelHandler c2ServerChannelHandler;
    private ChannelFuture lastWriteFuture = null;
    private Channel ch;
    private String id = null;

    public C2NetworkClient(EventLoopGroup workerGroup, C2ServerChannelHandler c2ServerChannelHandler,
                           int c2ClientPort, String c2ClientHost) {
        this.c2ServerChannelHandler = c2ServerChannelHandler;
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new C2ClientChannelInitializer(this));

            // Start the connection attempt.
            ch = b.connect(c2ClientHost, c2ClientPort).sync().channel();
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        }
    }

    public void sendInput(String line) {
        try {
            // Sends the received line to the server.
            if (!line.isEmpty()) {
                int actor_id = 0;
                try {
                    actor_id = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    logger.info("Not a number, NumberFormatException.");
                }
                lastWriteFuture = ch.writeAndFlush(JwsUtils.getSignedFieldWithId(
                        C2KeyManager.c2RsaPrivateKey, id, "actor_id", String.valueOf(actor_id)) + "\r\n");
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

    public void handleMessageFromC3(JwtClaims jwtClaims) {
        if (id == null && jwtClaims.hasClaim("id")) {
            id = (String) jwtClaims.getClaimsMap().get("id");
        }
        if (jwtClaims.hasClaim("message")) {
            logger.info("Received from C3: " + jwtClaims.getClaimsMap().get("message"));
            c2ServerChannelHandler.forwardReply((String) jwtClaims.getClaimsMap().get("message"));
        } else if (jwtClaims.hasClaim("actor_info")) {
            c2ServerChannelHandler.forwardReply((String) jwtClaims.getClaimsMap().get("actor_info"));
        }
    }

    public void stopClient() throws JoseException {
        lastWriteFuture = ch.writeAndFlush(JwsUtils.getSimpleSignedMessageWithId(C2KeyManager.c2RsaPrivateKey, id, "bye") + "\r\n");
        try {
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        }
    }

}

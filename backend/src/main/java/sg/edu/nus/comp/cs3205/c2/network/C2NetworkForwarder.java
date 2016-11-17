package sg.edu.nus.comp.cs3205.c2.network;

import com.google.gson.JsonSyntaxException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c2.csrf.C2CsrfManager;
import sg.edu.nus.comp.cs3205.c2.key.C2KeyManager;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;
import sg.edu.nus.comp.cs3205.common.data.json.LogoutRequest;
import sg.edu.nus.comp.cs3205.common.data.json.NotLoggedInResponse;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class C2NetworkForwarder {

    private static Logger logger = LoggerFactory.getLogger(C2NetworkForwarder.class);

    private C2ServerChannelHandler c2ServerChannelHandler;
    private Channel channel;
    private C2CsrfManager c2CsrfManager;

    public C2NetworkForwarder(EventLoopGroup workerGroup, C2ServerChannelHandler c2ServerChannelHandler,
                              int c2ClientPort, String c2ClientHost, C2CsrfManager c2CsrfManager) {
        this.c2CsrfManager = c2CsrfManager;
        this.c2ServerChannelHandler = c2ServerChannelHandler;
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new C2ClientChannelInitializer(this));
            // Start the connection attempt.
            channel = b.connect(c2ClientHost, c2ClientPort).sync().channel();
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        }
    }

    public void handleInputFromC1(String line) throws Exception {
        // uncomment to enable checking of signed messages from C1
//        BaseJsonFormat baseJsonFormat = JsonUtils.consumeSignedBaseJsonFormat(C2KeyManager.c1RsaPublicKey, line);
        try {
            BaseJsonFormat baseJsonFormat = JsonUtils.fromJsonString(line);
            if (baseJsonFormat != null && JsonUtils.hasJsonFormat(baseJsonFormat)) {
                if (!c2CsrfManager.checkCsrf(baseJsonFormat)) {
                    logger.info("Invalid csrf received");
                    if (baseJsonFormat.getData().containsKey("auth_token")) {
                        String auth_token = baseJsonFormat.getData().get("auth_token");
                        baseJsonFormat = new LogoutRequest();
                        Map<String, String> map = new HashMap<>();
                        map.put("auth_token", auth_token);
                        baseJsonFormat.setData(map);
                        baseJsonFormat.setId("c2");
                        NotLoggedInResponse notLoggedInResponse = new NotLoggedInResponse();
                        notLoggedInResponse.setData(map);
                        notLoggedInResponse.setId("c2");
                        c2ServerChannelHandler.forwardReplyToC1(notLoggedInResponse);
                    } else {
                        throw new Exception();
                    }
                }
                logger.info("Sending to C3: \"" + baseJsonFormat.getJsonString() + "\"");
                ChannelFuture lastWriteFuture = sendMessageToC3(JsonUtils.getSignedBaseJsonFormat(
                        C2KeyManager.c2RsaPrivateKey, baseJsonFormat));
                if (lastWriteFuture != null) {
                    lastWriteFuture.sync();
                }
                return;
            }
        } catch (JsonSyntaxException e) {
            logger.error("JsonSyntaxException: ", e);
        }
        logger.info("Invalid request received");
        throw new Exception();
    }

    public void handleMessageFromC3(BaseJsonFormat baseJsonFormat) throws JoseException, NoSuchAlgorithmException {
        baseJsonFormat = c2CsrfManager.addCsrf(baseJsonFormat);
        c2ServerChannelHandler.forwardReplyToC1(baseJsonFormat);
    }

    public void stopClient() {
        try {
            c2ServerChannelHandler.close();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        }
    }

    private ChannelFuture sendMessageToC3(String message) {
        logger.debug("Sending to C3: \"" + message + "\"");
        return channel.writeAndFlush(message + "\r\n");
    }

}

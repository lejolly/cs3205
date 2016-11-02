package sg.edu.nus.comp.cs3205.c2.network;

import com.google.gson.JsonSyntaxException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c2.key.C2KeyManager;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

public class C2NetworkForwarder {

    private static Logger logger = LoggerFactory.getLogger(C2NetworkForwarder.class.getSimpleName());

    private C2ServerChannelHandler c2ServerChannelHandler;
    private Channel channel;

    public C2NetworkForwarder(EventLoopGroup workerGroup, C2ServerChannelHandler c2ServerChannelHandler,
                              int c2ClientPort, String c2ClientHost) {
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
        try {
            BaseJsonFormat baseJsonFormat = JsonUtils.fromJsonString(line);
            if (baseJsonFormat != null && JsonUtils.hasJsonFormat(baseJsonFormat)) {
                logger.info("Sending to C3: \"" + baseJsonFormat.getJsonString() + "\"");
                ChannelFuture lastWriteFuture = channel.writeAndFlush(JsonUtils.getSignedBaseJsonFormat(
                        C2KeyManager.c2RsaPrivateKey, baseJsonFormat) + "\r\n");
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

    public void handleMessageFromC3(BaseJsonFormat baseJsonFormat) {
        c2ServerChannelHandler.forwardReplyToC1(baseJsonFormat.getJsonString());
    }

    public void stopClient() {
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        }
    }

}

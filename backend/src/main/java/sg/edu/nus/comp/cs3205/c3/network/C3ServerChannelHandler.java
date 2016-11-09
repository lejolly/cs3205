package sg.edu.nus.comp.cs3205.c3.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.C3RequestManager;
import sg.edu.nus.comp.cs3205.c3.key.C3KeyManager;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

@ChannelHandler.Sharable
public class C3ServerChannelHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(C3ServerChannelHandler.class);

    private C3RequestManager c3RequestManager;

    C3ServerChannelHandler(C3RequestManager c3RequestManager) {
        this.c3RequestManager = c3RequestManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String id = ctx.channel().toString();
        logger.info("New connection from C2: " + id);
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        BaseJsonFormat baseJsonFormat = JsonUtils.consumeSignedBaseJsonFormat(C3KeyManager.c2RsaPublicKey, request);
        if (baseJsonFormat != null && JsonUtils.hasJsonFormat(baseJsonFormat)) {
            logger.info("Received from C2: " + baseJsonFormat.getJsonString());
            BaseJsonFormat response = c3RequestManager.handleRequestFromC2(baseJsonFormat);
            if (response != null) {
                logger.info("Sending response: " +  response.getJsonString());
                sendMessageToC2(ctx, JsonUtils.getSignedBaseJsonFormat(C3KeyManager.c3RsaPrivateKey, response));
                return;
            }
        }
        logger.warn("Invalid request received: " + request);
        logger.info("Closing connection " + ctx.channel());
        sendMessageToC2(ctx, "error");
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception: ", cause);
        logger.info("Closing connection " + ctx.channel());
        sendMessageToC2(ctx, "error");
        ctx.close();
    }

    private void sendMessageToC2(ChannelHandlerContext ctx, String message) {
        logger.debug("Sending to C2: \"" + message + "\"");
        ctx.write(message + "\r\n");
        ctx.flush();
    }

}

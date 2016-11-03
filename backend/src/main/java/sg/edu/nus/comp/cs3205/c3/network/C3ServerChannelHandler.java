package sg.edu.nus.comp.cs3205.c3.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.auth.C3LoginManager;
import sg.edu.nus.comp.cs3205.c3.key.C3KeyManager;
import sg.edu.nus.comp.cs3205.c3.session.C3SessionManager;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat.JSON_FORMAT;
import sg.edu.nus.comp.cs3205.common.data.json.LoginRequest;
import sg.edu.nus.comp.cs3205.common.data.json.SaltRequest;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

@ChannelHandler.Sharable
public class C3ServerChannelHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(C3ServerChannelHandler.class);

    private C3SessionManager c3SessionManager;
    private C3LoginManager c3LoginManager;

    C3ServerChannelHandler(C3SessionManager c3SessionManager, C3LoginManager c3LoginManager) {
        this.c3SessionManager = c3SessionManager;
        this.c3LoginManager = c3LoginManager;
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
            BaseJsonFormat response = null;
            JSON_FORMAT format = JsonUtils.getJsonFormat(baseJsonFormat);
            logger.info("Received " + format);
            if (format == JSON_FORMAT.SALT_REQUEST) {
                SaltRequest saltRequest = SaltRequest.fromBaseFormat(baseJsonFormat);
                if (saltRequest != null) {
                    response = c3LoginManager.getUserSalt(saltRequest);
                }
            } else if (format == JSON_FORMAT.LOGIN_REQUEST) {
                LoginRequest loginRequest = LoginRequest.fromBaseFormat(baseJsonFormat);
                if (loginRequest != null) {
                    response = c3LoginManager.getLoginResponse(loginRequest);
                }
            }
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
